package net.peakgames.libgdx.stagebuilder.core.demo;


import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder;

public class ButtonScreen extends DemoScreen {

    public ButtonScreen(final AbstractGame game) {
        super(game);
    }


    @Override
    public void show() {
        super.show();
        StageBuilder.disableMultiTouch(this.stage);
    }
}
