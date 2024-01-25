package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private int tileSize = 76;

    private TextureRegion wallRegion, entryRegion, exitRegion, fireRegion, ghostRegion, treasureRegion, floorRegion;

    private float characterX;
    private float characterY;

    int entryX;
    int entryY;

    private int livesRemaining = 5; // Initial number of lives
    private boolean keyCollected = false;


    int mazeWidth;
    int mazeHeight;

    int maxX;
    int maxY;


    private float characterSpeed = 300f; // Adjust the speed as needed


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
        Texture floorTexture = new Texture(Gdx.files.internal("basictiles.png"));


        // Load your textures
        wallRegion = new TextureRegion(wallTexture, 0, 0, 16, 16);
        entryRegion = new TextureRegion(entryTexture, 0, 16, 16, 16);
        exitRegion = new TextureRegion(exitTexture, 0, 0, 16, 16);
        fireRegion = new TextureRegion(fireTexture, 80, 48, 16, 16);
        ghostRegion = new TextureRegion(ghostTexture, 96, 64, 16, 16);
        treasureRegion = new TextureRegion(treasureTexture, 64, 64, 16, 16);
        floorRegion = new TextureRegion(floorTexture, 0, 16, 16, 16);


        properties.load(inputStream);

        mazeMap = new HashMap<>();

        for (String key : properties.stringPropertyNames()) {
            String[] coordinates = key.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = Integer.parseInt(properties.getProperty(key));
            mazeMap.put(x + "," + y, value);

            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }


       /* for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            if (entry.getValue() == 1) {
                String[] coordinates = entry.getKey().split(",");
                entryX = Integer.parseInt(coordinates[0]) * tileSize;
                entryY = Integer.parseInt(coordinates[1]) * tileSize;
                break; // Exit the loop once the entry point is found
            }
        }

        */



        // Create and configure the camera for the game view
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;

        //Set the initial position of the camera to the center of the maze
        //camera.position.set(mazeWidth * 0.5f, mazeHeight * 0.5f, 0);

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
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!checkCollision(characterX, characterY + characterSpeed * Gdx.graphics.getDeltaTime())) {
                characterY += characterSpeed * Gdx.graphics.getDeltaTime();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!checkCollision(characterX, characterY - characterSpeed * Gdx.graphics.getDeltaTime())) {
                characterY -= characterSpeed * Gdx.graphics.getDeltaTime();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!checkCollision(characterX + characterSpeed * Gdx.graphics.getDeltaTime(), characterY)) {
                characterX += characterSpeed * Gdx.graphics.getDeltaTime();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!checkCollision(characterX - characterSpeed * Gdx.graphics.getDeltaTime(), characterY)) {
                characterX -= characterSpeed * Gdx.graphics.getDeltaTime();
            }

        }


        // Set the new position of the character
        camera.position.set(characterX + 96, characterY + 64, 0); // Adjusted for character size
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen


        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                game.getSpriteBatch().draw(floorRegion, x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }


        // Draw the character
        sinusInput += delta;
        game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                characterX,
                characterY,
                36,
                56
        );


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
        renderHUD();

    }


    private boolean checkCollision(float x, float y) {
        // Iterate through mazeMap to check for collision with walls
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            int value = entry.getValue();
            if (value == 0) { // Wall
                String[] coordinates = entry.getKey().split(",");
                float wallX = Integer.parseInt(coordinates[0]) * tileSize;
                float wallY = Integer.parseInt(coordinates[1]) * tileSize;

                if (x < wallX + tileSize && x + 48 > wallX && y < wallY + tileSize && y + 64 > wallY) {
                    // Collision detected with a wall
                    System.out.println("Collision detected with a wall!");
                    return true;
                }
            }
        }
        // No collision detected
        return false;
    }

    private void renderHUD() {
        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        // Draw lives remaining
        font.draw(game.getSpriteBatch(), "Lives: " + livesRemaining, 10, Gdx.graphics.getHeight() - 20);

        // Draw key collected status
        String keyStatus = keyCollected ? "Key Collected" : "Key Not Collected";
        font.draw(game.getSpriteBatch(), keyStatus, 10, Gdx.graphics.getHeight() - 40);

        game.getSpriteBatch().end();
    }

    // Add methods to update lives and key status based on game events
    public void decreaseLives() {
        livesRemaining--;
        if (livesRemaining <= 0) {
            // Game over logic
            game.goToMenu();
        }
    }

    public void collectKey() {
        keyCollected = true;
        // Add logic for what happens when the key is collected
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