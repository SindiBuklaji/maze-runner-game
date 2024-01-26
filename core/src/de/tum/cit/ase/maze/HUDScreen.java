package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HUDScreen {

    private final Stage stage;
    private final Label livesLabel;
    private final Label keyStatusLabel;

    public HUDScreen(Skin skin) {
        OrthographicCamera camera = new OrthographicCamera();
        ScreenViewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        livesLabel = new Label("Lives: 0", skin);
        keyStatusLabel = new Label("Key Not Collected", skin);

        table.add(livesLabel).pad(10);
        table.row();
        table.add(keyStatusLabel).pad(10);

        stage.addActor(table);
    }

    public void update(int livesRemaining, boolean keyCollected) {
        livesLabel.setText("Lives: " + livesRemaining);
        keyStatusLabel.setText(keyCollected ? "Key Collected" : "Key Not Collected");
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