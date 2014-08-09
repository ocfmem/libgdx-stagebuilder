package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextButtonModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class TextButtonBuilder extends ButtonBuilder {


    public TextButtonBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        TextButtonModel textButtonModel = (TextButtonModel) model;
        setTextures(textButtonModel);

        BitmapFont font = assets.getFont(textButtonModel.getFontName());

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(up, down, up, font);
        if (textButtonModel.getFontColor() != null) {
            style.fontColor = Color.valueOf(textButtonModel.getFontColor());
        }
        if (disabled != null) {
            style.disabled = disabled;
        }
        if ( checked != null){
            style.checked = checked;
        }

        TextButton textButton = new TextButton(getLocalizedString(textButtonModel.getText()).replace("\\n", String.format("%n")), style);
        normalizeModelSize(textButtonModel, up.getMinWidth(), up.getMinHeight());
        setBasicProperties(textButtonModel, textButton);
        setTextButtonProperties(textButtonModel, font, textButton);

        return textButton;
    }

    protected void setTextButtonProperties(TextButtonModel textButtonModel, BitmapFont font, TextButton textButton) {
        float positionMultiplier = resolutionHelper.getPositionMultiplier();
        textButton.padBottom(textButtonModel.getLabelPaddingBottom() * positionMultiplier);
        textButton.padTop(textButtonModel.getLabelPaddingTop() * positionMultiplier);
        textButton.padRight(textButtonModel.getLabelPaddingRight() * positionMultiplier);
        textButton.padLeft(textButtonModel.getLabelPaddingLeft() * positionMultiplier);
        Label label = textButton.getLabel();
        label.setWrap(textButtonModel.isWrap());
        if (textButtonModel.getAlignment() != null) {
            int alignment = calculateAlignment(textButtonModel.getAlignment());
            label.setAlignment(alignment);
        }
        Cell labelCell = textButton.getLabelCell();
        if (textButtonModel.isFontAutoScale()) {
            autoScaleTextButton(textButton);
        } else if (textButtonModel.getFontScale() != 1) {
            labelCell.height(textButton.getHeight());
            labelCell.bottom();
            label.setFontScale(font.getScaleX() * textButtonModel.getFontScale());
            label.setAlignment(Align.center);
        }
    }

    private void autoScaleTextButton(TextButton textButton) {
        Label label = textButton.getLabel();
        float textButtonWidth = textButton.getWidth() - textButton.getPadLeft() - textButton.getPadRight();
        float labelWidth = label.getWidth();
        if (labelWidth > textButtonWidth) {
            float scaleDownFactor = textButtonWidth / labelWidth;
            label.setFontScale(label.getStyle().font.getScaleX() * scaleDownFactor);
            label.setWidth(label.getWidth() * scaleDownFactor);
        }
    }
}
