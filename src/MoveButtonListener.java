import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * MoveButtonListener handles the action of moving to an adjacent scene.
 */
public class MoveButtonListener implements ActionListener {

    private GameFrame gameFrame;

    /**
     * Constructs a MoveButtonListener with the specified game frame.
     *
     * @param gameFrame the game frame
     */
    public MoveButtonListener(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> adjacentScenes = gameFrame.getCurrentScene().getAdjacentScenes();
        if (adjacentScenes.isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame,"No adjacent scenes available.");
            return;
        }
        if (adjacentScenes.size() == 1) {
            String nextSceneKey = adjacentScenes.get(0);
            Scene nextScene = gameFrame.getScenes().get(nextSceneKey);
            gameFrame.setCurrentScene(nextScene);
            gameFrame.updateScene();
        } else {
            String nextSceneKey = (String) JOptionPane.showInputDialog(
                    gameFrame,
                    "Choose your next destination: ",
                    "Move",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    adjacentScenes.toArray(),
                    adjacentScenes.get(0)
            );
            if (nextSceneKey != null) {
                Scene nextScene = gameFrame.getScenes().get(nextSceneKey);
                gameFrame.setCurrentScene(nextScene);
                gameFrame.updateScene();
            }
        }
    }
}
