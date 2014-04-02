package net.peakgames.libgdx.stagebuilder.core.widgets.listwidget;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;
import java.util.TimerTask;

public interface IListWidgetAdapter {

    public void initialize(List<TimerTask> items);

    public int getCount();

    public Object getItem(int position);

    public void addItem(Object item);

    public Actor getActor(int position, Actor reusableActor);

    public void notifyDataSetChanged();

    public void registerDataSetChangeListener(ListWidgetDataSetChangeListener listener);

    public void actorRemoved(Actor actor);

}
