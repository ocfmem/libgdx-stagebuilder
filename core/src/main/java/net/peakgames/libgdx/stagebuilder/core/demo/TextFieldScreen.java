package net.peakgames.libgdx.stagebuilder.core.demo;


import net.peakgames.libgdx.stagebuilder.core.AbstractGame;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import net.peakgames.libgdx.stagebuilder.core.widgets.ToggleWidget;

public class TextFieldScreen extends DemoScreen {

    public TextFieldScreen(AbstractGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        findToggleWidget("toggleWidget").setToggleListener(new ToggleWidget.ToggleListener() {
            @Override
            public void widgetToggled(boolean isLeft) {
                if (isLeft) {
                    findLabel("toggle_status_label").setText("ENABLED");
                } else {
                    findLabel("toggle_status_label").setText("DISABLED");
                }
            }
        });
    }
}
