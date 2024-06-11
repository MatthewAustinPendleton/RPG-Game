import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class Inventory {
    private Map<String, Item> items;
    private JPanel inventoryPanel;
    private Map<Integer, Item> slotItems; // Mapping from slot index to items
    private int maxCapacity = 24;
    private GameFrame gameFrame;
    private Set<String> uniqueItems; // Set to track unique items
    private int updateThreshold = 5; // Number of items to batch before updating UI
    private int updateCounter = 0;

    public Inventory(JPanel inventoryPanel, GameFrame gameFrame) {
        this.items = new HashMap<>();
        this.inventoryPanel = inventoryPanel;
        this.slotItems = new HashMap<>();
        this.gameFrame = gameFrame;
        this.uniqueItems = new HashSet<>(); // Initialize the set
        initInventoryPanel();
    }

    public Item getItemByName(String name) {
        return items.get(name);
    }

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

    public boolean addItem(Item item) {
        int emptySlot = findFirstEmptySlot();
        if (emptySlot == -1) {
            return false;
        }
        boolean isNewItem = !items.containsKey(item.getName());
        if (isNewItem) {
            uniqueItems.add(item.getName());
        }
        if (items.containsKey(item.getName())) {
            items.get(item.getName()).incrementCount(item.getCount());
        } else {
            items.put(item.getName(), item);
            slotItems.put(emptySlot, item);
        }
        updateCounter++;
        if (updateCounter >= updateThreshold) {
            updateCounter = 0;
        }
        SwingUtilities.invokeLater(this::refreshInventoryPanel);
        return true;
    }


    private void updateItemPanel(Item item) {
        for (int i = 0; i < maxCapacity; i++) {
            if (slotItems.containsKey(i) && slotItems.get(i).getName().equals(item.getName())) {
                JPanel slot = (JPanel) inventoryPanel.getComponent(i);
                slot.removeAll();
                slot.add(createItemPanel(item));
                slot.revalidate();
                slot.repaint();
                break;
            }
        }
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

    public void removeItem(Item item, int count) {
        if (items.containsKey(item.getName())) {
            Item inventoryItem = items.get(item.getName());
            if (inventoryItem.getCount() > count) {
                inventoryItem.decrementCount(count);
            } else {
                items.remove(item.getName());
                slotItems.values().remove(inventoryItem);
                uniqueItems.remove(item.getName()); // Remove from unique items set
            }
            refreshInventoryPanel();
        }
    }

    public boolean isFull() {
        return slotItems.size() >= maxCapacity;
    }

    public Collection<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    public void clear() {
        items.clear();
        slotItems.clear();
        uniqueItems.clear(); // Clear the set of unique items
        refreshInventoryPanel();
        System.out.println("Inventory cleared.");
    }

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

    public void handleItemDepositToBank(Item item) {
        BankWindow bankWindow = gameFrame.getBankWindow();
        bankWindow.addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), item.getCount()));
        removeItem(item, item.getCount());
        gameFrame.refreshInventoryPanel();
        bankWindow.refreshBankPanel();
    }
}