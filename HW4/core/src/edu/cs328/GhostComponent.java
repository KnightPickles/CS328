package edu.cs328;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GhostComponent extends UnitComponent {
	
	//Unit properties
	float timeUntilAttack = 0;
	
	//Command stuff
	Vector2 startMovePosition;
	Vector2 desiredMovePosition;
	float timeToPosition = 0;
	float currTime = 0;
	
	boolean leashedAttack = false;
	Vector2 leashStartPos;
	float leashDist = 25f;
	Entity target;
	
	//States
	public enum behaviour {
		Stop,
		Move,
		Attack,
		Follow,
		AttackMove
	}
	behaviour currBehaviour = behaviour.Stop;
	
	public GhostComponent(Box2dComponent b2dc, UnitStats stats, Entity myEntity) {
		super (b2dc, stats, myEntity);
	}
	
	@Override
	public void draw(Batch batch) {
		if (!alive)
			return;
		
		super.draw(batch);
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
		if (!EntityManager._instance.gc.get(target).alive) {
			SetStopState();
			return;
		}
		
		Box2dComponent tar = EntityManager._instance.boxc.get(target);
		float dist = tar.position.dst(position);
		float timeToTarget = dist/stats.moveSpeed;
		
		if (dist <= stats.attackDistance) {
			
			if (timeUntilAttack <= 0) {
				EntityManager._instance.gc.get(target).receiveDamage(stats.attackDamage);
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
		this.target = target;

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
		
		if (EntityManager._instance.sc.get(target).friendly) { //Follow friendly unit
			currBehaviour = behaviour.Follow;
			return;
		} else { //Attack enemy unit
			currBehaviour = behaviour.Attack;
			return;
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
	}
}
