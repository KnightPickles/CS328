package com.mygdx.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class MyInputProcessor implements InputProcessor {

	public static boolean wIsDown;
	public static boolean aIsDown;
	public static boolean sIsDown;
	public static boolean dIsDown;
	
	public static boolean rightMouseIsDown;
	
	public static InputMode inputMode = InputMode.PlayMode;
	public enum InputMode {
		PlayMode,	//Regular playing/movement/etc
		BuildMode	//Trying to build new turrets
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
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 1) {
			rightMouseIsDown = false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
