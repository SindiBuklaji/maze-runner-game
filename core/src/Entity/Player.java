package Entity;

import de.tum.cit.ase.maze.GameScreen;
import de.tum.cit.ase.maze.MazeRunnerGame;

public class Player extends Entity {

    GameScreen gameScreen;
    MazeRunnerGame mazeRunnerGame;

    public Player(GameScreen gameScreen, MazeRunnerGame mazeRunnerGame) {
        this.gameScreen = gameScreen;
        this.mazeRunnerGame = mazeRunnerGame;
    }

    public void setDefaultValues(){
        x=100;
        y=100;
        speed=4;
    }
}
