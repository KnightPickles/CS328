package edu.cs328;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 2/2/16.
 */
public class SimpleGameObject {
    Game game;
    int objID;
    boolean isTrigger = false;
    Vector2 pos, posPrev;
    float angle, anglePrev;
    float xBound, yBound;

    public Sprite sprite;

    SimpleGameObject(Game game, int objID, Sprite sprite, float x, float y) {
        this.game = game;
        this.objID = objID;
        this.sprite = sprite;
        this.pos = this.posPrev = new Vector2(x,y);
        this.xBound = GameObjectManager.xBound;
        this.yBound = GameObjectManager.yBound;
        game.getGameObjectManager().addSimpleGameObject(this);
    }

    void update() {
        if(pos.x < 0) pos.x = posPrev.x = 0;
        if(pos.y < 0) pos.y = posPrev.y = 0;
        if(pos.x > xBound) pos.x = posPrev.x = xBound;
        if(pos.y > yBound) pos.y = posPrev.y = yBound;
        if(sprite != null) sprite.setPosition(pos.x, pos.y);
    }

    void render(ShapeRenderer sr) {
        System.out.println("SGO");
    }

    void render(Batch batch) {
        System.out.println("SGO");
    }

    SimpleGameObject collided(ArrayList<SimpleGameObject> candidates) {
        return null;
    }
}
