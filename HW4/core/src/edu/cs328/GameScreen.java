package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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


    public static GameScreen _instance;

    Batch batch;
    TextureAtlas atlas;
    OrthographicCamera camera;
    OrthographicCamera cameraGUI;
    Viewport viewport;
    BitmapFont font;
    GlyphLayout layout;
    World world;
    Map map;
    EntityManager entityManager;
    SelectionManager selectionManager;
    ShapeRenderer shapeRenderer;

    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    @Override
    public void show() {
        _instance = this;

        // Box2D
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new GameCollision());
        debugRenderer = new Box2DDebugRenderer();

        // Graphics
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / HW4.SCALE, Gdx.graphics.getHeight() / HW4.SCALE);
        viewport = new FitViewport(Gdx.graphics.getWidth() / HW4.SCALE, Gdx.graphics.getHeight() / HW4.SCALE, camera);
        batch = new SpriteBatch();
        atlas = new TextureAtlas("rts.atlas");
        map = new Map(100, 100, atlas, camera);
        entityManager = new EntityManager(atlas, world);
        shapeRenderer = new ShapeRenderer();

        selectionManager = new SelectionManager();
        MyInputProcessor inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        // Font
        layout = new GlyphLayout();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 4;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render(float delta) {
        // Update
        entityManager.update();
        world.step(1f/60f, 6, 2);
        cameraUpdate();

        // Draw
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //debugMatrix = batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0);
        //debugRenderer.render(world, debugMatrix);

        batch.setTransformMatrix(camera.view);
        batch.setProjectionMatrix(camera.projection);
        batch.begin();
        map.draw(batch);
        entityManager.draw(batch);
        batch.end();

        if (renderLines.size > 0) {
            shapeRenderer.setProjectionMatrix(camera.projection);
            shapeRenderer.setTransformMatrix(camera.view);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < renderLines.size; i+=2) {
                shapeRenderer.line(renderLines.get(i), renderLines.get(i+1));
            }
            shapeRenderer.end();
        }

        debugRenderer.render(world, camera.combined);
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
        int mx = Gdx.input.getX() / HW4.SCALE;
        int my = Gdx.input.getY() / HW4.SCALE;
        int threshold = Gdx.graphics.getHeight() / HW4.SCALE / 50; // for when mouse is within ~x% of screen bounds
        int moveSpeed = 2;

        // Move camera if mouse is within threshold bounds of screen border.
        // First 4 if's for corner bounds. Last 4 for edges.
        if(mx < threshold && my < threshold) {
            camera.position.x -= moveSpeed; // slow it a bit
            camera.position.y += moveSpeed;
        } else if(mx < threshold && my > Gdx.graphics.getHeight() / HW4.SCALE - threshold) {
            camera.position.x -= moveSpeed;
            camera.position.y -= moveSpeed;
        } else if(mx > Gdx.graphics.getWidth() / HW4.SCALE - threshold && my < threshold) {
            camera.position.x += moveSpeed;
            camera.position.y += moveSpeed;
        } else if(mx > Gdx.graphics.getWidth() / HW4.SCALE - threshold && my > Gdx.graphics.getHeight() / HW4.SCALE - threshold) {
            camera.position.x += moveSpeed;
            camera.position.y -= moveSpeed;
        } else if(mx < threshold) {
            camera.position.x -= moveSpeed;
        } else if(mx > Gdx.graphics.getWidth() / HW4.SCALE - threshold) {
            camera.position.x += moveSpeed;
        } else if(my < threshold) {
            camera.position.y += moveSpeed;
        } else if(my > Gdx.graphics.getHeight() / HW4.SCALE - threshold) {
            camera.position.y -= moveSpeed;
        }

        // Camera bounds set to world size in Map
        camera.update();
    }

    public void drawFont(String text, int x, int y, boolean center) {
        layout.setText(font, text);
        float w = layout.width;
        float h = layout.height;
        if (center) {
            font.draw(batch, text, x - w / 2, y - h / 2);
        } else font.draw(batch, text, x, y);
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
