package de.tum.cit.ase.maze.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;

public class WinScreen implements Screen {
    private MazeRunnerGame mazeRunnerGame;
    private final Stage stage;
    private Sound buttonClick;

    public WinScreen(MazeRunnerGame game) {
        this.mazeRunnerGame = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        buttonClick = Gdx.audio.newSound(Gdx.files.internal("buttton-click.mp3"));

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("You won the game!", game.getSkin(), "title")).padBottom(80).row();
        table.add(new Label("Press 'Enter' to go MENU.", game.getSkin(), "title")).padBottom(60).row();
    }

    @Override
    public void show() {
        // Initialize any resources or setup needed when the screen is shown
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage

        // Check for input to return to the main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            buttonClick.play();
            mazeRunnerGame.goToMenu();
        }
    }

    // Other methods from the Screen interface (resize, pause, resume, hide, dispose) can be implemented if needed

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
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
        buttonClick.dispose();
        // Dispose of any resources when the screen is no longer needed
    }
}
