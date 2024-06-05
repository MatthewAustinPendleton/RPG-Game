import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanelInitializer {
    private GameFrame gameFrame;
    private JPanel buttonPanel;

    public ButtonPanelInitializer(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public void initButtonPanel(JLayeredPane layeredPane) {
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Disable the background

        // Use FlowLayout with reduced gaps
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // Set the bounds of the button panel to be below the scene description
        int sceneImagePanelWidth = gameFrame.getSceneImagePanel().getWidth();
        int xOffset = 50;
        int yOffset = 18;
        buttonPanel.setBounds(xOffset, 650 + yOffset, sceneImagePanelWidth - 2 * xOffset, 100); // Adjust as necessary

        // Move Button
        JButton moveButton = createButton("Move", e -> gameFrame.moveAction());
        adjustButtonWidth(moveButton);
        buttonPanel.add(moveButton);

        // Forage Button
        JButton forageButton = createButton("Forage", e -> gameFrame.forageAction());
        adjustButtonWidth(forageButton);
        buttonPanel.add(forageButton);

        // Farm Button
        JButton farmButton = createButton("Farm", e -> {
            JOptionPane.showMessageDialog(gameFrame, "Welcome to your farm!");
            gameFrame.setCurrentScene(gameFrame.getScenes().get("farm"));
        });
        adjustButtonWidth(farmButton);
        buttonPanel.add(farmButton);

        // Bank Button
        JButton bankButton = createButton("Bank", e -> gameFrame.toggleBankWindow());
        adjustButtonWidth(bankButton);
        buttonPanel.add(bankButton);

        // Deposit All Button
        JButton depositAllButton = createButton("Deposit All", e -> gameFrame.depositAllItemsToBank());
        adjustButtonWidth(depositAllButton);
        buttonPanel.add(depositAllButton);

        // Add buttons to the layered pane
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER); // Add to a higher layer

        gameFrame.setMoveButton(moveButton);
        gameFrame.setForageButton(forageButton);
        gameFrame.setBankButton(bankButton);
        gameFrame.setDepositAllButton(depositAllButton);
        gameFrame.setFarmButton(farmButton);

        // Initialize button states based on conditions
        gameFrame.updateFarmButtonVisibility();
        gameFrame.updateButtonStates();

        // Force repaint and revalidate
        buttonPanel.repaint();
        buttonPanel.revalidate();
        layeredPane.repaint();
        layeredPane.revalidate();

        System.out.println("FarmButton bounds after setting: " + farmButton.getBounds());
        System.out.println("FarmButton visibility after setting: " + farmButton.isVisible());
        System.out.println("FarmButton enabled after setting: " + farmButton.isEnabled());
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        System.out.println("Created button with text: " + text);
        System.out.println("Button text: " + button.getText());
        return button;
    }

    private void adjustButtonWidth(JButton button) {
        int preferredWidth = FontUtil.calculatePreferredWidth(button);
        button.setPreferredSize(new Dimension(preferredWidth, button.getPreferredSize().height));
    }


    public void addButton(JButton button, GridBagConstraints gbc) {
        buttonPanel.add(button, gbc);
        gameFrame.mainButtons.add(button);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void removeButton(JButton button) {
        buttonPanel.remove(button);
        gameFrame.mainButtons.remove(button);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    // Add a getter method for buttonPanel
    public JPanel getButtonPanel() {
        return buttonPanel;
    }
}
