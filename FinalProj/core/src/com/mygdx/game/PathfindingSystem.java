package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Created by KnightPickles on 4/14/16.
 */
public class PathfindingSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<ComponentPath> pathm = ComponentMapper.getFor(ComponentPath.class);
    private ComponentMapper<ComponentPosition> posm = ComponentMapper.getFor(ComponentPosition.class);

    public PathfindingSystem() {}

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ComponentPath.class, ComponentPosition.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            ComponentPath path = pathm.get(entity);
            ComponentPosition pos = posm.get(entity);

        }
    }
}