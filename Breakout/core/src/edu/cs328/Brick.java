package edu.cs328;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by KnightPickles on 2/3/16.
 */
public class Brick extends SimpleGameObject {

    public float width, height;
    public Color color = null;

    Brick(Game game, int objID, float x, float y, float width, float height, Color color) {
        super(game, objID, null, x, y);
        this.color = color;
        this.width = width;
        this.height = height;
    }

    @Override
    void render(ShapeRenderer sr) {
        if(color != null) sr.setColor(color);
        //sr.rect(10, 10, 30, 20);
        sr.rect(pos.x, pos.y, width, height);
    }

    @Override
    void render(Batch b) {

    }
}
