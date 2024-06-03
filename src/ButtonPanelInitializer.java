import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        JButton farmButton = new JButton("Farm");

        gameFrame.setMoveButton(moveButton);
        gameFrame.setForageButton(forageButton);
        gameFrame.setBankButton(bankButton);
        gameFrame.setDepositAllButton(depositAllButton);
        gameFrame.setFarmButton(farmButton);

        gameFrame.mainButtons.add(moveButton);
        gameFrame.mainButtons.add(forageButton);
        gameFrame.mainButtons.add(bankButton);
        gameFrame.mainButtons.add(depositAllButton);
        gameFrame.mainButtons.add(farmButton);

        buttonPanel.add(moveButton);
        buttonPanel.add(forageButton);
        buttonPanel.add(bankButton);
        buttonPanel.add(depositAllButton);
        buttonPanel.add(farmButton);
        buttonPanel.add(gameFrame.selectionBox);

        moveButton.addActionListener(e -> gameFrame.moveAction());
        forageButton.addActionListener(e -> gameFrame.forageAction());
        bankButton.addActionListener(e -> gameFrame.toggleBankWindow());
        depositAllButton.addActionListener(e -> gameFrame.depositAllItemsToBank());
        farmButton.addActionListener(e -> gameFrame.farmAction());

        // Custom key bindings for the farm button
        InputMap farmInputMap = farmButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap farmActionMap = farmButton.getActionMap();

        farmInputMap.put(KeyStroke.getKeyStroke("SPACE"), "farmAction");
        farmInputMap.put(KeyStroke.getKeyStroke("ENTER"), "farmAction");

        farmActionMap.put("farmAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.farmAction();
            }
        });

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