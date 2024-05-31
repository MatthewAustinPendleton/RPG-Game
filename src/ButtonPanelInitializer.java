import javax.swing.*;
import java.awt.*;

public class ButtonPanelInitializer {

    private GameFrame gameFrame;
    private JPanel buttonPanel;

    public ButtonPanelInitializer(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public void initButtonPanel(JLayeredPane layeredPane) {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton moveButton = new JButton("Move");
        JButton forageButton = new JButton("Forage");
        JButton bankButton = new JButton("Bank");
        JButton depositAllButton = new JButton("Deposit All");

        gameFrame.setMoveButton(moveButton);
        gameFrame.setForageButton(forageButton);
        gameFrame.setBankButton(bankButton);
        gameFrame.setDepositAllButton(depositAllButton);

        gameFrame.mainButtons.add(moveButton);
        gameFrame.mainButtons.add(forageButton);
        gameFrame.mainButtons.add(bankButton);
        gameFrame.mainButtons.add(depositAllButton);

        buttonPanel.add(moveButton);
        buttonPanel.add(forageButton);
        buttonPanel.add(bankButton);
        buttonPanel.add(depositAllButton);
        buttonPanel.add(gameFrame.selectionBox);

        moveButton.addActionListener(e -> gameFrame.moveAction());
        forageButton.addActionListener(e -> gameFrame.forageAction());
        bankButton.addActionListener(e -> gameFrame.toggleBankWindow());
        depositAllButton.addActionListener(e -> gameFrame.depositAllItemsToBank());

        buttonPanel.setBounds(50, 700, 850, 50); // Adjust the bounds as necessary
        buttonPanel.setBackground(Color.LIGHT_GRAY); // Set a background color to make it visible

        layeredPane.add(buttonPanel, JLayeredPane.DEFAULT_LAYER);

        // Ensure focus is on the button panel for key bindings to work
        buttonPanel.setFocusable(true);
        buttonPanel.requestFocusInWindow();

        // Delay updating the selection box until the layout is complete
        SwingUtilities.invokeLater(() -> gameFrame.updateSelectionBox());

        System.out.println("ButtonPanel initialized and added to layeredPane");
        System.out.println("ButtonPanel bounds: " + buttonPanel.getBounds());
    }

    public JPanel getButtonPanel() {
        return buttonPanel;
    }
}
