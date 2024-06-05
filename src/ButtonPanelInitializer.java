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

        // Fixed spacing based on the earlier image
        int buttonWidth = 80;
        int buttonHeight = 30;
        int gap = 20; // Adjust this value to change the space between the buttons

        // Calculate the total width occupied by the buttons and gaps
        int totalWidth = 3 * buttonWidth + 2 * gap;

        // Calculate the starting X position to center the buttons
        int startX = (sceneImagePanelWidth - totalWidth) / 2;

        // Move Button
        JButton moveButton = new JButton("Move");
        moveButton.setBounds(startX, 10, buttonWidth, buttonHeight);
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.moveAction();
            }
        });
        buttonPanel.add(moveButton);

        // Forage Button
        JButton forageButton = new JButton("Forage");
        forageButton.setBounds(startX + buttonWidth + gap, 10, buttonWidth, buttonHeight);
        forageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.forageAction();
            }
        });
        buttonPanel.add(forageButton);

        // Farm Button
        JButton farmButton = new JButton("Farm");
        farmButton.setBounds(startX + 2 * (buttonWidth + gap), 10, buttonWidth, buttonHeight);
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

        // Force repaint and revalidate
        buttonPanel.repaint();
        buttonPanel.revalidate();
        layeredPane.repaint();
        layeredPane.revalidate();

        System.out.println("FarmButton bounds after setting: " + farmButton.getBounds());
        System.out.println("FarmButton visibility after setting: " + farmButton.isVisible());
        System.out.println("FarmButton enabled after setting: " + farmButton.isEnabled());
    }

    // Add a getter method for buttonPanel
    public JPanel getButtonPanel() {
        return buttonPanel;
    }
}