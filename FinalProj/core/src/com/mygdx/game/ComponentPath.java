package com.mygdx.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by KnightPickles on 4/14/16.
 */
public class ComponentPath implements Component {
    public ArrayList<Vector2> path = new ArrayList<Vector2>();
}
