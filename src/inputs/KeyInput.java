package inputs;

import app.Game;
import enums.GameState;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class KeyInput extends KeyAdapter {

    private Game game;

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent k) {
        if (Game.gameState == GameState.GAME_LEVEL_ONE || Game.gameState == GameState.GAME_LEVEL_TWO || Game.gameState == GameState.GAME_LEVEL_THREE) {
            try {
                this.game.keyPressed(k);
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }

    public void keyReleased(KeyEvent k) {
        this.game.keyReleased(k);
    }
}
