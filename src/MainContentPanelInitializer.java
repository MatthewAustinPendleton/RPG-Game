import javax.swing.*;
import java.awt.*;

/**
 * Initializes the main content panel of the game.
 */
public class MainContentPanelInitializer {

    private GameFrame gameFrame;

    public MainContentPanelInitializer(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    /**
     * Initializes the main content panel with the specified layered pane and current scene.
     *
     * @param layeredPane  the layered pane to add the main content panel to
     * @param currentScene the current scene
     */
    public void initMainContentPanel(JLayeredPane layeredPane, Scene currentScene) {
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(null);
        mainContentPanel.setBounds(50, 20, 850, 650);
        mainContentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        mainContentPanel.setBackground(Color.BLACK); // Set background to black

        BackgroundPanel sceneImagePanel = new BackgroundPanel(currentScene.getImagePath());
        sceneImagePanel.setBounds(0, 0, 850, 600);
        sceneImagePanel.setBorder(BorderFactory.createEmptyBorder());

        JLabel sceneDescription = new JLabel(currentScene.getDescription(), SwingConstants.CENTER);
        sceneDescription.setFont(new Font("Serif", Font.BOLD, 18));
        sceneDescription.setBounds(0, 600, 850, 50);
        sceneDescription.setBackground(Color.LIGHT_GRAY);
        sceneDescription.setOpaque(true);

        mainContentPanel.add(sceneImagePanel);
        mainContentPanel.add(sceneDescription);
        layeredPane.add(mainContentPanel, JLayeredPane.DEFAULT_LAYER);

        gameFrame.setSceneImagePanel(sceneImagePanel);
        gameFrame.setSceneDescription(sceneDescription);
    }
}
