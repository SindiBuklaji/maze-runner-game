package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.maze.Direction;
import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.MazeRunnerGame;

import java.util.*;

public class Ghost {

    private  float ghostSpeed;
    private Vector2 moveDir;
    private TextureRegion ghostFrame;
    private float currentX;
    private float currentY;
    private GameScreen gameScreen;
    private Map<Direction,Vector2> possibleDirections;
    private MazeRunnerGame game;
    private Map<String, Integer> mazeMap;
    private float speedFactor;


    public  Ghost(MazeRunnerGame game, Map<String, Integer> mazeMap, GameScreen gameScreen, float ghostSpeed, TextureRegion ghostFrame, float currentX, float currentY){
        this.game = game;
        this.mazeMap = mazeMap;
        this.gameScreen = gameScreen;
        this.ghostSpeed = ghostSpeed;
        this.ghostFrame = ghostFrame;
        this.currentX = currentX;
        this.currentY = currentY;

        possibleDirections = new HashMap<>();
        possibleDirections.put(Direction.UP, new Vector2(0,1));
        possibleDirections.put(Direction.DOWN, new Vector2(0,-1));
        possibleDirections.put(Direction.RIGHT, new Vector2(1,0));
        possibleDirections.put(Direction.LEFT, new Vector2(-1,0));
        List<Direction> keysList = new ArrayList<>(possibleDirections.keySet());
        Direction randomKey = getRandomElement(keysList);
        moveDir = possibleDirections.get(randomKey);

    }
    private  void ChangeMoveDirectionRandomly(Direction keyToExclude){
        List<Direction> keysList = new ArrayList<>(possibleDirections.keySet());
        keysList.remove(keyToExclude);
        Direction randomKey = getRandomElement(keysList);
        moveDir = possibleDirections.get(randomKey);
    }

    private boolean checkCollision(float x, float y) {
        int tileX = (int) (x / gameScreen.getTileSize());
        int tileY = (int) (y / gameScreen.getTileSize());

        // Check collision with walls
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = tileX + dx;
                int checkY = tileY + dy;

                if (mazeMap.containsKey(checkX + "," + checkY)) {
                    int value = mazeMap.get(checkX + "," + checkY);
                    if ((value == 0 || (value == 2 && !game.isKeyCollected())) && x >= checkX * gameScreen.getTileSize() &&
                            x < (checkX + 1) * gameScreen.getTileSize() && y >= checkY * gameScreen.getTileSize() &&
                            y < (checkY + 1) * gameScreen.getTileSize()) {
                        // Collision detected with a wall
                        return true;
                    }
                }
            }
        }

        // Check collision with map boundaries
        return x < 0 || x > gameScreen.getMazeWidth() || y < 0 || y > gameScreen.getMazeWidth();
    }

    public void Move() {
        float minDistanceToMove = 1f;
        float maxDistanceToMove = 5f;

        float distanceToMove = minDistanceToMove + (float) Math.random() * (maxDistanceToMove - minDistanceToMove);

        float nextPosX = currentX + moveDir.x * Gdx.graphics.getDeltaTime() * ghostSpeed;
        float nextPosY = currentY + moveDir.y * Gdx.graphics.getDeltaTime() * ghostSpeed;

        boolean collided = false;

        // Rest of the collision and boundary checks remain unchanged
        if (moveDir.x == 1 && checkCollision(nextPosX, currentY)) { // right
            ChangeMoveDirectionRandomly(Direction.RIGHT);
            collided = true;
        } else if (moveDir.x == -1 && checkCollision(nextPosX, currentY)) // left
        {
            ChangeMoveDirectionRandomly(Direction.LEFT);
            collided = true;
        } else if (moveDir.y == 1 && checkCollision(currentX, nextPosY)) // up
        {
            ChangeMoveDirectionRandomly(Direction.UP);
            collided = true;
        } else if (moveDir.y == -1 && checkCollision(currentX, nextPosY)) // down
        {
            ChangeMoveDirectionRandomly(Direction.DOWN);
            collided = true;
        }

        if (nextPosX >= gameScreen.mazeHeight) { // Right
            ChangeMoveDirectionRandomly(Direction.RIGHT);
        } else if (nextPosX <= 0) // Left
        {
            ChangeMoveDirectionRandomly(Direction.LEFT);
        } else if (nextPosY >= gameScreen.mazeWidth) // UP
        {
            ChangeMoveDirectionRandomly(Direction.UP);
        } else if (nextPosY <= 0) // down
        {
            ChangeMoveDirectionRandomly(Direction.DOWN);
        }

        //Check collision with map boundaries
        if (!collided && (nextPosX >= gameScreen.getMazeWidth() || nextPosX <= 0 || nextPosY >= gameScreen.getMazeHeight() || nextPosY <= 0)) {
            ChangeMoveDirectionRandomly(null); }

        if(!collided) {
        currentX += moveDir.x * Gdx.graphics.getDeltaTime() * ghostSpeed;
        currentY += moveDir.y * Gdx.graphics.getDeltaTime() * ghostSpeed;
        }
    }




    public TextureRegion getGhostFrame() {
        return ghostFrame;
    }


    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }
    private <T> T getRandomElement(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}