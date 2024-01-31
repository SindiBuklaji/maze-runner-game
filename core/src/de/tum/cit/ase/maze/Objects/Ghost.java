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
        // Iterate through mazeMap to check for collision with walls
        for (Map.Entry<String, Integer> entry : mazeMap.entrySet()) {
            int value = entry.getValue();
            if (value == 0 || value == 2 && !game.isKeyCollected()) { // Wall
                String[] coordinates = entry.getKey().split(",");
                float wallX = Integer.parseInt(coordinates[0]) * gameScreen.getTileSize();
                float wallY = Integer.parseInt(coordinates[1]) * gameScreen.getTileSize();
                float offset = 48f; // Adjust the offset as needed

                if (currentX < wallX-10 + gameScreen.getTileSize() && currentX + offset > wallX-20 && currentY < wallY-10 + gameScreen.getTileSize() && currentY + offset > wallY) {
                    // Collision detected with a wall
                    return true;
                }
            }
        }
        // Check for collision with map boundaries
        if (x < 0 || gameScreen.getMaxX() > gameScreen.getMazeWidth() || y < 0 || gameScreen.getMaxY() > gameScreen.getMazeHeight()) {
            // Collision detected with the map boundaries
            return true;
        }

        // No collision detected
        return false;
    }

    public void Move() {
        float minDistanceToMove = 1f;
        float maxDistanceToMove = 5f;

        float distanceToMove = minDistanceToMove + (float) Math.random() * (maxDistanceToMove - minDistanceToMove);

        float nextPosX = currentX + moveDir.x * Gdx.graphics.getDeltaTime() * ghostSpeed;
        float nextPosY = currentY + moveDir.y * Gdx.graphics.getDeltaTime() * ghostSpeed;

        // Check if the ghost has moved the desired distance
        if (Math.abs(nextPosX - currentX) >= 16 * distanceToMove || Math.abs(nextPosY - currentY) >= 16 * distanceToMove) {
            // Change direction randomly after moving the desired distance
            ChangeMoveDirectionRandomly(null);
        }

        // Rest of the collision and boundary checks remain unchanged
        if (checkCollision(nextPosX, currentY) && moveDir.x == 1) { // right
            ChangeMoveDirectionRandomly(Direction.RIGHT);
        } else if (checkCollision(nextPosX, currentY) && moveDir.x == -1) // left
        {
            ChangeMoveDirectionRandomly(Direction.LEFT);
        } else if (checkCollision(currentX, nextPosY) && moveDir.y == 1) // up
        {
            ChangeMoveDirectionRandomly(Direction.UP);
        } else if (checkCollision(currentX, nextPosY) && moveDir.y == -1) // down
        {
            ChangeMoveDirectionRandomly(Direction.DOWN);
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

        currentX += moveDir.x * Gdx.graphics.getDeltaTime() * ghostSpeed;
        currentY += moveDir.y * Gdx.graphics.getDeltaTime() * ghostSpeed;
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