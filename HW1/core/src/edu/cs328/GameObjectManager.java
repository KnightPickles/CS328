package edu.cs328;

import com.badlogic.gdx.physics.box2d.BodyDef;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 1/20/16.
 */
public class GameObjectManager {
    private ArrayList<DynamicGameObject> dynamicGOList;
    DynamicGameObject dynamicGO;
    private ArrayList<SimpleGameObject> simpleGOList;
    SimpleGameObject simpleGO;

    public static float xBound, yBound;

    public GameObjectManager(float x, float y) {
        xBound = x;
        yBound = y;
        dynamicGOList = new ArrayList<DynamicGameObject>();
        simpleGOList = new ArrayList<SimpleGameObject>();
    }

    public void update() {
        for(SimpleGameObject SGO : simpleGOList) {
            SGO.update();
        }
    }

    // store object data used for interpolation
    public void savePos() {
        for(SimpleGameObject SGO : simpleGOList) {
            SGO.posPrev.x = SGO.sprite.getX();
            SGO.posPrev.y = SGO.sprite.getY();
            SGO.anglePrev = SGO.sprite.getRotation();
        }

        /*
        for(DynamicGameObject DGO : dynamicGOList) {
            if(DGO.body == null) continue;
            if(DGO.body.getType() == BodyDef.BodyType.DynamicBody && DGO.body.isActive() == true) {
                DGO.posPrev.x = DGO.body.getPosition().x;
                DGO.posPrev.y = DGO.body.getPosition().y;
                DGO.anglePrev = DGO.body.getAngle();
            }
        }*/
    }

    // interpolate formula: current-state * alpha + previous-state * (1.0 - alpha)
    public void interpolate(float alpha) {
        for(SimpleGameObject SGO : simpleGOList) {
            SGO.pos.x = SGO.sprite.getX() * alpha + SGO.posPrev.x * (1.0f - alpha);
            SGO.pos.y = SGO.sprite.getY() * alpha + SGO.posPrev.y * (1.0f - alpha);
            SGO.angle = SGO.sprite.getRotation() * alpha + SGO.anglePrev * (1.0f - alpha);
        }

        /*for(DynamicGameObject DGO : dynamicGOList) {
            if(DGO.body == null) continue;
            if (DGO.body.getType() == BodyDef.BodyType.DynamicBody && DGO.body.isActive() == true) {
                //---- interpolate: currentState*alpha + previousState * ( 1.0 - alpha ); ------------------
                DGO.pos.x = DGO.body.getPosition().x * alpha + DGO.posPrev.x * (1.0f - alpha);
                DGO.pos.y = DGO.body.getPosition().y * alpha + DGO.posPrev.y * (1.0f - alpha);
                DGO.angle = DGO.body.getAngle() * alpha + DGO.anglePrev * (1.0f - alpha);
            }
        }*/
    }

    public ArrayList<DynamicGameObject> getDynamicGameObjectList() { return dynamicGOList; }

    public ArrayList<SimpleGameObject> getSimpleGameObjectList() { return simpleGOList; }

    public void addDynamicGameObject(DynamicGameObject dynamicGameObject) { dynamicGOList.add(dynamicGameObject); }

    public void addSimpleGameObject(SimpleGameObject SGO) { simpleGOList.add(SGO); }

    public void removeDynamicGameObject(DynamicGameObject dynamicGameObject) { dynamicGOList.remove(dynamicGameObject); }

    public void removeSimpleGameObject(SimpleGameObject SGO) { simpleGOList.remove(SGO); }

    public void clearList() {
        dynamicGOList.clear();
        simpleGOList.clear();
    }
}
