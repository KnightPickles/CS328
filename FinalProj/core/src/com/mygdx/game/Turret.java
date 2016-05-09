package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Turret extends GameObject {

	TurretInfo myInfo;
	
	Sprite rotateSprite;
	Circle targetFinder;

	GameObject target; //What we're targetting
	float attackCooldown = 0;
	public int goldValue = 0; //Gold spent on this turret
	public double costAccumulator = 1.1;
	public int upgradeCost = 10;

	public TurretType turretType;
	public enum TurretType {
		Red,
		Green,
		Blue
	}

	public Turret(Turret t) {
		super((GameObject)t);
		myInfo = new TurretInfo(t.myInfo);
		rotateSprite = new Sprite(t.sprite);
		targetFinder = new Circle(t.targetFinder);
		target = new GameObject(target);
		attackCooldown = t.attackCooldown;
		goldValue = t.goldValue;
		costAccumulator = t.costAccumulator;
		upgradeCost = t.upgradeCost;
		turretType = t.turretType;
	}
	
	public Turret(TurretInfo info, Vector2 spawnPos) {
		myInfo = new TurretInfo(info);
		sprite = MainGameClass._instance.atlas.createSprite(myInfo.spriteName);
		sprite.setPosition(spawnPos.x, spawnPos.y);
		if (myInfo.trackTarget) {
			rotateSprite = MainGameClass._instance.atlas.createSprite(myInfo.rotateSpriteName);
			rotateSprite.setPosition(spawnPos.x, spawnPos.y +2);
		}
		goldValue = myInfo.cost;
		
		if (myInfo.redLevel >= 1)
			turretType = TurretType.Red;
		else if (myInfo.greenLevel >= 1)
			turretType = TurretType.Green;
		else if (myInfo.blueLevel >= 1)
			turretType = TurretType.Blue;
		
		setBody(true, false, 0, 0);
		targetFinder = new Circle();
		targetFinder.set(position, myInfo.range);
	}
	
	@Override
	public void update() {

		findTarget();
		trackTarget();
		shootTarget();
		
		attackCooldown -= Gdx.graphics.getDeltaTime();

		super.update();
	}
	
	@Override
	public void draw() {
        MainGameClass._instance.batch.begin();
        sprite.draw(MainGameClass._instance.batch);
        rotateSprite.draw(MainGameClass._instance.batch);
        MainGameClass._instance.batch.end();
	}
	
	public void upgradeRedLevel() {
		//Add upgrade cost to goldValue
		myInfo.redLevel++;
		if (myInfo.redLevel > myInfo.greenLevel && myInfo.redLevel > myInfo.blueLevel) {
			turretType = TurretType.Red;
		}
		checkUpgrade();
	}
	
	public void upgradeGreenLevel() {
		//Add upgrade cost to goldValue
		myInfo.greenLevel++;
		if (myInfo.greenLevel > myInfo.redLevel && myInfo.greenLevel > myInfo.blueLevel) {
			turretType = TurretType.Green;
		}
		checkUpgrade();
	}
	
	public void upgradeBlueLevel() {
		//Add upgrade cost to goldValue
		myInfo.blueLevel++;
		if (myInfo.blueLevel > myInfo.redLevel && myInfo.blueLevel > myInfo.greenLevel) {
			turretType = TurretType.Blue;
		}
		checkUpgrade();
	}
	
	void checkUpgrade() {
		int upgradeLevel = (int)Math.floor(((myInfo.redLevel + myInfo.greenLevel + myInfo.blueLevel)/10));
		switch (turretType) {
		case Red:
			initializeTurret("red" + upgradeLevel);
			break;
		case Blue:
			initializeTurret("blue" + upgradeLevel);
			break;
		case Green: 
			initializeTurret("green" + upgradeLevel);
			break;
		}
	}
	
	//Setup turret with a new turret info name
	void initializeTurret(String turretName) {
		TurretInfo info = EntityManager._instance.turretTable.get(turretName);
		myInfo = info;
		sprite = MainGameClass._instance.atlas.createSprite(myInfo.spriteName);
		sprite.setPosition(position.x, position.y);
		
		if (myInfo.trackTarget) {
			rotateSprite = MainGameClass._instance.atlas.createSprite(myInfo.rotateSpriteName);
			rotateSprite.setPosition(position.x, position.y +2);
		}
	}
	
	//Looks for a valid target in our range
	//Right now it will just find the first target in range, but we can add different AI modes, (units with gold > units with low health > units in front > units in back) etc
	void findTarget() {
		if (target != null && 
				(!target.active 
						|| !EntityManager._instance.ghosts.contains(target) 
						|| position.dst(target.position) > myInfo.range))
			target = null;
		
		if (target != null)
			return;
		
		for (GameObject g : EntityManager._instance.ghosts) {
			if (targetFinder.contains(g.position)) {
				target = g;
				return;
			}
		}
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
		if (attackCooldown <= 0) {
			EntityManager._instance.spawnProjectile(myInfo.projectileSpriteName, target, myInfo.projectileDamage,  myInfo.projectileSpeed, position);
			attackCooldown = myInfo.attackCooldown; //Reset attack CD
		}
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
			float degrees = (float) ((Math.atan2 (target.position.x - position.x, -(target.position.y - position.y))*180.0d/Math.PI)+90.0f);
			rotateSprite.setRotation(degrees);
		}
		
		//If we have no target, rotate the turret back to the stationary position, angle 0 or whatever
		//Assuming angles are working on 0-360 and it doesnt go to 380 degrees for 20 degrees or -20 for 340 degrees, in which case this'll need to be fixed
		if (target == null) {
			float angle = rotateSprite.getRotation() % 360;
			if (angle < 0)
				angle += 360;
			float diff = myInfo.trackingSpeed * Gdx.graphics.getDeltaTime();
			if (angle > diff && angle < 360-diff) {
				if (angle > 180)
					angle += diff;
				else angle -= diff;
			}
			rotateSprite.setRotation(angle);
		}
	}
}
