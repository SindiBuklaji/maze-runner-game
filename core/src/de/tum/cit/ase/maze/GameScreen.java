package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.Objects.Ghost;
import de.tum.cit.ase.maze.Objects.Door;
import de.tum.cit.ase.maze.Objects.Exit;
import de.tum.cit.ase.maze.Objects.Wall;
import de.tum.cit.ase.maze.Screens.GameOverScreen;
import de.tum.cit.ase.maze.Screens.HUDScreen;
import de.tum.cit.ase.maze.Screens.PauseScreen;
import de.tum.cit.ase.maze.Screens.WinScreen;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game; // Reference to the main game class
    private final OrthographicCamera camera; // Camera for rendering
    private final BitmapFont font;
    private Stage stage; // Stage for UI elements
    private Sound buttonClick;

    private float sinusInput = 0f;
    private Map<String, Integer> mazeMap;
    private List<Ghost> ghostList;
    private int tileSize = 64;

    private TextureRegion entryRegion, closedDoorRegion, openDoorRegion, treasureRegion, opentreasureRegion, floorRegion, verticalWallRegion, horizontalWallRegion;
    // Declare variables to store the current animation
    private Animation<TextureRegion> currentAnimation;
    // Declare variables to store the fire animation
    private Animation<TextureRegion> fireAnimation;

    private float characterX;
    private float characterY;
    private boolean collisionOccurred = false;

    public int mazeWidth;
    public int mazeHeight;

    int maxX;
    int maxY;

    private int livesRemaining = 5; // Initial number of lives
    private float characterSpeed = 300f; // Adjust the speed as needed
    private boolean inCooldown = false;
    private float cooldownTimer = 0f;
    public static final float COOLDOWN_DURATION = 2f;
    private final HUDScreen hud;

    private boolean characterStartPositionSet = false;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game  The main game class, used to access global resources and methods.
     * @param level The level to load.
     */

    public GameScreen(MazeRunnerGame game, int level) throws IOException {
        this.game = game;
        this.camera = new OrthographicCamera();
        stage = new Stage();
        hud = new HUDScreen(game.getSkin()); // Pass the camera to HUDScreen
        Gdx.input.setInputProcessor(stage);

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        buttonClick = Gdx.audio.newSound(Gdx.files.internal("buttton-click.mp3"));

        currentAnimation = game.getCharacterStillDownAnimation(); // Initialize with the default animation

        // Initialize the fire animation
        fireAnimation = game.getFireAnimation();

        // Calculate maze dimensions based on window size
      /*  mazeWidth = Gdx.graphics.getWidth();
        mazeHeight = Gdx.graphics.getHeight(); */


        String levelFileName = "level-" + level + ".properties";
        FileHandle fileHandle = Gdx.files.internal("maps/" + levelFileName);
        InputStream inputStream = fileHandle.read();
        Properties properties = new Properties();

        // Load Textures for game elements

        Texture entryTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture closedDoorTexture = new Texture(Gdx.files.internal("things.png"));
        Texture openDoorTexture = new Texture(Gdx.files.internal("things.png"));
        Texture treasureTexture = new Texture(Gdx.files.internal("things.png"));
        Texture opentreasureTexture = new Texture(Gdx.files.internal("things.png"));
        Texture floorTexture = new Texture(Gdx.files.internal("basictiles.png"));

        Texture verticalWallTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture horizontalWallTexture = new Texture(Gdx.files.internal("basictiles.png"));


        // Load your textures
        entryRegion = new TextureRegion(entryTexture, 0, 128, 16, 16);
        closedDoorRegion = new TextureRegion(closedDoorTexture, 0, 32, 16, 16);
        openDoorRegion = new TextureRegion(openDoorTexture, 0, 0, 16, 16);
        treasureRegion = new TextureRegion(treasureTexture, 64, 64, 16, 16);
        opentreasureRegion = new TextureRegion(opentreasureTexture, 48, 64, 16, 16);
        floorRegion = new TextureRegion(floorTexture, 0, 128, 16, 16);

        verticalWallRegion = new TextureRegion(verticalWallTexture, 80, 0, 16, 16);
        horizontalWallRegion = new TextureRegion(horizontalWallTexture, 96, 0, 16, 16);

        // Load level properties from a file

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

        mazeWidth = maxX * tileSize;
        mazeHeight = maxY * tileSize;


        // Create and configure the camera for the game view
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;

        //Set the initial position of the camera to the center of the maze
        camera.position.set(mazeWidth * 0.5f, mazeHeight * 0.5f, 0);

        //Update the camera projection
        camera.update();

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        // Call setCharacterStartPosition after loading the maze
        setCharacterStartPosition();
        ghostList = new ArrayList<>();

        // Create Ghost objects based on level properties

        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = entry.getValue();

            if (value == 4) {
                TextureRegion ghostFrame = game.getGhostAnimation().getKeyFrame(sinusInput, true);
                Ghost newGhost = new Ghost(game, mazeMap, this,10f, ghostFrame, (x * tileSize), (y * tileSize));
                ghostList.add(newGhost);
            }
        }

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

    /**
     * Renders the game screen.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Check if the player is in cooldown for any recent action
        if (inCooldown) {
            cooldownTimer -= delta;
            // Exit cooldown once the timer reaches zero
            if (cooldownTimer <= 0) {
                inCooldown = false;
            }
        }

        // Check if the key is collected in the game
        game.isKeyCollected();

        // Check if the game is not paused
        if (!game.isPaused()) {
            // Check for escape key press to go back to the menu
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                buttonClick.play();
                pause();
                game.setScreen(new PauseScreen(game, this));
            }

            // Update character position based on arrow key inputs
            else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                // Move character up if no collision detected
                if (!checkCollision(characterX, characterY + characterSpeed * Gdx.graphics.getDeltaTime())) {
                    characterY += characterSpeed * Gdx.graphics.getDeltaTime();
                    currentAnimation = game.getCharacterUpAnimation();
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (!checkCollision(characterX, characterY - characterSpeed * Gdx.graphics.getDeltaTime())) {
                    characterY -= characterSpeed * Gdx.graphics.getDeltaTime();
                    currentAnimation = game.getCharacterDownAnimation();
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (!checkCollision(characterX + characterSpeed * Gdx.graphics.getDeltaTime(), characterY)) {
                    characterX += characterSpeed * Gdx.graphics.getDeltaTime();
                    currentAnimation = game.getCharacterRightAnimation();
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (!checkCollision(characterX - characterSpeed * Gdx.graphics.getDeltaTime(), characterY)) {
                    characterX -= characterSpeed * Gdx.graphics.getDeltaTime();
                    currentAnimation = game.getCharacterLeftAnimation();
                }
            } else {
                // If no arrow key is pressed, set character animation to still
                if (currentAnimation == game.getCharacterDownAnimation()) {
                    currentAnimation = game.getCharacterStillDownAnimation();
                }
                if (currentAnimation == game.getCharacterUpAnimation()) {
                    currentAnimation = game.getCharacterStillUpAnimation();
                }
                if (currentAnimation == game.getCharacterLeftAnimation()) {
                    currentAnimation = game.getCharacterStillLeftAnimation();
                }
                if (currentAnimation == game.getCharacterRightAnimation()) {
                    currentAnimation = game.getCharacterStillRightAnimation();
                }
            }
        }


        // Set the new position of the character in the camera
        camera.position.set(characterX + 96, characterY + 64, 0); // Adjusted for character size
        camera.update();
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen


        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        // Draw floor tiles
        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                game.getSpriteBatch().draw(floorRegion, x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        // Draw ghosts and handle collisions
        for (Ghost ghost : ghostList) {
            ghost.Move();
            game.getSpriteBatch().draw(ghost.getGhostFrame(), ghost.getCurrentX(), ghost.getCurrentY(), tileSize, tileSize);
            if (collidesWithCharacter(ghost.getCurrentX(), ghost.getCurrentY())) {
                collisionOccurred = true;
                decreaseLives(delta);
            }
        }
        // Draw walls, doors, exits, fire, ghosts, and keys based on the map
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            // Extract coordinates and value from the entry
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = entry.getValue();

            // Check the value to determine the type of element to draw
            // 0 == walls
            if (value == 0) {
                Wall wall = new Wall(isVerticalWall(x, y) ? verticalWallRegion : horizontalWallRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                wall.render(game.getSpriteBatch());

                // 1 == open paths
            } else if (value == 1) {
                Door door = new Door(entryRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                door.render(game.getSpriteBatch());

                // 2 == door
            } else if (value == 2) {
                // Draw doors and check for key collision to win the game
               /* Exit exit = new Exit(closedDoorRegion, openDoorRegion, x, y, tileSize, tileSize, game);
                exit.render(game.getSpriteBatch()); */
                float doorX = x * tileSize;
                float doorY = y * tileSize;
                game.getSpriteBatch().draw(closedDoorRegion, doorX, doorY, tileSize, tileSize);

                // Only draw the doors if the key has been collected
                if (!game.isKeyCollected()) {
                    game.getSpriteBatch().draw(openDoorRegion, doorX, doorY, tileSize, tileSize);
                }

                if (collidesWithCharacter(x, y) && game.isKeyCollected()) {
                    game.setScreen(new WinScreen(game));
                    game.gameWinSound();
                }

                // 3 == fire
            } else if (value == 3) {
                // Draw fire and check for collision with the character
                TextureRegion fireFrame = game.getFireAnimation().getKeyFrame(sinusInput, true);
                game.getSpriteBatch().draw(fireFrame, x * tileSize, y * tileSize, tileSize, tileSize);
                if (collidesWithCharacter(x, y)) {
                    collisionOccurred = true;
                    decreaseLives(delta);
                }

                // 4 == ghosts
            } else if (value == 4) {
                // Draw ghosts and check for collision with the character
                for (Ghost ghost : ghostList) {
                    ghost.Move();
                    game.getSpriteBatch().draw(ghost.getGhostFrame(), ghost.getCurrentX() * tileSize, ghost.getCurrentY() * tileSize, tileSize, tileSize);
                    if (collidesWithCharacter(ghost.getCurrentX(), ghost.getCurrentY())) {
                        collisionOccurred = true;
                        decreaseLives(delta);
                    }
                }

                // 5 = treasure (key)
            } else if (value == 5) {
                // Draw treasure (key) and check for key collection
                if (collidesWithCharacter(x, y) && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                    game.keyCollectionStatus();
                    game.getSpriteBatch().draw(opentreasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                    game.doorOpeningSound();
                } else if (game.isKeyCollected()) {
                    game.getSpriteBatch().draw(opentreasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                    hud.updateKeyStatus(game.isKeyCollected());
                } else {
                    game.getSpriteBatch().draw(treasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                }

            }
        }

        // Draw the character with animation
        sinusInput += delta;
        game.getSpriteBatch().draw(
                currentAnimation.getKeyFrame(sinusInput, true),
                characterX,
                characterY,
                tileSize + 10,
                tileSize + 40
        );


        game.getSpriteBatch().end(); // Important to call this after drawing everything

        // Check if the player is in cooldown and apply visual effects
        // Player gets a red gradient when he gets damage (collision with ghost or fire)
        if (inCooldown) {
            cooldownTimer -= delta;
            game.getSpriteBatch().begin();

            // Calculate lerp factor based on cooldown timer
            float lerpFactor = 1 - Math.max(0, cooldownTimer / COOLDOWN_DURATION);

            // Set color with transparency using lerp
            float targetAlpha = 0.5f; // Adjust the target alpha as needed
            float currentAlpha = 1 - lerpFactor;
            float lerpedAlpha = MathUtils.lerp(currentAlpha, targetAlpha, lerpFactor);

            Color originalColor = game.getSpriteBatch().getColor().cpy();

            game.getSpriteBatch().setColor(1, 1 - lerpedAlpha, 1 - lerpedAlpha, lerpedAlpha);

            // Draw the player with the adjusted color
            game.getSpriteBatch().draw(
                    currentAnimation.getKeyFrame(sinusInput, true),
                    characterX,
                    characterY,
                    tileSize + 10,
                    tileSize + 40
            );

            // Reset the color to the original after drawing
            game.getSpriteBatch().setColor(originalColor);

            game.getSpriteBatch().end();

            // Exit cooldown once the timer reaches zero
            if (cooldownTimer <= 0) {
                inCooldown = false;
            }
        }
        // Update and draw the stage and HUD
        stage.act(delta);
        stage.draw();
        hud.draw();
        camera.update();

       // Request rendering if not paused
        if (!game.isPaused()) {
            Gdx.graphics.requestRendering();
        }

    }

    /**
     * Checks for collisions with walls and map boundaries.
     *
     * @param x The x-coordinate to check for collision.
     * @param y The y-coordinate to check for collision.
     * @return True if a collision is detected, false otherwise.
     */

    private boolean checkCollision(float x, float y) {
        // Iterate through mazeMap to check for collision with walls
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            int value = entry.getValue();
            if (value == 0 || value == 2 && game.isKeyCollected() == false) { // Wall
                String[] coordinates = entry.getKey().split(",");
                float wallX = Integer.parseInt(coordinates[0]) * tileSize;
                float wallY = Integer.parseInt(coordinates[1]) * tileSize;
                float offset = 48f; // Adjust the offset as needed

                if (x < wallX - 10 + tileSize && x + offset > wallX - 20 && y < wallY - 10 + tileSize && y + offset > wallY) {
                    // Collision detected with a wall
                    return true;
                }
            }
        }
        // Check for collision with map boundaries
        if (x < 0 || maxX > mazeWidth || y < 0 || maxY > mazeHeight) {
            // Collision detected with the map boundaries
            return true;
        }

        // No collision detected
        return false;
    }

    // Check if the character collides with a specific tile
    private boolean collidesWithCharacter(float tileX, float tileY) {
        float characterX = camera.position.x - 96; // Adjusted for character size
        float characterY = camera.position.y - 64; // Adjusted for character size

        // Check collision with ghosts
        for (Ghost ghost : ghostList) {
            float ghostX = ghost.getCurrentX();
            float ghostY = ghost.getCurrentY();

            if (Math.abs(characterX - ghostX) < tileSize / 2 && Math.abs(characterY - ghostY) < tileSize / 2) {
                return true;
            }
        }

        float tileCenterX = tileX * tileSize;
        float tileCenterY = tileY * tileSize;

        return Math.abs(characterX - tileCenterX) < tileSize / 2 &&
                Math.abs(characterY - tileCenterY) < tileSize / 2;

    }


    private boolean isVerticalWall(int x, int y) {
        // Check if there's a wall to the north or south
        boolean hasWallBelow = mazeMap.containsKey((x) + "," + (y - 1)) && mazeMap.get((x) + "," + (y - 1)) == 0;

        return (hasWallBelow);
    }

    @Override
    public void resize(int width, int height) {
        Vector3 Oldlocation = camera.position.cpy();
        camera.setToOrtho(false);
        camera.position.set(Oldlocation);
        hud.resize(width, height);
        stage.getViewport().update(width, height, true);
        System.out.println(stage.getViewport());
        camera.update();
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
        hud.dispose();
        game.getSpriteBatch().dispose();
        buttonClick.dispose();
    }

    public void decreaseLives(float delta) {
        if (!inCooldown && collisionOccurred) {
            livesRemaining--;
            inCooldown = true;
            cooldownTimer = COOLDOWN_DURATION;
            game.collisionSound();
        }

        hud.updateLives(livesRemaining);
        if (livesRemaining <= 0) {
            // Set game over state
            game.setScreen(new GameOverScreen(game));
            game.gameOverSound();
        }

    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getTileSize() {
        return tileSize;
    }


}