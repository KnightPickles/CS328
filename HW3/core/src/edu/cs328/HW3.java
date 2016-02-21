package edu.cs328;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class HW3 extends ApplicationAdapter {
	Batch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	BitmapFont font;

	Sprite player;
	float x,y;
	int dir = 0;

	Animation walkAnimation;
	TextureRegion[] walkFrames;
	TextureRegion region;
	float stateTime;

	@Override
	public void create() {
		camera = new OrthographicCamera(840, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(0, 0, 0, 1);

		atlas = new TextureAtlas("spritesheet.atlas");
		player = atlas.createSprite("spaceman_stand0");

		walkFrames = new TextureRegion[3];
		walkFrames[0] = atlas.findRegion("spaceman_walk0");
		walkFrames[1] = atlas.findRegion("spaceman_walk1");
		walkFrames[2] = atlas.findRegion("spaceman_walk2");
		walkAnimation = new Animation(0.25f, walkFrames);

		x = 400;
		y = 200;
		player.setPosition(x,y);
		player.scale(10);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		player.setPosition(x,y);

		batch.begin();
		player.draw(batch);
		batch.end();

		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if(dir != 0) {
				player.flip(true, false);
				dir = 0;
			}
			stateTime += Gdx.graphics.getDeltaTime();
			region = walkAnimation.getKeyFrame(stateTime, true);
			//x -= 10;
			camera.translate(camera.position.x - 10, camera.position.y);

			player.setRegion(region);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			if(dir != 1) {
				player.flip(true, false);
				dir = 1;
			}
			stateTime += Gdx.graphics.getDeltaTime();
			region = walkAnimation.getKeyFrame(stateTime, true);
			camera.translate(camera.position.x + 10, camera.position.y);
			player.setRegion(region);
		}
	}
}