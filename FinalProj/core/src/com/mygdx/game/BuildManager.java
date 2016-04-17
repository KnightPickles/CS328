package com.mygdx.game;

public class BuildManager {

	public static BuildManager _instance;
	
	public BuildManager() {
		if (_instance != null) System.out.println("Trying to create multiple BuildManagers");
		_instance = this;
		
		createBuildingGrid();
	}
	
	public void update(float delta) {
		//Render a building blueprint and map grid so we can see the spaces that we are building on and whether or not theyre occupied
	}
	
	//Enable showing the build template
	public void enableBuildMode() {
		
	}
	
	//Disable showing the build template
	public void disableBuildMode() {
		
	}
	
	void createBuildingGrid() {
		//Create a vector2 of "grid pieces" or use MapTiles (havent looked at how theyre setup yet)
		//Mark map tiles or grid pieces as buildable, and when built on, mark them as occupied
	}
	
	public void leftTouched(int screenX, int screenY) {
		//Check if we can build at (x,y) there should be no existing buildings, not be on a path, not be where the player is standing, etc
		//Check if we have the money
		//Build
	}	
}
