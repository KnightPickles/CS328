package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.sun.glass.ui.Window;

public class BuildManager {

	public static BuildManager _instance;

	public boolean inBuildMode = false;
	public boolean[][] occupiedTiles;
	public int width, height;

	Sprite occupied, free;

	int level = 0;
	
	public BuildManager() {
		if (_instance != null) System.out.println("Trying to create multiple BuildManagers");
		_instance = this;
		occupied = MainGameClass._instance.atlas.createSprite("red_indicator");
		occupied.setAlpha(0.2f);
		free = MainGameClass._instance.atlas.createSprite("blue_indicator");
		free.setAlpha(0.2f);
		level = LevelManager._instance.level;
		createBuildingGrid();
	}
	
	public void update(float delta) {
		if(level != LevelManager._instance.level) {
			createBuildingGrid();
			level = LevelManager._instance.level;
		}
		//Render a building blueprint and map grid so we can see the spaces that we are building on and whether or not theyre occupied

		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			toggleBuildMode();

		MainGameClass._instance.batch.begin();
		if(inBuildMode) {
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					if(occupiedTiles[x][y]) {
						occupied.setPosition(x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
						occupied.draw(MainGameClass._instance.batch);
					} else {
						free.setPosition(x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
						free.draw(MainGameClass._instance.batch);
					}
				}
			}
		}
		MainGameClass._instance.batch.end();
	}
	
	//Enable showing the build template
	public void enableBuildMode() {
		inBuildMode = true;
		GameScreen._instance.inputProcessor.inputMode = MyInputProcessor.InputMode.BuildMode;
	}
	
	//Disable showing the build template
	public void disableBuildMode() {
		inBuildMode = false;
		GameScreen._instance.inputProcessor.inputMode = MyInputProcessor.InputMode.PlayMode;
	}

	public void toggleBuildMode() {
		inBuildMode = !inBuildMode;
		if(inBuildMode) GameScreen._instance.inputProcessor.inputMode = MyInputProcessor.InputMode.BuildMode;
		else GameScreen._instance.inputProcessor.inputMode = MyInputProcessor.InputMode.PlayMode;
	}
	
	void createBuildingGrid() {
		//Create a vector2 of "grid pieces" or use MapTiles (havent looked at how theyre setup yet)
		//Mark map tiles or grid pieces as buildable, and when built on, mark them as occupied
		width = Map._instance.worldWidth;
		height = Map._instance.worldHeight;
		occupiedTiles = new boolean[width][height];
		for(int i = 0; i < width * height; i++)
			occupiedTiles[i / width][i % height] = false;
		for(Vector2 v : Map._instance.traversableCoords)
			occupiedTiles[(int)v.x][(int)v.y] = true;
	}
	
	public void leftTouched(int screenX, int screenY) {
		//Check if we can build at (x,y) there should be no existing buildings, not be on a path, not be where the player is standing, etc
		//Check if we have the money
		//Build
		if(inBuildMode) {
			//System.out.println(GameScreen._instance.camera.viewportWidth * MainGameClass.SCALE + " " + GameScreen._instance.camera.viewportHeight * MainGameClass.SCALE);

			float yScreen = (GameScreen._instance.camera.viewportHeight * MainGameClass.SCALE - screenY) / MainGameClass.SCALE / MainGameClass.PPM;
			float xScreen = screenX / MainGameClass.SCALE / MainGameClass.PPM;

			// Mother of god these coordinate translations
			//System.out.println(xScreen + " " + yScreen + " " + (int)xScreen + " " + (int)yScreen);

			if(!occupiedTiles[(int)xScreen][(int)yScreen]) {
				float xPos = (int)xScreen * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2;
				float yPos = (int)yScreen * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2;
				if(EntityManager._instance.buildTurret("blue0", new Vector2((int)xPos, (int)yPos)))
					occupiedTiles[(int) xScreen][(int) yScreen] = true;
			} else System.out.println("Cannot build here. This space is occupied.");
		}
	}	
}
