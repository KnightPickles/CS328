package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {

	public static int gold = 0;
	public int goldTemp = gold;

	public float moveSpeed = 20f;
	public float attackCooldown = .3f; 
	public float attackCooldownRemaining = 0f;
	public int attackDamage = 10;
	
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
		
		initiateAnimationTextureRegions();
	}
	
	@Override
	public void update() {
		checkAttack();
		movement();
		attack();

		position = body.getPosition();

		if(goldTemp != gold && goldTemp < gold) {
			System.out.println("You gained " + (gold - goldTemp) + " gold! You now have " + gold);
			goldTemp = gold;
		}
	}
	
	@Override
    public void draw() {
        MainGameClass._instance.batch.begin();
        checkFlipCurrentFrame(); 
        if (direction == Direction.Up) { //Draw weapon first when facing up
        	MainGameClass._instance.batch.draw(currentAttackFrame, position.x - currentAttackFrame.getRegionWidth()/2, position.y - currentAttackFrame.getRegionHeight()/2);
            MainGameClass._instance.batch.draw(currentFrame, position.x - currentFrame.getRegionWidth()/2, position.y - currentFrame.getRegionHeight()/2);
        } else {
	        MainGameClass._instance.batch.draw(currentFrame, position.x - currentFrame.getRegionWidth()/2, position.y - currentFrame.getRegionHeight()/2);
	        int offset = 0; //Weapon offset
	        if (attackState == AttackState.Attack) {
	        	if (direction == Direction.Right)
	        		offset = 7;
	        	else if (direction == Direction.Left)
	        		offset = -7;
	        }
	        if (attackState == AttackState.Idle && direction == Direction.Down) {
	        	offset = 6;
	        }
	        MainGameClass._instance.batch.draw(currentAttackFrame, position.x - currentAttackFrame.getRegionWidth()/2 + offset, position.y - currentAttackFrame.getRegionHeight()/2);
        }
        MainGameClass._instance.batch.end();
    }
	
	public void upgradeRedLevel() {
		redUpgradeLevel++;
		checkNewWeapon();
	}
	
	public void upgradeGreenLevel() {
		greenUpgradeLevel++;
		checkNewWeapon();
	}
	
	public void upgradeBlueLevel() {
		blueUpgradeLevel++;
		checkNewWeapon();
	}
	
	//Check if we have reached a new breaking point for a new weapon
	void checkNewWeapon() {
		if (redUpgradeLevel > blueUpgradeLevel && redUpgradeLevel > greenUpgradeLevel) { //Red/sword
			weaponType = WeaponType.Sword;
		}
		else if (blueUpgradeLevel > redUpgradeLevel && blueUpgradeLevel > greenUpgradeLevel) { //Blue staff
			weaponType = WeaponType.Staff;
		}
		else if (greenUpgradeLevel > redUpgradeLevel && greenUpgradeLevel > blueUpgradeLevel) { //Green bow
			weaponType = WeaponType.Bow;
		}
		weaponUpgradeLevel = (int) Math.floor((redUpgradeLevel + blueUpgradeLevel + greenUpgradeLevel)/10f);
		setWeaponSprite();
	}
	
	//TODO: make new animations for the weapon types and switch between them here
	void setWeaponSprite() {
		if (weaponType == WeaponType.Sword) {
			//Set animation to sword[weaponUpgradeLevel]
		}
		else if (weaponType == WeaponType.Staff) {
			
		}
		else {
			
		}
	}
	
	void checkAttack() {
		if (MyInputProcessor.rightMouseIsDown && MyInputProcessor.isPlayMode() && attackCooldownRemaining <= 0) {
			attackCooldownRemaining = attackCooldown;
			attackStateTime = 0f;
			attackState = AttackState.Attack;
		}
	}
	
	void checkFlipCurrentFrame() {
		if (direction == Direction.Left) {
			if (!currentFrame.isFlipX()) {
				currentFrame.flip(true, false);
			}
		}
		if (direction == Direction.Right) {
			if (currentFrame.isFlipX()) {
				currentFrame.flip(true, false);
			}
		}
	}
	
	void setState(State newState) {
		if (newState == state) 
			return;
		
		stateTime = 0f;
		state = newState;
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
			updateMeleeHitBox();
			
			for (GameObject g : EntityManager._instance.ghosts) {
				if (meleeHitBox.overlaps(g.sprite.getBoundingRectangle())) {
					g.receiveDamage(attackDamage);
				}
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
	
	void initiateAnimationTextureRegions() {
		//Move/player textures
		walkFrames = new TextureRegion[4][];
		
		walkFrames[0] = new TextureRegion[2];
		walkFrames[0][0] = MainGameClass._instance.atlas.findRegion("warrior_walk_u0");
		walkFrames[0][1] = MainGameClass._instance.atlas.findRegion("warrior_walk_u1");
		
		walkFrames[1] = new TextureRegion[2];
		walkFrames[1][0] = MainGameClass._instance.atlas.findRegion("warrior_walk_d0");
		walkFrames[1][1] = MainGameClass._instance.atlas.findRegion("warrior_walk_d1");
		
		walkFrames[2] = new TextureRegion[2];
		walkFrames[2][0] = MainGameClass._instance.atlas.findRegion("warrior_walk_r0");
		walkFrames[2][1] = MainGameClass._instance.atlas.findRegion("warrior_walk_r1");
		
		walkFrames[3] = new TextureRegion[2];
		walkFrames[3][0] = MainGameClass._instance.atlas.findRegion("warrior_walk_r0");
		walkFrames[3][1] = MainGameClass._instance.atlas.findRegion("warrior_walk_r1");
		
		idleFrames = new TextureRegion[4][1];
		
		idleFrames[0] = new TextureRegion[1];
		idleFrames[0][0] = MainGameClass._instance.atlas.findRegion("warrior_idle_u0");
		
		idleFrames[1] = new TextureRegion[1];
		idleFrames[1][0] = MainGameClass._instance.atlas.findRegion("warrior_idle_d0");
		
		idleFrames[2] = new TextureRegion[1];
		idleFrames[2][0] = MainGameClass._instance.atlas.findRegion("warrior_idle_r0");
		
		idleFrames[3] = new TextureRegion[1];
		idleFrames[3][0] = MainGameClass._instance.atlas.findRegion("warrior_idle_r0");
		
		//Weapon/attack textures
		attackFrames = new TextureRegion[4][];
		
		attackFrames[0] = new TextureRegion[3];
		attackFrames[0][0] = MainGameClass._instance.atlas.findRegion("sword_u", 0);
		attackFrames[0][1] = MainGameClass._instance.atlas.findRegion("sword_u", 1);
		attackFrames[0][2] = MainGameClass._instance.atlas.findRegion("sword_u", 2);
		
		attackFrames[1] = new TextureRegion[3];
		attackFrames[1][0] = MainGameClass._instance.atlas.findRegion("sword_d", 0);
		attackFrames[1][1] = MainGameClass._instance.atlas.findRegion("sword_d", 1);
		attackFrames[1][2] = MainGameClass._instance.atlas.findRegion("sword_d", 2);
		
		attackFrames[2] = new TextureRegion[3];
		attackFrames[2][0] = MainGameClass._instance.atlas.findRegion("sword_l", 0);
		attackFrames[2][1] = MainGameClass._instance.atlas.findRegion("sword_l", 1);
		attackFrames[2][2] = MainGameClass._instance.atlas.findRegion("sword_l", 2);
		
		attackFrames[3] = new TextureRegion[3];
		attackFrames[3][0] = MainGameClass._instance.atlas.findRegion("sword_r", 0);
		attackFrames[3][1] = MainGameClass._instance.atlas.findRegion("sword_r", 1);
		attackFrames[3][2] = MainGameClass._instance.atlas.findRegion("sword_r", 2);
		
		idleAttackFrames = new TextureRegion[4][1];
		
		idleAttackFrames[0] = new TextureRegion[1];
		idleAttackFrames[0][0] = MainGameClass._instance.atlas.findRegion("sword_u_idle");
		
		idleAttackFrames[1] = new TextureRegion[1];
		idleAttackFrames[1][0] = MainGameClass._instance.atlas.findRegion("sword_d_idle");
		
		idleAttackFrames[2] = new TextureRegion[1];
		idleAttackFrames[2][0] = MainGameClass._instance.atlas.findRegion("sword_r_idle");
		
		idleAttackFrames[3] = new TextureRegion[1];
		idleAttackFrames[3][0] = MainGameClass._instance.atlas.findRegion("sword_l_idle");
		
		setDir(Direction.Down);
	}
	
}
