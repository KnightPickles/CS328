package edu.cs328;

import java.util.ArrayList;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SelectionManager {

	private HW4 game;
	public static SelectionManager _instance;
	OrthographicCamera camera;
	
	public Entity singleSelected; //If we are selecting one Entity, this is that Entity
	public ArrayList<Entity> tempSelected = new ArrayList<Entity>();
	public ArrayList<Entity> selected = new ArrayList<Entity>();
	public ArrayList<Vector2> targets = new ArrayList<Vector2>();

	Sprite targetIndicator;
	
	//Drag select stuff
	Vector2 startDrag = new Vector2(0, 0);
	boolean dragging = false;
	
	public SelectionManager(HW4 game) {
		this.game = game;
		if (_instance != null) System.out.println("Creating multiple selection managers");
		_instance = this;			
		camera = GameScreen._instance.camera;
		targetIndicator = game.atlas.createSprite("blue_indicator");
	}

	public static void drawDashedLine(ShapeRenderer renderer, Vector2 dashes, Vector2 start, Vector2 end, float width) {
		if (dashes.x == 0) {
			return ;
		}
		float dirX = end.x - start.x;
		float dirY = end.y - start.y;

		float length = Vector2.len(dirX, dirY);
		dirX /= length;
		dirY /= length;

		float curLen = 0;
		float curX = 0;
		float curY = 0;

		renderer.begin(ShapeRenderer.ShapeType.Filled);
		renderer.setColor(0, 0, 0, 1);

		while (curLen <= length) {
			curX = (start.x+dirX*curLen);
			curY = (start.y+dirY*curLen);
			renderer.rectLine(curX,curY , curX+dirX*dashes.x, curY+dirY*dashes.x, width);
			curLen += (dashes.x + dashes.y);

		}
		renderer.end();
	}

	public void render(ShapeRenderer sh, Batch batch) {
		for(Entity e : selected) {
			GhostComponent gc = e.getComponent(GhostComponent.class);
			if(!targets.contains(gc.desiredMovePosition) && gc.desiredMovePosition != null){
				targets.add(gc.desiredMovePosition);
			}
		}
		for(Entity e : selected) {
			GhostComponent gc = e.getComponent(GhostComponent.class);
			if(gc.position != null && gc.desiredMovePosition != null) {
				drawDashedLine(sh, new Vector2(2, 1), gc.position, gc.desiredMovePosition, 1);
			}
		}
		batch.begin();
		for(Vector2 vec : targets) {
			targetIndicator.setPosition(vec.x - targetIndicator.getWidth() / 2, vec.y - targetIndicator.getHeight() / 2);
			targetIndicator.draw(batch);
		}
		batch.end();
		targets.clear();
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
			if (!EntityManager._instance.GetListBuildings().contains(e, true)) {
				EntityManager._instance.sc.get(e).selected = true;
				selected.add(e);
			}
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
			
			if (!EntityManager._instance.sc.get(e).selectable || !EntityManager._instance.sc.get(e).friendly)
				continue;
			
			Box2dComponent b = EntityManager._instance.boxc.get(e);
			
			float minX = Math.min(startDrag.x, input.x);
			float maxX = Math.max(startDrag.x, input.x);
			float minY = Math.min(startDrag.y, input.y);
			float maxY = Math.max(startDrag.y, input.y);
			
			Rectangle r = new Rectangle();
			r.width = (maxX - minX);
			r.height = (maxY - minY);
			r.setCenter(new Vector2((startDrag.x + input.x)/2, (startDrag.y + input.y)/2));
			
			Vector2 spritePos = new Vector2(b.sprite.getX(), b.sprite.getY());
			
			float spriteWidth = EntityManager._instance.boxc.get(e).sprite.getWidth()/2;
			float spriteHeight = EntityManager._instance.boxc.get(e).sprite.getHeight()/2;
			
			if (b.sprite.getBoundingRectangle().overlaps(r))
				tempSelected.add(e);
			else if (tempSelected.contains(e)) {
				tempSelected.remove(e);
			}
		}
		
		GameScreen._instance.renderSelectionBox(startDrag, new Vector2(input.x, input.y));
	}
	
	public void endDragSelect() {
		//Select whatever is in selected
		SelectEntities();

		GameScreen._instance.stopRenderSelectionBox();
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
			
			if (!EntityManager._instance.sc.get(e).selectable || !EntityManager._instance.sc.get(e).friendly)
				continue;
			
			Box2dComponent b = EntityManager._instance.boxc.get(e);
			if (b.sprite.getBoundingRectangle().contains(input.x, input.y)) {
				SelectEntity(e);
				return;
			}
		}
	}
	
	public void rightTouched(int xPos, int yPos) {
		Entity tar = getObjectUnderCursor();
		
		if (singleSelected != null) { 		
			Vector3 pos = new Vector3(xPos, yPos, 0);
			camera.unproject(pos);
			if (EntityManager._instance.GetListBuildings().contains(singleSelected, true)) 
				EntityManager._instance.bc.get(singleSelected).rightClickCommand(new Vector2(pos.x, pos.y), tar);
			else
				EntityManager._instance.gc.get(singleSelected).rightClickCommand(new Vector2(pos.x, pos.y), tar);
			return;
		}
		
		if (selected.size() > 0) {
			for (Entity e : selected) {
				Vector3 pos = new Vector3(xPos, yPos, 0);
				camera.unproject(pos);
				EntityManager._instance.gc.get(e).rightClickCommand(new Vector2(pos.x, pos.y), tar);
			}
			return;
		}
	}
	
	public static Entity getObjectUnderCursor() {
		float xPos = Gdx.input.getX();
		float yPos = Gdx.input.getY();
		Vector3 input = new Vector3(xPos, yPos, 0);
		_instance.camera.unproject(input);
		
		ImmutableArray<Entity> selectables = EntityManager._instance.GetListSelectables();
		for (Entity e : selectables) {			
			Box2dComponent b = EntityManager._instance.boxc.get(e);
			if (b.sprite.getBoundingRectangle().contains(input.x, input.y)) {
				return e;
			}
		}
		
		return null;
	}
}
