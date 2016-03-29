package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by KnightPickles on 3/26/16.
 */
public class GameScreen implements Screen {

    private HW4 game;
    public static GameScreen _instance;

    OrthographicCamera camera;
    Viewport viewport;
    World world;
    Map map;
    EntityManager entityManager;
    SelectionManager selectionManager;

    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    private GameGUI gui;

    public GameScreen(HW4 game) {
        this.game = game;
    }

    @Override
    public void show() {
        _instance = this;
        HW4.stop = HW4.win = false;
        game.batch = new SpriteBatch();


        camera = new OrthographicCamera(Gdx.graphics.getWidth() / HW4.SCALE, Gdx.graphics.getHeight() / HW4.SCALE);
        viewport = new FitViewport(Gdx.graphics.getWidth() / HW4.SCALE, Gdx.graphics.getHeight() / HW4.SCALE, camera);

        // Box2D
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new GameCollision());
        debugRenderer = new Box2DDebugRenderer();

        map = new Map(100, 100, game.atlas, camera, 1);
        entityManager = new EntityManager(game.atlas, world);
        selectionManager = new SelectionManager(game);
        gui = new GameGUI(game, selectionManager);
        MyInputProcessor inputProcessor = new MyInputProcessor();
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gui.stage);
        inputMultiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        // Update
        if(!HW4.stop) {
            entityManager.update();
            world.step(1f / 60f, 6, 2);
            cameraUpdate();
        }

        gui.update();


        // Draw
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
        //debugRenderer.render(world, debugMatrix);

        game.batch.setTransformMatrix(camera.view);
        game.batch.setProjectionMatrix(camera.projection);
        game.shapeRenderer.setTransformMatrix(camera.view);
        game.shapeRenderer.setProjectionMatrix(camera.projection);
        map.draw(game.batch);
        entityManager.draw(game.batch);
        selectionManager.render(game.shapeRenderer, game.batch);

        if (renderLines.size > 0) {
            game.shapeRenderer.setProjectionMatrix(camera.projection);
            game.shapeRenderer.setTransformMatrix(camera.view);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < renderLines.size; i+=2) {
                game.shapeRenderer.line(renderLines.get(i), renderLines.get(i+1));
            }
            game.shapeRenderer.end();
        }

        gui.render();

        //debugRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void cameraUpdate() {
        // pixel coords starting 0,0 in top left corner
        int mx = Gdx.input.getX() / game.SCALE;
        int my = Gdx.input.getY() / game.SCALE;
        int threshold = Gdx.graphics.getHeight() / game.SCALE / 18; // for when mouse is within ~x% of screen bounds
        int moveSpeed = 2;

        // Move camera if mouse is within threshold bounds of screen border.
        // First 4 if's for corner bounds. Last 4 for edges.
        if(mx < threshold && my < threshold) {
            camera.position.x -= moveSpeed; // slow it a bit
            camera.position.y += moveSpeed;
        } else if(mx < threshold && my > Gdx.graphics.getHeight() / game.SCALE - threshold) {
            camera.position.x -= moveSpeed;
            camera.position.y -= moveSpeed;
        } else if(mx > Gdx.graphics.getWidth() / game.SCALE - threshold && my < threshold) {
            camera.position.x += moveSpeed;
            camera.position.y += moveSpeed;
        } else if(mx > Gdx.graphics.getWidth() / game.SCALE - threshold && my > Gdx.graphics.getHeight() / game.SCALE - threshold) {
            camera.position.x += moveSpeed;
            camera.position.y -= moveSpeed;
        } else if(mx < threshold) {
            camera.position.x -= moveSpeed;
        } else if(mx > Gdx.graphics.getWidth() / game.SCALE - threshold) {
            camera.position.x += moveSpeed;
        } else if(my < threshold) {
            camera.position.y += moveSpeed;
        } else if(my > Gdx.graphics.getHeight() / game.SCALE - threshold) {
            camera.position.y -= moveSpeed;
        }
        // limit camera bounds
        if (camera.position.x <= 0) camera.position.x = 0;
        if (camera.position.y <= 0) camera.position.y = 0;
        if (camera.position.x + camera.viewportWidth >= map.worldWidth * HW4.PPM)
            camera.position.x = map.worldWidth * HW4.PPM - camera.viewportWidth;
        if (camera.position.y + camera.viewportHeight >= map.worldHeight * HW4.PPM)
            camera.position.y = map.worldHeight * HW4.PPM - camera.viewportHeight;

        camera.update();
    }

    public OrthographicCamera GetCamera() {
        return camera;
    }

    Array<Vector2> renderLines = new Array<Vector2>();
    public void renderSelectionBox(Vector2 start, Vector2 end) {

        renderLines.clear();
        renderLines.add(new Vector2(start.x, start.y));
        renderLines.add(new Vector2(end.x, start.y));

        renderLines.add(new Vector2(start.x, start.y));
        renderLines.add(new Vector2(start.x, end.y));

        renderLines.add(new Vector2(start.x, end.y));
        renderLines.add(new Vector2(end.x, end.y));

        renderLines.add(new Vector2(end.x, start.y));
        renderLines.add(new Vector2(end.x, end.y));
    }

    public void stopRenderSelectionBox() {
        renderLines.clear();
    }
}
