package edu.cs328;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by KnightPickles on 1/20/16.
 */
public class DynamicGameObject {
    private Game game;
    private World world;
    public Body body;
    public int objID;

    // In meters
    public float width, height;
    public Vector2 pos, posPrev;
    public float angle, anglePrev;

    DynamicGameObject(Game game, int objID, float x, float y, float h, float w) {
        this.game = game;
        this.objID = objID;
        width = w;
        height = h;
        world = null;
        body = null;
        pos = posPrev = new Vector2(x,y);
        game.getGameObjectManager().addDynamicGameObject(this);
    }

    public void update(float deltaTime) {
        if(body == null) return;
        //--- update position & angle for ver. TS
        pos.x = body.getPosition().x;	// not neccessary due to interpolation ... only testing difference to fixed timestep
        pos.y = body.getPosition().y;	// not neccessary due to interpolation ... only testing difference to fixed timestep
        angle = body.getAngle();	// not neccessary due to interpolation ... only testing difference to fixed timestep

        //-- borders ---
        if(body.getPosition().y + height < GameParameters.MAPBORDERBOTTOM ) {
            body.setTransform(body.getPosition().x, GameParameters.MAPBORDERTOP, 0);
            posPrev.x = body.getPosition().x;
            posPrev.y = body.getPosition().y;
            pos.x = posPrev.x;
            pos.y = posPrev.y;
        }

        //-- check top mapborder ---
        if(body.getPosition().y > GameParameters.MAPBORDERTOP) {
            body.setTransform(body.getPosition().x, GameParameters.MAPBORDERBOTTOM, 0);
            posPrev.x = body.getPosition().x;
            posPrev.y = body.getPosition().y;
            pos.x = posPrev.x;
            pos.y = posPrev.y;
        }

        //--- check if object is out of playfield and deactivate
        if(body.getPosition().x < -2f || body.getPosition().x > 26f) {
            body.setActive(false);
        }

        if(body.getPosition().y < -3f) {
            body.setActive(false);
        }

    }

}
