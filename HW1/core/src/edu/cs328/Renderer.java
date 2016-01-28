package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 1/22/16.
 */
public class Renderer {
    private Game game;
    private GameScreen gameScreen;
    private World box2dWorld;
    private TiledMap map;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera GAMEcam;
    private OrthographicCamera HUDcam;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float ppm, PPM;

    public StringBuffer stringBuffer;

    public Renderer(Game game, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.game = game;
        this.batch 		= game.getSpritebatch();
        this.shapeRenderer = game.getShapeRenderer();
        //this.box2dWorld = gameScreen.box2dWorld;
        //this.map 		= gameScreen.map;

        GAMEcam = game.getGAMECamera();
        HUDcam	= game.getHUDCamera();

        ppm = 1 / game.getPPM();
        PPM = game.getPPM();
        Gdx.app.log("game.getPPM(): " + game.getPPM(), "ppm: " +ppm);

        // load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
        //mapRenderer = new OrthogonalTiledMapRenderer(map, ppm, batch);
        stringBuffer = new StringBuffer();
    }

    public void renderTiledMap () {
        //---- move tiledmap-camera to the middle of the playfield -----------------
        /*GAMEcam.position.x = (GameCore.VIRTUAL_WIDTH_GAME / 2f) -
                ((GameCore.VIRTUAL_WIDTH_GAME / 2f) - (GameCore.PLAYFIELDWIDTH / 2f));
        GAMEcam.position.y = (GameCore.VIRTUAL_HEIGHT_GAME / 2f) -
                ((GameCore.VIRTUAL_HEIGHT_GAME / 2f) - (GameCore.PLAYFIELDHEIGHT / 2f));
        GAMEcam.update();

        //---- render tiledmap -----------------
        //mapRenderer.setView(GAMEcam);
        //mapRenderer.render();

        //---- move tiledmap-camera back to the middle of the viewport for correct unproject-coords -----------------
        GAMEcam.position.x = GameCore.VIRTUAL_WIDTH_GAME / 2f;
        GAMEcam.position.y = GameCore.VIRTUAL_HEIGHT_GAME / 2f;*/
    }

    public void renderGamePlay () {
        game.updateGAMECam();
        //batch.begin();
        //batch.enableBlending();
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0,0,0,1);

        for (i = 0; i < GameScreen.BOXCOUNT; i++) {

            boxObject = gameScreen.boxes.get(i);

            if (boxObject.body.isActive()) {
                winkel =  (MathUtils.radiansToDegrees * boxObject.angle);
                //shapeRenderer.circle(boxObject.pos.x + (boxObject.width / 2), boxObject.pos.y + (boxObject.height / 2), boxObject.width);
                shapeRenderer.circle(boxObject.pos.x * PPM,boxObject.pos.y * PPM, boxObject.width * PPM / 2);

                // draw(box, boxObject.pos.x, boxObject.pos.y, 0, 0, boxObject.width, boxObject.height, 1, 1, winkel);
            }
        }

        shapeRenderer.end();

        //----- Debugging ---------------------------
        if (debugRender) debugRenderer.render(box2dWorld, game.getHUDCamera().combined.scale(game.getPPM(), game.getPPM(), game.getPPM()));*/
    }

    public void renderHUD () {
        game.updateHUDCam();
        batch.begin();

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("FPS: ").append(gameScreen.lastFPS);
        Assets.font.getData().setScale(1f);
        Assets.font.draw(batch, stringBuffer, 680, 470);

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("SPS: ").append(gameScreen.lastSPS);
        Assets.font.getData().setScale(1f);
        Assets.font.draw(batch, stringBuffer, 740, 470);

        batch.end();
    }
}
