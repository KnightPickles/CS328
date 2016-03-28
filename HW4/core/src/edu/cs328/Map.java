package edu.cs328;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 3/25/16.
 */
public class Map {
    float[][] map;
    public int worldWidth;
    public int worldHeight;
    int xOff = 0;
    int yOff = 0;

    TextureAtlas atlas;
    Camera camera;

    Map(int w, int h, TextureAtlas atlas, Camera camera, int seed) {
        worldWidth = w;
        worldHeight = h;
        this.atlas = atlas;
        this.camera = camera;
        SimplexNoise smpn = new SimplexNoise(seed);
        map = smpn.generateOctavedSimplexNoise(worldWidth, worldHeight, 3, 1.0f, 0.015f);
        smpn = null;
    }

    public void offset(int x, int y) {
        xOff = x;
        yOff = y;
    }

    public void draw(Batch batch) {
        batch.begin();
        // crude testing of simplex noise
        for(int i = 0; i < map[0].length; i++) {
            for(int j = 0; j < map[1].length; j++) {
                if(i * HW4.PPM + HW4.PPM >= camera.position.x &&
                        i * HW4.PPM - HW4.PPM <= camera.position.x + camera.viewportWidth &&
                        j * HW4.PPM + HW4.PPM >= camera.position.y &&
                        j * HW4.PPM - HW4.PPM <= camera.position.y + camera.viewportHeight) { // cull tiles for performance - better if an image could be generated
                    if (map[i][j] <= 0.1) {
                        batch.draw(atlas.findRegion("grass"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE / 2 + xOff, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2 + yOff);
                    } else if (map[i][j] > 0.1 && map[i][j] <= 0.4) {
                        batch.draw(atlas.findRegion("dirt"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE / 2 + xOff, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2 + yOff);
                    } else if (map[i][j] > 0.4) {
                        batch.draw(atlas.findRegion("sand"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE / 2 + xOff, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2 + yOff);
                    }
                    if (map[i][j] <= -1.5) {
                        batch.draw(atlas.findRegion("bluewater"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE / 2 + xOff, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2 + yOff);
                    }
                }
            }
        }
        batch.end();
    }
}
