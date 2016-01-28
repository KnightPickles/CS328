package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by KnightPickles on 1/22/16.
 */
public class Assets {
    private final static String FILE_IMAGE_ATLAS = "uiskin.atlas";
    public static TextureAtlas imageAtlasGame;
    private final static String FILE_UI_SKIN = "uiskin.json";
    public static Skin skin;
    public static Texture items;
    public static BitmapFont font;

    public static Skin getSkin(){
        FileHandle skinFile = Gdx.files.internal(FILE_UI_SKIN);
        skin = new Skin( skinFile );
        return skin;
    }

    public static void loadResources (Game game, AssetManager m_assetManager) {
        // Backgrounds
        TextureLoader.TextureParameter param;
        param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        // Items
        ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), game.getResolutions());
        System.out.println("Asset path: " + resolver.resolve(FILE_IMAGE_ATLAS).path());
        m_assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
        m_assetManager.load(FILE_IMAGE_ATLAS, TextureAtlas.class);

        // Fonts
        m_assetManager.load("font.fnt", BitmapFont.class);
    }

    public static void assignResources (AssetManager m_assetManager) {
        if (m_assetManager.isLoaded(FILE_IMAGE_ATLAS)) {
            imageAtlasGame = m_assetManager.get(FILE_IMAGE_ATLAS, TextureAtlas.class);
        }

        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
}
