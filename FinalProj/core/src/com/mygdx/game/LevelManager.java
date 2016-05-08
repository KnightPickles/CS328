package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

/**
 * Created by KnightPickles on 4/17/16.
 */
public class LevelManager {

    public static LevelManager _instance;

    public enum Difficulty {
        EASY, MEDIOCRE, NORMAL, HARD
    }
    Difficulty difficulty = Difficulty.NORMAL;

    public enum WaveState {
        WAVE_START, WAVE_RUNNING, WAVE_FINISHED, LEVEL_FINISHED, STOP
    }
    WaveState waveState = WaveState.WAVE_START;
    int level = 0, levels = 0, wave = 0, numWaves = 0;
    public boolean waveStartTimer = false;
    Random random = new Random();

    LevelManager(int numLevels, int numWaves, Difficulty difficulty) {
        _instance = this;
        this.levels = numLevels;
        this.numWaves = numWaves;
        this.difficulty = difficulty;
        Map._instance.loadLevelFromImage("level" + 0 + ".png");
    }

    void update() {
        switch(waveState) {
            case WAVE_START:
                waveStartTimer = false;
                if(EntityManager._instance.ghosts.isEmpty()) {
                    System.out.println("Begining wave " + (wave + 1));
                    startWave();
                } else waveState = WaveState.WAVE_RUNNING;
                break;
            case WAVE_RUNNING:
                if(EntityManager._instance.ghosts.isEmpty()) {
                    System.out.println("Completed wave " + wave);
                    waveState = WaveState.WAVE_FINISHED;
                }
                break;
            case WAVE_FINISHED:
                if(wave >= numWaves) waveState = WaveState.LEVEL_FINISHED;
                else { // start a new wave
                    if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                        waveState = WaveState.WAVE_START;
                        System.out.println("Wave manually started");
                    }
                    if(!waveStartTimer) {
                        waveStartTimer = true;
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                if(waveStartTimer) {
                                    System.out.println("Wave automatically started");
                                    waveStartTimer = false;
                                    waveState = WaveState.WAVE_START;
                                }
                            }
                        }, 5);
                    }
                }
                break;
            case LEVEL_FINISHED:
                System.out.println("Level Completed");
                EntityManager._instance.startNewLevel();
                wave = 0;
                level++;
                if(level >= levels) {
                    waveState = WaveState.STOP;
                } else {
                    Map._instance.loadLevelFromImage("level" + level + ".png");
                    waveState = WaveState.WAVE_START;
                }
                break;
            case STOP: // trigger a display of heuristics and whatnot
                break;
        }
    }

    void startWave() {
        for (int j = 0; j < (wave + 1) * difficulty.ordinal() * 3; j++) {
            final int d = j;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    spawnRandomizedEnemy();
                }
            }, j * 0.5f);
        }
        wave++;
    }

    void spawnRandomizedEnemy() {
        if(waveState != WaveState.WAVE_START && waveState != WaveState.WAVE_RUNNING) return;
        Ghost.Color[] colors = {Ghost.Color.RED, Ghost.Color.GREEN, Ghost.Color.BLUE, Ghost.Color.PURPLE};
        //double[] colorWeight = {numWaves / (wave + 1), wave / numWaves, wave / 2, wave};
        double[] colorWeight = {0.92, 0.05, 0.02, 0.01};
        Ghost.Size[] sizes = {Ghost.Size.X11, Ghost.Size.X22, Ghost.Size.X33};
        double[] sizeWeight = {0.94, 0.05, 0.01};

        double colorTotal = 0.0d;
        double sizeTotal = 0.0d;
        for(int i = 0; i < 4; i++) {
            if(i < 3) sizeTotal += sizeWeight[i];
            colorTotal += colorWeight[i];
        }

        int colorIndex = -1, sizeIndex = -1;
        boolean colIndexed = false, sizIndexed = false;
        double randomColor = Math.random() * colorTotal;
        double randomSize = Math.random() * sizeTotal;

        for(int i = 0; i < 4; i++) {
            if(i < 3) randomSize -= sizeWeight[i];
            randomColor -= colorWeight[i];
            if(i < 3 && !sizIndexed && randomSize <= 0.0d) {
                sizeIndex = i;
                sizIndexed = true;
            }
            if(!colIndexed && randomColor <= 0.0d) {
                colorIndex = i;
                colIndexed = true;
            }
        }

        //if(EntityManager._instance.)
        EntityManager._instance.spawnGhost(sizes[sizeIndex], colors[colorIndex]);
    }
}
