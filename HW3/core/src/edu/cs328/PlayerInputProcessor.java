package edu.cs328;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by KnightPickles on 2/21/2016.
 */
public class PlayerInputProcessor implements InputProcessor {
    Player player;

    PlayerInputProcessor(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.RIGHT)
            player.setLVel(Math.max(player.body.getLinearVelocity().x, 1f), player.body.getLinearVelocity().y);
        if(keycode == Input.Keys.LEFT)
            player.setLVel(Math.min(player.body.getLinearVelocity().x, -1f), player.body.getLinearVelocity().y);
        if(keycode == Input.Keys.SPACE)
            player.setLVel(player.body.getLinearVelocity().x, Math.max(player.body.getLinearVelocity().y, 1f));

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
