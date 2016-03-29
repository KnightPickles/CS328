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
		this.sprite = sprite;		
		Init(playerControlled, e, position, world);
	}
	
	public Box2dComponent(boolean playerControlled, Entity e, Vector2 position, World world, GhostComponent.UnitType unitType) {
		if (playerControlled) {
			String spriteName = "greenghost1";
			switch (unitType) {
			case MeleeFighter: spriteName = "greenghost" + BuildingComponent.alliedUpgradeLevel; break;
			case RangedFighter: spriteName = "purpleghost" + BuildingComponent.alliedUpgradeLevel; break;
			case Worker: spriteName = "blueghost1"; break;
			}
			
			sprite = HW4._instance.atlas.createSprite(spriteName);
		} else {
			if (unitType != GhostComponent.UnitType.Worker)
				sprite = HW4._instance.atlas.createSprite("redghost5");
			else sprite = HW4._instance.atlas.createSprite("redghost1");
		}
		
		Init(playerControlled, e, position, world);
	}
	
	void Init(boolean playerControlled, Entity e, Vector2 position, World world) {
		this.playerControlled = playerControlled;
		myEntity = e;
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
		body.setTransform(position, 0);
		this.position = position;
		sprite.setPosition(position.x, position.y);
	}
	
	public void upgrade(int level, GhostComponent.UnitType type) {
		if (playerControlled) {
			String spriteName = "greenghost1";
			switch (type) {
			case MeleeFighter: spriteName = "greenghost" + level; break;
			case RangedFighter: spriteName = "purpleghost" + level; break;
			case Worker: spriteName = "blueghost1"; break;
			}
			
			sprite = HW4._instance.atlas.createSprite(spriteName);
			sprite.setPosition(position.x, position.y);
		}		
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
