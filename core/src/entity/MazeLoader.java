package entity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MazeLoader {

    private Properties properties;
    private int[][] maze;

    public MazeLoader() {
        properties = new Properties();
    }

        public void loadMaze(String fileName) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (input == null) {
                    System.out.println("Sorry, unable to find " + fileName);
                    return;
                }

                // Load the properties file
                properties.load(input);

                // Initialize maze dimensions
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;

                // Find maximum coordinates to determine maze size
                for (String key : properties.stringPropertyNames()) {
                    String[] coordinates = key.split(",");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }

                // Initialize maze with zeros
                maze = new int[maxX + 1][maxY + 1];

                // Populate maze with values from properties
                for (String key : properties.stringPropertyNames()) {
                    String[] coordinates = key.split(",");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    int value = Integer.parseInt(properties.getProperty(key));
                    maze[x][y] = value;
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

    public int[][] getMaze() {
        return maze;
    }

    public static void main(String[] args) {
        MazeLoader mazeLoader = new MazeLoader();
        mazeLoader.loadMaze("level-1.properties");
        int[][] maze = mazeLoader.getMaze();

        // Print the loaded maze
        for (int[] row : maze) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
