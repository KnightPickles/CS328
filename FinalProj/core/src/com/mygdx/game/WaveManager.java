package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

/**
 * Created by KnightPickles on 4/17/16.
 */
public class WaveManager {

    public static WaveManager _instance;



    public enum WaveState {
        START, STOP, PAUSED, FINISHED
    }

    WaveState waveState = WaveState.STOP;
    int wave = 0, numWaves, levelTime = 20, waveTime = 5, difficulty = 2;

    WaveManager(int numWaves) {
        _instance = this;
        this.numWaves = numWaves;

        startWave();
    }

    void update() {
        if(wave >= numWaves) {
            waveState = WaveState.FINISHED;
            wave = 0;
        }


        switch(waveState) {
            case STOP:
                if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    waveState = WaveState.START;
                }
                break;
            case START:
                if(EntityManager._instance.ghosts.isEmpty())
                    startWave();
                //else waveState = WaveState.STOP;
                break;
            case PAUSED:
                // pause stuff
                break;
            case FINISHED:
                System.out.println("Wave Complete");
                break;
        }
    }

    void startWave() {
        for (int j = 0; j < (wave + 1) * difficulty; j++) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    spawnEnemies();
                }
            }, j * 0.5f);
        }
        wave++;
    }

    void spawnEnemies() {
        // percent chance its x ghost
        Random r = new Random();



        EntityManager._instance.ghosts.add(new Ghost(Ghost.Size.X11, Ghost.Color.RED));
    }

    void weightedRandomGhost() {
        Ghost.Color[] colors = {Ghost.Color.RED, Ghost.Color.GREEN, Ghost.Color.BLUE, Ghost.Color.PURPLE};
        //double[] colorWeight = {3 * wave / 10, 3 * wave / 5, 10};
        Ghost.Size[] sizes = {Ghost.Size.X11, Ghost.Size.X22, Ghost.Size.X33};


// Compute the total weight of all items together
       /* double totalWeight = 0.0d;
        for (Item i : items)
        {
            totalWeight += i.getWeight();
        }
// Now choose a random item
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < items.length; ++i)
        {
            random -= items[i].getWeight();
            if (random <= 0.0d)
            {
                randomIndex = i;
                break;
            }
        }
        Item myRandomItem = items[randomIndex];*/
    }
}
