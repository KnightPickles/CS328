package edu.cs328;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by KnightPickles on 2/21/16.
 */
public class GameObjectManager {
    ArrayList<SimpleGameObject> simpleGO = new ArrayList<SimpleGameObject>();
    BufferedImage level = null;
    int[][] levelData = null;

    void update() {
        for(SimpleGameObject o : simpleGO)
            o.update();
    }

    void draw(Batch batch) {
        for(SimpleGameObject o : simpleGO)
             o.draw(batch);
    }

    void addObject(SimpleGameObject object) {
        simpleGO.add(object);
    }

    void loadGameFromFile(String filename) {
        try {
            level = ImageIO.read(new File(filename));
            int width = level.getWidth();
            int height = level.getHeight();
            levelData = new int[height][width];

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    levelData[row][col] = level.getRGB(col, row);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

    }
}
