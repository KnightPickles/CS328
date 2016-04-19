package com.mygdx.game;

/**
 * Created by KnightPickles on 4/10/2016.
 */
public class MapTile {
    // implement 2d array of tiles
    private boolean path;
    private boolean occupied;

    boolean isValidPath() {
        return path;
    }

    public boolean canBuild() {
        return !path && !occupied;
    }
}
