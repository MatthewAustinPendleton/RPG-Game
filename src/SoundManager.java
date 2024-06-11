import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> soundClips;
    private Clip foragingClip;

    public SoundManager() {
        soundClips = new HashMap<>();
        foragingClip = null;
    }

    private void preloadSound(String soundFilePath) {
        try {
            URL soundURL = getClass().getResource(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundClips.put(soundFilePath, clip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playSound(String filePath) {
        Clip clip = soundClips.get(filePath);
        if (clip == null) {
            preloadSound(filePath);
            clip = soundClips.get(filePath);
        }
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void playForagingSound() {
        if (foragingClip == null) {
            preloadSound("/foraging.wav");
            foragingClip = soundClips.get("/foraging.wav");
        }
        if (foragingClip != null) {
            foragingClip.setFramePosition(0);
            foragingClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopForagingSound() {
        if (foragingClip != null && foragingClip.isRunning()) {
            foragingClip.stop();
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

    public void stopSound(String filePath) {
        Clip clip = soundClips.get(filePath);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
        if (foragingClip != null && foragingClip.isRunning()) {
            foragingClip.stop();
        }
    }
}