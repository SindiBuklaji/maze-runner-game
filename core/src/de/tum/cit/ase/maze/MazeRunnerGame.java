package de.tum.cit.ase.maze;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.io.IOException;

import static de.tum.cit.ase.maze.GameScreen.COOLDOWN_DURATION;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game implements ApplicationListener {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;


    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;
    private SpriteBatch hudSpriteBatch;

    // UI Skin
    private Skin skin;

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> fireAnimation;

    // Texture for the player in different positions

    private int lives = 5; // Set the initial number of lives


    // Maze array
    int[][] maze;

    public NativeFileChooser nativeFileChooser;

    private Vector2 characterPosition;

    private int currentLevel = 1; // Default level

    private boolean keyCollected = false;
    private int livesRemaining = 5;
    private Sound openDoorSound;

    private Sound collisionSound;

    private HUDScreen hud;


    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    //for movement
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, shotKeyPressed, spacePressed;

    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.nativeFileChooser = fileChooser;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation
        this.loadFireAnimation(); // Load fire animation

        hudSpriteBatch = new SpriteBatch();

        // Play some background music
        // Background sound
       /* Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play(); */

        openDoorSound = Gdx.audio.newSound(Gdx.files.internal("door-opening.wav"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision-sound.wav"));

        goToMenu(); // Navigate to the menu screen

    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame(int level) throws IOException {
        this.setScreen(new GameScreen(this, level)); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    // Add a method to reset the game state
    public void resetGameState() {
        keyCollected = false;
        livesRemaining = 5; // You can set the initial number of lives as needed
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int animationFrames = 4;
        int frameWidth = 16;
        int frameHeight = 32;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkRightFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkDownFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkUpFrames.add(new TextureRegion(walkSheet, col * frameWidth, 64, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkLeftFrames.add(new TextureRegion(walkSheet, col * frameWidth, 96, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkRightFrames.add(new TextureRegion(walkSheet, col * frameWidth, 32, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkDownFrames);
        characterUpAnimation = new Animation<>(0.1f, walkUpFrames);
        characterLeftAnimation = new Animation<>(0.1f, walkLeftFrames);
        characterRightAnimation = new Animation<>(0.1f, walkRightFrames);

    }

    public void loadFireAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("objects.png"));

        int animationFrames = 5;
        int frameWidth = 16;
        int frameHeight = 16;

        Array<TextureRegion> walkFireFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            walkFireFrames.add(new TextureRegion(walkSheet, col * frameWidth + 64, 48, frameWidth, frameHeight));
        }

        fireAnimation = new Animation<>(0.1f, walkFireFrames);

    }


    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public void resumeGame() {
        setScreen(gameScreen);
    }

    public SpriteBatch getHudSpriteBatch() {
        return hudSpriteBatch;
    }

    public Animation<TextureRegion> getFireAnimation() {
        return fireAnimation;
    }

    // Add getters for game state variables if needed
    public boolean isKeyCollected() {
        return keyCollected;
    }

    public int getLivesRemaining() {
        return livesRemaining;
    }

    // Add methods to update lives and key status based on game events

    public boolean keyCollectionStatus() {
       return keyCollected = true;
    }

    public void doorOpeningSound() {
        openDoorSound.play();
    }

    public void collisionSound() {
        collisionSound.play();
    }

}