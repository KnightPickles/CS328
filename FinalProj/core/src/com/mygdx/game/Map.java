package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

public class Map {

    public class Node {
        int Id;
        Node parent;
        Vector2 pos;
        float f,g,h; // f = g+h; g = cost to reach this node; h = guess at cost to reach goal from this node
        
        Node(int Id, Node parent, Vector2 pos, float g, float h) {
            this.Id = Id;
            this.parent = parent;
            this.pos = pos;
            this.g = g;
            this.h = h;
            f = g + h;
        }

        Node(Node node) {
            this.Id = node.Id;
            this.parent = node.parent;
            this.pos = node.pos;
            this.g = node.g;
            this.h = node.h;
            f = g + h;
        }
    }

    TextureAtlas atlas;
    Camera camera;
    //String level;

    public int worldWidth;
    public int worldHeight;
    public ArrayList<Sprite> tiles = new ArrayList<Sprite>();
    public ArrayList<Vector3> traversableCoords = new ArrayList<Vector3>();
    public ArrayList<Vector3> spawnCoords = new ArrayList<Vector3>(); // z for blocked or not
    public Vector2 goal;

    Map(String level, TextureAtlas atlas, Camera camera) {
        this.atlas = atlas;
        this.camera = camera;
        loadLevelFromImage(level);
    }
	
	public void draw(Batch batch) {
		batch.begin();
        for(Sprite s : tiles)
            s.draw(batch);
		batch.end();
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
                        traversableCoords.add(new Vector3(x, y, 0));
                        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                            /* // for pulling the spawn point offscreen to allow enemies to enter from offscreen
                            if(x == 0) x -= 1;
                            else x += 1;
                            if(y == 0) y -= 1;
                            else y += 1;*/
                            spawnCoords.add(new Vector3(x, y, 0));
                        }
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
        System.out.println("target " + new Vector2(10,10));
        aStar4(new Vector2(0,0), new Vector2(10,10));
    }

    public float dist(Vector2 a, Vector2 b) {
        return (float)Math.sqrt(Math.pow(b.y - a.y, 2) + Math.pow(b.x - a.x, 2));
    }

    public ArrayList<Vector2> aStar4(Vector2 pos, Vector2 goal) {
        ArrayList<Node> open = new ArrayList<Node>();
        ArrayList<Node> closed = new ArrayList<Node>();
        open.add(new Node(0, null, pos, 0f, dist(pos, goal))); // Add our pos as the starting node

        while(!open.isEmpty()) {
            Node focus = open.get(0);
            for(Node n : open) if(focus.f > n.f) focus = new Node(n); // get our most promising node
            open.remove(focus.Id);
            int id = open.size();
            ArrayList<Node> children = new ArrayList<Node>();
            for(int i = 1; i <= 4; i++) { // generate focus node's children and evaluate them
                Vector2 cpos = new Vector2((i < 2 || i > 3) ? focus.pos.x + 1 : focus.pos.x - 1, i <= 2 ? focus.pos.y + 1 : focus.pos.y - 1);
                if(cpos.equals(goal)) {
                    System.out.println("Found optimal path");
                    return null; // return generated list of parent nodes here
                }
                // if cpos is in traversableCoords skip this child

                Node child = new Node(0, focus, cpos, focus.g + dist(cpos, focus.pos), dist(cpos, goal)); // id = 0; will set later
                //System.out.println(cpos + " " + goal + " dist: " +  (dist(cpos, goal) + focus.g + dist(cpos, focus.pos)));
                boolean skip = false;
                for(Node n : open) // if there is a better node already in open, skip this node
                    if(n.pos.equals(child.pos) && n.f < child.f)
                        skip = true;
                for(Node n : closed) // if there... in closed, skip node
                    if(n.pos.equals(child.pos) && n.f < child.f)
                        skip = true;
                if(!skip) {
                    child.Id = id++; // ++ Id for next valid child
                    //if(id < 50) System.out.println(child.Id + " " + child.f + " " + child.pos);
                    open.add(child);
                }
            }
            closed.add(focus);
            //System.out.println(open);

            //if(id < 20) System.out.println(" " + focus.f);
            if(id > 1000) {
                System.out.println("premature break");
                break;
            }
        }

        return null;
    }
}
