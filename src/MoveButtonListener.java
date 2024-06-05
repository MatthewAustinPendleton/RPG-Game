import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MoveButtonListener implements ActionListener {
    private GameFrame gameFrame;

    public MoveButtonListener(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Scene currentScene = gameFrame.getCurrentScene();
        List<String> adjacentScenes = currentScene.getAdjacentScenes();

        // Debug statements
        System.out.println("Attempting to move from Scene: " + currentScene.getName());
        System.out.println("Adjacent Scenes: " + adjacentScenes);

        if (adjacentScenes.isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame, "No adjacent scenes available.");
            return;
        }

        // Move to the first adjacent scene for simplicity (or implement selection logic)
        String nextSceneName = adjacentScenes.get(0);
        Scene nextScene = gameFrame.getScenes().get(nextSceneName);

        if (nextScene != null) {
            gameFrame.setCurrentScene(nextScene);
        } else {
            JOptionPane.showMessageDialog(gameFrame, "Scene not found: " + nextSceneName);
        }
    }
}