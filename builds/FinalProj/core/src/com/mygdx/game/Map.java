package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Map {

    static Map _instance;

    public class Node {
        Node parent;
        Vector2 pos;
        float f,g,h; // f = g+h; g = cost to reach this node; h = guess at cost to reach goal from this node
        
        Node(Node parent, Vector2 pos, float g, float h) {
            this.parent = parent;
            this.pos = pos;
            this.g = g;
            this.h = h;
            f = g + h;
        }

        Node(Node node) {
            this.parent = node.parent;
            this.pos = node.pos;
            this.g = node.g;
            this.h = node.h;
            f = node.g + node.h;
        }
    }

    TextureAtlas atlas;
    Camera camera;
    MainGameClass game;
    Random r;

    public int levelGold = 0;
    public int totalLevelGold;
    public int treasureValue = 100;
    public float minGoldRemaining = .6f;
    public int curLev = 0;

    public int worldWidth;
    public int worldHeight;
    public ArrayList<Sprite> tiles = new ArrayList<Sprite>();
    public ArrayList<Vector2> traversableCoords = new ArrayList<Vector2>();
    public ArrayList<Vector2> traversableCoords2x2 = new ArrayList<Vector2>();
    public ArrayList<Vector2> traversableCoords3x3 = new ArrayList<Vector2>();
    public ArrayList<Vector2> spawnCoords = new ArrayList<Vector2>(); // z for blocked or not
    public ArrayList<Vector2> spawnCoords2x2 = new ArrayList<Vector2>(); // z for blocked or not
    public ArrayList<Vector2> spawnCoords3x3 = new ArrayList<Vector2>(); // z for blocked or not
    public ArrayList<Vector2> goals = new ArrayList<Vector2>();
    
    //Sprite os;
    //Sprite x2;

    Map() {
        if(_instance != null) System.out.println("Creating multiple maps");
        _instance = this;
        this.atlas = MainGameClass._instance.atlas;
        this.camera = GameScreen._instance.camera;
        this.game = MainGameClass._instance;
    }
	
	public void draw(Batch batch) {
        batch.begin();
        for(Sprite s : tiles)
            s.draw(batch);
		batch.end();
    }

    public void takeGold(int gold) {
        levelGold -= gold;
        int g = (int)(totalLevelGold * minGoldRemaining);
        if(g < 0) g = 0;
        GUI.prompt("Have " + ((int)(Map._instance.totalLevelGold * (1 - Map._instance.minGoldRemaining) + 1) - (Map._instance.totalLevelGold - Map._instance.levelGold)) + " left out of " + g  + " total");
        if ((float)levelGold/(float)totalLevelGold < minGoldRemaining) {
        	GameScreen._instance.setDefeat();
        }
    }

    public int takeGoldFromChest(Vector2 v, int gold) {
    	if (goals.contains(v)) {
    		int i = goals.indexOf(v);
    		//System.out.println("took gold from chest + " + i);
    		((Chest)EntityManager._instance.chests.get(i)).takeGold(gold);
    		return i;
    	}
    	return -1;
    }
    
    void loadLevelFromImage(String filename) {
        try {
            curLev++;
            BufferedImage level;
            level = ImageIO.read(new File(filename));
            int width = level.getWidth();
            int height = level.getHeight();
            tiles.clear();
            traversableCoords.clear();
            spawnCoords.clear();
            worldWidth = width;
            worldHeight = height;
            levelGold = 0;

            tiles = new ArrayList<Sprite>();
            traversableCoords = new ArrayList<Vector2>();
            traversableCoords2x2 = new ArrayList<Vector2>();
            traversableCoords3x3 = new ArrayList<Vector2>();
            spawnCoords = new ArrayList<Vector2>(); // z for blocked or not
            spawnCoords2x2 = new ArrayList<Vector2>(); // z for blocked or not
            spawnCoords3x3 = new ArrayList<Vector2>(); // z for blocked or not
            goals = new ArrayList<Vector2>();
            r = new Random(System.currentTimeMillis());

            // Buffered Image coordinates start at 0,0 in the top left corner. Gdx 0,0 is in bottom left.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(level.getRGB(x, height - y - 1)); // height-y translates to 0,0 in bot left
                    Sprite s = null;
                    if (c.equals(Color.BLACK)) {
                        if(curLev >= 1 || curLev == 4) {
                            s = atlas.createSprite("dirt");
                        }
                        if(curLev == 2) {
                            if(r.nextInt(100) <= 98) s = atlas.createSprite("dirt");
                            else s = atlas.createSprite("mud");
                        }
                        if(curLev == 3) {
                            s = atlas.createSprite("sand");
                        }
                        if(curLev == 5) {
                            s = atlas.createSprite("sand");
                        }

                        //1x1 traversable tile/spawn
                        traversableCoords.add(new Vector2(x, y));
                        if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
                            spawnCoords.add(new Vector2(x, y));

                        //2x2 traversable tile/spawn
                        if(x + 1 < width && y + 1 < height) {
                            boolean x22 = true;
                            for(int yi = y; yi <= y + 1; yi++)
                                for(int xi = x; xi <= x + 1; xi++)
                                    if(!(new Color(level.getRGB(xi, height - yi - 1))).equals(Color.BLACK))
                                        x22 = false;
                            if(x22) traversableCoords2x2.add(new Vector2(x, y));
                            if(x22 && (x == 0 || x == width - 2 || y == 0 || y == height - 2))
                                spawnCoords2x2.add(new Vector2(x, y));
                        }

                        //3x3 traversable tile/spawn
                        if(x + 2 < width && y + 2 < height) {
                            boolean x33 = true;
                            for(int yi = y; yi <= y + 2; yi++)
                                for(int xi = x; xi <= x + 2; xi++)
                                    if(!new Color(level.getRGB(xi, height - yi - 1)).equals(Color.BLACK))
                                        x33 = false;
                            if(x33) traversableCoords3x3.add(new Vector2(x,y));
                            if(x33 && (x == 0 || x + 3 == width || y == 0 || y + 3 == height))
                                spawnCoords3x3.add(new Vector2(x,y));
                        }
                    } else if (c.equals(Color.YELLOW)) {
                        s = atlas.createSprite("dirt");
                        Vector2 goal = new Vector2(x, y);
                        goals.add(goal);
                        //goals.add()
                        levelGold += treasureValue;
                        traversableCoords.add(goal);
                        traversableCoords2x2.add(goal);
                        traversableCoords3x3.add(goal);

                    } else {
                        if(curLev >= 0 || curLev == 2) {
                            if(r.nextInt(100) <= 90) s = atlas.createSprite("grass");
                            else s = atlas.createSprite("tallgrass");
                        }
                        if(curLev == 3) {
                            if(r.nextInt(100) > 10) s = atlas.createSprite("grass");
                            else if(r.nextInt(100) <= 10 && r.nextInt(100) >= 5) s = atlas.createSprite("tallgrass");
                        }
                        if(curLev >= 4) {
                            if(r.nextInt(100) <= 98) s = atlas.createSprite("sand");
                            else s = atlas.createSprite("aquawater");
                        }
                    }
                    if(!s.equals(null)) {
                        s.setPosition(x * MainGameClass.PPM - camera.viewportWidth / 2, y * MainGameClass.PPM - camera.viewportHeight / 2);
                        tiles.add(s);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Vector2 v : goals) {
        	Vector2 spawn = new Vector2(v.x * MainGameClass.PPM - camera.viewportWidth / 2, v.y * MainGameClass.PPM - camera.viewportHeight / 2);
        	EntityManager._instance.createChest(spawn, treasureValue);
        }
        totalLevelGold = levelGold;
    }
    
    public Vector2 getNonZackCoords(Vector2 v) {
    	Vector2 goodVector = new Vector2(v.x * MainGameClass.PPM - camera.viewportWidth / 2, v.y * MainGameClass.PPM - camera.viewportHeight / 2);
    	return goodVector;
    }

    public ArrayList<Vector2> pathToGoal(Vector2 currPos) {
        return findShortestPath(currPos, goals, traversableCoords);
    }

    public ArrayList<Vector2> pathToGoal2x2(Vector2 currPos) {
        return findShortestPath(currPos, goals, traversableCoords2x2);
    }

    public ArrayList<Vector2> pathToGoal3x3(Vector2 currPos) {
        return findShortestPath(currPos, goals, traversableCoords3x3);
    }

    public float heuristic(Vector2 a, Vector2 b) { return (float)Math.sqrt(Math.pow(b.y - a.y, 2) + Math.pow(b.x - a.x, 2)); }

    public float gScore(Node current) { return current.g + 1; }

    public ArrayList<Vector2> findShortestPath(Vector2 currPos, ArrayList<Vector2> goals, ArrayList<Vector2> validCoords) {
        ArrayList<ArrayList<Vector2>> paths = new ArrayList<ArrayList<Vector2>>();
        ArrayList<Vector2> path = null;
        for(Vector2 g : goals) {
            ArrayList<Vector2> p = aStar4(currPos, g, validCoords);
            if(p != null)
                paths.add(p);
        }
        if(paths.size() == 0) return null;
        int min = paths.get(0).size();
        int ID = 0;
        for(ArrayList<Vector2> vl : paths) {
            if(vl.size() <= min) {
                min = vl.size();
                path = vl;
            }
        }
        return path;
    }

    public ArrayList<Vector2> reconstructPath(Node n) {
        if(n.equals(null)) return null;
        ArrayList<Vector2> path = new ArrayList<Vector2>();
        while(n != null) {
            path.add(0, n.pos);
            n = n.parent;
        }
        return path;
    }

    // Does pathfinding in four directions. Typically done in 8 or semi-8.
    public ArrayList<Vector2> aStar4(Vector2 pos, Vector2 target, ArrayList<Vector2> validCoords) {
        if(pos == null || target == null) return null;
        ArrayList<Node> open = new ArrayList<Node>();
        ArrayList<Node> closed = new ArrayList<Node>();
        open.add(new Node(null, pos, 0f, heuristic(pos, target))); // Add our pos as the starting node

        while(!open.isEmpty()) {
            int ID = 0;
            Node current = new Node(open.get(0));
            for (int i = 0; i < open.size(); i++) {
                if (open.get(i).f < current.f) {
                    current = new Node(open.get(i));
                    ID = i;
                }
            }
            if(current.pos.equals(target))
                return reconstructPath(current);
            open.remove(ID);
            closed.add(current);
            ArrayList<Node> children = new ArrayList<Node>();
            for(int i = 0; i < 4; i++) { // generate current node's children and evaluate them
                int sign = i < 2 ? 1 : -1;
                Vector2 cpos = new Vector2(i % 2 == 0 ? current.pos.x : current.pos.x + sign, i % 2 == 0 ? current.pos.y + sign : current.pos.y);
                if (!validCoords.contains(cpos)) continue;
                Node child = new Node(current, cpos, gScore(current), heuristic(cpos, target));
                boolean inClosed = false, inOpen = false;
                for(Node n : closed)
                    if(n.pos.equals(cpos)) inClosed = true;
                for(Node n : open)
                    if(n.pos.equals(cpos)) inOpen = true;
                if(inClosed) continue;
                if(!inOpen)
                    open.add(child);
            }
        }
        return null; // Failed to find path
    }
}
