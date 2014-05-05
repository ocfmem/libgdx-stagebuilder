package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextFieldModel;
import net.peakgames.libgdx.stagebuilder.core.model.ToggleWidgetModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.widgets.ToggleWidget;

public class ToggleWidgetBuilder extends ActorBuilder{

    public ToggleWidgetBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        ToggleWidgetModel toggleWidgetModel = (ToggleWidgetModel)model;

        ToggleWidget.ToggleWidgetStyle toggleWidgetStyle = new ToggleWidget.ToggleWidgetStyle();
        toggleWidgetStyle.toggleButtonPadding = toggleWidgetModel.getToggleButtonPadding();

        toggleWidgetStyle.backgroundDrawable = new TextureRegionDrawable(assets.getTextureAtlas(toggleWidgetModel.getAtlasName()).findRegion(toggleWidgetModel.getBackgroundImageName()));
        toggleWidgetStyle.backgroundDrawable.setMinWidth( toggleWidgetStyle.backgroundDrawable.getMinWidth() * resolutionHelper.getSizeMultiplier());
        toggleWidgetStyle.backgroundDrawable.setMinHeight(toggleWidgetStyle.backgroundDrawable.getMinHeight() * resolutionHelper.getSizeMultiplier());
 
        toggleWidgetStyle.toggleButtonDrawable = new TextureRegionDrawable(assets.getTextureAtlas(toggleWidgetModel.getAtlasName()).findRegion(toggleWidgetModel.getButtonImageName()));
        toggleWidgetStyle.toggleButtonDrawable.setMinWidth( toggleWidgetStyle.toggleButtonDrawable.getMinWidth() * resolutionHelper.getSizeMultiplier());
        toggleWidgetStyle.toggleButtonDrawable.setMinHeight( toggleWidgetStyle.toggleButtonDrawable.getMinHeight() * resolutionHelper.getSizeMultiplier());

        if(toggleWidgetModel.getButtonDownImageName() == null){
            toggleWidgetStyle.toggleButtonDownDrawable = toggleWidgetStyle.toggleButtonDrawable;
        }else{
            toggleWidgetStyle.toggleButtonDownDrawable = new TextureRegionDrawable(assets.getTextureAtlas(toggleWidgetModel.getAtlasName()).findRegion(toggleWidgetModel.getButtonDownImageName()));
            toggleWidgetStyle.toggleButtonDownDrawable.setMinWidth( toggleWidgetStyle.toggleButtonDownDrawable.getMinWidth() * resolutionHelper.getSizeMultiplier());
            toggleWidgetStyle.toggleButtonDownDrawable.setMinHeight( toggleWidgetStyle.toggleButtonDownDrawable.getMinHeight() * resolutionHelper.getSizeMultiplier());
        }

        ToggleWidget toggleWidget = new ToggleWidget(toggleWidgetStyle);

        String initialToggle = toggleWidgetModel.getInitialToggle();
        if(initialToggle != null){
            if(initialToggle.equalsIgnoreCase("left")){
                toggleWidget.toggleToLeft();
            }else if(initialToggle.equalsIgnoreCase("right")){
                toggleWidget.toggleToRight();
            }else{
                //wrong argument
            }
        }

        normalizeModelSize(model, model.getWidth(), model.getHeight());
        setBasicProperties(model, toggleWidget);

        return toggleWidget;
    }

}
