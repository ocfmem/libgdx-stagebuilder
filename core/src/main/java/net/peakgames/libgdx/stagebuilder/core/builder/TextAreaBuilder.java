package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextAreaModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextFieldModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class TextAreaBuilder extends ActorBuilder{

    public TextAreaBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        TextAreaModel textAreaModel = (TextAreaModel)model;
        
        BitmapFont font = assets.getFont(textAreaModel.getFontName());
        Color fontColor = Color.valueOf(textAreaModel.getFontColor());
        
        TextureAtlas textureAtlas = assets.getTextureAtlas(textAreaModel.getAtlasName());
        
        NinePatchDrawable cursor = createNinePatchDrawable(textAreaModel.getCursorImageName(), textureAtlas, textAreaModel.getCursorOffset());
        cursor.getPatch().setColor(fontColor);
        NinePatchDrawable selection = createNinePatchDrawable(textAreaModel.getSelectionImageName(), textureAtlas, textAreaModel.getSelectionOffset());
        NinePatchDrawable background = null;

        if(textAreaModel.getBackgroundImageName() != null){
            background = createNinePatchDrawable(textAreaModel.getBackgroundImageName(), textureAtlas, textAreaModel.getBackGroundOffset());
            background.setLeftWidth(textAreaModel.getPadding());
            background.setRightWidth(textAreaModel.getPadding());
            background.setBottomHeight(textAreaModel.getPadding());
            background.setTopHeight(textAreaModel.getPadding());
        }


        TextFieldStyle textAreaStyle = new TextFieldStyle(font, fontColor, cursor, selection, background);
        TextArea textArea = new TextArea(getLocalizedString(textAreaModel.getText()), textAreaStyle);
        textArea.setPasswordMode(textAreaModel.isPassword());
        textArea.setPasswordCharacter(textAreaModel.getPasswordChar().charAt(0));
        if(textAreaModel.getHint() != null) textArea.setMessageText(getLocalizedString(textAreaModel.getHint()));
        normalizeModelSize(model, model.getWidth(), model.getHeight());
        setBasicProperties(model, textArea);
        
        return textArea;
    }

    protected void updateDrawableSize( TextureRegionDrawable textureRegionDrawable){
        float sizeMultiplier = resolutionHelper.getSizeMultiplier();
        textureRegionDrawable.setMinWidth( textureRegionDrawable.getMinWidth() * sizeMultiplier);
        textureRegionDrawable.setMinHeight( textureRegionDrawable.getMinHeight() * sizeMultiplier);
    }
    
    private NinePatchDrawable createNinePatchDrawable(String imageName, TextureAtlas textureAtlas ,int patchOffset) {
    	 NinePatchDrawable ninePatchDrawable = new NinePatchDrawable();
         NinePatch patch = new NinePatch(textureAtlas.findRegion(imageName), patchOffset, patchOffset, patchOffset, patchOffset);
         ninePatchDrawable.setPatch(patch);
         return ninePatchDrawable;
    }
}
