package net.peakgames.libgdx.stagebuilder.core.widgets.listwidget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder;

import java.util.Collections;
import java.util.List;

/**
 * An abstract list widget adapter that uses list of items for populating list view.
 */
public abstract class ListWidgetAdapter implements IListWidgetAdapter {

    protected List<Object> items = Collections.EMPTY_LIST;
    protected ListWidgetDataSetChangeListener dataSetChangeListener;
    protected StageBuilder stageBuilder;

    public ListWidgetAdapter(StageBuilder stageBuilder) {
        this.stageBuilder = stageBuilder;
    }

    @Override
    public void initialize(List items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public abstract Actor getActor(int position, Actor reusableActor);

    @Override
    public void notifyDataSetChanged() {
        dataSetChangeListener.onListWidgetDataSetChanged();
    }

    @Override
    public void registerDataSetChangeListener(ListWidgetDataSetChangeListener listener) {
        this.dataSetChangeListener = listener;
    }

    @Override
    public void addItem(Object item) {
        this.items.add(item);
    }

    @Override
    public void actorRemoved(Actor actor) {
    }
}
