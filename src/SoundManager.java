import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class SoundManager {

    private Clip forageSoundClip;

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

    public void playCollectSound(int itemWeight) {
        if (itemWeight > 15) {
            playSound("/collect.wav");
        } else {
            playSpecialCollectSound();
        }
    }

    private void playSpecialCollectSound() {
        playSound("/specialCollect.wav");
        playSound("/wow.wav");
    }

    public void stopSound() {
        System.out.println("Stopping the sound.");
        if (forageSoundClip != null && forageSoundClip.isRunning()) {
            forageSoundClip.stop();
            forageSoundClip.close();
        }
    }
}
