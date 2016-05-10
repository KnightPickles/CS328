package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {

	public static int gold = 50;
	public int goldTemp = gold;
    public static int playerLoot = 0;

	public float moveSpeed = 20f;
	public float attackCooldown = .3f; 
	public int attackDamage = 10;
	public float attackCooldownRemaining = 0f;
	public Vector2 attackLocation = new Vector2(0,0);
	
	public int redUpgradeLevel = 0;
	public int greenUpgradeLevel = 0;
	public int blueUpgradeLevel = 0;
	
	public int weaponUpgradeLevel = 0; //upgrades at 10 20 stat points
	WeaponType weaponType = WeaponType.Sword;
	enum WeaponType {
		Sword,
		Bow,
		Staff
	}
	Sound[] swordHit = new Sound[3];
	Sound bowHit, magicHit;
	
	Animation walkAnimation;
	Animation idleAnimation;
	TextureRegion[][] walkFrames;
	TextureRegion[][] idleFrames;
	TextureRegion currentFrame;
	float stateTime = 0f;
	
	Animation attackAnimation;
	Animation attackIdleAnimation;
	TextureRegion[][] attackFrames;
	TextureRegion[][] idleAttackFrames;
	TextureRegion currentAttackFrame;
	float attackStateTime = 0f;
	
	Rectangle meleeHitBox;
	Random r;
	
	enum State {
		Idle,
		Move
	}
	State state = State.Idle;
	
	enum AttackState {
		Idle,
		Attack
	}
	AttackState attackState = AttackState.Idle;
	
	enum Direction {
		Up,
		Down,
		Left,
		Right
	}
	Direction direction = null; //Should match starting sprites direction
	
	public Player(Vector2 spawnPos) {
		sprite = MainGameClass._instance.atlas.createSprite("warrior_idle_d0");
		
		setBody(false, false, 0, 0);
		
		meleeHitBox = new Rectangle();
		meleeHitBox.setCenter(spawnPos);
		meleeHitBox.setSize(10, 10);
		
		for (int i = 0; i < 3; i++) {
			swordHit[i] = Gdx.audio.newSound(Gdx.files.internal("jab" + i + ".mp3"));
		}
		bowHit = Gdx.audio.newSound(Gdx.files.internal("bow.mp3"));
		magicHit = Gdx.audio.newSound(Gdx.files.internal("magicBall.mp3"));
		
		r = new Random();
		
		gold = 500;
        playerLoot = 0;
		
		upgradeRedLevel();
		//upgradeGreenLevel();
		//upgradeBlueLevel();
	}
	
	@Override
	public void update() {
		checkAttack();
		movement();
		attack();

		position = body.getPosition();

		if(goldTemp != gold && goldTemp < gold) {
			GUI.prompt("You gained " + (gold - goldTemp) + " gold! You now have " + gold);
			goldTemp = gold;
		}
	}
	
	@Override
    public void draw() {
        MainGameClass._instance.batch.begin();
        checkFlipCurrentFrame(); 
        if (direction == Direction.Up) { //Draw weapon first when facing up
        	int idleOffset = getIdleOffset();
        	MainGameClass._instance.batch.draw(currentAttackFrame, position.x - currentAttackFrame.getRegionWidth()/2 + idleOffset, position.y - currentAttackFrame.getRegionHeight()/2);
            MainGameClass._instance.batch.draw(currentFrame, position.x - currentFrame.getRegionWidth()/2, position.y - currentFrame.getRegionHeight()/2);
        } else {
	        MainGameClass._instance.batch.draw(currentFrame, position.x - currentFrame.getRegionWidth()/2, position.y - currentFrame.getRegionHeight()/2);
	        int offset = getWeaponOffset(); //Weapon offset
	        MainGameClass._instance.batch.draw(currentAttackFrame, position.x - currentAttackFrame.getRegionWidth()/2 + offset, position.y - currentAttackFrame.getRegionHeight()/2);
        }
        MainGameClass._instance.batch.end();
    }
	
	public int getWeaponOffset() {
		if (weaponType == WeaponType.Sword) {
			int offset = 0;
			if (attackState == AttackState.Attack) {
	        	if (direction == Direction.Right)
	        		offset = 7;
	        	else if (direction == Direction.Left)
	        		offset = -7;
	        }
	        if (attackState == AttackState.Idle && direction == Direction.Down) {
	        	offset = 6;
	        } 
	        return offset;
		}
		return 0;
	}
	
	void checkFlipCurrentFrame() {
		if (direction == Direction.Left) {
			if (!currentFrame.isFlipX()) {
				currentFrame.flip(true, false);
			}
			if (weaponType == WeaponType.Sword) {
				if (attackState == AttackState.Attack && !currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				} else if (attackState == AttackState.Idle && currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				}
			} else if (weaponType == WeaponType.Staff || weaponType == WeaponType.Bow) {
				if (attackState == AttackState.Attack && !currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				} else if (attackState == AttackState.Idle && !currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				}
			}
		}
		if (direction == Direction.Right) {
			if (currentFrame.isFlipX()) {
				currentFrame.flip(true, false);
			}
			if (weaponType == WeaponType.Sword) {
				if (attackState == AttackState.Attack && currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				} else if (attackState == AttackState.Idle && !currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				} 
			} else if (weaponType == WeaponType.Staff || weaponType == WeaponType.Bow) {
				if (attackState == AttackState.Attack && currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				} else if (attackState == AttackState.Idle && currentAttackFrame.isFlipX()) {
					currentAttackFrame.flip(true, false);
				}
			}
		}
	}
	
	int getIdleOffset() {
		int idleOffset = 0;
    	if (attackState == AttackState.Idle && weaponType == WeaponType.Sword)
    		idleOffset = 6;
    		
    	return idleOffset;
	}
	
	public void upgradeRedLevel() {
		redUpgradeLevel++;
		checkNewWeapon();
	}

	public void downgradeRedLevel() {
		redUpgradeLevel--;
		checkNewWeapon();
	}
	
	public void upgradeGreenLevel() {
		greenUpgradeLevel++;
		checkNewWeapon();
	}

	public void downgradeGreenLevel() {
		greenUpgradeLevel--;
		checkNewWeapon();
	}
	
	public void upgradeBlueLevel() {
		blueUpgradeLevel++;
		checkNewWeapon();
	}

	public void downgradeBlueLevel() {
		blueUpgradeLevel--;
		checkNewWeapon();
	}
	
	//Check if we have reached a new breaking point for a new weapon
	void checkNewWeapon() {
		if (redUpgradeLevel > blueUpgradeLevel && redUpgradeLevel > greenUpgradeLevel) { //Red/sword
			if(weaponType != WeaponType.Sword) GUI.prompt("Class changed to warrior!");
			weaponType = WeaponType.Sword;
		}
		else if (blueUpgradeLevel > redUpgradeLevel && blueUpgradeLevel > greenUpgradeLevel) { //Blue staff
			if(weaponType != WeaponType.Staff) GUI.prompt("Class changed to wizard!");
			weaponType = WeaponType.Staff;
		}
		else if (greenUpgradeLevel > redUpgradeLevel && greenUpgradeLevel > blueUpgradeLevel) { //Green bow
			if(weaponType != WeaponType.Bow) GUI.prompt("Class changed to ranger!");
			weaponType = WeaponType.Bow;
		}
		weaponUpgradeLevel = (int) Math.floor((redUpgradeLevel + blueUpgradeLevel + greenUpgradeLevel)/10f);
		updateWeaponStats();
		setWeaponSprite();
	}
	
	void updateWeaponStats() {
		if (weaponType == WeaponType.Sword) {
			moveSpeed = 20f + greenUpgradeLevel;
			attackCooldown = .38f + (blueUpgradeLevel * 0.02f);
			attackDamage = 18 + redUpgradeLevel;
		}
		else if (weaponType == WeaponType.Bow) {
			moveSpeed = 25f + greenUpgradeLevel;
			attackCooldown = .43f + (blueUpgradeLevel * 0.02f);
			attackDamage = 15 + redUpgradeLevel;
		}
		else if (weaponType == WeaponType.Staff) {
			moveSpeed = 16f + greenUpgradeLevel;
			attackCooldown = .5f + (blueUpgradeLevel * 0.02f);
            attackDamage = 20 + redUpgradeLevel;
		}
	}
	
	void setWeaponSprite() {
		if (weaponType == WeaponType.Sword) {
			initiateAnimationTextureRegions("warrior");
		}
		else if (weaponType == WeaponType.Staff) {
			initiateAnimationTextureRegions("wizard");
		}
		else {
			initiateAnimationTextureRegions("ranger");
		}
	}
	
	void checkAttack() {
		if (MyInputProcessor.rightMouseIsDown && MyInputProcessor.isPlayMode() && attackCooldownRemaining <= 0) {
			attackCooldownRemaining = attackCooldown;
			attackStateTime = 0f;
			attackState = AttackState.Attack;
			if (weaponType == WeaponType.Staff) {
				attackLocation = new Vector2(MyInputProcessor.rightMouseLocation.x, MyInputProcessor.rightMouseLocation.y);
			}
		}
	}
	
	void setState(State newState) {
		if (newState == state) 
			return;
		
		stateTime = 0f;
		state = newState;
	}
	
	void playAttackSound() {
		if (weaponType == WeaponType.Sword) {
			int rand = r.nextInt(3);
			long i = swordHit[rand].play();
			swordHit[rand].setVolume(i, .3f * GameScreen.volumeModifier);
		} else if (weaponType == WeaponType.Bow) {
			long i = bowHit.play();
			bowHit.setVolume(i, .3f * GameScreen.volumeModifier);
		} else if (weaponType == WeaponType.Staff) {
			long i = magicHit.play();
			magicHit.setVolume(i, .6f * GameScreen.volumeModifier);
		}
	}
	
	void setDir(Direction dir) {
		if (dir == direction)
			return;
		
		stateTime = 0f;
		walkAnimation = new Animation(0.25f, walkFrames[dir.ordinal()]);
		idleAnimation = new Animation(0.25f, idleFrames[dir.ordinal()]);
		attackAnimation = new Animation(attackCooldown/attackFrames[dir.ordinal()].length, attackFrames[dir.ordinal()]);
		attackIdleAnimation = new Animation(0.25f, idleAttackFrames[dir.ordinal()]);
		direction = dir;
	}
	
	void attack() {
		if (attackCooldownRemaining <= 0) {
			attackState = AttackState.Idle;
		}
		
		if (attackState == AttackState.Attack) {
			currentAttackFrame = attackAnimation.getKeyFrame(attackStateTime, false);
		} else if (attackState == AttackState.Idle) {
			currentAttackFrame = attackIdleAnimation.getKeyFrame(attackStateTime, true);
		}

		float delta = Gdx.graphics.getDeltaTime();
		attackCooldownRemaining -= delta;
		attackStateTime += delta;
		
		if (attackCooldownRemaining <= 0 && attackState == AttackState.Attack) { //If we just finished attacking
			if (weaponType == WeaponType.Sword) {
				updateMeleeHitBox();
				
				boolean hit = false;
				for (GameObject g : EntityManager._instance.ghosts) {
					if (meleeHitBox.overlaps(g.sprite.getBoundingRectangle())) {
						g.receiveDamage(attackDamage);
						hit = true;
					}
				}
				if (hit) 
					playAttackSound();
			}
			else if (weaponType == WeaponType.Bow) {					
				Vector2 arrowSpawn = new Vector2(position.x - sprite.getWidth()/2f, position.y - sprite.getHeight()/2f);
				if (direction == Direction.Left)
					arrowSpawn.x -= sprite.getWidth();
				else if (direction == Direction.Right)
					arrowSpawn.x += sprite.getWidth();
				else if (direction == Direction.Up)
					arrowSpawn.y += sprite.getHeight();
				else if (direction == Direction.Down)
					arrowSpawn.y -= sprite.getHeight();
				EntityManager._instance.spawnArrow(weaponUpgradeLevel, direction, attackDamage, arrowSpawn);
				playAttackSound();
			} 
			else if (weaponType == WeaponType.Staff) {
				playAttackSound();
				Vector2 magicSpawn = new Vector2(position.x - sprite.getWidth()/2f, position.y - sprite.getHeight()/2f);
				EntityManager._instance.spawnMagicBall(weaponUpgradeLevel, attackLocation, attackDamage, magicSpawn);
			}
		}
	}
	
	void movement() {
		Vector2 moveAdd = new Vector2(0, 0);
		if (MyInputProcessor.wIsDown) 
			moveAdd.y += 1;
		if (MyInputProcessor.aIsDown)
			moveAdd.x -= 1;
		if (MyInputProcessor.dIsDown)
			moveAdd.x += 1;
		if (MyInputProcessor.sIsDown)
			moveAdd.y -= 1;
		
		if (moveAdd.x == 1)
			setDir(Direction.Right);
		else if (moveAdd.x == -1)
			setDir(Direction.Left);
		else if (moveAdd.y == 1)
			setDir(Direction.Up);
		else if (moveAdd.y == -1)
			setDir(Direction.Down);
		
		if (moveAdd.isZero()) {
			setState(State.Idle);
			currentFrame = idleAnimation.getKeyFrame(stateTime, true);
			body.setLinearVelocity(Vector2.Zero);
		} else {			
			setState(State.Move);
			
			float deltaTime = Gdx.graphics.getDeltaTime();
	        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
			moveAdd.nor();
//			moveAdd = new Vector2(moveAdd.x * moveSpeed * deltaTime + position.x, moveAdd.y * moveSpeed * deltaTime + position.y);
			moveAdd = new Vector2(moveAdd.x * moveSpeed * deltaTime * 100, moveAdd.y * moveSpeed * deltaTime * 100);
//			body.setTransform(moveAdd, 0);
			body.setLinearVelocity(moveAdd);
		}
		stateTime += Gdx.graphics.getDeltaTime();
	}

	void updateMeleeHitBox() {
		if (direction == Direction.Up)
			meleeHitBox.setCenter(new Vector2(position.x, position.y + 7));
		else if (direction == Direction.Down)
			meleeHitBox.setCenter(new Vector2(position.x, position.y - 7));
		else if (direction == Direction.Left)
			meleeHitBox.setCenter(new Vector2(position.x - 7, position.y));
		else if (direction == Direction.Right)
			meleeHitBox.setCenter(new Vector2(position.x + 7, position.y));
	}
	
	void initiateAnimationTextureRegions(String type) { //wizard warrior ranger
		//Move/player textures
		walkFrames = new TextureRegion[4][];
		
		walkFrames[0] = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			walkFrames[0][i] = MainGameClass._instance.atlas.findRegion(type + "_walk_u" + i);
		}
		
		walkFrames[1] = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			walkFrames[1][i] = MainGameClass._instance.atlas.findRegion(type + "_walk_d" + i);
		}
		
		walkFrames[2] = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			walkFrames[2][i] = MainGameClass._instance.atlas.findRegion(type + "_walk_r" + i);
		}
		
		walkFrames[3] = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			walkFrames[3][i] = MainGameClass._instance.atlas.findRegion(type + "_walk_r" + i);
		}
		
		idleFrames = new TextureRegion[4][1];
		
		idleFrames[0] = new TextureRegion[1];
		idleFrames[0][0] = MainGameClass._instance.atlas.findRegion(type + "_idle_u0");
		
		idleFrames[1] = new TextureRegion[1];
		idleFrames[1][0] = MainGameClass._instance.atlas.findRegion(type + "_idle_d0");
		
		idleFrames[2] = new TextureRegion[1];
		idleFrames[2][0] = MainGameClass._instance.atlas.findRegion(type + "_idle_r0");
		
		idleFrames[3] = new TextureRegion[1];
		idleFrames[3][0] = MainGameClass._instance.atlas.findRegion(type + "_idle_r0");
		
		//Weapon/attack textures
		attackFrames = new TextureRegion[4][];
		
		int numAttackFrames = 3;
		String weaponType = "sword";
		if (type == "wizard") {
			weaponType = "staff";
			numAttackFrames = 2;
		}
		else if (type == "ranger") {
			weaponType = "bow";
			numAttackFrames = 2;
		}
		
		attackFrames[0] = new TextureRegion[numAttackFrames];
		for (int i = 0; i < numAttackFrames; i++) {
			attackFrames[0][i] = MainGameClass._instance.atlas.findRegion(weaponType + "_u", i);
		}
		
		attackFrames[1] = new TextureRegion[numAttackFrames];
		for (int i = 0; i < numAttackFrames; i++) {
			attackFrames[1][i] = MainGameClass._instance.atlas.findRegion(weaponType + "_d", i);
		}
		
		attackFrames[2] = new TextureRegion[numAttackFrames];
		for (int i = 0; i < numAttackFrames; i++) {
			attackFrames[2][i] = MainGameClass._instance.atlas.findRegion(weaponType + "_r", i);
		}
		
		attackFrames[3] = new TextureRegion[numAttackFrames];
		for (int i = 0; i < numAttackFrames; i++) {
			attackFrames[3][i] = MainGameClass._instance.atlas.findRegion(weaponType + "_r", i);
		}
		
		idleAttackFrames = new TextureRegion[4][1];
		
		idleAttackFrames[0] = new TextureRegion[1];
		idleAttackFrames[0][0] = MainGameClass._instance.atlas.findRegion(weaponType + "_u_idle");
		
		idleAttackFrames[1] = new TextureRegion[1];
		idleAttackFrames[1][0] = MainGameClass._instance.atlas.findRegion(weaponType + "_d_idle");
		
		idleAttackFrames[2] = new TextureRegion[1];
		idleAttackFrames[2][0] = MainGameClass._instance.atlas.findRegion(weaponType + "_r_idle");
		
		idleAttackFrames[3] = new TextureRegion[1];
		idleAttackFrames[3][0] = MainGameClass._instance.atlas.findRegion(weaponType + "_r_idle");
		
		setDir(Direction.Down);
	}
	
}
