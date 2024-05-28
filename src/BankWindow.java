import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BankWindow extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(BankWindow.class.getName());

    private GameFrame gameFrame;
    private JPanel bankPanel;
    private Map<String, Item> bankInventory;
    private Map<String, JPanel> itemPanels;

    public BankWindow(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.bankInventory = new HashMap<>();
        this.itemPanels = new HashMap<>();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bankPanel = new JPanel();
        bankPanel.setLayout(new GridLayout(0, 4, 5, 5)); // Use 0 for rows to allow vertical expansion
        JScrollPane scrollPane = new JScrollPane(bankPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrollbar
        add(scrollPane, BorderLayout.CENTER);
        refreshBankPanel();
    }

    void refreshBankPanel() {
        LOGGER.log(Level.INFO, "Refreshing bank panel...");
        long startTime = System.currentTimeMillis();

        bankPanel.removeAll();
        for (Item item : bankInventory.values()) {
            JPanel itemPanel = createItemPanel(item);
            bankPanel.add(itemPanel);
        }
        bankPanel.revalidate();
        bankPanel.repaint();

        long endTime = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "Bank panel refreshed in " + (endTime - startTime) + " ms");
    }

    private JPanel createItemPanel(Item item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource(item.getIconPath()));
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
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
                }
            }
        });

        itemPanels.put(item.getName(), itemPanel);
        return itemPanel;
    }

    private void showWithdrawMenu(MouseEvent e, Item item) {
        JPopupMenu withdrawMenu = new JPopupMenu();
        JMenuItem withdrawItem = new JMenuItem("Withdraw");
        JMenuItem withdrawAllItem = new JMenuItem("Withdraw All");

        withdrawItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String countStr = JOptionPane.showInputDialog(gameFrame, "Enter amount to withdraw:", item.getCount());
                if (countStr != null && !countStr.trim().isEmpty()) {
                    try {
                        int count = Integer.parseInt(countStr);
                        if (count > 0 && count <= item.getCount()) {
                            Item inventoryItem = new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), count);
                            if (gameFrame.getInventory().addItem(inventoryItem)) {
                                removeItemFromBank(item, count);
                                refreshBankPanel();
                                gameFrame.refreshInventoryPanel();
                            } else {
                                JOptionPane.showMessageDialog(gameFrame, "Inventory is full.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(gameFrame, "Invalid amount.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(gameFrame, "Invalid input. Please enter a number.");
                    }
                }
            }
        });

        withdrawAllItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = item.getCount();
                Item inventoryItem = new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), count);
                if (gameFrame.getInventory().addItem(inventoryItem)) {
                    removeItemFromBank(item, count);
                    refreshBankPanel();
                    gameFrame.refreshInventoryPanel();
                } else {
                    JOptionPane.showMessageDialog(gameFrame, "Inventory is full.");
                }
            }
        });

        withdrawMenu.add(withdrawItem);
        withdrawMenu.add(withdrawAllItem);
        withdrawMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public void addItemToBank(Item item) {
        LOGGER.log(Level.INFO, "Adding item to bank: " + item.getName() + " x" + item.getCount());
        long startTime = System.currentTimeMillis();

        if (bankInventory.containsKey(item.getName())) {
            bankInventory.get(item.getName()).incrementCount(item.getCount());
        } else {
            bankInventory.put(item.getName(), item);
        }
        long endTime = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "Item added to bank inventory in " + (endTime - startTime) + " ms");
    }

    public void removeItemFromBank(Item item, int count) {
        LOGGER.log(Level.INFO, "Removing item from bank: " + item.getName() + " x" + count);
        long startTime = System.currentTimeMillis();

        if (bankInventory.containsKey(item.getName())) {
            Item bankItem = bankInventory.get(item.getName());
            if (bankItem.getCount() > count) {
                bankItem.decrementCount(count);
            } else {
                bankInventory.remove(item.getName());
            }
        }
        long endTime = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "Item removed from bank inventory in " + (endTime - startTime) + " ms");
    }

    // Method to deposit all items using a background thread to prevent UI lag
    public void depositAllItemsToBank() {
        LOGGER.log(Level.INFO, "Starting deposit all items to bank...");
        long startTime = System.currentTimeMillis();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (Item item : new ArrayList<>(gameFrame.getInventory().getItems())) {
                    addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), item.getCount()));
                }
                gameFrame.getInventory().clear();
                return null;
            }

            @Override
            protected void done() {
                long endTime = System.currentTimeMillis();
                LOGGER.log(Level.INFO, "Deposit all items to bank completed in " + (endTime - startTime) + " ms");

                refreshBankPanel();
                gameFrame.refreshInventoryPanel();
            }
        };
        worker.execute();
    }
}
