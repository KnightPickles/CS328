package edu.cs328;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by KnightPickles on 3/26/16.
 */
public class EntityManager extends EntitySystem {
    public class SpriteComponent implements Component {
        Sprite sprite = null;
    }

    Engine engine = new Engine();

    private ImmutableArray<Entity> entities;

    ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    EntityManager(TextureAtlas atlas) {
        SpriteComponent sc = new SpriteComponent();
        for(int i = 0; i < 50; i++) {
            sc.sprite = atlas.createSprite("redghost5");
            sc.sprite.setPosition(i * HW4.PPM, i * HW4.PPM);
            Entity e = new Entity();
            e.add(sc);
            engine.addEntity(e);
        }
    }

    public void update() {
        entities = engine.getEntitiesFor(Family.all(SpriteComponent.class).get());
        for(Entity e : entities) {
            SpriteComponent sc = sm.get(e);
            //sc.sprite.setPosition(10, 10);
        }
    }

    public void draw(Batch batch) {
        entities = engine.getEntitiesFor(Family.all(SpriteComponent.class).get());
        for(Entity e : entities) {
            SpriteComponent sc = sm.get(e);
            if(sc.sprite != null)
                sc.sprite.draw(batch);

        }
    }
}


