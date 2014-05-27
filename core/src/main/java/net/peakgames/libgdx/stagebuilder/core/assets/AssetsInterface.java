package net.peakgames.libgdx.stagebuilder.core.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AssetsInterface {

    public BitmapFont getFont(String fontName);

    public TextureAtlas getTextureAtlas(String atlasName);

    public Texture getTexture(String textureName);

    public void addAssetConfiguration(String key, String fileName, Class<?> type);

    public void removeAssetConfiguration(String key);

    public void loadAssetsSync(String key);

    public void loadAssetsAsync(String key, AssetLoaderListener listener);

    public Map<String, List<AssetLoader.AssetConfig>> getAssetsConfiguration();

    public void unloadAssets(String key);

    public void unloadAssets(String key, Set<String> exludedSet);

    public AssetManager getAssetMAnager();

    public Vector2 findBestResolution();

}
