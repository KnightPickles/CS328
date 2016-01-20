package edu.cs328;

import com.badlogic.gdx.physics.box2d.BodyDef;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 1/20/16.
 */
public class GameObjectManager {
    private ArrayList<DynamicGameObject> dynamicGOList;
    DynamicGameObject dynamicGO;

    public GameObjectManager() {
        dynamicGOList = new ArrayList<DynamicGameObject>();
    }

    // store object data used for interpolation
    public void savePos() {
        for(DynamicGameObject DGO : dynamicGOList) {
            if(DGO.body == null) continue;
            if(DGO.body.getType() == BodyDef.BodyType.DynamicBody && DGO.body.isActive() == true) {
                DGO.posPrev.x = DGO.body.getPosition().x;
                DGO.posPrev.y = DGO.body.getPosition().y;
                DGO.anglePrev = DGO.body.getAngle();
            }
        }
    }

    // interpolate formula: current-state * alpha + previous-state * (1.0 - alpha)
    public void interpolate(float alpha) {
        for(DynamicGameObject DGO : dynamicGOList) {
            if(DGO.body == null) continue;
            if (DGO.body.getType() == BodyDef.BodyType.DynamicBody && DGO.body.isActive() == true) {
                //---- interpolate: currentState*alpha + previousState * ( 1.0 - alpha ); ------------------
                DGO.pos.x = DGO.body.getPosition().x * alpha + DGO.posPrev.x * (1.0f - alpha);
                DGO.pos.y = DGO.body.getPosition().y * alpha + DGO.posPrev.y * (1.0f - alpha);
                DGO.angle = DGO.body.getAngle() * alpha + DGO.anglePrev * (1.0f - alpha);
            }
        }
    }

    public ArrayList<DynamicGameObject> getDynamicGameObjectList() { return dynamicGOList; }

    public void addDynamicGameObject(DynamicGameObject dynamicGameObject) { dynamicGOList.add(dynamicGameObject); }

    public void removeDynamicGameObject(DynamicGameObject dynamicGameObject) { dynamicGOList.remove(dynamicGameObject); }

    public void clearList() { dynamicGOList.clear(); }
}
