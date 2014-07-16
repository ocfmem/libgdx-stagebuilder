package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.SelectBoxModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class SelectBoxBuilder extends ActorBuilder {

    public static final Color DEFAULT_COLOR = Color.BLACK;
    public static final String DELIMITER = ";";
    public static int DEFAULT_PADDING_LEFT = 5;
    public static int DEFAULT_PADDING_RIGHT = 5;

    public SelectBoxBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        float positionMultiplier = resolutionHelper.getPositionMultiplier();
        DEFAULT_PADDING_RIGHT = (int) (DEFAULT_PADDING_RIGHT * positionMultiplier);
        DEFAULT_PADDING_LEFT = (int) (DEFAULT_PADDING_LEFT * positionMultiplier);

        SelectBoxModel selectBoxModel = (SelectBoxModel)model;
        selectBoxModel.setPaddingLeft((int) (selectBoxModel.getPaddingLeft() * positionMultiplier));
        selectBoxModel.setPaddingRight((int) (selectBoxModel.getPaddingRight() * positionMultiplier));

        TextureAtlas textureAtlas = assets.getTextureAtlas(selectBoxModel.getAtlasName());

        TextureRegionDrawable hScroll = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));
        TextureRegionDrawable hScrollKnob = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));
        TextureRegionDrawable vScroll = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));
        TextureRegionDrawable vScrollKnob = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));

        TextureRegionDrawable selection = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));

        TextureRegionDrawable selectBoxBackground = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelectionBackground()));

        NinePatchDrawable drawable = new NinePatchDrawable();
        int patchSize = calculatePatchSize(positionMultiplier, selectBoxModel, selectBoxBackground);

        NinePatch n = new NinePatch(textureAtlas.findRegion(selectBoxModel.getBackground()), patchSize, patchSize, patchSize, patchSize);
        drawable.setPatch(n);
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle(drawable, hScroll, hScrollKnob, vScroll, vScrollKnob);

        BitmapFont font = assets.getFont(selectBoxModel.getFontName());
        Color fontColor = selectBoxModel.getFontColor()==null ? DEFAULT_COLOR : Color.valueOf(selectBoxModel.getFontColor());
        Color fontColorSelected = selectBoxModel.getFontColorSelected()==null ? DEFAULT_COLOR : Color.valueOf(selectBoxModel.getFontColorSelected());
        Color fontColorUnselected = selectBoxModel.getFontColorUnselected()==null ? DEFAULT_COLOR : Color.valueOf(selectBoxModel.getFontColorUnselected());

        selection.setLeftWidth(selectBoxModel.getPaddingLeft()==0 ? DEFAULT_PADDING_LEFT : selectBoxModel.getPaddingLeft());
        selection.setRightWidth(selectBoxModel.getPaddingRight() == 0 ? DEFAULT_PADDING_RIGHT : selectBoxModel.getPaddingRight());
        selection.setTopHeight(5 * positionMultiplier);
        selection.setBottomHeight(5 * positionMultiplier);

        String[] values = new String[0];
        String filterValues =  getLocalizedString(selectBoxModel.getValue());
        if (selectBoxModel.getValue() != null && !filterValues.isEmpty()) {
            values = filterValues.split(DELIMITER);
        }
        
        autoScaleFont(font, values, selectBoxModel.getMaxTextWidth() * positionMultiplier);

        com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(font, fontColorSelected, fontColorUnselected, selection);

        selectBoxBackground.setLeftWidth(selectBoxModel.getPaddingLeft());
        selectBoxBackground.setRightWidth(selectBoxModel.getPaddingRight());
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(font, fontColor, selectBoxBackground, scrollPaneStyle, listStyle);


        SelectBox selectBox = new SelectBox(style);
        selectBox.setItems(values);
        selectBox.setName(selectBoxModel.getName());
        selectBox.getScrollPane().setScrollingDisabled(selectBoxModel.isHorizontalScrollDisabled(), selectBoxModel.isVerticalScrollDisabled());

        selectBox.setBounds(selectBoxModel.getX(), selectBoxModel.getY(), selectBoxBackground.getRegion().getRegionWidth(), selectBoxBackground.getRegion().getRegionHeight());

        normalizeModelSize(selectBoxModel, selectBoxBackground.getRegion().getRegionWidth(), selectBoxBackground.getRegion().getRegionHeight());
        setBasicProperties(selectBoxModel, selectBox);

        return selectBox;
    }

    private void autoScaleFont(BitmapFont font, String[] values, float maxWidth) {
        if (maxWidth <= 0) {
            return;
        }
        float max = 0;
        for (String value : values) {
            float textWidth = font.getBounds(value).width;
            if (textWidth > max) {
                max = textWidth;
            }
        }
        if (max > maxWidth) {
            font.setScale(font.getScaleX() * (maxWidth/max));
        }
    }

    /**
     * TODO Bu metod icinde sadece height kontrolu yapiliyor.
     * bottom ve top patch toplami nine-patch resminin yuksekliginden buyuk ise
     * patch size yuksekligi gecmeyecek sekilde guncelleniyor.
     * @param positionMultiplier
     * @param selectBoxModel
     * @param selectBoxBackground
     * @return patch size
     */
    private int calculatePatchSize(float positionMultiplier, SelectBoxModel selectBoxModel, TextureRegionDrawable selectBoxBackground) {
        int patchSize = (int) (positionMultiplier * selectBoxModel.getPatchSize());
        if (patchSize > (selectBoxBackground.getMinHeight() /2f)) {
            patchSize = (int) (selectBoxBackground.getMinHeight() /2f) - 2 ;
        }
        return patchSize;
    }

}
