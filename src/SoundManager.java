import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Manages the playing of sound effects in the application.
 */
public class SoundManager {

    private Clip forageSoundClip;

    /**
     * Plays a sound from the specified file path.
     *
     * @param soundFilePath the path to the sound file to be played
     */
    public void playSound(String soundFilePath) {
        try {
            URL soundURL = getClass().getResource(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioInputStream);
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Plays a collect sound effect based on the weight of the item.
     * For items with a probability weight greater than 15, a regular collect sound is played.
     * For items with a probability weight of 15 or less, a special collect sound is played, followed by "wow!"
     *
     * @param itemWeight the weight of the collected item.
     */
    public void playCollectSound(int itemWeight) {
        if (itemWeight > 15) {
            playSound("/collect.wav");
        } else {
            playSpecialCollectSound();
        }
    }

    /**
     * Plays a special collect sound followed by the "wow" noise.
     */
    private void playSpecialCollectSound() {
        playSound("/specialCollect.wav");
        playSound("/wow.wav");
    }

    /**
     * Stops the currently playing sound.
     */
    public void stopSound() {
        if (forageSoundClip != null && forageSoundClip.isRunning()) {
            forageSoundClip.stop();
            forageSoundClip.close();
        }
    }
}
