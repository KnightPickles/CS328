package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * Created by KnightPickles on 4/15/16.
 */
public class Ghost extends GameObject {

    public enum Color {
        RED, BLUE, GREEN, PURPLE
    }

    public enum Size {
        X11, X22, X33;
    }

    Size size;
    Color color;
    int hasGold = 0;
    int maxGold = 0;
    int goldValue = 0;
    float moveSpeed = 10f;
    
    Sprite goldBag;
    
    Vector2 spawn;
    Vector2 pos;
    Vector2 deltaPos;
    List<Vector2> path;
    int pathPos = 0;

    Random r = new Random(System.currentTimeMillis());

    Ghost(Size size, Color color) {
        this.size = size;
        this.color = color;

        switch(color) {
            default:
            case RED: 
            	sprite = MainGameClass._instance.atlas.createSprite("redghost5");
            	moveSpeed = 15f;
            	maxHealth = 50;
                maxGold = 30;
                goldValue = 5;
            	break;
            case BLUE: 
            	sprite = MainGameClass._instance.atlas.createSprite("blueghost5"); 
            	moveSpeed = 20f;
            	maxHealth = 10;
                maxGold = 25;
                goldValue = 10;
            	break;
            case GREEN: 
            	sprite = MainGameClass._instance.atlas.createSprite("greenghost5"); 
            	moveSpeed = 25f;
            	maxHealth = 75;
                maxGold = 20;
                goldValue = 15;
            	break;
            case PURPLE: 
            	sprite = MainGameClass._instance.atlas.createSprite("purpleghost5"); 
            	moveSpeed = 30f;
            	maxHealth = 65;
                maxGold = 15;
                goldValue = 20;
            	break;            	
        }

        setSize(size);

        goldBag = MainGameClass._instance.atlas.createSprite("goldBag");
        if (goldBag != null)
        	goldBag.setPosition(spawn.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, spawn.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
        goldValue *= size.ordinal() + 1;
        health *= (size.ordinal() + 1);

        // translating map coords to game coords
        sprite.setPosition(spawn.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, spawn.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
        setBody(false, true, 0, 0);

        pos = deltaPos = spawn;
        health = maxHealth;
    }

    void setSize(Size size) {
        switch(size) {
            default:
            case X11:
                spawn = Map._instance.spawnCoords.get(r.nextInt(Map._instance.spawnCoords.size()));
                path = Map._instance.pathToGoal(spawn);
                break;
            case X22:
                if(Map._instance.spawnCoords2x2.size() <= 0) {
                    setSize(Size.X11);
                    return;
                }
                sprite.scale(1);
                spawn = Map._instance.spawnCoords2x2.get(r.nextInt(Map._instance.spawnCoords2x2.size()));
                if(path == null) {
                    setSize(Size.X11);
                    return;
                }
                break;
            case X33:
                if(Map._instance.spawnCoords3x3.size() <= 0) {
                    setSize(Size.X22);
                    return;
                }
                sprite.scale(2);
                spawn = Map._instance.spawnCoords3x3.get(r.nextInt(Map._instance.spawnCoords3x3.size()));
                path = Map._instance.pathToGoal3x3(spawn);
                if(path == null) {
                    setSize(Size.X22);
                    return;
                }
                break;
        }
    }

    @Override
    public void update() {
    	ghostMoveUpdate();
    	
    	if (flashing) {
    		currFlashTime -= Gdx.graphics.getDeltaTime();
    		if (currFlashTime <= 0) {
    			sprite.setColor(c);
    			flashing = false;
    		}
    	}
        
        super.update();
        if (goldBag != null && hasGold > 0)
        	goldBag.setPosition((position.x) - sprite.getWidth()/2 , (position.y) - sprite.getHeight()/2);
    }

    @Override
    public void draw() {
        /*MainGameClass._instance.batch.begin();
        for(Vector2 v : path) {
            os.setPosition(v.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, v.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);
            os.draw(MainGameClass._instance.batch);
        }
        MainGameClass._instance.batch.end();*/
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
        deltaPos = new Vector2((int)((sprite.getX() + sprite.getWidth() / 2 + GameScreen._instance.camera.viewportWidth / 2) / MainGameClass.PPM), (int)((sprite.getY() + sprite.getWidth() / 2 + GameScreen._instance.camera.viewportHeight / 2) / MainGameClass.PPM));
        if(deltaPos.equals(dest)) {
            pos = new Vector2(deltaPos);
            if(hasGold > 0 && pos.equals(spawn)) {
                Map._instance.takeGold(hasGold);
                super.killUnit();
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
    
    com.badlogic.gdx.graphics.Color c;
    boolean flashing;
    float flashTime = .18f;
    float currFlashTime = 0f;
    void flashGhost() {
    	if (flashing)
    		return;

    	c = sprite.getColor();
    	com.badlogic.gdx.graphics.Color flashColor = new com.badlogic.gdx.graphics.Color(1, 1, 1, .5f);
    	sprite.setColor(flashColor);
    	flashing = true;
    	currFlashTime = flashTime;
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

    @Override
    protected void receiveDamage(int amount) {
    	flashGhost();
    	health -= amount;
    	if (health <= 0)
    		killUnit();
    }
    
    @Override
    public void killUnit() {
        Player.gold += goldValue;
        super.killUnit();
    }
}
