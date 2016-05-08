package com.mygdx.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MyInputProcessor implements InputProcessor {

	public static InputProcessor _instance;

	public static boolean wIsDown;
	public static boolean aIsDown;
	public static boolean sIsDown;
	public static boolean dIsDown;
	
	public static boolean rightMouseIsDown;
	public static Vector2 rightMouseLocation;
	
	public static InputMode inputMode = InputMode.PlayMode;
	public enum InputMode {
		PlayMode,	//Regular playing/movement/etc
		BuildMode	//Trying to build new turrets
	}

	MyInputProcessor() {
		if(_instance != null) System.out.println("Creating multiple input processors");
		_instance = this;
	}

	public static boolean isPlayMode() {
		return inputMode == InputMode.PlayMode;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.W)
			wIsDown = true;
		if (keycode == Keys.A)
			aIsDown = true;
		if (keycode == Keys.S)
			sIsDown = true;
		if (keycode == Keys.D)
			dIsDown = true;
		
		if (keycode == Keys.B) {
			if (inputMode != InputMode.BuildMode) {
				inputMode = InputMode.BuildMode;
				BuildManager._instance.enableBuildMode();
			}
			else {
				inputMode = InputMode.PlayMode;
				BuildManager._instance.disableBuildMode();
			}
		}
			
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.W)
			wIsDown = false;
		if (keycode == Keys.A)
			aIsDown = false;
		if (keycode == Keys.S)
			sIsDown = false;
		if (keycode == Keys.D)
			dIsDown = false;
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == 0 && inputMode == InputMode.PlayMode) {
			SelectionManager._instance.leftTouched(screenY, screenY);
		} else if (button == 0 && inputMode == InputMode.BuildMode) {
			BuildManager._instance.leftTouched(screenX, screenY);
		}
			
		if (button == 1 && inputMode == InputMode.PlayMode) {
			rightMouseIsDown = true;
			setRightMouseLocation(screenX, screenY);
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 1) {
			rightMouseIsDown = false;
			setRightMouseLocation(screenX, screenY);
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (rightMouseIsDown)
			setRightMouseLocation(screenX, screenY);

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	void setRightMouseLocation(int screenX, int screenY) {
		rightMouseLocation = new Vector2(screenX, screenY);
		Vector3 pos = new Vector3(rightMouseLocation.x, rightMouseLocation.y, 0);
		GameScreen._instance.camera.unproject(pos);
		rightMouseLocation = new Vector2(pos.x, pos.y);
	}

	
}
