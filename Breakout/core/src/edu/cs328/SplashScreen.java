package edu.cs328;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Created by KnightPickles on 1/21/16.
 */
public class SplashScreen implements Screen {
    protected GameCore game;
    public Stage stage;
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

    /*public void setupButtons() {
        // Table -> stage.addActor(), TextButton -> .addListener(...), tableName.add(textButtonName);

        Table menuTable = new Table();
        stage.addActor(menuTable);
        menuTable.setPosition(400, 460);
        menuTable.debug().defaults().space(6);
        TextButton btnPlayMenu = new TextButton("Start", skin);
        btnPlayMenu.addListener(new ActorGestureListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                game.setScreen(new GameScreen(game, false, false));
            }});
        menuTable.add(btnPlayMenu).width(BUTTONWIDTH).height(BUTTONHEIGHT);

    }*/

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
