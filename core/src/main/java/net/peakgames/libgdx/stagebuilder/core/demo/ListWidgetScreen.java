package net.peakgames.libgdx.stagebuilder.core.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget.LabelListAdapter;
import net.peakgames.libgdx.stagebuilder.core.widgets.ListWidget;

import java.util.ArrayList;
import java.util.List;

public class ListWidgetScreen extends DemoScreen {
    public ListWidgetScreen(AbstractGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        List<String> items = new ArrayList<String>();
        for (int i = 1; i <= 200; i++) {
            items.add("list item " + i);
        }
        LabelListAdapter listAdapter = new LabelListAdapter(getStageBuilder());
        listAdapter.initialize(items);
        ListWidget labelListWidget = (ListWidget) findActor("label_list_widget");
        labelListWidget.setListAdapter(listAdapter);

    }

    float sumDelta = 0;
    @Override
    public void render(float delta) {
        super.render(delta);
        sumDelta += delta;
        if (sumDelta > 1) {
            sumDelta = 0;
            Label fpsLabel = findLabel("fps");
            fpsLabel.setText("FPS : " + Gdx.graphics.getFramesPerSecond());
        }
    }
}
