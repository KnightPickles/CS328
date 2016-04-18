package com.mygdx.game;

public class TurretInfo {

	public String turretName;
	
	//Basic stuff
	public String spriteName;
	public float range;
	public float attackCooldown;
	public int cost;
	
	//Upgrade stuff
	public int redLevel;
	public int greenLevel;
	public int blueLevel;
	
	//Tracking stuff 
	public String rotateSpriteName; //If there's a rotating sprite turret "head" (like a cannon barrel that would point at targets)
	public boolean trackTarget; //Does the turret have a rotating sprite to track to the target (like a cannon barrel should point at targets)
	public float trackingSpeed; //How fast does it rotate to track (will probably be clamped to make sure it can always track the fastest enemy targets)
	
	//Projectile stuff
	public enum ProjectileType {
		Ballistic, //e.g. cannonball
		Laser, //e.g. railgun
		AOE, //e.g. pounds the ground slowing enemies around the turret
		Buff, //e.g. gives an aura to nearby turrets buffing attack speed
	}
	public ProjectileType projectileType;
	public String projectileSpriteName; //Name of sprite used for any projectile type
	public int projectileDamage;
	public float projectileSpeed;
	
}
