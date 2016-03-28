package edu.cs328;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cs328.BuildingComponent.BuildingType;

public class GhostComponent extends UnitComponent {
	
	//Unit properties
	float timeUntilAttack = 0;
	int upgradeLevel = 2;
	
	//Command stuff
	Vector2 startMovePosition;
	Vector2 desiredMovePosition;
	float timeToPosition = 0;
	float currTime = 0;
	
	boolean leashedAttack = false;
	Vector2 leashStartPos;
	float leashDist = 25f;
	Entity target;

	Sprite healthBarBackground;
	Sprite healthBarLevel;
	
	public enum UnitType {
		Worker,
		MeleeFighter,
		RangedFighter
	}
	UnitType unitType;
	
	//States
	public enum behaviour {
		Stop,
		Move,
		Attack,
		Follow,
		Haunting,
		AttackMove
	}
	behaviour currBehaviour = behaviour.Stop;
	
	public GhostComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity, UnitType type, TextureAtlas atlas) {
		super (b2dc, stats, myEntity);
		unitType = type;

		healthBarBackground = atlas.createSprite("healthbar");
		if(bc.playerControlled) {
			healthBarLevel = atlas.createSprite("health_blue");
		} else healthBarLevel = atlas.createSprite("health_red");
		
		while (upgradeLevel < BuildingComponent.alliedUpgradeLevel)
			upgrade();
	}
	
	public void upgrade() {
		if (unitType == UnitType.Worker)
			return;
		
		if (upgradeLevel >= 5)
			return;
		
		upgradeLevel++;
		stats.maxHealth += 10;
		stats.health += 10;
		stats.attackDamage += 1;
		bc.upgrade(upgradeLevel);
	}
	
	@Override
	public void draw(Batch batch) {
		if (!alive)
			return;
		
		super.draw(batch);

		healthBarBackground.setPosition(bc.sprite.getX() - 1, bc.sprite.getY() + bc.sprite.getHeight() + 1);
		healthBarBackground.draw(batch);
		healthBarLevel.draw(batch);
		for(int i = 0; i < healthBarBackground.getWidth(); i++) {
			if (i * stats.maxHealth / healthBarBackground.getWidth() <= stats.health) {
				healthBarLevel.setPosition(bc.sprite.getX() - 1 + i, bc.sprite.getY() + bc.sprite.getHeight() + 1);
				healthBarLevel.draw(batch);
			}
		}
	}
	
	@Override
	public void update() {
		if (!alive)
			return;
		
		switch(currBehaviour) {
		case Stop: Stop(); break;
		case Move: Move(); break;
		case Attack: Attack(); break;
		case Follow: Follow(); break;
		case Haunting: Haunt(); break;
		case AttackMove: AttackMove(); break;
		}
		
		super.update();
	}
	
	public void Follow() {
		if (!EntityManager._instance.gc.get(target).alive) {
			SetStopState();
			return;
		}
		
		Box2dComponent tar = EntityManager._instance.boxc.get(target);
		desiredMovePosition = tar.position;
		float dist = tar.position.dst(position);
		float timeToTarget = dist/stats.moveSpeed;
		if (dist < bc.sprite.getWidth())
			return;
		float x = MathUtils.lerp(position.x, tar.position.x, Gdx.graphics.getDeltaTime()/(timeToTarget));
		float y = MathUtils.lerp(position.y, tar.position.y, Gdx.graphics.getDeltaTime()/(timeToTarget));
		position = new Vector2(x, y);
		bc.setPosition(position);
	}
	
	public void Attack() {
		if (!EntityManager._instance.GetListSelectables().contains(target, true)) {
			SetStopState();
			return;
		}
		
		
		Box2dComponent tar = EntityManager._instance.boxc.get(target);
		float dist = tar.position.dst(position);
		float timeToTarget = dist/stats.moveSpeed;
		desiredMovePosition = tar.position;
		
		if (dist <= stats.attackDistance) {
			if (timeUntilAttack <= 0) {
				if (unitType == UnitType.RangedFighter) { 
					EntityManager._instance.createProjectile(myEntity, target, stats.attackDamage);
				} else {
					if (EntityManager._instance.GetListBuildings().contains(target, true)) {
						EntityManager._instance.bc.get(target).receiveDamage(stats.attackDamage, myEntity);
					} else {
						EntityManager._instance.gc.get(target).receiveDamage(stats.attackDamage, myEntity);
					}
				}
				timeUntilAttack = stats.attackCooldown; //Attacked so reset attack CD
			} else {
				timeUntilAttack -= Gdx.graphics.getDeltaTime();
			}
		} else {
			if (dist < Math.min(8f, stats.attackDistance))
				return;
			float x = MathUtils.lerp(position.x, tar.position.x, Gdx.graphics.getDeltaTime()/(timeToTarget));
			float y = MathUtils.lerp(position.y, tar.position.y, Gdx.graphics.getDeltaTime()/(timeToTarget));
			position = new Vector2(x, y);
			bc.setPosition(position);
		}
		
		if (leashedAttack && position.dst(leashStartPos) > leashDist) {
			SetMove(leashStartPos);
			return;
		}
	}
	
	public void SetMove(Vector2 pos) {
		target = null;
		currBehaviour = behaviour.Move;
		float moveLength = pos.dst(position); //How far were going
		timeToPosition = moveLength/stats.moveSpeed;
		currTime = 0;
		startMovePosition = position;
		desiredMovePosition = pos;
		return;
	}
	
	public void Stop() {
		if (unitType == UnitType.Worker) //Don't seek combat if we are a worker 
			return;
		
		//Check for enemies 
		ImmutableArray<Entity> entities = EntityManager._instance.GetListSelectables();
		for (Entity e : entities) {
			if (EntityManager._instance.sc.get(e).friendly == !stats.playerControlled) {
				Box2dComponent b2dc = EntityManager._instance.boxc.get(e);
				if (position.dst(b2dc.position) < 30) {
					AttackTarget(e, true);
					return;
				}
			}
		}
	}
	
	//Hasn't been tested or used yet, needs to be checked/debugged before use
	public void AttackMove() {
		//Check for enemies 
		ImmutableArray<Entity> entities = EntityManager._instance.GetListSelectables();
		for (Entity e : entities) {
			if (!EntityManager._instance.sc.get(e).friendly) {
				Box2dComponent b2dc = EntityManager._instance.boxc.get(e);
				if (position.dst(b2dc.position) < 30) {
					AttackTarget(e, false);
					return;
				}
			}
		}
		
		//If no enemies, keep moving to target position
		if (timeToPosition > 0 && currTime <= timeToPosition) {
			currTime += Gdx.graphics.getDeltaTime();
			float progress = currTime/timeToPosition;
			float x = MathUtils.lerp(startMovePosition.x, desiredMovePosition.x, progress);
			float y = MathUtils.lerp(startMovePosition.y, desiredMovePosition.y, progress);
			position = new Vector2(x, y);
			
			bc.setPosition(position);
		}
		
		if (Math.abs(position.dst(desiredMovePosition)) < .00001f) {
			SetStopState();
			return;
		}
	}
	
	public void Move() {
		if (timeToPosition > 0 && currTime <= timeToPosition) {
			currTime += Gdx.graphics.getDeltaTime();
			float progress = currTime/timeToPosition;
			float x = MathUtils.lerp(startMovePosition.x, desiredMovePosition.x, progress);
			float y = MathUtils.lerp(startMovePosition.y, desiredMovePosition.y, progress);
			position = new Vector2(x, y);

			bc.setPosition(position);
		}
		
		if (Math.abs(position.dst(desiredMovePosition)) < .25f) { //.25 is a lot of room for error, but in testing sometimes it stopped as close as .2 from its destination 
			SetStopState();
			return;
		}

	}
	
	@Override
	public void rightClickCommand(Vector2 pos, Entity target) {
		super.rightClickCommand(pos, target);
		this.target = target;
		leashedAttack = false;
		
		//Regular movement command
		if (target == null) {
			currBehaviour = behaviour.Move;
			float moveLength = pos.dst(position); //How far were going
			timeToPosition = moveLength/stats.moveSpeed;
			currTime = 0;
			startMovePosition = position;
			desiredMovePosition = pos;
			return;
		}

		if (EntityManager._instance.GetListBuildings().contains(target, true)) { //Target building
			if (unitType == UnitType.Worker) {
				if (EntityManager._instance.bc.get(target).buildingType == BuildingType.HauntedMansion) {
					startHaunting(target);
					return;
				}
			}
			
			if (EntityManager._instance.sc.get(target).friendly) { //Friendly building
				SetMove(pos);
			} else { //Enemy building
				AttackTarget(target, false);
			}
			
		} else { //Target unit
			if (EntityManager._instance.sc.get(target).friendly) { //Follow friendly unit
				currBehaviour = behaviour.Follow;
				return;
			} else { //Attack enemy unit
				currBehaviour = behaviour.Attack;
				return;
			}
		}
	}
	
	Entity mainBase;
	Entity targetMansion;
	public void startHaunting(Entity target) {
		if (mainBase == null) {
			ImmutableArray<Entity> buildings = EntityManager._instance.GetListBuildings();
			for (Entity e : buildings) {
				if (EntityManager._instance.bc.get(e).buildingType == BuildingType.MainBase
						&& EntityManager._instance.bc.get(e).stats.playerControlled) {
					mainBase = e;
					break;
				}
			}
		}

		Box2dComponent tar = EntityManager._instance.boxc.get(target);
		desiredMovePosition = tar.position;

		targetMansion = target;
		currBehaviour = behaviour.Haunting;
		hauntStep = 0;
		currHideTime = 0;
	}
	
	public static int money = 200; //if we get enough reason to make a new class then put this in there
	float currHideTime = 0;
	float hideTime = 3;
	int hauntStep; //0 move to hauntedmansion 1 sit for few sec 2 run back to base
	public void Haunt() {
		if (hauntStep == 0) {
			Box2dComponent tar = EntityManager._instance.boxc.get(targetMansion);
			float dist = tar.position.dst(position);
			float timeToTarget = dist/stats.moveSpeed;
			float x = MathUtils.lerp(position.x, tar.position.x, Gdx.graphics.getDeltaTime()/(timeToTarget));
			float y = MathUtils.lerp(position.y, tar.position.y, Gdx.graphics.getDeltaTime()/(timeToTarget));
			position = new Vector2(x, y);
			bc.setPosition(position);
			if (position.dst(tar.position) < .25f) 
				hauntStep = 1;
		}
		if (hauntStep == 1) {
			bc.HideUnit();
			currHideTime += Gdx.graphics.getDeltaTime();
			if (currHideTime > hideTime) {
				bc.StopHideUnit();
				hauntStep = 2;
				currHideTime = 0;
			}
		}
		if (hauntStep == 2) {
			Box2dComponent tar = EntityManager._instance.boxc.get(mainBase);
			float dist = tar.position.dst(position);
			float timeToTarget = dist/stats.moveSpeed;
			float x = MathUtils.lerp(position.x, tar.position.x, Gdx.graphics.getDeltaTime()/(timeToTarget));
			float y = MathUtils.lerp(position.y, tar.position.y, Gdx.graphics.getDeltaTime()/(timeToTarget));
			position = new Vector2(x, y);
			bc.setPosition(position);
			if (position.dst(tar.position) < .25f) {
				hauntStep = 0;
				money += 50;
			}
		}
	}
	
	public void AttackTarget(Entity e, Boolean leashed) {
		leashedAttack = leashed;
		leashStartPos = position;
		currBehaviour = behaviour.Attack;
		target = e;
	}
	
	public void SetStopState() {
		currBehaviour = behaviour.Stop;
		target = null;
		desiredMovePosition = null;
	}
	
	@Override
	public void receiveDamage(int amount, Entity source) {
		super.receiveDamage(amount, source);

		if (unitType == UnitType.Worker && currBehaviour == behaviour.Stop) { //If we are hit as a worker we run away
			Box2dComponent b = EntityManager._instance.boxc.get(source);
			Vector2 negV = new Vector2(-(b.position.x - position.x), -(b.position.y - position.y));
			negV = negV.nor();
			
			SetMove(new Vector2(position.x + (negV.x)*30, position.y + (negV.y)*30));
		}
	}
}
