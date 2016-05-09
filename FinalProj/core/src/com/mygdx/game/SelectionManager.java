package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class SelectionManager {

	public static SelectionManager _instance;
	
	public GameObject selected; //Only going to have single selection in this game for upgrading turrets or possibly viewing enemy unit stats	
	public Camera camera;
	
	public SelectionManager() {
		if (_instance != null) System.out.println("Creating multiple selection managers");
		_instance = this;			
		
		camera = GameScreen._instance.camera;
	}
	
	void selectObject(GameObject obj) {
		selected = obj;
	}
	
	public void leftTouched(int xPos, int yPos) {
		GameObject obj = getObjectUnderCursor();

		if (obj != null && obj != selected) {
			System.out.println(((Turret)obj).myInfo.redLevel);
			selectObject(obj);
		}
	}
	
	public static GameObject getObjectUnderCursor() {
		float xPos = Gdx.input.getX();
		float yPos = Gdx.input.getY();
		Vector3 input = new Vector3(xPos, yPos, 0);
		_instance.camera.unproject(input);
		
		for (GameObject e : EntityManager._instance.getSelectables()) {
			if (e.sprite.getBoundingRectangle().contains(input.x, input.y)) {
				return e;
			}
		}
		
		return null;
	}
}
