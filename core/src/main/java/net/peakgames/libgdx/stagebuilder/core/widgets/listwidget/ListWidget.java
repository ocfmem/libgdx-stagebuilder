package net.peakgames.libgdx.stagebuilder.core.widgets.listwidget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import net.peakgames.libgdx.stagebuilder.core.ICustomWidget;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Displays a list of scrollable items. List items are inserted to the list using an Adapter.
 * Adapter pulls content from a dataasource such as java.util.ArrayList (or any any other collection)
 *
 * This is a scene2d widget. Items are scrolled vertically.
 *
 * This widget reuses its child actors.
 */
public class ListWidget extends WidgetGroup implements ICustomWidget, ListWidgetDataSetChangeListener {

    private static final String TAG = "StageBuilder.ListWidget";

    public static final float DEFAULT_VELOCITY = 300f;
    public static final float DEFAULT_FLING_TIME = 1f; // 1 second.
    public static final float DEFAULT_BLOCKED_DRAG_MOVE_COEFFICIENT = 0.15f;
    public static final float MAX_FLING_DELAY_MS = 150;
    public static final float DEFAULT_FLING_VELOCITY_FRICTION = 0.95f;
    private float flingTime = DEFAULT_FLING_TIME;

    private IListWidgetAdapter listAdapter;
    private boolean drawDebug = false;
    private ShapeRenderer debugRenderer;
    private boolean needsLayout = false;
    private final Vector2 lastDragPoint = new Vector2();
    private Vector2 gameAreaPosition;
    private boolean allActorsVisible = true;
    private List<Actor> recycledActors = new ArrayList<Actor>();
    private ListWidgetState state = ListWidgetState.STEADY;
    private float touchDownY;
    private float flingVelocity;

    //distance (y) between last two drag events.
    private float dragDistance;
    private float clickCancelDragThreshold = 5f;
    private long lastTouchDragTime;

    private Vector2 tmpVector = new Vector2();


    private OnItemClickedListener onItemClickedListener;
    private InputListener listItemClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Actor actor = event.getListenerActor();
            int position = (Integer) actor.getUserObject();
            float dragDistance = Math.abs(actor.getY() + y - touchDownY);
            if (onItemClickedListener != null && dragDistance <= clickCancelDragThreshold) {
                onItemClickedListener.onItemClicked(listAdapter.getItem(position), actor, position);
            }
        }
    };

    private enum ListWidgetState {
        STEADY, SETTLE_TOP, SETTLE_BOTTOM, FLINGING, DRAGGING, DRAG_DOWN_BLOCKED, DRAG_UP_BLOCKED
    }

    private enum DragDirection {
        UP, DOWN
    }

    public void setListAdapter(IListWidgetAdapter listAdapter) {
        this.listAdapter = listAdapter;
        this.listAdapter.registerDataSetChangeListener(this);
        this.needsLayout = true;
    }

    @Override
    public void build(Map<String, String> attributes, AssetsInterface assetsInterface, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        this.gameAreaPosition = resolutionHelper.getGameAreaPosition();
        this.drawDebug = Boolean.valueOf(attributes.get("debug"));
        if (this.drawDebug) {
            this.debugRenderer = new ShapeRenderer();
        }
        float positionMultiplier = resolutionHelper.getPositionMultiplier();
        setSize(getWidth() * positionMultiplier, getHeight() * positionMultiplier);
        clickCancelDragThreshold = clickCancelDragThreshold * positionMultiplier;
        addListener(new ListWidgetTouchListener());
    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
        this.onItemClickedListener = listener;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.flush();
        clipBegin(getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
        clipEnd();
        if (this.drawDebug) {
            batch.end();
            debugRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.rect(getX() + this.gameAreaPosition.x, getY() + this.gameAreaPosition.y, getWidth(), getHeight());
            debugRenderer.setColor(Color.YELLOW);
            debugRenderer.end();
            batch.begin();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (needsLayout) {
            clearChildren();
            int count = listAdapter.getCount();
            for (int i = 0; i < count; i++) {
                Actor actor = addActorToListWidget(i);
                if (actor.getY() + actor.getHeight() < 0) {
                    allActorsVisible = false;
                    break;
                }
            }
            needsLayout = false;
        }

        switch (state) {
            case SETTLE_TOP:
                handleSettleTop(delta);
                break;
            case SETTLE_BOTTOM:
                handleSettleBottom(delta);
                break;
            case FLINGING:
                handleFling(delta);
                break;
            case DRAG_DOWN_BLOCKED:
                handleDragDownBlocked();
                break;
            case DRAG_UP_BLOCKED:
                handleDragUpBlocked();
                break;

            default:
                break;
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor hitActor = super.hit(x, y, touchable);
        if (hitActor == null) {
            return null;
        }
        if (hitActor == this) {
            return this;
        }
        //child hit && check if child is %100 visible (not culled)
        tmpVector.set(hitActor.getX(), hitActor.getY());
        tmpVector = hitActor.localToStageCoordinates(tmpVector);
        return (tmpVector.y > getY())  ? hitActor : null;
    }

    private void handleDragUpBlocked() {
        Actor bottomActor = getBottomActor();
        if (bottomActor != null && isDragging(System.currentTimeMillis())) {
            moveItems(DragDirection.UP, dragDistance * DEFAULT_BLOCKED_DRAG_MOVE_COEFFICIENT);
        }
    }

    private void handleDragDownBlocked() {
        Actor firstActor = getChildWithUserObject(0);
        if (firstActor != null && isDragging(System.currentTimeMillis())) {
            moveItems(DragDirection.DOWN, dragDistance * DEFAULT_BLOCKED_DRAG_MOVE_COEFFICIENT);
        }
    }

    private void handleFling(float delta) {
        flingTime = flingTime- delta;
        if (flingTime > 0 && flingVelocity != 0) {
            float moveDistance = delta * flingVelocity * -1f;
            if (Math.abs(moveDistance) > getHeight()) {
                //limit move distance.
                moveDistance = flingVelocity < 0 ? (getHeight() * -1) : getHeight();
            }
            flingVelocity = flingVelocity * DEFAULT_FLING_VELOCITY_FRICTION;
            DragDirection direction = flingVelocity > 0 ? DragDirection.UP : DragDirection.DOWN;
            if (checkDragBlocked(direction)) {
                if (direction == DragDirection.DOWN) {
                    state = ListWidgetState.SETTLE_TOP;
                } else {
                    state = ListWidgetState.SETTLE_BOTTOM;
                }
                return;
            }
            moveItems(direction, moveDistance);
        } else {
            flingVelocity = 0;
            state = ListWidgetState.STEADY;
        }
    }

    private void handleSettleTop(float delta) {
        float moveUp = (delta * DEFAULT_VELOCITY);
        Actor firstItemActor = getChildWithUserObject(0);
        if (firstItemActor != null) {
            if (getActorTopY(firstItemActor) + moveUp >= getHeight()) {
                moveUp = getHeight() - getActorTopY(firstItemActor);
                state = ListWidgetState.STEADY;
            }
        }
        moveChildrenBy(moveUp);
    }

    private void handleSettleBottom(float delta) {
        float moveDown = (delta * DEFAULT_VELOCITY);
        Actor lastActor = getBottomActor();
        if (moveDown + lastActor.getY() <= 0) {
            moveDown = lastActor.getY();
            state = ListWidgetState.STEADY;
        }
        moveChildrenBy(moveDown * -1);
    }

    @Override
    public void onListWidgetDataSetChanged() {
        needsLayout = true;
    }

    private class ListWidgetTouchListener extends InputListener {
        long touchDownTimestamp;

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            touchDownY = y;
            lastDragPoint.set(x, y);
            touchDownTimestamp = System.currentTimeMillis();
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            flingVelocity = calculateVelocity(y);
            if (flingVelocity != 0) {
                state = ListWidgetState.FLINGING;
                flingTime = DEFAULT_FLING_TIME;
            }
            touchDownY = 0;

            if (allActorsVisible) {
                state = ListWidgetState.SETTLE_TOP;
                return;
            }

            Actor firstItemActor = getChildWithUserObject(0);
            if (firstItemActor != null) {
                float moveUpY = getHeight() - getActorTopY(firstItemActor);
                if (moveUpY > 0) {
                    state = ListWidgetState.SETTLE_TOP;
                }
            }
            Actor lastActor = getBottomActor();
            float moveDownY = lastActor.getY();
            if (moveDownY > 0) {
                state = ListWidgetState.SETTLE_BOTTOM;
            }
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            DragDirection dragDirection = lastDragPoint.y > y ? DragDirection.DOWN : DragDirection.UP;
            dragDistance = lastDragPoint.y - y;
            if (Math.abs(dragDistance) > clickCancelDragThreshold) {
                lastTouchDragTime = System.currentTimeMillis();
            }

            lastDragPoint.set(x, y);
            state = ListWidgetState.DRAGGING;

            if (allActorsVisible || checkDragBlocked(dragDirection)) {
                if (dragDirection == DragDirection.DOWN) {
                    state = ListWidgetState.DRAG_DOWN_BLOCKED;
                } else {
                    state = ListWidgetState.DRAG_UP_BLOCKED;
                }
                return;
            }

            moveItems(dragDirection, dragDistance);
        }

        private float calculateVelocity(float y) {
            long now = System.currentTimeMillis();
            if (isDragging(now)) {
                float duration = ((float) (now - touchDownTimestamp)) / 1000f;
                float distance = y - touchDownY;
                return distance / duration;
            } else {
                return 0f;
            }
        }
    }

    private boolean isDragging(long now) {
        return now - lastTouchDragTime < MAX_FLING_DELAY_MS;
    }

    /**
     * @param dragDirection touchDragged direction (UP or DOWN)
     * @return returns true if drag is allowed.
     */
    private boolean checkDragBlocked(DragDirection dragDirection) {
        if (dragDirection == DragDirection.DOWN) {
            Actor firstItemActor = getChildWithUserObject(0);
            if (firstItemActor != null && firstItemActor.getY() + firstItemActor.getHeight() <= getHeight()) {
                //if first item, block dragging
                return true;
            }
        } else {
            Actor bottomActor = getBottomActor();
            if (bottomActor.getY() > 0 && (getActorIndex(bottomActor) == listAdapter.getCount() - 1)) {
                //if last item, block dragging
                return true;
            }
        }
        return false;
    }

    private void moveItems(DragDirection dragDirection, float dragDistance) {
        if ((dragDirection == DragDirection.UP && dragDistance>0) || (dragDirection == DragDirection.DOWN && dragDistance < 0)) {
            return;
        }

        findRecycledActors(dragDirection, Math.abs(dragDistance));
        if (recycledActors.isEmpty() == false) {
            if (dragDirection == DragDirection.UP) {
                for (Actor actor : recycledActors) {
                    Actor bottomActor = getBottomActor();
                    int maxIndex = getActorIndex(bottomActor);
                    if (maxIndex >= listAdapter.getCount() - 1) {
                        break;
                    }
                    Actor newActor = listAdapter.getActor(maxIndex + 1, actor);
                    newActor.setUserObject(maxIndex + 1);
                    newActor.setY(bottomActor.getY() - newActor.getHeight());
                    removeActor(actor);
                    listAdapter.actorRemoved(actor);
                    addActor(newActor);
                }
            } else {
                for (Actor actor : recycledActors) {
                    Actor topActor = getTopActor();
                    int minIndex = getActorIndex(topActor);
                    if (minIndex == 0) {
                        break;
                    }
                    Actor newActor = listAdapter.getActor(minIndex - 1, actor);
                    newActor.setUserObject(minIndex - 1);
                    newActor.setY(getActorTopY(topActor));
                    removeActor(actor);
                    listAdapter.actorRemoved(actor);
                    addActor(newActor);
                }
            }
        }

        for (Actor actor : getChildren()) {
            actor.setY(actor.getY() - dragDistance);
        }
    }

    private void findRecycledActors(DragDirection dragDirection, float dragDistance) {
        recycledActors.clear();
        if (dragDirection == DragDirection.UP) {
            Actor topActor = getTopActor();
            if (topActor == null) {
                return;
            }
            int actorIndex = getActorIndex(topActor);
            for (int i = actorIndex; i < listAdapter.getCount(); i++) {
                Actor actor = getChildWithUserObject(i);
                if (actor.getY() + dragDistance > getHeight()) {
                    recycledActors.add(actor);
                } else {
                    break;
                }
            }
        } else {
            //drag DOWN
            Actor bottomActor = getBottomActor();
            int actorIndex = getActorIndex(bottomActor);
            for (int i = actorIndex; i >= 0; i--) {
                Actor actor = getChildWithUserObject(i);
                if (getActorTopY(actor) - dragDistance < 0) {
                    recycledActors.add(actor);
                } else {
                    break;
                }
            }
        }
    }

    private Actor addActorToListWidget(final int listAdapterIndex) {
        final Actor actor = listAdapter.getActor(listAdapterIndex, null);
        actor.setUserObject(listAdapterIndex);
        addActor(actor);
        actor.setY(getHeight() - (listAdapterIndex + 1) * actor.getHeight());
        actor.addListener(listItemClickListener);
        return actor;
    }


    /**
     * tum child actor'leri (list items) value kadar y ekseninde hareket ettirir.
     *
     * @param value
     */
    private void moveChildrenBy(float value) {
        SnapshotArray<Actor> children = getChildren();
        int count = children.size;
        for (int i = 0; i < count; i++) {
            Actor child = children.get(i);
            child.setY(child.getY() + value);
        }
    }

    private Actor getBottomActor() {
        Actor bottomActor = null;
        float minY = Integer.MAX_VALUE;
        for (Actor actor : getChildren()) {
            if (actor.getY() < minY) {
                bottomActor = actor;
                minY = bottomActor.getY();
            }
        }
        return bottomActor;
    }

    private Actor getTopActor() {
        Actor topActor = null;
        float maxY = 0;
        for (Actor actor : getChildren()) {
            if (actor.getY() > maxY) {
                topActor = actor;
                maxY = actor.getY();
            }
        }
        return topActor;
    }

    /**
     * Actor'un list adapter'daki sirasini doner.
     *
     * @param actor
     * @return actor index.
     */
    private int getActorIndex(Actor actor) {
        Object userObject = actor.getUserObject();
        if (userObject == null) {
            throw new RuntimeException("Actor " + actor + " does not have a userObject");
        }
        return (Integer) userObject;
    }

    private Actor getChildWithUserObject(int index) {
        SnapshotArray<Actor> children = getChildren();
        int count = children.size;
        for (int i = 0; i < count; i++) {
            Actor child = children.get(i);
            if (child.getUserObject() != null) {
                if (index == (Integer)child.getUserObject()) {
                    return child;
                }
            }
        }
        return null;
    }

    private float getActorTopY(Actor actor) {
        return actor.getY() + actor.getHeight();
    }
}
