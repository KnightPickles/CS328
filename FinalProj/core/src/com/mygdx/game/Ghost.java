package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    int hasGold = 0;
    int maxGold = 0;
    float moveSpeed = 10f;
    
    Vector2 spawn;
    Vector2 pos;
    Vector2 deltaPos;
    List<Vector2> path;
    int pathPos = 0;

    Sprite os;

    Ghost(Size size, Color color) {
        this.size = size;
        this.color = color;
        Random r = new Random(System.currentTimeMillis());

        switch(color) {
            default:
            case RED: 
            	sprite = MainGameClass._instance.atlas.createSprite("redghost5");
            	moveSpeed = 15f;
                maxGold = 30;
            	break;
            case BLUE: 
            	sprite = MainGameClass._instance.atlas.createSprite("blueghost5"); 
            	moveSpeed = 80f;
                maxGold = 25;
            	break;
            case GREEN: 
            	sprite = MainGameClass._instance.atlas.createSprite("greenghost5"); 
            	moveSpeed = 25f;
                maxGold = 20;
            	break;
            case PURPLE: 
            	sprite = MainGameClass._instance.atlas.createSprite("purpleghost5"); 
            	moveSpeed = 30f;
                maxGold = 15;
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

        pos = deltaPos = spawn;
        path = Map._instance.pathToGoal(spawn);

        os = MainGameClass._instance.atlas.createSprite("blue_indicator");
    }

    @Override
    public void update() {
    	ghostMoveUpdate();
        
        super.update();
    }

    @Override
    public void draw() {
        MainGameClass._instance.batch.begin();
        for(Vector2 v : path) {
            os.setPosition(v.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, v.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
            os.draw(MainGameClass._instance.batch);
        }
        MainGameClass._instance.batch.end();
        super.draw();
    }
    
    void ghostMoveUpdate() {
    	if (!validPath()) {
    		killUnit();
    		return;
    	}

        // Ghost does not line up correctly all the time
        Vector2 dest = new Vector2(path.get(pathPos + 1)); //Goal is the next path position
        Vector2 dir = new Vector2(dest);
        dir.sub(pos); //Get vector between this and goal
        dir = new Vector2(dir.x * moveSpeed, dir.y * moveSpeed); //Take move speed into account
        deltaPos = new Vector2((int)((sprite.getX() + GameScreen._instance.camera.viewportWidth / 2) / MainGameClass.PPM), (int)((sprite.getY() + GameScreen._instance.camera.viewportHeight / 2) / MainGameClass.PPM));
        if(deltaPos.equals(dest)) {
            pos = new Vector2(deltaPos);
            if(hasGold > 0 && pos.equals(spawn)) {
                killUnit();
                return;
            }
            if(pathPos == path.size() - 2) {
                Collections.reverse(path);
                hasGold = maxGold;
                pathPos = 0;
            } else pathPos++;
        }

        body.setLinearVelocity(dir); //Move
        //System.out.println(dir);
        //body.setTransform(position.x * Gdx.graphics.getDeltaTime(), position.y + dir.y * Gdx.graphics.getDeltaTime(), 0);

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
