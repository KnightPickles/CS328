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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


public class HW3 extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;
	Batch batch;
	OrthographicCamera camera;
	BitmapFont font;

	@Override
	public void create() {
		camera = new OrthographicCamera(840, 480);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(0, 0, 0, 1);
		font = new BitmapFont();
		font.setColor(0, 0, 0, 1);


	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}