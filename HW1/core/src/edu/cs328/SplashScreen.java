package edu.cs328;

import com.badlogic.gdx.Screen;

/**
 * Created by KnightPickles on 1/21/16.
 */
public class SplashScreen implements Screen {
    protected GameCore game;
    boolean isLoaded = false;

    SplashScreen(GameCore game) {
        this.game = game;
        //Assets.loadResources(game, game.getAssetManager());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        /*if(game.getAssetManager().update() == true) {
            if (isLoaded == false) {
                Assets.assignResources(game.getAssetManager());
                isLoaded = true;
            }
            else game.setScreen(new GameScreen(game, false, false));
        }*/
        game.setScreen(new GameScreen(game, false, false));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
