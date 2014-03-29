package net.peakgames.libgdx.stagebuilder.core.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget.ComplextListAdapter;
import net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget.LabelListAdapter;
import net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget.ListItem;
import net.peakgames.libgdx.stagebuilder.core.widgets.listwidget.ListWidget;
import net.peakgames.libgdx.stagebuilder.core.widgets.listwidget.OnItemClickedListener;

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
        for (int j = 1; j <= 5; j++) {
            items.add("list_item " + j);
        }
        final LabelListAdapter listAdapter = new LabelListAdapter(getStageBuilder());
        listAdapter.initialize(items);
        ListWidget labelListWidget = (ListWidget) findActor("label_list_widget");
        labelListWidget.setListAdapter(listAdapter);


        findButton("addLabelItemButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listAdapter.addItem("test " + System.currentTimeMillis());
                listAdapter.notifyDataSetChanged();
            }
        });

        final ListWidget complexListWidget = (ListWidget) findActor("complex_list_widget");
        final ComplextListAdapter complextListAdapter = new ComplextListAdapter(getStageBuilder());

        List<ListItem> complexItemList = new ArrayList<ListItem>();
        for (int i = 0; i < 20; i++) {
            complexItemList.add(ListItem.generateRandom());

        }
        complextListAdapter.initialize(complexItemList);
        complexListWidget.setListAdapter(complextListAdapter);

        findButton("addComplexItemButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                complextListAdapter.addItem(ListItem.generateRandom());
                complextListAdapter.notifyDataSetChanged();
            }
        });

        complexListWidget.setOnItemClickListener(new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Actor view, int position) {
                Gdx.app.log(TAG, item + " clicked postion : " + position + " actor " + view);
            }
        });
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
