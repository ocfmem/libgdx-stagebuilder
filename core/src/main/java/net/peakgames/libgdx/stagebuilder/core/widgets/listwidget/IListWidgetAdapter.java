package net.peakgames.libgdx.stagebuilder.core.widgets.listwidget;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;
import java.util.TimerTask;

public interface IListWidgetAdapter {

    public void initialize(List<TimerTask> items);

    public int getCount();

    public Object getItem(int position);

    public void addItem(Object item);

    /**
     *
     * @param position position of the list item in the adapter.
     * @param reusableActor if reusableActor is null you should create one. If it is not null update it and list widget will use updated list item.
     * @return an actor that all fields are populated with data at position
     */
    public Actor getActor(int position, Actor reusableActor);

    public void notifyDataSetChanged();

    public void registerDataSetChangeListener(ListWidgetDataSetChangeListener listener);

    public void actorRemoved(Actor actor);

}
