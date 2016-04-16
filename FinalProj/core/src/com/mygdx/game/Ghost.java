package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
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
    Vector2 spawn;
    Vector2 pos;
    ArrayList<Vector2> path;

    Ghost(Size size, Color color) {
        this.size = size;
        this.color = color;
        Random r = new Random(System.currentTimeMillis());

        switch(color) {
            default:
            case RED: sprite = MainGameClass._instance.atlas.createSprite("redghost5"); break;
            case BLUE: sprite = MainGameClass._instance.atlas.createSprite("blueghost5"); break;
            case GREEN: sprite = MainGameClass._instance.atlas.createSprite("greenghost5"); break;
            case PURPLE: sprite = MainGameClass._instance.atlas.createSprite("purpleghost5"); break;
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

        pos = spawn;
        path = Map._instance.pathToGoal(pos);
        // translating map coords to game coords
        sprite.setPosition(spawn.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, spawn.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);

        setBody(false, true, 0, 0);
    }

    public void update() {
        if(body.getPosition().equals())
        body.setLinearVelocity()
    }
}
