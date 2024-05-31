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
    private void preloadSound(String soundFilePath) {
        try {
            URL soundURL = getClass().getResource(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            forageSoundClip = AudioSystem.getClip();
            forageSoundClip.open(audioInputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playSound(String filePath) {
        stopSound(); // Stop any currently playing sound

        try {
            if (forageSoundClip == null || !forageSoundClip.isOpen()) {
                preloadSound(filePath); // Ensure the sound is preloaded
            }
            forageSoundClip.setFramePosition(0); // Reset to the start
            forageSoundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("Stopping the sound.");
        if (forageSoundClip != null && forageSoundClip.isRunning()) {
            forageSoundClip.stop();
            forageSoundClip.close();
        }
    }
}
