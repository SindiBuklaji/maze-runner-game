package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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

    private TextureRegion verticalWallRegion;
    private TextureRegion horizontalWallRegion;
    private float characterX;
    private float characterY;

    int entryX;
    int entryY;

    private int livesRemaining = 5; // Initial number of lives
    private boolean keyCollected = false;
    private boolean collisionOccurred = false;

    int mazeWidth;
    int mazeHeight;

    int maxX;
    int maxY;

    private float characterSpeed = 300f; // Adjust the speed as needed
    private float collisionCooldown = 0f;
    private final float cooldownDuration = 2f; // Set the cooldown duration in seconds
    private final HUDScreen hud;
    private boolean gameOver;

    private boolean characterStartPositionSet = false;

    // Declare variables to store the current animation
    private Animation<TextureRegion> currentAnimation;

    // Declare variables to store the fire animation
    private Animation<TextureRegion> fireAnimation;

    private final Preferences prefs;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game  The main game class, used to access global resources and methods.
     * @param level
     */

    public GameScreen(MazeRunnerGame game, int level) throws IOException {
        this.game = game;

        prefs = Gdx.app.getPreferences("GamePreferences");
        loadGameState();

        hud = new HUDScreen(game.getSkin());
        // Draw the HUD
        hud.draw();


        currentAnimation = game.getCharacterDownAnimation(); // Initialize with the default animation

        // Initialize the fire animation
        fireAnimation = game.getFireAnimation();

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

        Texture verticalWallTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture horizontalWallTexture = new Texture(Gdx.files.internal("basictiles.png"));


        // Load your textures
        wallRegion = new TextureRegion(wallTexture, 0, 0, 16, 16);
        entryRegion = new TextureRegion(entryTexture, 0, 16, 16, 16);
        exitRegion = new TextureRegion(exitTexture, 0, 0, 16, 16);
        fireRegion = new TextureRegion(fireTexture, 80, 48, 16, 16);
        ghostRegion = new TextureRegion(ghostTexture, 96, 64, 16, 16);
        treasureRegion = new TextureRegion(treasureTexture, 64, 64, 16, 16);
        floorRegion = new TextureRegion(floorTexture, 0, 16, 16, 16);

        verticalWallRegion = new TextureRegion(verticalWallTexture, 16, 0, 16, 16);
        horizontalWallRegion = new TextureRegion(horizontalWallTexture, 32, 0, 16, 16);

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

        // Call setCharacterStartPosition after loading the maze
        setCharacterStartPosition();

    }

    private void setCharacterStartPosition() {
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = entry.getValue();

            if (value == 1) {
                characterX = x * tileSize;
                characterY = y * tileSize;
                characterStartPositionSet = true;
                return;
            }
        }
    }

    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {

       /* float characterX = camera.position.x - 96; // Adjusted for character size
        float characterY = camera.position.y - 64; // Adjusted for character size */

        // Update HUD information
        hud.update(livesRemaining, keyCollected);

        // Decrease the collisionCooldown
        collisionCooldown = Math.max(0f, collisionCooldown - delta);

        // Check if cooldown has elapsed and collision has occurred
        if (collisionCooldown <= 0 && collisionOccurred) {
            // Reset the collision flag
            collisionOccurred = false;

            // Decrease the lives after cooldown has elapsed
            decreaseLives();

            // Reset the cooldown timer
            collisionCooldown = cooldownDuration;
        }

        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game));;
        }


        // Update character position based on arrow key inputs
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!checkCollision(characterX, characterY + characterSpeed * Gdx.graphics.getDeltaTime())) {
            characterY += characterSpeed * Gdx.graphics.getDeltaTime();
            currentAnimation = game.getCharacterUpAnimation();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!checkCollision(characterX, characterY - characterSpeed * Gdx.graphics.getDeltaTime())) {
            characterY -= characterSpeed * Gdx.graphics.getDeltaTime();
            currentAnimation = game.getCharacterDownAnimation();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!checkCollision(characterX + characterSpeed * Gdx.graphics.getDeltaTime(), characterY)) {
                characterX += characterSpeed * Gdx.graphics.getDeltaTime();
                currentAnimation = game.getCharacterRightAnimation();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!checkCollision(characterX - characterSpeed * Gdx.graphics.getDeltaTime(), characterY)) {
            characterX -= characterSpeed * Gdx.graphics.getDeltaTime();
            currentAnimation = game.getCharacterLeftAnimation();
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

        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = entry.getValue();

            // 0 == walls
            if (value == 0) {
                int wallX = x * tileSize;
                int wallY = y * tileSize;

                //check orientation of the wall (vertical or horizontal)
                boolean isVerticalWall = isVerticalWall(x, y);

                // Choose the appropriate texture based on the wall orientation
                TextureRegion wallTexture;
                if (isVerticalWall) {
                    wallTexture = verticalWallRegion;
                } else {
                    wallTexture = horizontalWallRegion;
                }

                // Draw the wall with the selected texture
                game.getSpriteBatch().draw(wallTexture, wallX, wallY, tileSize, tileSize);
                // 1 == open paths
            } else if (value == 1) {
                game.getSpriteBatch().draw(entryRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                // 2 == exits (door)
            } else if (value == 2) {
               // game.getSpriteBatch().draw(exitRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                float doorX = x * tileSize;
                float doorY = y * tileSize;

                // Only draw the doors if the key has been collected
                if (keyCollected) {
                    game.getSpriteBatch().draw(exitRegion, doorX, doorY, tileSize, tileSize);
                } else {
                    game.getSpriteBatch().draw(exitRegion, doorX, doorY, tileSize, tileSize);
                }
                // 3 == fire
            } else if (value == 3) {
                game.getSpriteBatch().draw(fireRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                if (collidesWithCharacter(x, y)) {
                    collisionOccurred = true; // Set collision flag to true
                    decreaseLives();
                }
                // 4 == ghosts
            } else if (value == 4) {
                game.getSpriteBatch().draw(ghostRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                if (collidesWithCharacter(x, y)) {
                    collisionOccurred = true; // Set collision flag to true
                    decreaseLives();
                }
            } else if (value == 5) {
                game.getSpriteBatch().draw(treasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                // Check if the character collides with the key
                if (collidesWithCharacter(x, y)) {
                    collectKey();
                    collisionOccurred = true; // Set collision flag to true
                }
            }
        }

        // Draw the character
        sinusInput += delta;
        game.getSpriteBatch().draw(
                currentAnimation.getKeyFrame(sinusInput, true),
                characterX,
                characterY,
                tileSize + 10,
                tileSize + 40
        );


        // Render game elements
        if (!gameOver) {
            // Render game elements as usual
        } else {
            // Switch to game over screen
            game.setScreen(new GameOverScreen(game));
        }

        game.getSpriteBatch().end(); // Important to call this after drawing everything

        camera.update();

    }

    private boolean checkCollision(float x, float y) {
        // Iterate through mazeMap to check for collision with walls
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            int value = entry.getValue();
            if (value == 0) { // Wall
                String[] coordinates = entry.getKey().split(",");
                float wallX = Integer.parseInt(coordinates[0]) * tileSize;
                float wallY = Integer.parseInt(coordinates[1]) * tileSize;
                float offset = 48f; // Adjust the offset as needed

                if (x < wallX-10 + tileSize && x + offset > wallX-20 && y < wallY-10 + tileSize && y + offset > wallY) {
                    // Collision detected with a wall
                    return true;
                }
            }
        }
        // No collision detected
        return false;
    }

    // Add methods to update lives and key status based on game events
    public void decreaseLives() {
        livesRemaining--;
        if (livesRemaining <= 0) {
            // Set game over state
            gameOver = true;
        }
    }

    public void collectKey() {
        keyCollected = true;
        // Add logic for what happens when the key is collected
    }

    // Check if the character collides with a specific tile
    private boolean collidesWithCharacter(int tileX, int tileY) {
        float characterX = camera.position.x - 96; // Adjusted for character size
        float characterY = camera.position.y - 64; // Adjusted for character size

        float tileCenterX = (tileX + 0.5f) * tileSize;
        float tileCenterY = (tileY + 0.5f) * tileSize;

        return Math.abs(characterX - tileCenterX) < tileSize / 2 &&
                Math.abs(characterY - tileCenterY) < tileSize / 2;
    }


    private boolean isVerticalWall(int x, int y) {
        // Check if there's a wall to the north or south
        //boolean hasWallAbove = mazeMap.containsKey((x) + "," + (y + 1)) && mazeMap.get((x) + "," + (y + 1)) == 0;
        boolean hasWallBelow = mazeMap.containsKey((x) + "," + (y - 1)) && mazeMap.get((x) + "," + (y - 1)) == 0;

        return (hasWallBelow);
    }

    private void saveGameState() {
        prefs.putFloat("characterX", characterX);
        prefs.putFloat("characterY", characterY);
        prefs.putInteger("livesRemaining", livesRemaining);
        prefs.putBoolean("keyCollected", keyCollected);

        // Add other fields as needed

        prefs.flush(); // Save the preferences immediately
    }

    private void loadGameState() {
        characterX = prefs.getFloat("characterX", characterX);
        characterY = prefs.getFloat("characterY", characterY);
        livesRemaining = prefs.getInteger("livesRemaining", livesRemaining);
        keyCollected = prefs.getBoolean("keyCollected", keyCollected);

        // Load other fields as needed
    }

    @Override
    public void resize(int width, int height) {
        hud.resize(width, height);
    }

    @Override
    public void pause() {
        saveGameState();
    }

    @Override
    public void resume() {
        loadGameState();
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        hud.dispose();
    }

}