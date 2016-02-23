package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import sun.plugin2.util.ColorUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by KnightPickles on 2/21/16.
 */
public class GameObjectManager {
    ArrayList<SimpleGameObject> simpleGO = new ArrayList<SimpleGameObject>();
    ArrayList<SimpleGameObject> removeGO = new ArrayList<SimpleGameObject>();

    TextureAtlas atlas;
    World world;

    Player player;
    OrthographicCamera camera;
    float xBounds;
    float yBounds;
    float xSpawn;
    float ySpawn;


    GameObjectManager(TextureAtlas atlas, World world, Player player, OrthographicCamera camera) {
        this.world = world;
        this.atlas = atlas;
        this.player = player;
        this.camera = camera;
        xBounds = camera.viewportWidth;
        yBounds = camera.viewportHeight;
    }

    BufferedImage level = null;
    int scale = 64;

    void update() {
        if(HW3.win) return;
        getInput();
        for(SimpleGameObject o : simpleGO) {
            o.update();
            if(o instanceof Fuel) {
                if(((Fuel) o).collected) removeGO.add(o);
            }
        }
        for(SimpleGameObject o : removeGO) {
            if(o instanceof PhysicsGameObject) ((PhysicsGameObject) o).destroyBody();
            simpleGO.remove(o);
        }
        removeGO.clear();

        if(player.sprite.getX() >= camera.position.x - 100) camera.position.x = player.sprite.getX() + 100;
        if(player.sprite.getY() >= camera.position.y + 100) camera.position.y = player.sprite.getY() - 100;
        if(player.sprite.getY() <= camera.position.y - 100) camera.position.y = player.sprite.getY() + 100;

        if(player.sprite.getY() - 30 < yBounds) {
            player.lives--;
            destroy(player);
            if(player.lives > 0) {
                camera.position.x = xSpawn;
                camera.position.y = ySpawn;
                player = new Player(atlas, world, camera.position.x - 200, camera.position.y + camera.viewportHeight / 2 - 30, player.lives, player.fuel);
                addObject(player);
            } else {
                player.lives = 0;
                HW3.stop = true;
            }
        }
    }

    void draw(Batch batch) {
        for(SimpleGameObject o : simpleGO)
             o.draw(batch);
    }

    void getInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            player.setLVel(Math.max(player.body.getLinearVelocity().x, 1f), player.body.getLinearVelocity().y);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            player.setLVel(Math.min(player.body.getLinearVelocity().x, -1f), player.body.getLinearVelocity().y);
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (player.fuel > 0) {
                player.setLVel(player.body.getLinearVelocity().x, Math.max(player.body.getLinearVelocity().y, 1f));
            } else if(player.isGrounded){
                player.setLVel(player.body.getLinearVelocity().x, Math.max(player.body.getLinearVelocity().y, 1f));
            }
        }
    }

    void addObject(SimpleGameObject object) {
        simpleGO.add(object);
    }

    void clear() {
        simpleGO.clear();
        removeGO.clear();
    }

    void destroy(SimpleGameObject object) { simpleGO.remove(object); }

    void loadGameFromFile(String filename) {
        try {
            level = ImageIO.read(new File(filename));
            int width = level.getWidth();
            int height = level.getHeight();
            clear();

            yBounds = level.getHeight() * -scale;

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Color c = new Color(level.getRGB(col, row));

                    if (c.equals(Color.BLACK)) {
                        addObject(new Brick(atlas, world, col * scale, -row * scale));
                    } else if (c.equals(Color.GREEN)) {
                        addObject(new Rocket(atlas, world, col * scale, -row * scale));
                    } else if (c.equals(Color.RED)) {
                        addObject(new Fuel(atlas, world, col * scale, -row * scale));
                    } else if (c.equals(Color.BLUE)) {
                        camera.translate(col * scale, -row * scale);
                        xSpawn = col * scale;
                        ySpawn = -row * scale;
                        this.player = new Player(atlas, world, camera.position.x - 200, camera.position.y + camera.viewportHeight / 2 - 30, 3, 10000);
                        addObject(this.player);
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
