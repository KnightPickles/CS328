package edu.cs328;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
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

	Sprite sprite;
	Body body;
	Vector2 position;
	
	//Unit properties
	float moveSpeed;
	
	//Commands
	Vector2 startMovePosition;
	Vector2 desiredMovePosition;
	float timeToPosition = 0;
	float currTime = 0;
	
	public Box2dComponent(Sprite sprite, Vector2 position, World world, float moveSpeed) {
		this.sprite = sprite;
		this.moveSpeed = moveSpeed;
		
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
		desiredMovePosition = position;
	}
	
	public void draw(Batch batch) {
		if (timeToPosition > 0 && currTime <= timeToPosition) {
			currTime += Gdx.graphics.getDeltaTime();
			float progress = currTime/timeToPosition;
			float x = MathUtils.lerp(startMovePosition.x, desiredMovePosition.x, progress);
			float y = MathUtils.lerp(startMovePosition.y, desiredMovePosition.y, progress);
			position = new Vector2(x, y);
			
			body.setTransform(position, body.getAngle());
		}
		
		position = new Vector2(body.getPosition().x, body.getPosition().y);
		sprite.setPosition(position.x - sprite.getWidth()/2, position.y - sprite.getHeight()/2);
		sprite.draw(batch);
	}
	
	public void moveCommand(Vector2 pos) {
		float moveLength = pos.dst(position); //How far were going
		timeToPosition = moveLength/moveSpeed;
		currTime = 0;
		startMovePosition = position;
		desiredMovePosition = pos;
	}
}
