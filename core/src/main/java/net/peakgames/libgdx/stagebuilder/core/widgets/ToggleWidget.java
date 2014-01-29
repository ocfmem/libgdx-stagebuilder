package net.peakgames.libgdx.stagebuilder.core.widgets;


/**
 * Created by Engin Mercan on 29.01.2014.
 */


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.peakgames.libgdx.stagebuilder.core.ICustomWidget;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.ToggleWidgetModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import java.util.Map;

public class ToggleWidget extends WidgetGroup {
    private static final float ANIMATE_SPEED = 500.0f;

    private AssetsInterface assets;
    private Image background;
    private Image toggleButton;

    private float downX;
    private float downButtonX;
    private boolean isLeft;
    private float maxButtonX;
    private float minButtonX;
    private boolean animating = false;
    private TogleListener toggleListener;
    private boolean pressed = false;

    public ToggleWidget(ToggleWidgetModel toggleWidgetModel, AssetsInterface assets, ResolutionHelper resolutionHelper) {

        this.assets = assets;
        String atlasName = toggleWidgetModel.getAtlasName();

        float sizeMultiplier = resolutionHelper.getSizeMultiplier();

        String toggleBG = toggleWidgetModel.getBackgroundImageName();
        String toggleButtonImage = toggleWidgetModel.getButtonImageName();
        background = new Image(assets.getTextureAtlas(atlasName).findRegion(toggleBG));
        addActor(background);
        background.setWidth(background.getWidth() * sizeMultiplier);
        background.setHeight(background.getHeight() * sizeMultiplier);

        toggleButton = new Image(assets.getTextureAtlas(atlasName).findRegion(toggleButtonImage));
        addActor(toggleButton);
        toggleButton.setWidth(toggleButton.getWidth() * sizeMultiplier);
        toggleButton.setHeight(toggleButton.getHeight() * sizeMultiplier);

        float positionMultiplier = resolutionHelper.getPositionMultiplier();
        setPosition(Float.valueOf(toggleWidgetModel.getX()) * positionMultiplier, Float.valueOf(getY()) * positionMultiplier);

        float width = background.getWidth();
        float height = Math.max(toggleButton.getHeight(), background.getHeight());

        setWidth(width);
        setHeight(height);

        background.setY((getHeight()-background.getHeight())/2);
        addListener(new ToggleWidgetClickListener());

        maxButtonX = getWidth() - toggleButton.getWidth();
        minButtonX = 0;

        float padding = toggleWidgetModel.getToggleButtonPadding();
        minButtonX += padding/2;
        maxButtonX -= padding/2;

        isLeft = true;
        toggleButton.setX(minButtonX);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(animating){
            if(isLeft){
                toggleButton.setX(toggleButton.getX()-ANIMATE_SPEED*delta);
                if(toggleButton.getX()<=minButtonX){
                    toggleButton.setX(minButtonX);
                    animating = false;
                }
            }else{
                toggleButton.setX(toggleButton.getX()+ANIMATE_SPEED*delta);
                if(toggleButton.getX()>=maxButtonX){
                    toggleButton.setX(maxButtonX);
                    animating = false;
                }

            }
        }
    }


    public void setTogleListener(TogleListener toggleListener) {
        this.toggleListener = toggleListener;
    }

    private class ToggleWidgetClickListener extends ClickListener {
        public void clicked (InputEvent event, float x, float y) {

        }

        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            if (!super.touchDown(event, x, y, pointer, button)){
                return false;
            }
            if (pointer == 0 && button != 0){
                return false;
            }

            if(toggleButton.hit(x-toggleButton.getX(), y, true) != null){
                pressed = true;
                downX = x;
                downButtonX = toggleButton.getX();
            }else{
                if(isLeft){
                    toggleToRightAnimated();
                }else{
                    toggleToLeftAnimated();
                }
            }
            return true;
        }

        public void touchDragged (InputEvent event, float x, float y, int pointer) {
            super.touchDragged(event, x, y, pointer);
            if(pressed){
                float newToggleButtonX = downButtonX+x-downX;
                newToggleButtonX = Math.max(minButtonX, newToggleButtonX);
                newToggleButtonX = Math.min(maxButtonX, newToggleButtonX);
                toggleButton.setX(newToggleButtonX);
            }

        }

        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            if(pressed){
                boolean newIsLeft = toggleButton.getX() + toggleButton.getWidth()/2 < getWidth()/2;
                if(toggleListener != null && isLeft != newIsLeft){
                    toggleListener.widgetToggled(newIsLeft);
                }
                isLeft = newIsLeft;
                animating = true;
                pressed = false;
            }
            super.touchUp(event, x, y, pointer, button);
        }
    }

    public void toggleToLeft(){
        if(!isLeft){
            isLeft = true;
            toggleButton.setX(minButtonX);
            if(toggleListener != null){
                toggleListener.widgetToggled(isLeft);
            }
        }
    }
    public void toggleToRight(){
        if(isLeft){
            isLeft = false;
            toggleButton.setX(maxButtonX);
            if(toggleListener != null){
                toggleListener.widgetToggled(isLeft);
            }
        }
    }

    public void toggleToLeftAnimated(){
        if(!isLeft){
            isLeft = true;
            animating = true;
            if(toggleListener != null){
                toggleListener.widgetToggled(isLeft);
            }
        }
    }
    public void toggleToRightAnimated(){
        if(isLeft){
            isLeft = false;
            animating = true;
            if(toggleListener != null){
                toggleListener.widgetToggled(isLeft);
            }
        }
    }
    public interface TogleListener{
        public void widgetToggled(boolean isLeft);
    }

}

