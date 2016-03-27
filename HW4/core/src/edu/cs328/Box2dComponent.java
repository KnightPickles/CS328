package edu.cs328;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2dComponent implements Component {

	Entity myEntity;
	Sprite sprite;
	Body body;
	Vector2 position;
	public boolean playerControlled;
	
	public Box2dComponent(boolean playerControlled, Entity e, Sprite sprite, Vector2 position, World world) {
		myEntity = e;
		this.sprite = sprite;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		
		body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);
		
		fixtureDef.shape = shape;
		Fixture fixture = body.createFixture(fixtureDef);
		
		sprite.setPosition(position.x, position.y);
		body.setTransform(position, 0);
		this.position = position;
	}
	
	public void draw(Batch batch) {
		position = new Vector2(body.getPosition().x, body.getPosition().y);
		
		if (body.isActive()) {
			sprite.setPosition(position.x - sprite.getWidth()/2, position.y - sprite.getHeight()/2);
			sprite.draw(batch);
		}
	}
	
	public void setPosition(Vector2 position) {
		body.setTransform(position, body.getAngle());
	}
	
	public void KillUnit() {
		body.destroyFixture(body.getFixtureList().first());	
	}
	
	public void HideUnit() {
		body.setActive(false);
	}
	
	public void StopHideUnit() {
		body.setActive(true);
	}
}
