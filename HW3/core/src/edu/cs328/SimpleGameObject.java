package edu.cs328;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by KnightPickles on 2/21/16.
 */
public class SimpleGameObject {
    Sprite sprite;
    float x,y;

    SimpleGameObject(TextureAtlas atlas, String atlasRegion, float x, float y) {
        if(atlas != null) {
            sprite = atlas.createSprite(atlasRegion);
            if(sprite == null) sprite = new Sprite();
        } else sprite = new Sprite();
        this.x = x;
        this.y = y;
        sprite.setPosition(x,y);
        sprite.setScale(HW3.SCALE);
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public void update() {

    }
}
