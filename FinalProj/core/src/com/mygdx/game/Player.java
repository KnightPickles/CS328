package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {

	public float moveSpeed = 20f;
	public float attackCooldown = .5f; 
	public float attackCooldownRemaining = 0f;
	
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
		
		initiateAnimationTextureRegions();
		
		setBody(false, false, 0, 0);
	}
	
	@Override
	public void update() {
		checkAttack();
		movement();
		attack();

		position = body.getPosition();
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
	        MainGameClass._instance.batch.draw(currentAttackFrame, position.x - currentAttackFrame.getRegionWidth()/2, position.y - currentAttackFrame.getRegionHeight()/2);
        }
        MainGameClass._instance.batch.end();
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
		attackAnimation = new Animation(attackCooldown/attackFrames.length, attackFrames[dir.ordinal()]);
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
		} else {			
			setState(State.Move);
			
			float deltaTime = Gdx.graphics.getDeltaTime();
	        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
			moveAdd.nor();
			moveAdd = new Vector2(moveAdd.x * moveSpeed * deltaTime + position.x, moveAdd.y * moveSpeed * deltaTime + position.y);
			body.setTransform(moveAdd, 0);
		}
		stateTime += Gdx.graphics.getDeltaTime();
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
		idleAttackFrames[2][0] = MainGameClass._instance.atlas.findRegion("sword_l_idle");
		
		idleAttackFrames[3] = new TextureRegion[1];
		idleAttackFrames[3][0] = MainGameClass._instance.atlas.findRegion("sword_r_idle");
		
		setDir(Direction.Down);
	}
	
}
