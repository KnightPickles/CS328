package edu.cs328;

import java.util.ArrayList;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SelectionManager {

	public static SelectionManager _instance;
	OrthographicCamera camera;
	
	public Entity singleSelected; //If we are selecting one Entity, this is that Entity
	public ArrayList<Entity> tempSelected = new ArrayList<Entity>();
	public ArrayList<Entity> selected = new ArrayList<Entity>();
	
	//Drag select stuff
	Vector2 startDrag = new Vector2(0, 0);
	boolean dragging = false;
	
	public SelectionManager() {
		if (_instance != null) System.out.println("Creating multiple selection managers");
		_instance = this;			
		camera = HW4._instance.camera;
	}
	
	void SelectEntity(Entity e) { 
		//Deselection
		//Deselect single unit
		if (singleSelected != null)  {
			EntityManager._instance.sc.get(singleSelected).selected = false;
			singleSelected = null;
		}
		
		//Deselect multiple units
		for (Entity entity : selected) {
			EntityManager._instance.sc.get(entity).selected = false;
		}
		selected.clear();
		
		//Select new object
		singleSelected = e;
		EntityManager._instance.sc.get(e).selected = true;
	}
	
	void SelectEntities() {
		//Deselection
		//Deselect single unit
		if (singleSelected != null) {
			EntityManager._instance.sc.get(singleSelected).selected = false;
			singleSelected = null;
		}
		
		//Deselect multiple units
		for (Entity e : selected) {
			EntityManager._instance.sc.get(e).selected = false;
		}
		selected.clear();
		
		//Select new units
		for (Entity e : tempSelected) {
			EntityManager._instance.sc.get(e).selected = true;
			selected.add(e);
		}
	}
	
	public void dragSelect(float xPos, float yPos) {
		if (!dragging) { //Initiate drag if we started at vector zero
			tempSelected.clear();
			Vector3 v = new Vector3(xPos, yPos, 0);
			camera.unproject(v);
			startDrag = new Vector2(v.x, v.y);
			dragging = true;
		}
		
		ImmutableArray<Entity> selectables = EntityManager._instance.GetListSelectables();
		Vector3 input = new Vector3(xPos, yPos, 0);
		camera.unproject(input);

		for (Entity e : selectables) {
			if (tempSelected.contains(e))
				continue;
			
			if (!EntityManager._instance.sc.get(e).selectable)
				continue;
			
			Box2dComponent b = EntityManager._instance.b2dc.get(e);
			
			float minX = Math.min(startDrag.x, input.x);
			float maxX = Math.max(startDrag.x, input.x);
			float minY = Math.min(startDrag.y, input.y);
			float maxY = Math.max(startDrag.y, input.y);
			
			Vector2 spritePos = new Vector2(b.sprite.getX(), b.sprite.getY());
			
			if (spritePos.x > minX && spritePos.x < maxX
					&& spritePos.y > minY && spritePos.y < maxY)
				tempSelected.add(e);
			else if (tempSelected.contains(e)) {
				tempSelected.remove(e);
			}
		}
		
		HW4._instance.renderSelectionBox(startDrag, new Vector2(input.x, input.y));
	}
	
	public void endDragSelect() {
		//Select whatever is in selected
		SelectEntities();
		
		HW4._instance.stopRenderSelectionBox();
		tempSelected.clear();
		startDrag = new Vector2(0, 0);
		dragging = false;
	}
	
	//Left click select
	public void leftTouched(int xPos, int yPos) {
		ImmutableArray<Entity> selectables = EntityManager._instance.GetListSelectables();
		Vector3 input = new Vector3(xPos, yPos, 0);
		camera.unproject(input);
		
		for (Entity e : selectables) {
			if (e == singleSelected)
				continue;
			
			if (!EntityManager._instance.sc.get(e).selectable)
				continue;
			
			Box2dComponent b = EntityManager._instance.b2dc.get(e);
			if (b.sprite.getBoundingRectangle().contains(input.x, input.y)) {
				SelectEntity(e);
				return;
			}
		}
	}
	
	public void rightTouched(int xPos, int yPos) {
		if (singleSelected != null) { 		
			Vector3 pos = new Vector3(xPos, yPos, 0);
			camera.unproject(pos);
			EntityManager._instance.b2dc.get(singleSelected).moveCommand(new Vector2(pos.x, pos.y));
			return;
		}
		
		if (selected.size() > 0) {
			for (Entity e : selected) {
				Vector3 pos = new Vector3(xPos, yPos, 0);
				camera.unproject(pos);
				EntityManager._instance.b2dc.get(e).moveCommand(new Vector2(pos.x, pos.y));
			}
			return;
		}
	}
}
