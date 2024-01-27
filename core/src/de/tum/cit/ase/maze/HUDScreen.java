package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;

public class HUDScreen {

    private final Stage stage;
    private Image[] heartImages = new Image[5];

    public HUDScreen(Skin skin) {
        OrthographicCamera hudCamera = new OrthographicCamera();
        ScreenViewport viewport = new ScreenViewport(hudCamera);
        stage = new Stage(viewport);

        Table table = new Table();
        table.top().padTop(70);
        table.setFillParent(true);

        TextureRegion heartTexture = new TextureRegion(new Texture(Gdx.files.internal("objects.png")));
        TextureRegion heartRegion = new TextureRegion(heartTexture, 0, 50, 16, 16);
        TextureRegionDrawable heartDrawable = new TextureRegionDrawable(heartRegion);


        for (int i = 0; i < heartImages.length; i++) {
            heartImages[i] =new Image(heartDrawable);
            heartImages[i].setScale(6);
            table.add(heartImages[i]).padTop(10).padRight(60);
        }

        stage.addActor(table);

        table.row();


    }

    public void update(int livesRemaining) {
        //heartImage.setVisible(livesRemaining > 0);
        for (int i = 0; i < heartImages.length; i++) {
            heartImages[i].setVisible(i < livesRemaining);
        }
    }

    public void draw() {
        stage.act(Math.min(stage.getWidth(), 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }

}