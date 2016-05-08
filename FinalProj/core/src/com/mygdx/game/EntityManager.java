package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Player.Direction;

public class EntityManager {

	public static EntityManager _instance;

	public List<GameObject> ghosts = new ArrayList<GameObject>();
	public List<GameObject> turrets = new ArrayList<GameObject>();
	public List<GameObject> projectiles = new ArrayList<GameObject>();
	public List<GameObject> chests = new ArrayList<GameObject>();
	public List<GameObject> gold = new ArrayList<GameObject>();
	public Player player;
	
	HashMap<String, TurretInfo> turretTable = new HashMap<String, TurretInfo>();

	public List<GameObject> objectsToDelete = new ArrayList<GameObject>(); //Objects queued for removal

    EntityManager() {
    	if (_instance != null) System.out.println("Creating multiple entity managers");
        _instance = this;
        
        createTurretInfo();
    
        spawnPlayer(new Vector2(50, 50));

        //spawnTurret("red0", new Vector2(40, 40));
        //spawnGhost(Ghost.Size.X11, Ghost.Color.RED);
    }
    
    public void spawnPlayer(Vector2 position) {
    	player = new Player(position);
    }
    
    public void spawnGhost(Ghost.Size size, Ghost.Color color) {
    	Ghost ghost = new Ghost(size, color);
    	ghosts.add(ghost);
    }

    public boolean buildTurret(String turretName, Vector2 position) {
        if(Player.gold >= turretTable.get(turretName).cost) {
            System.out.println("Spent " + turretTable.get(turretName).cost + " gold on a " + turretName);
            Player.gold -= turretTable.get(turretName).cost;
            Turret turret = new Turret(turretTable.get(turretName), position);
            turrets.add(turret);
            return true;
        } else {
            System.out.println(Player.gold + " is not enough to buy a ballista. It costs " + turretTable.get("red0").cost + ".");
            return false;
        }
    }
    
    public void spawnTurret(String turretName, Vector2 position) {
    	Turret turret = new Turret(turretTable.get(turretName), position);
    	turrets.add(turret);
    }
    
    public void spawnProjectile(String spriteName, GameObject target, int damage, float speed, Vector2 spawnPosition) {
    	Projectile p = new Projectile(spriteName, target, damage, speed, spawnPosition);
    	projectiles.add(p);
    }
    
    public void spawnArrow(int upgradeLevel, Direction direction, int damage, Vector2 spawnPosition) {
    	Vector2 dir = new Vector2(0, 0);
    	if (direction == Direction.Down)
    		dir = new Vector2(0, -1);
    	else if (direction == Direction.Up)
    		dir = new Vector2(0, 1);
    	else if (direction == Direction.Left)
    		dir = new Vector2(-1, 0);
    	else if (direction == Direction.Right)
    		dir = new Vector2(1, 0);
    	
    	Arrow arrow = new Arrow("arrow", dir, damage, upgradeLevel, spawnPosition);
    	projectiles.add(arrow);
    }
    
    public void spawnMagicBall(int weaponUpgradeLevel, Vector2 attackLocation, int attackDamage, Vector2 magicSpawn) {
    	
    	MagicBall magicBall = new MagicBall("ball", attackLocation, attackDamage, weaponUpgradeLevel, magicSpawn);
    	projectiles.add(magicBall);
    }
	
    public void createChest(Vector2 position, int value) {
    	Chest c = new Chest(position, value);
    	chests.add(c);
    }
    
    public void createGoldPile(int goldVal, int chestIndex, Vector2 chestPosition, Vector2 pos) {
    	GoldPile gp = new GoldPile(goldVal, chestIndex, chestPosition, pos);
    	gold.add(gp);
    }
    
    public void update(float delta) {
    	//Update ghosts
    	for (GameObject g : ghosts) {
    		((Ghost)g).update();
    	}
    	
    	for (GameObject g : turrets) {
    		((Turret)g).update();
    	}
    	
    	for (GameObject g : projectiles) {
    		if (g instanceof Projectile)
    			((Projectile)g).update();
    		else if (g instanceof Arrow)
    			((Arrow)g).update();
    		else if (g instanceof MagicBall)
    			((MagicBall)g).update();
    	}
    	for (GameObject g : chests) {
    		g.update();
    	}
    	for (GameObject g : gold) {
    		g.update();
    	}
    	
    	if (player != null)
    		player.update();

    	removeQueuedEntities(); 
    	draw();
    }
    
    public void startNewLevel() {
    	for (GameObject t : turrets) {
    		t.killUnit();
    		player.gold += ((Turret)t).goldValue;
    	}
    	for (GameObject p : projectiles) {
    		p.killUnit();
    	}
    	for (GameObject g : ghosts) {
    		g.killUnit();
    	}
    	for (GameObject g : chests) {
    		g.killUnit();
    	}
    	for (GameObject g : gold) {
    		g.killUnit();
    	}
    }
    
    void draw() {    	
    	//Draw ghosts
    	for (GameObject g : ghosts) {
    		((Ghost)g).draw();
    	}
    	
    	for (GameObject g : turrets) {
    		((Turret)g).draw();
    	}
    	
    	for (GameObject g : projectiles) {
    		if (g instanceof Projectile)
    			((Projectile)g).draw();
    		else if (g instanceof Arrow)
    			((Arrow)g).draw();
    		else if (g instanceof MagicBall)
    			((MagicBall)g).draw();
    	}
    	for (GameObject g : chests) {
    		g.draw();
    	}
    	for (GameObject g : gold) {
    		g.draw();
    	}
    	
    	if (player != null)
    		player.draw();
    }
    
    public List<GameObject> getSelectables() {
    	List<GameObject> selectables = new ArrayList<GameObject>();
    	
    	//Not selectable until we decide or have a reason for them to be selectable
    	/*for (GameObject g : ghosts) {
    		selectables.add(g);
    	}*/
    	
    	for (GameObject g : turrets) {
    		selectables.add(g);
    	}
    	
    	return selectables;
    }
    
    void removeQueuedEntities() {
    	for (GameObject e : objectsToDelete) {
	    	if (e instanceof Ghost) {
	    		if (ghosts.contains(e)) {
	    			ghosts.remove(e);
	    		}
	    	}
	    	if (e instanceof Turret) {
	    		if (turrets.contains(e)) {
	    			turrets.remove(e);
	    		}
	    	}
	    	if (e instanceof Projectile) {
	    		if (projectiles.contains(e))
	    			projectiles.remove(e);
	    	}
	    	if (e instanceof Arrow) {
	    		if (projectiles.contains(e))
	    			projectiles.remove(e);
	    	}
	    	if (e instanceof MagicBall) {
	    		if (projectiles.contains(e))
	    			projectiles.remove(e);
	    	}
	    	if (e instanceof GoldPile) {
	    		if (gold.contains(e))
	    			gold.remove(e);
	    	}
	    	if (e instanceof Chest) {
	    		if (chests.contains(e))
	    			chests.remove(e);
	    	}
    	}
    	objectsToDelete.clear();
    }
    
    public void removeEntity(GameObject e) {
    	objectsToDelete.add(e);
    }
    
    void createTurretInfo() {
    	//Red 0
    	TurretInfo info = new TurretInfo();
    	info.turretName = "red0";
    	info.spriteName = "ballista_base";
    	info.trackTarget = true;
    	info.rotateSpriteName = "ballista_head";
    	info.attackCooldown = 1f;
    	info.projectileSpriteName = "ballista_projectile";
    	info.range = 80;
    	info.trackingSpeed = 60f;
    	info.projectileType = TurretInfo.ProjectileType.Ballistic;
    	info.projectileDamage = 10;
    	info.projectileSpeed = 80f;
        info.cost = 25;
        info.redLevel = 1;
    	
    	turretTable.put(info.turretName, info);
    }
}
