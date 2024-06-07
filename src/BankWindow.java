import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * BankWindow manages the banking system where players can deposit and withdraw items.
 */
public class BankWindow extends JInternalFrame {
    private GameFrame gameFrame;
    private Map<String, Item> items;
    private JPanel bankPanel;

    public BankWindow(GameFrame gameFrame) {
        super("Bank", true, true, true, true);
        this.gameFrame = gameFrame;
        this.items = new HashMap<>();
        this.bankPanel = new JPanel(new GridLayout(8, 3, 5, 5));
        this.bankPanel.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(bankPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setSize(600, 400);

        // Add listener for closing the window
        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                gameFrame.toggleBankWindow();
            }
        });
    }

    public void addItemToBank(Item item) {
        if (items.containsKey(item.getName())) {
            items.get(item.getName()).incrementCount(item.getCount());
        } else {
            items.put(item.getName(), item);
        }
        refreshBankPanel();
    }

    public void depositAllItemsToBank() {
        Inventory inventory = gameFrame.getInventory();

        // Disable UI updates temporarily
        bankPanel.setVisible(false);

        // Use a Map to store item counts to update the bank in a batch
        Map<String, Integer> itemCountMap = new HashMap<>();

        for (Item item : inventory.getItems()) {
            itemCountMap.put(item.getName(), item.getCount());
        }

        // Add items to the bank in a batch
        for (Map.Entry<String, Integer> entry : itemCountMap.entrySet()) {
            String itemName = entry.getKey();
            int itemCount = entry.getValue();

            Item item = items.get(itemName);
            if (item != null) {
                item.incrementCount(itemCount);
            } else {
                Item inventoryItem = inventory.getItemByName(itemName);
                items.put(itemName, new Item(inventoryItem.getName(), inventoryItem.getIconPath(), inventoryItem.getWeight(), inventoryItem.getExperience(), inventoryItem.getLevelRequirement(), itemCount));
            }
        }

        // Clear the inventory after transferring items
        inventory.clear();

        // Refresh the UI once after all items are processed
        refreshBankPanel();

        // Re-enable UI updates
        bankPanel.setVisible(true);
    }

    public void refreshBankPanel() {
        bankPanel.removeAll();

        for (Item item : items.values()) {
            bankPanel.add(createItemPanel(item));
        }

        bankPanel.revalidate();
        bankPanel.repaint();
    }

    private JPanel createItemPanel(Item item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource(item.getIconPath()));
        Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel iconLabel = new JLabel(scaledIcon, JLabel.CENTER);
        JLabel nameLabel = new JLabel(item.getName(), JLabel.CENTER);
        JLabel countLabel = new JLabel("x" + item.getCount(), JLabel.CENTER);

        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(iconLabel, BorderLayout.CENTER);
        itemPanel.add(countLabel, BorderLayout.SOUTH);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showWithdrawMenu(e, item);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    withdrawItemFromBank(item, item.getCount());
                }
            }
        });

        return itemPanel;
    }

    private void showWithdrawMenu(MouseEvent e, Item item) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem withdrawItem = new JMenuItem("Withdraw");
        JMenuItem withdrawAllItem = new JMenuItem("Withdraw All");

        withdrawItem.addActionListener(e1 -> {
            String countStr = JOptionPane.showInputDialog(gameFrame, "Enter amount to withdraw:", item.getCount());
            int count = Integer.parseInt(countStr);
            if (count > 0 && count <= item.getCount()) {
                withdrawItemFromBank(item, count);
            } else {
                JOptionPane.showMessageDialog(gameFrame, "Invalid amount.");
            }
        });

        withdrawAllItem.addActionListener(e1 -> {
            withdrawItemFromBank(item, item.getCount());
        });

        menu.add(withdrawItem);
        menu.add(withdrawAllItem);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void withdrawItemFromBank(Item item, int count) {
        if (gameFrame.getInventory().isFull()) {
            JOptionPane.showMessageDialog(this, "Inventory is full. Cannot withdraw items.");
            return;
        }

        Item bankItem = items.get(item.getName());
        if (bankItem.getCount() > count) {
            bankItem.decrementCount(count);
        } else {
            items.remove(item.getName());
        }

        gameFrame.getInventory().addItem(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), count));
        refreshBankPanel();
        gameFrame.refreshInventoryPanel();
    }
}