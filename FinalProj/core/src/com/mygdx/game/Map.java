package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class Map {

    TextureAtlas atlas;
    Camera camera;
    public int worldWidth;
    public int worldHeight;
    
    Map(int w, int h, TextureAtlas atlas, Camera camera, int seed) {
        worldWidth = w;
        worldHeight = h;
        this.atlas = atlas;
        this.camera = camera;

    }
	
	public void draw(Batch batch) {
		batch.begin();

		
		
		batch.end();
	}
	
}
