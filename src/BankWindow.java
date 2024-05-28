import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class BankWindow extends JPanel {

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
        bankPanel.setLayout(new GridLayout(0, 4, 5, 5));
        JScrollPane scrollPane = new JScrollPane(bankPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        refreshBankPanel();
    }

    void refreshBankPanel() {
        bankPanel.removeAll();
        for (Item item : bankInventory.values()) {
            JPanel itemPanel = createItemPanel(item);
            bankPanel.add(itemPanel);
        }
        bankPanel.revalidate();
        bankPanel.repaint();
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
        if (bankInventory.containsKey(item.getName())) {
            bankInventory.get(item.getName()).incrementCount(item.getCount());
        } else {
            bankInventory.put(item.getName(), item);
        }
        refreshBankPanel();
    }

    public void removeItemFromBank(Item item, int count) {
        if (bankInventory.containsKey(item.getName())) {
            Item bankItem = bankInventory.get(item.getName());
            if (bankItem.getCount() > count) {
                bankItem.decrementCount(count);
            } else {
                bankInventory.remove(item.getName());
            }
            refreshBankPanel();
        }
    }
}