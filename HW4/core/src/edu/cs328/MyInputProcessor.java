package edu.cs328;

import com.badlogic.gdx.InputProcessor;

public class MyInputProcessor implements InputProcessor {

	boolean rightDown;
	boolean leftDown, leftDrag;
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == 0) {
			leftDown = true;
			return true;
		}
		
		if (button == 1) {
			rightDown = true;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 1 && rightDown) {
			rightDown = false;
			SelectionManager._instance.rightTouched(screenX, screenY);
			return true;
		} 
		
		if (button == 0 && !leftDrag && leftDown) {
			leftDown = false;
			SelectionManager._instance.leftTouched(screenX, screenY);
			return true;
		}
		
		if (button == 0 && leftDrag) {
			leftDrag = false;
			leftDown = false;
			SelectionManager._instance.endDragSelect();
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (leftDown) {
			leftDrag = true;
			SelectionManager._instance.dragSelect(screenX, screenY);
			return true;
		}
		
		if (rightDown) {
			SelectionManager._instance.rightTouched(screenX, screenY);
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
