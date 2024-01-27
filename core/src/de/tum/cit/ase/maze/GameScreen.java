package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

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
    private int tileSize = 68;

    private TextureRegion entryRegion, closedDoorRegion, openDoorRegion, fireRegion, ghostRegion, treasureRegion, opentreasureRegion, floorRegion;

    private TextureRegion verticalWallRegion;
    private TextureRegion horizontalWallRegion;
    private float characterX;
    private float characterY;
    private boolean collisionOccurred = false;


    int mazeWidth;
    int mazeHeight;

    int maxX;
    int maxY;
    private int livesRemaining = 5; // Initial number of lives
    private float characterSpeed = 300f; // Adjust the speed as needed
    private boolean inCooldown = false;
    private float cooldownTimer = 0f;
    public static final float COOLDOWN_DURATION = 2f;
    private final HUDScreen hud;

    private boolean characterStartPositionSet = false;

    // Declare variables to store the current animation
    private Animation<TextureRegion> currentAnimation;

    // Declare variables to store the fire animation
    private Animation<TextureRegion> fireAnimation;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game  The main game class, used to access global resources and methods.
     * @param level
     */

    public GameScreen(MazeRunnerGame game, int level) throws IOException {
        this.game = game;

        hud = new HUDScreen(game.getSkin()); // Pass the camera to HUDScreen

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

        Texture entryTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture closedDoorTexture = new Texture(Gdx.files.internal("things.png"));
        Texture openDoorTexture = new Texture(Gdx.files.internal("things.png"));
        Texture fireTexture = new Texture(Gdx.files.internal("objects.png"));
        Texture ghostTexture = new Texture(Gdx.files.internal("mobs.png"));
        Texture treasureTexture = new Texture(Gdx.files.internal("things.png"));
        Texture opentreasureTexture = new Texture(Gdx.files.internal("things.png"));
        Texture floorTexture = new Texture(Gdx.files.internal("basictiles.png"));

        Texture verticalWallTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture horizontalWallTexture = new Texture(Gdx.files.internal("basictiles.png"));


        // Load your textures
        entryRegion = new TextureRegion(entryTexture, 0, 128, 16, 16);
        closedDoorRegion = new TextureRegion(closedDoorTexture, 0, 32, 16, 16);
        openDoorRegion = new TextureRegion(openDoorTexture, 0, 0, 16, 16);
        fireRegion = new TextureRegion(fireTexture, 48, 32, 16, 16);
        ghostRegion = new TextureRegion(ghostTexture, 96, 64, 16, 16);
        treasureRegion = new TextureRegion(treasureTexture, 64, 64, 16, 16);
        opentreasureRegion = new TextureRegion(opentreasureTexture, 48, 64, 16, 16);
        floorRegion = new TextureRegion(floorTexture, 0, 128, 16, 16);

        verticalWallRegion = new TextureRegion(verticalWallTexture, 112, 0, 16, 16);
        horizontalWallRegion = new TextureRegion(horizontalWallTexture, 96, 0, 16, 16);

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

            if (inCooldown) {
                cooldownTimer -= delta;

                if (cooldownTimer <= 0) {
                    inCooldown = false;
                }
            }

        game.isKeyCollected();


        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pause();
            game.setScreen(new PauseScreen(game, this));
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
                // 2 == door
            } else if (value == 2) {
                float doorX = x * tileSize;
                float doorY = y * tileSize;
                game.getSpriteBatch().draw(closedDoorRegion, doorX, doorY, tileSize, tileSize);

                // Only draw the doors if the key has been collected
                if (!game.isKeyCollected()) {
                    game.getSpriteBatch().draw(openDoorRegion, doorX, doorY, tileSize, tileSize);
                }

                if (collidesWithCharacter(x,y) && game.isKeyCollected()) {
                        game.setScreen(new WinScreen(game));
                }

                // 3 == fire
            } else if (value == 3) {
                TextureRegion fireFrame = game.getFireAnimation().getKeyFrame(sinusInput, true);
                game.getSpriteBatch().draw(fireFrame, x*tileSize, y*tileSize, tileSize, tileSize);
                if (collidesWithCharacter(x, y)) {
                    collisionOccurred = true;
                      decreaseLives(delta);
                      game.collisionSound();
                }

                // 4 == ghosts
            } else if (value == 4) {
                game.getSpriteBatch().draw(ghostRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                 if (collidesWithCharacter(x, y)) {
                     collisionOccurred = true;
                     decreaseLives(delta);
                }

                // 5 = treasure (key)
            } else if (value == 5) {
                //Check if the character collides with the key
                if (collidesWithCharacter(x, y) && Gdx.input.isKeyPressed(Input.Keys.SPACE) ) {
                    game.keyCollectionStatus();
                    game.getSpriteBatch().draw(opentreasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                    game.doorOpeningSound();
                }
                else if (game.isKeyCollected()==true) {
                    game.getSpriteBatch().draw(opentreasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                }
                else {
                    game.getSpriteBatch().draw(treasureRegion, x * tileSize, y * tileSize, tileSize, tileSize);
                }

            }
        }

        // Draw the character
        sinusInput += delta;
        game.getSpriteBatch().draw(
                currentAnimation.getKeyFrame(sinusInput, true),
                characterX,
                characterY,
                tileSize+10,
                tileSize+40
        );

        game.getSpriteBatch().end(); // Important to call this after drawing everything

        // Check if the player is in cooldown
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

            game.getSpriteBatch().setColor(originalColor);


            game.getSpriteBatch().end();

            if (cooldownTimer <= 0) {
                inCooldown = false;
            }
        }

        hud.draw();
        camera.update();

    }

    private boolean checkCollision(float x, float y) {
        // Iterate through mazeMap to check for collision with walls
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            int value = entry.getValue();
            if (value == 0 || value == 2 && game.isKeyCollected() == false) { // Wall
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
        float characterX = camera.position.x -96; // Adjusted for character size
        float characterY = camera.position.y -64; // Adjusted for character size

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
        hud.resize(width, height);
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
    }

    public void decreaseLives(float delta) {
        if (!inCooldown && collisionOccurred) {
            livesRemaining--;
            inCooldown = true;
            cooldownTimer = COOLDOWN_DURATION;
        }

        hud.update(livesRemaining);
        if (livesRemaining <= 0) {
            // Set game over state
            game.setScreen(new GameOverScreen(game));
        }
        game.collisionSound();
    }
}