import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Inventory manages the items in the player's inventory and provides methods
 * to add, remove, and display items.
 */
public class Inventory {

    private Map<String, Item> items;
    private JPanel inventoryPanel;
    private Map<Integer, Item> slotItems; // Mapping from slot index to items
    private int maxCapacity = 24;
    private GameFrame gameFrame;

    /**
     * Constructs an Inventory with the specified inventory panel and game frame.
     *
     * @param inventoryPanel the inventory panel
     * @param gameFrame      the game frame
     */
    public Inventory(JPanel inventoryPanel, GameFrame gameFrame) {
        this.items = new HashMap<>();
        this.inventoryPanel = inventoryPanel;
        this.slotItems = new HashMap<>();
        this.gameFrame = gameFrame;
        initInventoryPanel();
    }

    public Item getItemByName(String name) {
        return items.get(name);
    }

    /**
     * Sets the slots panel to the specified panel.
     *
     * @param slotsPanel the slots panel
     */
    public void setSlotsPanel(JPanel slotsPanel) {
        this.inventoryPanel = slotsPanel;
        initInventoryPanel();
    }

    private void initInventoryPanel() {
        inventoryPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        for (int i = 0; i < maxCapacity; i++) {
            JPanel slot = createEmptySlotPanel();
            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            inventoryPanel.add(slot, gbc);
        }

        inventoryPanel.revalidate();
        inventoryPanel.repaint();

        // Debugging output
        System.out.println("Inventory panel initialized with slots.");
    }

    private JPanel createEmptySlotPanel() {
        JPanel slot = new JPanel(new BorderLayout());
        Dimension size = new Dimension(70, 70); // Ensure square dimensions
        slot.setPreferredSize(size);
        slot.setMinimumSize(size);
        slot.setMaximumSize(size);
        slot.setSize(size);
        slot.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return slot;
    }

    /**
     * Adds an item to the inventory.
     *
     * @param item the item to add
     * @return true if the item was added successfully, false if the inventory is full
     */
    public boolean addItem(Item item) {
        int emptySlot = findFirstEmptySlot();
        if (emptySlot == -1) {
            return false; // Inventory is full
        }

        if (items.containsKey(item.getName())) {
            items.get(item.getName()).incrementCount(item.getCount());
        } else {
            items.put(item.getName(), item);
            slotItems.put(emptySlot, item);
        }

        // Batch update UI after adding all items
        refreshInventoryPanel();
        return true;
    }

    private int findFirstEmptySlot() {
        for (int i = 0; i < maxCapacity; i++) {
            if (!slotItems.containsKey(i)) {
                return i;
            }
        }
        return -1; // No empty slots found
    }

    private void showDropOrDepositMenu(MouseEvent e, Item item) {
        JPopupMenu menu = new JPopupMenu();
        String currentSceneDescription = gameFrame.getCurrentScene().getDescription();

        if ("You are in the bank.".equals(currentSceneDescription)) {
            JMenuItem depositItem = new JMenuItem("Deposit to Bank");
            JMenuItem depositAllItem = new JMenuItem("Deposit All to Bank");

            depositItem.addActionListener(e1 -> {
                BankWindow bankWindow = gameFrame.getBankWindow();
                String countStr = JOptionPane.showInputDialog(gameFrame, "Enter amount to deposit:", item.getCount());
                int count = Integer.parseInt(countStr);
                if (count > 0 && count <= item.getCount()) {
                    bankWindow.addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), count));
                    removeItem(item, count);
                    gameFrame.refreshInventoryPanel();
                    bankWindow.refreshBankPanel();
                } else {
                    JOptionPane.showMessageDialog(gameFrame, "Invalid amount.");
                }
            });

            depositAllItem.addActionListener(e1 -> {
                BankWindow bankWindow = gameFrame.getBankWindow();
                bankWindow.addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), item.getCount()));
                removeItem(item, item.getCount());
                gameFrame.refreshInventoryPanel();
                bankWindow.refreshBankPanel();
            });

            menu.add(depositItem);
            menu.add(depositAllItem);
        }

        JMenuItem dropItem = new JMenuItem("Drop");
        dropItem.addActionListener(e12 -> removeItem(item, item.getCount()));
        menu.add(dropItem);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Removes the specified amount of an item from the inventory.
     *
     * @param item  the item to remove
     * @param count the amount to remove
     */
    public void removeItem(Item item, int count) {
        if (items.containsKey(item.getName())) {
            Item inventoryItem = items.get(item.getName());
            if (inventoryItem.getCount() > count) {
                inventoryItem.decrementCount(count);
            } else {
                items.remove(item.getName());
                slotItems.values().remove(inventoryItem);
            }
            refreshInventoryPanel();
        }
    }

    /**
     * Checks if the inventory is full.
     *
     * @return true if the inventory is full, false otherwise
     */
    public boolean isFull() {
        return slotItems.size() >= maxCapacity;
    }

    /**
     * Returns the items in the inventory.
     *
     * @return a collection of items in the inventory
     */
    public Collection<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Clears the inventory by removing all items.
     */
    public void clear() {
        items.clear();
        slotItems.clear();
        refreshInventoryPanel();

        // Debugging output
        System.out.println("Inventory cleared.");
    }

    /**
     * Refreshes the inventory panel to display the current items.
     */
    public void refreshInventoryPanel() {
        inventoryPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        for (int i = 0; i < maxCapacity; i++) {
            JPanel slot = createEmptySlotPanel();
            if (slotItems.containsKey(i)) {
                slot.add(createItemPanel(slotItems.get(i)));
            }
            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            inventoryPanel.add(slot, gbc);
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();

        // Debugging output
        System.out.println("Inventory panel refreshed.");
    }

    public JPanel createItemPanel(Item item) {
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

        itemPanel.setToolTipText(item.getName());

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showDropOrDepositMenu(e, item);
                } else if (SwingUtilities.isLeftMouseButton(e) && "You are in the bank.".equals(gameFrame.getCurrentScene().getDescription())) {
                    depositItemToBank(item);
                }
            }
        });

        return itemPanel;
    }

    private void depositItemToBank(Item item) {
        BankWindow bankWindow = gameFrame.getBankWindow();
        bankWindow.addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), item.getCount()));
        removeItem(item, item.getCount());
        gameFrame.refreshInventoryPanel();
        bankWindow.refreshBankPanel();
    }

    /**
     * Handles item deposit to the bank.
     *
     * @param item the item to deposit
     */
    public void handleItemDepositToBank(Item item) {
        BankWindow bankWindow = gameFrame.getBankWindow();
        bankWindow.addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), item.getCount()));
        removeItem(item, item.getCount());
        gameFrame.refreshInventoryPanel();
        bankWindow.refreshBankPanel();
    }
}
