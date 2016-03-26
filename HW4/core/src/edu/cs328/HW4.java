package edu.cs328;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class HW4 extends ApplicationAdapter {

	public static final float PPM = 8f; // Pixels per meter. Might be better at 8. Used for sprite coordinates and scaling.
	public static final int SCALE = 3;
	public int SCALED_W; // Gdx width / Scale
	public int SCALED_H;

	Screen currentScreen;

	@Override
	public void create() {
		currentScreen = new GameScreen();
		currentScreen.show();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		currentScreen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void pause() {
		currentScreen.pause();
	}

	@Override
	public void resume() {
		currentScreen.resume();
	}

	@Override
	public void dispose() {
		currentScreen.hide();
		currentScreen.dispose();

	}

	@Override
	public void resize(int width, int height) {

	}
}