package edu.cs328;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 2/22/16.
 */
public class Brick extends PhysicsGameObject {

    Brick(TextureAtlas atlas, World world, float x, float y) {
        super(atlas, "rock0", world, x, y, true, false);
    }
}
