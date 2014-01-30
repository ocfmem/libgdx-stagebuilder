package net.peakgames.libgdx.stagebuilder.core.widgets;


/**
 * Created by Engin Mercan on 29.01.2014.
 */

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ToggleWidget extends WidgetGroup {
    private static final float ANIMATE_SPEED = 500.0f;

    private Image background;
    private Image toggleButton;

    private float downX;
    private float downButtonX;
    private boolean isLeft;
    private float maxButtonX;
    private float minButtonX;
    private boolean animating = false;
    private ToggleListener toggleListener;
    private boolean pressed = false;
    private ToggleWidgetStyle style;
    public ToggleWidget(ToggleWidgetStyle style) {
        this.style = style;
        background = new Image(style.backgroundDrawable);
        addActor(background);


        toggleButton = new Image(style.toggleButtonDrawable);
        addActor(toggleButton);

        float width = background.getWidth();
        float height = Math.max(toggleButton.getHeight(), background.getHeight());

        setWidth(width);
        setHeight(height);

        background.setY((getHeight()-background.getHeight())/2);
        toggleButton.setY((getHeight()-toggleButton.getHeight())/2);

        addListener(new ToggleWidgetClickListener());

        maxButtonX = getWidth() - toggleButton.getWidth();
        minButtonX = 0;

        minButtonX += style.toggleButtonPadding/2;
        maxButtonX -= style.toggleButtonPadding/2;

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


    public void setToggleListener(ToggleListener toggleListener) {
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
                toggleButton.setDrawable(style.toggleButtonDownDrawable);
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
                boolean newIsLeft = toggleButton.getX() + toggleButton.getWidth()/2 < background.getWidth()/2;
                if(toggleListener != null && isLeft != newIsLeft){
                    toggleListener.widgetToggled(newIsLeft);
                }
                isLeft = newIsLeft;
                animating = true;
                pressed = false;
                toggleButton.setDrawable(style.toggleButtonDrawable);
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
    public interface ToggleListener{
        public void widgetToggled(boolean isLeft);
    }


    public static class ToggleWidgetStyle{
        public Drawable backgroundDrawable;
        public Drawable toggleButtonDrawable;
        public Drawable toggleButtonDownDrawable;
        public float toggleButtonPadding;
    }
}

