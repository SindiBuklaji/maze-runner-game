package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.Screens.HUDScreen;



public class Exit extends GameObject {
    private TextureRegion closedDoorRegion;
    private TextureRegion openDoorRegion;
    private float x, y;
    private boolean isDoorOpen;

    private MazeRunnerGame game;

    private HUDScreen hud;

    public Exit(TextureRegion closedDoorRegion, TextureRegion openDoorRegion, float x, float y, float width, float height, MazeRunnerGame game) {
        super(closedDoorRegion, x, y, width, height);
        this.closedDoorRegion = closedDoorRegion;
        this.openDoorRegion = openDoorRegion;
        this.game = game;
        this.hud = new HUDScreen(game.getSkin());
        this.x = x;
        this.y = y;
        this.isDoorOpen = false;
    }

    public void render(SpriteBatch spriteBatch) {
        if (!game.isKeyCollected()) {
            spriteBatch.draw(openDoorRegion, x * tileSize, y * tileSize, tileSize, tileSize);
        } else {
            spriteBatch.draw(closedDoorRegion, x * tileSize, y * tileSize, tileSize, tileSize);
        }
    }
}
