package edu.cs328;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 1/20/16.
 */
public class GameObjectManager {
    private ArrayList<SimpleGameObject> simpleGOList;
    SimpleGameObject simpleGO;

    public static float xBound, yBound;

    public GameObjectManager(float x, float y) {
        xBound = x;
        yBound = y;
        simpleGOList = new ArrayList<SimpleGameObject>();
    }

    public void update() {
        for(SimpleGameObject SGO : simpleGOList) SGO.update();
    }

    public void render(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(SimpleGameObject SGO : simpleGOList) SGO.render(sr);
        sr.end();
    }

    public void render(Batch b) {
        for(SimpleGameObject SGO : simpleGOList) SGO.render(b);
    }

    // store object data used for interpolation
    public void savePos() {
        for(SimpleGameObject SGO : simpleGOList) {
            if(SGO.sprite != null) {
                SGO.posPrev.x = SGO.sprite.getX();
                SGO.posPrev.y = SGO.sprite.getY();
                SGO.anglePrev = SGO.sprite.getRotation();
            }
        }
    }

    // interpolate formula: current-state * alpha + previous-state * (1.0 - alpha)
    public void interpolate(float alpha) {
        for(SimpleGameObject SGO : simpleGOList) {
            if(SGO.sprite != null) {
                SGO.pos.x = SGO.sprite.getX() * alpha + SGO.posPrev.x * (1.0f - alpha);
                SGO.pos.y = SGO.sprite.getY() * alpha + SGO.posPrev.y * (1.0f - alpha);
                SGO.angle = SGO.sprite.getRotation() * alpha + SGO.anglePrev * (1.0f - alpha);
            }
        }
    }


    public ArrayList<SimpleGameObject> getSimpleGameObjectList() { return simpleGOList; }


    public void addSimpleGameObject(SimpleGameObject SGO) { simpleGOList.add(SGO); }


    public void removeSimpleGameObject(SimpleGameObject SGO) { simpleGOList.remove(SGO); }

    public void clearList() {
        simpleGOList.clear();
    }
}
