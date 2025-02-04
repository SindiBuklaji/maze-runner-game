package de.tum.cit.ase.maze.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.GameScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.IOException;

public class PauseScreen implements Screen {
    private MazeRunnerGame mazeRunnerGame;
    private GameScreen gameScreen;
    private final Stage stage;
    private Sound buttonClick;


    private int selectedLevel; // Store the selected file handle

    public PauseScreen(MazeRunnerGame game, GameScreen level) {
        this.mazeRunnerGame = game;
        this.gameScreen = level;  // Initialize the gameScreen field

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        buttonClick = Gdx.audio.newSound(Gdx.files.internal("buttton-click.mp3"));

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("You paused the game.", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton resumeButton = new TextButton("Continue", game.getSkin());
        TextButton selectLevelButton = new TextButton("Load Map", game.getSkin());
        TextButton exitButton = new TextButton("Exit", game.getSkin());


        table.add(resumeButton).width(300).row();
        table.add(selectLevelButton).width(300).row();
        table.add(exitButton).width(300).row();

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Call the method to open the file chooser
                buttonClick.play();
                game.setPaused(false);
                Gdx.graphics.setContinuousRendering(true);
                buttonClick.play();
                game.setScreen(level);
            }
        });

        selectLevelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Call the method to open the file chooser
                buttonClick.play();
                selectedLevel = openFileChooser();
                try {
                    game.goToGame(selectedLevel); // Change to the game screen when button is pressed
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Call the method to open the file chooser
                buttonClick.play();
                Gdx.app.exit();
            }
        });


    }

    // Method to open the file chooser
    private int openFileChooser() {
        // Configure the file chooser
        NativeFileChooserConfiguration config = new NativeFileChooserConfiguration();
        config.directory = Gdx.files.internal("C:/IdeaProjects/itp2324itp2324projectwork-mon3mu1sindi/maps");

        // Create and show the file chooser
        mazeRunnerGame.nativeFileChooser.chooseFile(config, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                // Handle the chosen file, e.g., extract the level number from the file name
                Gdx.app.log("FileChooser", "Selected file: " + file.path());
                selectedLevel = extractLevelFromFileName(file.name());
            }

            @Override
            public void onCancellation() {
                // Handle cancellation if needed
            }

            @Override
            public void onError(Exception exception) {
                // Handle error if needed
            }
        });

        // Return the selected level (it might be 0 if none is selected)
        return selectedLevel;
    }

    // Method to extract the level number from the file name
    private int extractLevelFromFileName(String fileName) {
        // Implement your logic to extract the level number from the file name
        // For example, if file name is "level-2.properties", extract "2"
        String levelString = fileName.replaceAll("[^0-9]", "");
        return levelString.isEmpty() ? 0 : Integer.parseInt(levelString);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

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
        stage.dispose();
        buttonClick.dispose();
        // Dispose of any resources when the screen is no longer needed
    }
}
