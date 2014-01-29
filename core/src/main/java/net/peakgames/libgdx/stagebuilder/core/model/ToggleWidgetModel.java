package net.peakgames.libgdx.stagebuilder.core.model;

public class ToggleWidgetModel extends BaseModel {

    private String backgroundImageName;
    private String buttonImageName;
    private String atlasName;
    private float toggleButtonPadding;
    private String initialToggle;


    public String getAtlasName() {
        return atlasName;
    }

    public void setAtlasName(String atlasName) {
        this.atlasName = atlasName;
    }

    public float getToggleButtonPadding() {
        return toggleButtonPadding;
    }

    public void setToggleButtonPadding(float toggleButtonPadding) {
        this.toggleButtonPadding = toggleButtonPadding;
    }

    public String getBackgroundImageName() {
        return backgroundImageName;
    }

    public void setBackgroundImageName(String backgroundImageName) {
        this.backgroundImageName = backgroundImageName;
    }

    public String getButtonImageName() {
        return buttonImageName;
    }

    public void setButtonImageName(String buttonImageName) {
        this.buttonImageName = buttonImageName;
    }


    public String getInitialToggle() {
        return initialToggle;
    }

    public void setInitialToggle(String initialToggle) {
        this.initialToggle = initialToggle;
    }
}
