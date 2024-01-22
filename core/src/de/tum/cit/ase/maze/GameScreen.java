package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    //bane final po nuk bani
    private final OrthographicCamera camera;
    private final BitmapFont font;

    private float sinusInput = 0f;
    private Map<String, Integer> mazeMap;
    private int tileSize = 48;

    private TextureRegion wallRegion;
    private TextureRegion entryRegion;
    private TextureRegion exitRegion;
    private TextureRegion fireRegion;
    private TextureRegion ghostRegion;
    private TextureRegion treasureRegion;

    int mazeWidth;
    int mazeHeight;

    private float characterSpeed = 600f; // Adjust the speed as needed


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game  The main game class, used to access global resources and methods.
     * @param level
     */

    public GameScreen(MazeRunnerGame game, int level) throws IOException {
        this.game = game;

        // Calculate maze dimensions based on window size
        mazeWidth = Gdx.graphics.getWidth();
        mazeHeight = Gdx.graphics.getHeight();

        String levelFileName = "level-" + level + ".properties";
        FileHandle fileHandle = Gdx.files.internal("maps/" + levelFileName);
        InputStream inputStream = fileHandle.read();
        Properties properties = new Properties();

        Texture wallTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture entryTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture exitTexture = new Texture(Gdx.files.internal("things.png"));
        Texture fireTexture = new Texture(Gdx.files.internal("objects.png"));
        Texture ghostTexture = new Texture(Gdx.files.internal("mobs.png"));
        Texture treasureTexture = new Texture(Gdx.files.internal("basictiles.png"));

        // Load your textures
        wallRegion = new TextureRegion(wallTexture, 0, 0, 16, 16);
        entryRegion = new TextureRegion(entryTexture, 0, 16, 16, 16);
        exitRegion = new TextureRegion(exitTexture, 0, 0, 16, 16);
        fireRegion = new TextureRegion(fireTexture, 80, 48, 16, 16);
        ghostRegion = new TextureRegion(ghostTexture, 96, 64, 16, 16);
        treasureRegion = new TextureRegion(treasureTexture, 64, 64, 16, 16);

        properties.load(inputStream);

        mazeMap = new HashMap<>();
        int maxX = 0;
        int maxY = 0;

        for (String key : properties.stringPropertyNames()) {
            String[] coordinates = key.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = Integer.parseInt(properties.getProperty(key));
            mazeMap.put(x + "," + y, value);

            maxX = Math.max(maxX, x);
        }

        System.out.println(maxX);


        // Create and configure the camera for the game view
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;
        //this.viewport = new ScreenViewport(camera);

        //Set the initial position of the camera to the center of the maze
        camera.position.set(mazeWidth * 0.5f, mazeHeight * 0.5f, 0);

        //Update the camera projection
        camera.update();

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        float characterX = camera.position.x - 96; // Adjusted for character size
        float characterY = camera.position.y - 64; // Adjusted for character size

        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }
        // Update character position based on arrow key inputs
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            characterY += characterSpeed * Gdx.graphics.getDeltaTime();;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            characterY -= characterSpeed * Gdx.graphics.getDeltaTime();;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            characterX += characterSpeed * Gdx.graphics.getDeltaTime();;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            characterX -= characterSpeed * Gdx.graphics.getDeltaTime();;
        }

        // Set the new position of the character
        camera.position.set(characterX + 96, characterY + 64, 0); // Adjusted for character size
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen



        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin(); // Important to call this before drawing anything


        // Draw the character
        sinusInput += delta;
       game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                camera.position.x - 96,
                camera.position.y - 64,
                48,
                96
        );

       // Draw the fire


        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = entry.getValue();

            // 0 == walls
            if (value == 0) {
                game.getSpriteBatch().draw(wallRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                // 1 == open paths
            } else if (value == 1) {
                game.getSpriteBatch().draw(entryRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                // 2 == exits (door)
            } else if (value == 2) {
                game.getSpriteBatch().draw(exitRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                // 3 == fire
            } else if (value == 3) {
                game.getSpriteBatch().draw(fireRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                // 4 == ghosts
            } else if (value == 4) {
                game.getSpriteBatch().draw(ghostRegion, x * tileSize, y * tileSize, tileSize, tileSize);
            } else if (value == 5) {
                game.getSpriteBatch().draw(treasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        game.getSpriteBatch().end(); // Important to call this after drawing everything
        camera.update();

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
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }


}