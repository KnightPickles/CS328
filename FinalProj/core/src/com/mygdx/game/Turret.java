package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Turret extends GameObject {

	TurretInfo myInfo;
	
	Sprite rotateSprite;

	GameObject target; //What we're targetting
	float attackCooldown = 0;
	
	public Turret(TurretInfo info) {
		myInfo = info;
		sprite = MainGameClass._instance.atlas.createSprite(myInfo.spriteName);
		if (myInfo.trackTarget) {
			rotateSprite = MainGameClass._instance.atlas.createSprite(myInfo.rotateSpriteName);
		}
		
		setBody(false, false, 0, 0);
	}
	
	@Override
	public void update() {
		findTarget();
		trackTarget();
		shootTarget();
		
		attackCooldown -= Gdx.graphics.getDeltaTime();
		
		super.update();
	}
	
	//Looks for a valid target in our range
	//Right now it will just find the first target in range, but we can add different AI modes, (units with gold > units with low health > units in front > units in back) etc
	void findTarget() {
		
	}
	
	void shootTarget() {
		if (target == null)
			return;
		
		if (!canShootTarget())
			return;
		
		if (myInfo.projectileType == TurretInfo.ProjectileType.Ballistic) {
			shootBallistic();
		} else if (myInfo.projectileType == TurretInfo.ProjectileType.Laser) {
			shootLaser();
		} else if (myInfo.projectileType == TurretInfo.ProjectileType.Buff) {
			
		} else if (myInfo.projectileType == TurretInfo.ProjectileType.AOE) {
			
		}
	}
	
	void shootBallistic() {
		//Create projectile
		//Tell projectile its target, damage, speed, etc (it will deal damage when it hits)
		attackCooldown = myInfo.attackCooldown; //Reset attack CD
	}
	
	void shootLaser() {
		//Create laser sprite from here to target
		//deal damage to target
		attackCooldown = myInfo.attackCooldown; //Reset attack CD
	}
	
	boolean canShootTarget() {
		//Check if we are rotated enough to target
		if (myInfo.trackTarget) {
			
		}
		
		if (attackCooldown > 0)
			return false;
		
		return true;
	}
	
	void trackTarget() {
		if (!myInfo.trackTarget)
			return;
		
		//Need to do the math and figure out how to rotate the rotateSprite towards the targets vector
		if (target != null) {
			
		}
		
		//If we have no target, rotate the turret back to the stationary position, angle 0 or whatever
		//Assuming angles are working on 0-360 and it doesnt go to 380 degrees for 20 degrees or -20 for 340 degrees, in which case this'll need to be fixed
		if (target == null) {
			float angle = rotateSprite.getRotation();
			if (angle < 2 || angle > 358)
				return;
			
			if (angle < 180)
				rotateSprite.rotate(-myInfo.trackingSpeed * Gdx.graphics.getDeltaTime());
			if (angle >= 180)
				rotateSprite.rotate(myInfo.trackingSpeed * Gdx.graphics.getDeltaTime());
		}

	}
}
