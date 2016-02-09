package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.awt.*;

/**
 * Created by KnightPickles on 1/22/16.
 */
public class RenderManager {
    private Game game;
    private GameScreen gameScreen;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera GAMEcam;
    private OrthographicCamera HUDcam;
    private float ppm, PPM;

    public StringBuffer stringBuffer;
    public BitmapFont font = new BitmapFont();

    public RenderManager(Game game, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.game = game;
        this.batch 		= game.getSpritebatch();
        this.shapeRenderer = game.getShapeRenderer();

        GAMEcam = game.getGAMECamera();
        HUDcam	= game.getHUDCamera();

        ppm = 1 / game.getPPM();
        PPM = game.getPPM();
        Gdx.app.log("game.getPPM(): " + game.getPPM(), "ppm: " +ppm);

        stringBuffer = new StringBuffer();
    }

    public void renderHUD () {
        game.updateHUDCam();
        batch.begin();

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("FPS: ").append(gameScreen.lastFPS);
        font.getData().setScale(1f);
        font.draw(batch, stringBuffer, 680, 470);

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("SPS: ").append(gameScreen.lastSPS);
        font.getData().setScale(1f);
        font.draw(batch, stringBuffer, 740, 470);

        batch.end();
    }
}
