package edu.cs328;

public class UnitStats {
	
	boolean playerControlled;
	float moveSpeed;
	float attackDistance;
	float attackCooldown;
	int attackDamage;
	int maxHealth;
	int health;
	
	UnitStats(boolean pc, float moveSpeed, float attackDist, float attackCD, int attackDamage, int maxHealth) {
		this.playerControlled = pc;
		this.moveSpeed = moveSpeed;
		this.attackDamage = attackDamage;
		this.attackDistance = attackDist;
		this.attackCooldown = attackCD;
		this.maxHealth = maxHealth;
		
		this.health = this.maxHealth;
	}
}
