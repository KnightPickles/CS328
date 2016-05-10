package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	Sound[] redSounds = new Sound[3];
	Sound[] greenSounds = new Sound[3];
	Sound[] blueSounds = new Sound[3];

	int redUpgradeLevel = 0;
	int greenUpgradeLevel = 0;
	int blueUpgradeLevel = 1;
	
	ShapeRenderer sr = new ShapeRenderer();

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
		redUpgradeLevel = t.redUpgradeLevel;
		greenUpgradeLevel = t.greenUpgradeLevel;
		blueUpgradeLevel = t.blueUpgradeLevel;
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
		
		if (redUpgradeLevel >= 1)
			turretType = TurretType.Red;
		else if (greenUpgradeLevel >= 1)
			turretType = TurretType.Green;
		else if (blueUpgradeLevel >= 1)
			turretType = TurretType.Blue;
		
		setBody(true, false, 0, 0);
		targetFinder = new Circle();
		targetFinder.set(position, myInfo.range);
		loadSounds();
	}

	void endCondition() {
		if(turretType == TurretType.Green) {
			greenSounds[0].pause();
		}
	}
	
	@Override
	public void update() {

		findTarget();
		trackTarget();
		shootTarget();
		
		attackCooldown -= Gdx.graphics.getDeltaTime();

    	for (int i = 0; i < aoe.size(); i++) {
    		AOERenderer r = aoe.get(i);
    		r.update();
    	}
		
		super.update();
	}
	
	@Override
	public void draw() {
        MainGameClass._instance.batch.begin();
        sprite.draw(MainGameClass._instance.batch);
        if (myInfo.trackTarget)
        	rotateSprite.draw(MainGameClass._instance.batch);
        MainGameClass._instance.batch.end();
        if (turretType == TurretType.Green && target != null) {
        	sr.begin(ShapeType.Filled);
        	sr.setProjectionMatrix(GameScreen._instance.camera.combined);
        	sr.setColor(0, 1, 0, .7f);
        	sr.rectLine(position, target.position, 1);
        	sr.end();
        }
        else if (turretType == TurretType.Blue) {
        	for (int i = 0; i < aoe.size(); i++) {
        		AOERenderer r = aoe.get(i);
        		if (r.lifeDuration <= 0) {
        			aoe.remove(i);
        			i--;
        		} else {
        			r.draw(MainGameClass._instance.batch);
        		}
        	}
        }
	}
	
	void loadSounds() {
		for (int i = 0; i < 3; i ++) {
			redSounds[i] = Gdx.audio.newSound(Gdx.files.internal("red" + i + ".ogg"));
		}
		for (int i = 0; i < 3; i ++) {
			greenSounds[i] = Gdx.audio.newSound(Gdx.files.internal("green" + i + ".ogg"));
			greenSounds[i].loop(.4f * GameScreen.volumeModifier);
			greenSounds[i].pause();
		}
		for (int i = 0; i < 3; i ++) {
			blueSounds[i] = Gdx.audio.newSound(Gdx.files.internal("blue" + i + ".ogg"));
		}
		
	}
	
	void playAttackSound() {
		if (turretType == TurretType.Red) {
			int level = (int)Math.floor(redUpgradeLevel/10);
			long i = redSounds[level].play();
			redSounds[level].setVolume(i, .3f  * GameScreen.volumeModifier);
		} else if (turretType == TurretType.Green) {
			int level = (int)Math.floor(greenUpgradeLevel/10);
			if (target != null) {				
				greenSounds[level].resume();
			}
		} else if (turretType == TurretType.Blue) {
			int level = (int)Math.floor(blueUpgradeLevel/10);
			long i = blueSounds[level].play();
			blueSounds[level].setVolume(i, .3f  * GameScreen.volumeModifier);
		}
	}
	
	public void upgradeRedLevel() {
		//Add upgrade cost to goldValue
		redUpgradeLevel++;
		if (redUpgradeLevel > greenUpgradeLevel && redUpgradeLevel > blueUpgradeLevel) {
			turretType = TurretType.Red;
		}
		checkUpgrade();
	}
	
	public void upgradeGreenLevel() {
		//Add upgrade cost to goldValue
		greenUpgradeLevel++;
		if (greenUpgradeLevel > redUpgradeLevel && greenUpgradeLevel > blueUpgradeLevel) {
			turretType = TurretType.Green;
		}
		checkUpgrade();
	}
	
	public void upgradeBlueLevel() {
		//Add upgrade cost to goldValue
		blueUpgradeLevel++;
		if (blueUpgradeLevel > redUpgradeLevel && blueUpgradeLevel > greenUpgradeLevel) {
			turretType = TurretType.Blue;
		}
		checkUpgrade();
	}
	
	void checkUpgrade() {
		int upgradeLevel = 0; //(int)Math.floor(((redUpgradeLevel + greenUpgradeLevel + blueUpgradeLevel)/10));
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
			rotateSprite.setPosition(position.x - sprite.getWidth() / 2, position.y +2 - sprite.getWidth() / 2);
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
		
		if (target == null) {
			for (int i = 0; i < 3; i ++) {
				greenSounds[i].pause();
			}
		}
	}
	
	void shootTarget() {
		if (target == null)
			return;
		
		if (!canShootTarget())
			return;
		
		if (attackCooldown > 0)
			return;
		
		if (myInfo.projectileType == TurretInfo.ProjectileType.Ballistic) {
			shootBallistic();
		} else if (myInfo.projectileType == TurretInfo.ProjectileType.Laser) {
			shootLaser();
		} else if (myInfo.projectileType == TurretInfo.ProjectileType.Buff) {
			
		} else if (myInfo.projectileType == TurretInfo.ProjectileType.AOE) {
			shootAOE();
		}
	}
	
	void shootBallistic() {
		playAttackSound();
		EntityManager._instance.spawnProjectile(myInfo.projectileSpriteName, target, myInfo.projectileDamage,  myInfo.projectileSpeed, position);
		attackCooldown = myInfo.attackCooldown; //Reset attack CD
	}
	
	void shootLaser() {
		playAttackSound();
		target.receiveDamage(myInfo.projectileDamage);
		attackCooldown = myInfo.attackCooldown; //Reset attack CD			
	}
	
	ArrayList<AOERenderer> aoe = new ArrayList<AOERenderer>();
	void shootAOE() {
		AOERenderer r = new AOERenderer(myInfo.projectileSpriteName, .8f);
		for (GameObject g : EntityManager._instance.ghosts) {
			if (targetFinder.contains(g.position)) {
				g.receiveDamage(myInfo.projectileDamage);
				r.addTarget(g);
			}
		}
		aoe.add(r);
		attackCooldown = myInfo.attackCooldown;
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
