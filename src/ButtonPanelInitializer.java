import javax.swing.*;
import java.awt.*;

/**
 * Initializes the button panel of the game.
 */
public class ButtonPanelInitializer {

    private GameFrame gameFrame;

    public ButtonPanelInitializer(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    /**
     * Initializes the button panel with the specified layered pane.
     *
     * @param layeredPane the layered pane to add the button panel to
     */
    public void initButtonPanel(JLayeredPane layeredPane) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.setBounds(50, 680, 850, 50); // Adjusted position and size
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        JButton moveButton = new JButton("Move");
        moveButton.addActionListener(new MoveButtonListener(gameFrame));
        System.out.println("Move button initialized.");

        JButton forageButton = new JButton("Forage");
        forageButton.addActionListener(new ForageButtonListener(gameFrame.getForagingManager()));
        System.out.println("Forage button initialized.");

        JButton bankButton = new JButton("Bank");
        bankButton.addActionListener(e -> gameFrame.toggleBankWindow());
        System.out.println("Bank button initialized.");

        JButton depositAllButton = new JButton("Deposit All");
        depositAllButton.addActionListener(e -> gameFrame.getBankWindow().depositAllItemsToBank());
        System.out.println("Deposit All button initialized.");

        buttonPanel.add(moveButton);
        buttonPanel.add(forageButton);
        buttonPanel.add(bankButton);
        buttonPanel.add(depositAllButton);

        layeredPane.add(buttonPanel, JLayeredPane.DEFAULT_LAYER);
        System.out.println("Button panel added to layeredPane.");

        // Set buttons in GameFrame
        gameFrame.setMoveButton(moveButton);
        gameFrame.setForageButton(forageButton);
        gameFrame.setBankButton(bankButton);
        gameFrame.setDepositAllButton(depositAllButton);
    }
}