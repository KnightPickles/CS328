package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class EntityManager {

	public static EntityManager _instance;

	public List<GameObject> ghosts = new ArrayList<GameObject>();
	public List<GameObject> turrets = new ArrayList<GameObject>();
	public List<GameObject> projectiles = new ArrayList<GameObject>();
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

    public void buildTurret(String turretName, Vector2 position) {
        if(Player.gold >= turretTable.get("red0").cost) {
            System.out.println("Spent " + turretTable.get("red0").cost + " gold on a ballista");
            Player.gold -= turretTable.get("red0").cost;
            Turret turret = new Turret(turretTable.get(turretName), position);
            turrets.add(turret);
        } else System.out.println(Player.gold + " is not enough to buy a ballista. It costs " + turretTable.get("red0").cost + ".");
    }
    
    public void spawnTurret(String turretName, Vector2 position) {
    	Turret turret = new Turret(turretTable.get(turretName), position);
    	turrets.add(turret);
    }
    
    public void spawnProjectile(String spriteName, GameObject target, int damage, float speed, Vector2 spawnPosition) {
    	Projectile p = new Projectile(spriteName, target, damage, speed, spawnPosition);
    	projectiles.add(p);
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
    		((Projectile)g).update();
    	}
    	
    	if (player != null)
    		player.update();

    	removeQueuedEntities(); 
    	draw();
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
    		((Projectile)g).draw();
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
    	
    	turretTable.put(info.turretName, info);
    }
}
