package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by KnightPickles on 4/15/16.
 */
public class Ghost extends GameObject {

    public enum Color {
        RED, BLUE, GREEN, PURPLE
    }

    public enum Size {
        X11, X22, X33
    }

    Size size;
    Color color;
    float moveSpeed = 10f;
    
    Vector2 spawn;
    Vector2 pos;
    Vector2 deltaPos;
    List<Vector2> path;
    int pathPos = 0;

    Ghost(Size size, Color color) {
        this.size = size;
        this.color = color;
        Random r = new Random(System.currentTimeMillis());

        switch(color) {
            default:
            case RED: 
            	sprite = MainGameClass._instance.atlas.createSprite("redghost5");
            	moveSpeed = 15f;
            	break;
            case BLUE: 
            	sprite = MainGameClass._instance.atlas.createSprite("blueghost5"); 
            	moveSpeed = 20f;
            	break;
            case GREEN: 
            	sprite = MainGameClass._instance.atlas.createSprite("greenghost5"); 
            	moveSpeed = 25f;
            	break;
            case PURPLE: 
            	sprite = MainGameClass._instance.atlas.createSprite("purpleghost5"); 
            	moveSpeed = 30f;
            	break;
        }
        switch(size) {
            default:
            case X11:
                spawn = Map._instance.spawnCoords.get(r.nextInt(Map._instance.spawnCoords.size()));
                break;
            case X22:
                sprite.scale(2);
                spawn = Map._instance.spawnCoords2x2.get(r.nextInt(Map._instance.spawnCoords2x2.size()));
                break;
            case X33:
                sprite.scale(3);
                spawn = Map._instance.spawnCoords3x3.get(r.nextInt(Map._instance.spawnCoords3x3.size()));
                break;
        }
        
        // translating map coords to game coords
        sprite.setPosition(spawn.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, spawn.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
        setBody(false, true, 0, 0);
        
        path = Map._instance.pathToGoal(spawn);
    }

    @Override
    public void update() {
    	ghostMoveUpdate();
        
        super.update();
    }
    
    void ghostMoveUpdate() {
    	if (!validPath()) {
    		killUnit();
    		return;
    	}
    	
    	float deltaTime = Gdx.graphics.getDeltaTime();
        Vector2 dest = path.get(pathPos + 1); //Goal is the next path position
        Vector2 dir = dest.sub(position); //Get vector between this and goal
        dir.nor();
        dir = new Vector2(dir.x * (deltaTime * moveSpeed), dir.y * (deltaTime *moveSpeed)); //Take move speed into account
        Vector2 nextPos = new Vector2(position.x + dir.x, position.y + dir.y); //Add that dir vector onto our curr position
        body.setTransform(nextPos, 0); //Move
        
        if (Vector2.dst(position.x, position.y, dest.x, dest.y) < 1f) { //Check if we've reached next node
        	pathPos++;
        	if (pathPos >= path.size()-1) { //Reached gold/end of path
        		if (Vector2.dst(position.x, position.y, spawn.x, spawn.y) < 1f) { //Not sure if spawn point lines up with first path node so this will need to be checked/fixed
        			killUnit(); //Kill unit but dont give player money for it, and take away however much gold was taken from the middle from the player
        			return;
        		}
        		pathPos = 0;
        		Collections.reverse(path);
        	}
        }
    }
    
    boolean validPath() { 
    	if (path == null) {
    		System.out.println("Path invalid, null");
    		return false;
    	}
    	if (path.size() <= 1) { 
    		System.out.println("Path invalid, Size: " + path.size());
    		return false;
    	}
    	return true;
    }
    
    void killUnit() {
    	if (body != null && body.getFixtureList().size >= 1)
    		body.destroyFixture(body.getFixtureList().first());
    	EntityManager._instance.removeEntity(this);
    }
}
