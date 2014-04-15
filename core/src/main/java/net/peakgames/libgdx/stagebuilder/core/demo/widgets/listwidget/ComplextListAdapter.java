package net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder;
import net.peakgames.libgdx.stagebuilder.core.widgets.listwidget.ListWidgetAdapter;

import java.util.HashMap;
import java.util.Map;

public class ComplextListAdapter extends ListWidgetAdapter {

    Map<String, Drawable> drawableCache = new HashMap<String, Drawable>();

    public ComplextListAdapter(StageBuilder stageBuilder) {
        super(stageBuilder);
    }

    @Override
    public Actor getActor(int position, Actor reusableActor) {
        if (reusableActor == null) {
            try {
                Group group = stageBuilder.buildGroup("listwidget/list_item_complex.xml");
                updateActor(position, group);
                return group;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            updateActor(position, (Group) reusableActor);
            return reusableActor;
        }
        return null;
    }

    private void updateActor(final int position, Group group) {
        Label nameLabel = (Label) group.findActor("name");
        Label chipsLabel = (Label) group.findActor("chips");
        ListItem item = (ListItem) items.get(position);
        nameLabel.setText(item.getName());
        chipsLabel.setText(item.getChips() + "");

        Image logo = (Image) group.findActor("logo");
        Drawable drawable = drawableCache.get(item.getFrame());
        if (drawable == null) {
            TextureAtlas.AtlasRegion atlasRegion = stageBuilder.getAssets().getTextureAtlas("common.atlas").findRegion(item.getFrame());
            drawable = new TextureRegionDrawable(atlasRegion);
            drawableCache.put(item.getFrame(), drawable);
        }
        logo.setDrawable(drawable);
    }

    //For testing dynamic removal
    public void removeTopActor(){
        if(!this.items.isEmpty()){
            this.items.remove(0);
        }
    }

    @Override
    public void actorRemoved(Actor actor) {
    }
}
