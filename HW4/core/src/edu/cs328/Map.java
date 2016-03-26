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

    TextureAtlas atlas;
    Camera camera;

    Map(int w, int h, TextureAtlas atlas, Camera camera) {
        worldWidth = w;
        worldHeight = h;
        this.atlas = atlas;
        this.camera = camera;
        SimplexNoise smpn = new SimplexNoise(1);
        map = smpn.generateOctavedSimplexNoise(worldWidth, worldHeight, 3, 1.0f, 0.015f);
        smpn = null;
    }

    public void draw(Batch batch) {
        // limit camera bounds
        if(camera.position.x <= 0) camera.position.x = 0;
        if(camera.position.y <= 0) camera.position.y = 0;
        if(camera.position.x + camera.viewportWidth >= worldWidth * HW4.PPM) camera.position.x = worldWidth * HW4.PPM - camera.viewportWidth;
        if(camera.position.y + camera.viewportHeight >= worldHeight * HW4.PPM) camera.position.y = worldHeight * HW4.PPM - camera.viewportHeight;

        // crude testing of simplex noise
        for(int i = 0; i < map[0].length; i++) {
            for(int j = 0; j < map[1].length; j++) {
                if(i * HW4.PPM + HW4.PPM >= camera.position.x &&
                        i * HW4.PPM - HW4.PPM <= camera.position.x + camera.viewportWidth &&
                        j * HW4.PPM + HW4.PPM >= camera.position.y &&
                        j * HW4.PPM - HW4.PPM <= camera.position.y + camera.viewportHeight) { // cull tiles for performance - better if an image could be generated
                    if (map[i][j] <= 0.1) {
                        batch.draw(atlas.findRegion("grass"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE/ 2, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2);
                    } else if (map[i][j] > 0.1 && map[i][j] <= 0.4) {
                        batch.draw(atlas.findRegion("dirt"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE/ 2, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2);
                    } else if (map[i][j] > 0.4) {
                        batch.draw(atlas.findRegion("sand"), i * HW4.PPM - Gdx.graphics.getWidth() / HW4.SCALE/ 2, j * HW4.PPM - Gdx.graphics.getHeight() / HW4.SCALE / 2);
                    }
                }
            }
        }
    }
}
