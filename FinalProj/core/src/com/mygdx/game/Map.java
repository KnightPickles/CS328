package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.sun.javaws.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Map {

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
    //String level;

    public int worldWidth;
    public int worldHeight;
    public ArrayList<Sprite> tiles = new ArrayList<Sprite>();
    public ArrayList<Vector2> traversableCoords = new ArrayList<Vector2>();
    public ArrayList<Vector2> spawnCoords = new ArrayList<Vector2>(); // z for blocked or not
    public Vector2 goal;

    public ArrayList<Vector2> path = new ArrayList<Vector2>();
    Sprite os;

    Map(String level, MainGameClass game, TextureAtlas atlas, Camera camera) {
        this.atlas = atlas;
        this.camera = camera;
        this.game = game;
        loadLevelFromImage(level);
        os = atlas.createSprite("blue_indicator");
    }
	
	public void draw(Batch batch) {
        Random rand = new Random(System.nanoTime());
        //path = aStar4(new Vector2(rand.nextInt(50), rand.nextInt(50)), new Vector2(rand.nextInt(50),rand.nextInt(50)));
        //path = aStar4(traversableCoords.get(rand.nextInt(traversableCoords.size())), traversableCoords.get(rand.nextInt(traversableCoords.size())));
        path = pathToGoal(spawnCoords.get(rand.nextInt(spawnCoords.size())));


		batch.begin();
        for(Sprite s : tiles)
            s.draw(batch);
        if(path != null && path.size() > 0) {
            for(Vector2 v : path) {
                os.setPosition(v.x * MainGameClass.PPM - camera.viewportWidth / 2, v.y * MainGameClass.PPM - camera.viewportHeight / 2);
                os.draw(batch);
            }
        }
		batch.end();
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
    }

    /* Returns the next position to move in with respect to the goal as a Vec3 (x,y,0)
     * otherwise it returns the position of the fastest path's blocking barrier as a
     * Vec3(x,y,1)
     */
    public Vector3 nextNodeToGoal(Vector2 fromPos) {
        return null;
    }

    void loadLevelFromImage(String filename) {
        try {
            BufferedImage level;
            level = ImageIO.read(new File(filename));
            int width = level.getWidth();
            int height = level.getHeight();
            tiles.clear();
            traversableCoords.clear();
            spawnCoords.clear();
            goal = new Vector2(width / 2, height / 2);

            // Buffered Image coordinates start at 0,0 in the top left corner. Gdx 0,0 is in bottom left.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(level.getRGB(x, height - y - 1)); // height-y translates to 0,0 in bot left
                    Sprite s = null;
                    if (c.equals(Color.BLACK)) {
                        s = atlas.createSprite("dirt");
                        traversableCoords.add(new Vector2(x, y));
                        if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
                            spawnCoords.add(new Vector2(x, y));
                    } else if (c.equals(Color.YELLOW)) {
                        s = atlas.createSprite("sand");
                        goal = new Vector2(x, y);
                    } else {
                        s = atlas.createSprite("grass");
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
        traversableCoords.add(goal);
    }

    public ArrayList<Vector2> pathToGoal(Vector2 currPos) {
        return aStar4(currPos, goal);
    }

    public float dist(Vector2 a, Vector2 b) {
        return (float)Math.sqrt(Math.pow(b.y - a.y, 2) + Math.pow(b.x - a.x, 2));
    }

    public float heuristic(Vector2 pos, Vector2 target) {
        return dist(pos,target);
    }

    public float gScore(Node current, Vector2 nextPos) {
        return current.g + dist(current.pos, nextPos);
    }

    public float gScore(Node current) {
        return current.g + 1;
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

    public ArrayList<Vector2> aStar4(Vector2 pos, Vector2 target) {
        if(pos.equals(null) || target.equals(null)) return null;
        //Sprite os = atlas.createSprite("blue_indicator");
        //Sprite cs = atlas.createSprite("red_indicator");
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
                if (!traversableCoords.contains(cpos)) continue;
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
