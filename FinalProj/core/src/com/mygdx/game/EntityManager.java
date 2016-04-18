package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class EntityManager {

	public static EntityManager _instance;

	public List<GameObject> ghosts = new ArrayList<GameObject>();
	public List<GameObject> turrets = new ArrayList<GameObject>();
	public Player player;

	public List<GameObject> objectsToDelete = new ArrayList<GameObject>(); //Objects queued for removal

    EntityManager() {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
        
        player = new Player(new Vector2(50, 50));
        
        //Ghost ghost = new Ghost(Ghost.Size.X11, Ghost.Color.BLUE);
        //ghosts.add(ghost);
    }
	
    public void update(float delta) {
    	//Update ghosts
    	for (GameObject g : ghosts) {
    		((Ghost)g).update();
    	}
    	
    	for (GameObject g : turrets) {
    		((Turret)g).update();
    	}
    	
    	if (player != null)
    		player.update();

    	removeQueuedEntities(); 
    	draw();
    }
    
    void draw() {    	
    	//Draw ghosts
    	for (GameObject g : ghosts) {
    		((Ghost)g).draw();
    	}
    	
    	for (GameObject g : turrets) {
    		((Turret)g).draw();
    	}
    	
    	if (player != null)
    		player.draw();
    }
    
    public List<GameObject> getSelectables() {
    	List<GameObject> selectables = new ArrayList<GameObject>();
    	
    	//Not selectable until we decide or have a reason for them to be selectable
    	/*for (GameObject g : ghosts) {
    		selectables.add(g);
    	}*/
    	
    	for (GameObject g : turrets) {
    		selectables.add(g);
    	}
    	
    	return selectables;
    }
    
    void removeQueuedEntities() {
    	for (GameObject e : objectsToDelete) {
	    	if (e instanceof Ghost) {
	    		if (ghosts.contains(e)) {
	    			ghosts.remove(e);
	    		}
	    	}
	    	if (e instanceof Turret) {
	    		if (turrets.contains(e)) {
	    			turrets.remove(e);
	    		}
	    	}
    	}
    	objectsToDelete.clear();
    }
    
    public void removeEntity(GameObject e) {
    	objectsToDelete.add(e);
    }
}
