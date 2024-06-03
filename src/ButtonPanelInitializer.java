import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ButtonPanelInitializer {
    private GameFrame gameFrame;
    private JPanel buttonPanel;

    public ButtonPanelInitializer(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public void initButtonPanel(JLayeredPane layeredPane) {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setOpaque(false); // Disable the background

        // Set the bounds of the button panel to be below the scene description
        int sceneImagePanelWidth = gameFrame.getSceneImagePanel().getWidth();
        int xOffset = 50;
        int yOffset = 18;
        buttonPanel.setBounds(xOffset, 650 + yOffset, sceneImagePanelWidth, 50); // Adjust as necessary

        // Move Button
        JButton moveButton = new JButton("Move");
        moveButton.setBounds((sceneImagePanelWidth / 2) - 100, 10, 80, 30); // Adjusted positions to center
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.moveAction();
            }
        });
        buttonPanel.add(moveButton);

        // Forage Button
        JButton forageButton = new JButton("Forage");
        forageButton.setBounds((sceneImagePanelWidth / 2) + 20, 10, 80, 30); // Adjusted positions to center
        forageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.forageAction();
            }
        });
        buttonPanel.add(forageButton);

        // Bank Button
        JButton bankButton = new JButton("Bank");
        bankButton.setBounds(0, 0, 0, 0); // Initially hidden
        bankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.toggleBankWindow();
            }
        });
        buttonPanel.add(bankButton);

        // Deposit All Button
        JButton depositAllButton = new JButton("Deposit All");
        depositAllButton.setBounds(0, 0, 0, 0); // Initially hidden
        depositAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.depositAllItemsToBank();
            }
        });
        buttonPanel.add(depositAllButton);

        // Farm Button
        JButton farmButton = new JButton("Farm");
        farmButton.setBounds(0, 0, 0, 0); // Initially hidden
        farmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameFrame, "Welcome to your farm!");
                updateFarmSceneAdjacency();
                gameFrame.setCurrentScene(gameFrame.getScenes().get("farm"));
            }

            private void updateFarmSceneAdjacency() {
                Scene currentScene = gameFrame.getCurrentScene();
                Scene farmScene = gameFrame.getScenes().get("farm");

                // Ensure currentScene is not null
                if (currentScene != null && farmScene != null) {
                    List<String> farmAdjacent = new ArrayList<>();
                    farmAdjacent.add(currentScene.getName());

                    farmScene.setAdjacentScenes(farmAdjacent);
                    gameFrame.getScenes().put("farm", farmScene);

                    // Debug statements
                    System.out.println("Current Scene: " + currentScene.getName());
                    System.out.println("Farm Scene Adjacency Updated: " + farmScene.getAdjacentScenes());
                    System.out.println("Scenes Map: " + gameFrame.getScenes());
                } else {
                    System.err.println("Error: currentScene or farmScene is null.");
                }
            }
        });
        buttonPanel.add(farmButton);

        // Add buttons to the mainButtons list
        gameFrame.mainButtons.add(moveButton);
        gameFrame.mainButtons.add(forageButton);
        gameFrame.mainButtons.add(bankButton);
        gameFrame.mainButtons.add(depositAllButton);
        gameFrame.mainButtons.add(farmButton);

        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER); // Add to a higher layer

        gameFrame.setMoveButton(moveButton);
        gameFrame.setForageButton(forageButton);
        gameFrame.setBankButton(bankButton);
        gameFrame.setDepositAllButton(depositAllButton);
        gameFrame.setFarmButton(farmButton);

        // Initialize button states based on conditions
        gameFrame.updateFarmButtonVisibility();
    }

    // Add a getter method for buttonPanel
    public JPanel getButtonPanel() {
        return buttonPanel;
    }
}
