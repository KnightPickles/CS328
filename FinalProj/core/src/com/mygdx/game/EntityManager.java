package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class EntityManager {

	public static EntityManager _instance;

	public List<GameObject> ghosts = new ArrayList<GameObject>();
	public List<GameObject> turrets = new ArrayList<GameObject>();
	public Player player;


    EntityManager() {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
        
        player = new Player();
        
        Ghost ghost = new Ghost(Ghost.Size.X11, Ghost.Color.BLUE);
        ghosts.add(ghost);
    }
	
    public void update(float delta) {
    	//Update ghosts
    	for (GameObject g : ghosts) {
    		((Ghost)g).update();
    	}
    	
    	for (GameObject g : turrets) {
    		((Turret)g).update();
    	}
    	
    	player.update();
    	
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
    
    public void removeEntity(GameObject e) {
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
}
