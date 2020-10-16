package audio;

import app.Game;
import enums.StateSound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by MARIO on 2/13/2017.
 */
public class Music {

    public static Clip clip;

    public static void music() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        String gongFile = "./resources/audio/Forest_Night.wav";
        InputStream in = new BufferedInputStream(new FileInputStream(gongFile));
        clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(in));
    }

    public static void dragonFire() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (Game.stateSound == StateSound.ON) {
            String dr = "./resources/audio/DragonFire.wav";
            InputStream in = new BufferedInputStream(new FileInputStream(dr));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.start();
        }
    }

    public static void enemyDie() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (Game.stateSound == StateSound.ON) {
            String enDie = "./resources/audio/EnemyDie.wav";
            InputStream in = new BufferedInputStream(new FileInputStream(enDie));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.start();
        }
    }

    public static void dragonRoar() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (Game.stateSound == StateSound.ON) {
            String dragonRoar = "./resources/audio/DragonRoar.wav";
            InputStream in = new BufferedInputStream(new FileInputStream(dragonRoar));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.start();
        }
    }

    public static void bossDies() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (Game.stateSound == StateSound.ON) {
            String bossDies = "./resources/audio/BossDies.wav";
            InputStream in  = new BufferedInputStream(new FileInputStream(bossDies));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.start();
        }
    }

    public static void bossShoots() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (Game.stateSound == StateSound.ON) {
            String bossShoots = "./resources/audio/BossFire.wav";
            InputStream in  = new BufferedInputStream(new FileInputStream(bossShoots));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.start();
        }
    }

    public static void bossShotHit() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (Game.stateSound == StateSound.ON) {
            String bossShotHit = "./resources/audio/BossShotHit.wav";
            InputStream in  = new BufferedInputStream(new FileInputStream(bossShotHit));
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.start();
        }
    }
}

