package net.peakgames.libgdx.stagebuilder.core.assets;

import com.badlogic.gdx.scenes.scene2d.Group;

public interface StageBuilderListener {
    public void onGroupBuilded(String fileName, Group group);
    public void onGroupBuildFailed(String fileName, Exception e);
}
