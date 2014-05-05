package net.peakgames.libgdx.stagebuilder.core.builder;

import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextFieldModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextFieldBuilder extends ActorBuilder{

    public TextFieldBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        TextFieldModel textFieldModel = (TextFieldModel)model;
        
        BitmapFont font = assets.getFont(textFieldModel.getFontName());
        Color fontColor = Color.valueOf(textFieldModel.getFontColor());
        
        TextureAtlas textureAtlas = assets.getTextureAtlas(textFieldModel.getAtlasName());
        
        NinePatchDrawable cursor = createNinePatchDrawable(textFieldModel.getCursorImageName(), textureAtlas, textFieldModel.getCursorOffset());
        cursor.getPatch().setColor(fontColor);
        NinePatchDrawable selection = createNinePatchDrawable(textFieldModel.getSelectionImageName(), textureAtlas, textFieldModel.getSelectionOffset());
        NinePatchDrawable background = null;

        if(textFieldModel.getBackgroundImageName() != null){
            background = createNinePatchDrawable(textFieldModel.getBackgroundImageName(), textureAtlas, textFieldModel.getBackGroundOffset());
            background.setLeftWidth(textFieldModel.getPadding());
            background.setRightWidth(textFieldModel.getPadding());
            background.setBottomHeight(textFieldModel.getPadding());
            background.setTopHeight(textFieldModel.getPadding());
        }
        
        TextFieldStyle textFieldStyle = new TextFieldStyle(font, fontColor, cursor, selection, background);
        TextField textField = new TextField(getLocalizedString(textFieldModel.getText()), textFieldStyle);
        textField.setPasswordMode(textFieldModel.isPassword());
        textField.setPasswordCharacter(textFieldModel.getPasswordChar().charAt(0));
        if(textFieldModel.getHint() != null) textField.setMessageText(getLocalizedString(textFieldModel.getHint()));
        normalizeModelSize(model, model.getWidth(), model.getHeight());
        setBasicProperties(model, textField);
        
        return textField;
    }

    protected void updateDrawableSize( TextureRegionDrawable textureRegionDrawable){
        float sizeMultiplier = resolutionHelper.getSizeMultiplier();
        textureRegionDrawable.setMinWidth( textureRegionDrawable.getMinWidth() * sizeMultiplier);
        textureRegionDrawable.setMinHeight( textureRegionDrawable.getMinHeight() * sizeMultiplier);
    }
    
    private NinePatchDrawable createNinePatchDrawable(String imageName, TextureAtlas textureAtlas ,int patchOffset) {
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(imageName);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        int left = patchOffset;
        int right = patchOffset;
        int top = patchOffset;
        int bottom = patchOffset;

        if (left > drawable.getMinWidth() / 2f || right > drawable.getMinWidth() / 2f) {
            left = (int) (drawable.getMinWidth() / 2f) - 2;
            right = (int) (drawable.getMinWidth() / 2f) - 2;
        }
        if (top > drawable.getMinHeight() / 2f || bottom > drawable.getMinHeight() / 2f) {
            top = (int) (drawable.getMinHeight() / 2f) - 2;
            bottom = (int) (drawable.getMinHeight() / 2f) - 2;
        }

        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable();
        NinePatch patch = new NinePatch(region, left, right, top, bottom);
        ninePatchDrawable.setPatch(patch);
        return ninePatchDrawable;
    }
}
