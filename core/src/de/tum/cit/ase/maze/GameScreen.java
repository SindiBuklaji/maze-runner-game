package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entity.MazeLoader;
import entity.Player;
import entity.Wall;

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
     /*

    private Texture playerUp1;
    private Texture playerUp2;
    private Texture playerUp3;
    private Texture playerUp4;

    private Texture playerDown1;
    private Texture playerDown2;
    private Texture playerDown3;
    private Texture playerDown4;

    private Texture playerLeft1;
    private Texture playerLeft2;
    private Texture playerLeft3;
    private Texture playerLeft4;

    private Texture playerRight1;
    private Texture playerRight2;
    private Texture playerRight3;
    private Texture playerRight4;

    // Texture for the wall in different positions
    private Texture wallmiddle1;
    private Texture wallmiddle2;
    private Texture wallUp1;
    private Texture wallUp2;

    private float tileSize = 100;
    private List<Wall> walls;

    private Player player;

    private int[][] maze;
     */

    private Map<String, Integer> mazeMap;
    private int tileSize = 64;

    private TextureRegion wallRegion;
    private TextureRegion entryRegion;
    private TextureRegion exitRegion;
    private TextureRegion fireRegion;
    private TextureRegion ghostRegion;

    int mazeWidth;
    int mazeHeight;

    //private Viewport viewport;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */

    public GameScreen(MazeRunnerGame game) throws IOException {
        this.game = game;

        // Calculate maze dimensions based on window size
        mazeWidth = Gdx.graphics.getWidth();
        mazeHeight = Gdx.graphics.getHeight();

        FileHandle fileHandle = Gdx.files.internal("maps/level-1.properties");
        InputStream inputStream = fileHandle.read();
        Properties properties = new Properties();

        Texture wallTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture entryTexture = new Texture(Gdx.files.internal("basictiles.png"));
        Texture exitTexture = new Texture(Gdx.files.internal("things.png"));
        Texture fireTexture = new Texture(Gdx.files.internal("things.png"));
        Texture ghostTexture = new Texture(Gdx.files.internal("mobs.png"));

        // Load your textures
        wallRegion = new TextureRegion(wallTexture, 0, 0, 16, 16);
        entryRegion = new TextureRegion(entryTexture, 6, 6, 16, 16);
        exitRegion = new TextureRegion(exitTexture, 0, 0, 16, 16);
        fireRegion = new TextureRegion(fireTexture, 7, 7, 16, 16);
        ghostRegion = new TextureRegion(ghostTexture, 12, 12, 16, 16);

        properties.load(inputStream);

        mazeMap = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            String[] coordinates = key.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int value = Integer.parseInt(properties.getProperty(key));
            mazeMap.put(x + "," + y, value);
        }

       /* MazeLoader mazeLoader = new MazeLoader();
        mazeLoader.loadMaze("maps/level-1.properties");
        maze = mazeLoader.getMaze();

        player = new Player(
                game.maze[0].length * 0.5f * tileSize,
                game.maze.length * 0.5f * tileSize,
                new Animation<>(0.2f, playerUp1, playerUp2, playerUp3, playerUp4),
                new Animation<>(0.2f, playerDown1, playerDown2, playerDown3, playerDown4),
                new Animation<>(0.2f, playerLeft1, playerLeft2, playerLeft3, playerLeft4),
                new Animation<>(0.2f, playerRight1, playerRight2, playerRight3, playerRight4)
        );

        */


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
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        camera.update(); // Update the camera
/*
        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
        float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);

 */


        // Set up and begin drawing with the sprite batch
        //SpriteBatch batch = new SpriteBatch();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        // Render the text
        //   font.draw(game.getSpriteBatch(), "Press ESC to go to menu", textX, textY);

        // Draw the character next to the text :) / We can reuse sinusInput here
       /* game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                textX - 96,
                textY - 64,
                64,
                128
        );

        game.getSpriteBatch().draw(
                player.getCurrentFrame(),
                player.getX() - 96,
                player.getY() - 64,
                64,
                128
        );

        */

        // ShapeRenderer shapeRenderer = new ShapeRenderer();
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //batch.begin();

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
            }

            // shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
        }

        //  shapeRenderer.end();
        // render the maze
        //   renderMaze();
        // batch.end();


        game.getSpriteBatch().end(); // Important to call this after drawing everything
        camera.update();

    }

    public void renderMaze() {
        // int[][] mazeArray = maze; // Replace this with your actual maze array reference

        /* float tileSize = 100;
        // Adjust this based on the size of your maze tiles
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                float x = col * tileSize;
                float y = row * tileSize;

                // Adjust these conditions based on your maze elements
                switch (maze[row][col]) {
                    case 0:
                        // Empty path, you can leave it blank or draw a floor texture
                        break;
                    case 1:
                        // Wall, draw the appropriate wall texture based on position
                        drawWallTexture(col, row);
                        break;
                    // Add more cases for other maze elements if needed
                }
            }
        }

        //render the walls
        for (Wall wall : walls) {
            wall.draw(game.getSpriteBatch());
        }

         */
    }

   /* private Texture drawWallTexture(int col, int row) {
        // Implement your logic to determine the wall texture based on col and row values
        // You can use conditions or calculations to decide the appropriate texture
        // For now, let's just draw a placeholder texture
        return (col + row) % 2 == 0 ? new Texture("wallup1.png") : new Texture("wallup2.png");
    }

    */

    // Draw the player if the current cell is an empty path (0)
    ////   drawPlayerTexture(x, y, col, row);

    //private void drawPlayerTexture(float x, float y, int col, int row) {
    // Determine the appropriate player texture based on position
    // You can use conditions based on col and row values
    // Adjust this based on your actual player texture logic

    //  Texture playerTexture = determinePlayerTexture(col, row);

    // Draw the player texture
    //game.getSpriteBatch().draw(playerTexture, x, y, 32, 32);
    //}

    // private Texture determinePlayerTexture(int col, int row) {
    // Implement your logic to determine the player texture based on col and row values
    // Return the appropriate player texture

    // Example logic:
    // For simplicity, let's alternate between two player textures
    //   return (col + row) % 2 == 0 ? getPlayerUp1() : getPlayerDown1();
    //}

   /* private void createWalls() {
        // Add walls to the list based on your maze array
        int[][] mazeArray = maze;
        float tileSize = 200; // Adjust this based on the size of your maze tiles

        for (int row = 0; row < mazeArray.length; row++) {
            for (int col = 0; col < mazeArray[row].length; col++) {
                float x = col * tileSize;
                float y = row * tileSize;

                if (mazeArray[row][col] == -1) {
                    // Create a new wall and add it to the list
                    walls.add(new Wall(x, y));
                }
                // Add more conditions or logic if needed for other types of elements in the maze
            }
        }
    }

    */

    // private void drawEnemyTexture(float x, float y) {
    // Implement logic to draw the enemy or obstacle texture at position (x, y)
    // Use the appropriate enemy or obstacle texture

    // Example:
    //   game.getSpriteBatch().draw(game.getEnemyTexture(), x, y, 32, 32);
    //}


    @Override
    public void resize(int width, int height) {
        // viewport.update(width, height, true);
       // camera.position.set(width * 0.5f * tileSize, height * 0.5f * tileSize, 0);
        //camera.update();
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

    // Additional methods and logic can be added as needed for the game screen


        /* public Texture getPlayerUp1 () {
            return playerUp1;
        }

        public void setPlayerUp1 (Texture playerUp1){
            this.playerUp1 = playerUp1;
        }

        public Texture getPlayerUp2 () {
            return playerUp2;
        }

        public void setPlayerUp2 (Texture playerUp2){
            this.playerUp2 = playerUp2;
        }

        public Texture getPlayerUp3 () {
            return playerUp3;
        }

        public void setPlayerUp3 (Texture playerUp3){
            this.playerUp3 = playerUp3;
        }

        public Texture getPlayerUp4 () {
            return playerUp4;
        }

        public void setPlayerUp4 (Texture playerUp4){
            this.playerUp4 = playerUp4;
        }

        public Texture getPlayerDown1 () {
            return playerDown1;
        }

        public void setPlayerDown1 (Texture playerDown1){
            this.playerDown1 = playerDown1;
        }

        public Texture getPlayerDown2 () {
            return playerDown2;
        }

        public void setPlayerDown2 (Texture playerDown2){
            this.playerDown2 = playerDown2;
        }

        public Texture getPlayerDown3 () {
            return playerDown3;
        }

        public void setPlayerDown3 (Texture playerDown3){
            this.playerDown3 = playerDown3;
        }

        public Texture getPlayerDown4 () {
            return playerDown4;
        }

        public void setPlayerDown4 (Texture playerDown4){
            this.playerDown4 = playerDown4;
        }

        public Texture getPlayerLeft1 () {
            return playerLeft1;
        }

        public void setPlayerLeft1 (Texture playerLeft1){
            this.playerLeft1 = playerLeft1;
        }

        public Texture getPlayerLeft2 () {
            return playerLeft2;
        }

        public void setPlayerLeft2 (Texture playerLeft2){
            this.playerLeft2 = playerLeft2;
        }

        public Texture getPlayerLeft3 () {
            return playerLeft3;
        }

        public void setPlayerLeft3 (Texture playerLeft3){
            this.playerLeft3 = playerLeft3;
        }

        public Texture getPlayerLeft4 () {
            return playerLeft4;
        }

        public void setPlayerLeft4 (Texture playerLeft4){
            this.playerLeft4 = playerLeft4;
        }

        public Texture getPlayerRight1 () {
            return playerRight1;
        }

        public void setPlayerRight1 (Texture playerRight1){
            this.playerRight1 = playerRight1;
        }

        public Texture getPlayerRight2 () {
            return playerRight2;
        }

        public void setPlayerRight2 (Texture playerRight2){
            this.playerRight2 = playerRight2;
        }

        public Texture getPlayerRight3 () {
            return playerRight3;
        }

        public void setPlayerRight3 (Texture playerRight3){
            this.playerRight3 = playerRight3;
        }

        public Texture getPlayerRight4 () {
            return playerRight4;
        }

        public void setPlayerRight4 (Texture playerRight4){
            this.playerRight4 = playerRight4;
        }

        public Texture getWallmiddle1 () {
            return wallmiddle1;
        }

        public void setWallmiddle1 (Texture wallmiddle1){
            this.wallmiddle1 = wallmiddle1;
        }

        public Texture getWallmiddle2 () {
            return wallmiddle2;
        }

        public void setWallmiddle2 (Texture wallmiddle2){
            this.wallmiddle2 = wallmiddle2;
        }

        public Texture getWallUp1 () {
            return wallUp1;
        }

        public void setWallUp1 (Texture wallUp1){
            this.wallUp1 = wallUp1;
        }

        public Texture getWallUp2 () {
            return wallUp2;
        }

        public void setWallUp2 (Texture wallUp2){
            this.wallUp2 = wallUp2;
        }

         */
}

