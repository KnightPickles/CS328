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
    Vector2 deltaPos;
    ArrayList<Vector2> path;
    int pathPos = 0;

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

        pos = deltaPos = spawn;
        path = Map._instance.pathToGoal(pos);
        // translating map coords to game coords
        sprite.setPosition(spawn.x * MainGameClass.PPM - GameScreen._instance.camera.viewportWidth / 2, spawn.y * MainGameClass.PPM - GameScreen._instance.camera.viewportHeight / 2);

        setBody(false, true, 0, 0);
    }

    public void update() {
        super.update();

        if(path != null || path.size() > 1) {
            Vector2 lastPos = new Vector2(pos);
            Vector2 nextPos = new Vector2(path.get(pathPos + 1));
            nextPos.sub(pos);
            nextPos.x *= 100;
            nextPos.y *= 100;
            body.setLinearVelocity(nextPos);
            Vector2 d = new Vector2((int)(sprite.getX() / MainGameClass.PPM + GameScreen._instance.camera.viewportWidth / 2), (int)(sprite.getY() / MainGameClass.PPM + GameScreen._instance.camera.viewportWidth / 2));
            System.out.println(pos + " " + nextPos + " " + d);

            //pos.x = (int)(sprite.getX() / MainGameClass.PPM + GameScreen._instance.camera.viewportWidth / 2);
            //pos.y = (int)(sprite.getY() / MainGameClass.PPM + GameScreen._instance.camera.viewportWidth / 2);
            /*if(!pos.equals(lastPos)) {
                pathPos++;
            }*/

            /*if((int)(sprite.getX() / MainGameClass.PPM + GameScreen._instance.camera.viewportWidth / 2) == path.get(pathPos + 1).x
                && (int)(sprite.getY() / MainGameClass.PPM + GameScreen._instance.camera.viewportWidth / 2) == path.get(pathPos + 1).y) {

            }*/

            /*deltaPos.add(new Vector2(nextPos.x * MainGameClass.PPM * deltaTime * 2, nextPos.y * MainGameClass.PPM * deltaTime * 2));
            unit.sprite.translate(nextPos.x * MainGameClass.PPM * deltaTime * 2 * 8, nextPos.y * MainGameClass.PPM * deltaTime * 2 * 8);
            //unit.sprite.translate(nextPos.x * MainGameClass.PPM, nextPos.y * MainGameClass.PPM);


            ghost.pos = new Vector2((int) ghost.deltaPos.x, (int) ghost.deltaPos.y);
            //ghost.pos = new Vector2(ghost.path.get(1));*/
        }
    }
}
