package de.tum.cit.ase.maze;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.Screens.MenuScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.io.IOException;

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
    private Animation<TextureRegion> characterDownAnimation, characterUpAnimation, characterLeftAnimation, characterRightAnimation;
    private Animation<TextureRegion> characterStillDownAnimation, characterStillUpAnimation, characterStillLeftAnimation, characterStillRightAnimation;
    private Animation<TextureRegion> fireAnimation;
    private Animation<TextureRegion> ghostAnimation;
    public NativeFileChooser nativeFileChooser;
    private int currentLevel = 1; // Default level
    private boolean keyCollected = false;
    private int livesRemaining = 5;
    private Sound openDoorSound, collisionSound, gameWinSound, gameOverSound;
    private boolean paused = false;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */

    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.nativeFileChooser = fileChooser;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();

        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation
        this.loadFireAnimation(); // Load fire animation
        this.loadGhostAnimation(); // Load ghost animation

        hudSpriteBatch = new SpriteBatch();

        // Play some background music
        // Background sound
        Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background2.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        openDoorSound = Gdx.audio.newSound(Gdx.files.internal("door-opening2.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision-sound.wav"));
        gameWinSound = Gdx.audio.newSound(Gdx.files.internal("game-win.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("game-over.wav"));

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
        Array<TextureRegion> stillDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> stillUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> stillRightFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> stillLeftFrames = new Array<>(TextureRegion.class);

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
        for (int col = 6; col < 8; col++) {
            stillDownFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }
        for (int col = 6; col < 8; col++) {
            stillUpFrames.add(new TextureRegion(walkSheet, col * frameWidth, 64, frameWidth, frameHeight));
        }
        for (int col = 6; col < 8; col++) {
            stillLeftFrames.add(new TextureRegion(walkSheet, col * frameWidth, 96, frameWidth, frameHeight));
        }
        for (int col = 6; col < 8; col++) {
            stillRightFrames.add(new TextureRegion(walkSheet, col * frameWidth, 32, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkDownFrames);
        characterUpAnimation = new Animation<>(0.1f, walkUpFrames);
        characterLeftAnimation = new Animation<>(0.1f, walkLeftFrames);
        characterRightAnimation = new Animation<>(0.1f, walkRightFrames);
        characterStillDownAnimation = new Animation<>(2f, stillDownFrames);
        characterStillUpAnimation = new Animation<>(2f, stillUpFrames);
        characterStillRightAnimation = new Animation<>(2f, stillRightFrames);
        characterStillLeftAnimation = new Animation<>(2f, stillLeftFrames);

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

    public void loadGhostAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("mobs.png"));

        int animationFrames = 9;
        int frameWidth = 16;
        int frameHeight = 16;

        Array<TextureRegion> walkGhostFrames = new Array<>(TextureRegion.class);

        for (int col = 6; col < animationFrames; col++) {
            walkGhostFrames.add(new TextureRegion(walkSheet, col * frameWidth, 64, frameWidth, frameHeight));
        }

        ghostAnimation = new Animation<>(0.3f, walkGhostFrames);

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

    public Animation<TextureRegion> getFireAnimation() {
        return fireAnimation;
    }

    // Add getters for game state variables if needed
    public boolean isKeyCollected() {
        return keyCollected;
    }

    public boolean keyCollectionStatus() {
        return keyCollected = true;
    }

    public void doorOpeningSound() {
        openDoorSound.play();
    }

    public void collisionSound() {
        collisionSound.play();
    }

    public void gameWinSound() {gameWinSound.play();}

    public void gameOverSound() {gameOverSound.play();}

    public Animation<TextureRegion> getCharacterStillDownAnimation() {
        return characterStillDownAnimation;
    }

    public Animation<TextureRegion> getCharacterStillUpAnimation() {
        return characterStillUpAnimation;
    }

    public Animation<TextureRegion> getCharacterStillLeftAnimation() {
        return characterStillLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterStillRightAnimation() {
        return characterStillRightAnimation;
    }

    public Animation<TextureRegion> getGhostAnimation() {
        return ghostAnimation;
    }

    public boolean isPaused() {
        return paused;
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}