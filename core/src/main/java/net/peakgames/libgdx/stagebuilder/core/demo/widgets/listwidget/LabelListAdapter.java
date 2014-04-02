package net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder;
import net.peakgames.libgdx.stagebuilder.core.widgets.listwidget.ListWidgetAdapter;

public class LabelListAdapter extends ListWidgetAdapter {

    public LabelListAdapter(StageBuilder stageBuilder) {
        super(stageBuilder);
    }

    @Override
    public Actor getActor(int position, Actor reusableActor) {
        if (reusableActor == null) {
            try {
                Group group = stageBuilder.buildGroup("listwidget/list_item_simple.xml");
                String value = (String) getItem(position);
                Label label = (Label) group.findActor("list_item_label");
                label.setText(value);
                return group;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String value = (String) getItem(position);
            Label label = (Label) ((Group)reusableActor).findActor("list_item_label");
            label.setText(value);
            return reusableActor;
        }
        return null;
    }
}
