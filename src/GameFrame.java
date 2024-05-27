import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

public class GameFrame extends JFrame {

    private BankWindow bankWindow;
    private BackgroundPanel sceneImagePanel;
    private JLabel sceneDescription;
    private JPanel inventoryPanel;
    private Inventory inventory;
    private JButton moveButton;
    private JButton forageButton;
    private JButton bankButton;
    private JButton depositAllButton;
    private ForagingManager foragingManager;
    private Map<String, Scene> scenes;
    private Scene currentScene;
    private JLayeredPane layeredPane;
    private JTabbedPane tabbedPane;
    private JPanel statsPanel;
    private JProgressBar foragingProgressBar;
    private JLabel foragingLevelLabel;
    private JPanel collectionsCardPanel;
    private CardLayout collectionsCardLayout;
    private Map<String, JPanel> collectionsPanels;
    private Set<String> discoveredItems;
    private Map<String, ImageIcon> preloadedImages;
    private JScrollPane collectionsScrollPane;

    public GameFrame(Map<String, Scene> scenes) {
        this.scenes = scenes;
        this.currentScene = scenes.get("forest");
        this.discoveredItems = new HashSet<>();

        this.foragingManager = new ForagingManager(this);

        setTitle("Java GUI Game");
        setSize(1400, 800);  // Increased width for more space
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a custom content pane
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.BLACK); // Set the background color to black
        setContentPane(contentPane);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1400, 800));
        layeredPane.setBackground(Color.BLACK); // This is just a fallback, main color is set in contentPane

        preloadImages(); // Preload all images
        initMainContentPanel();
        initTabbedPanel();
        initButtonPanel();

        bankWindow = new BankWindow(this);
        bankWindow.setVisible(false);
        bankWindow.setBounds(200, 100, 600, 400);
        layeredPane.add(bankWindow, JLayeredPane.POPUP_LAYER);

        contentPane.add(layeredPane, BorderLayout.CENTER);

        updateButtonStates();
        setVisible(true);

        // Ensure the collections tab is scrolled to the top initially
        SwingUtilities.invokeLater(() -> collectionsScrollPane.getVerticalScrollBar().setValue(0));
    }

    private void preloadImages() {
        preloadedImages = new HashMap<>();
        for (Scene scene : scenes.values()) {
            for (Item item : scene.getLootTable()) {
                if (!preloadedImages.containsKey(item.getName())) {
                    ImageIcon originalIcon = new ImageIcon(getClass().getResource(item.getIconPath()));
                    preloadedImages.put(item.getName(), originalIcon);
                }
            }
        }
    }

    public BankWindow getBankWindow() {
        return bankWindow;
    }

    private void initMainContentPanel() {
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(null);
        mainContentPanel.setBounds(50, 20, 850, 650);
        mainContentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        mainContentPanel.setBackground(Color.BLACK); // Set background to black

        sceneImagePanel = new BackgroundPanel(currentScene.getImagePath());
        sceneImagePanel.setBounds(0, 0, 850, 600);
        sceneImagePanel.setBorder(BorderFactory.createEmptyBorder());

        sceneDescription = new JLabel(currentScene.getDescription(), SwingConstants.CENTER);
        sceneDescription.setFont(new Font("Serif", Font.BOLD, 18));
        sceneDescription.setBounds(0, 600, 850, 50);
        sceneDescription.setBackground(Color.LIGHT_GRAY);
        sceneDescription.setOpaque(true);

        mainContentPanel.add(sceneImagePanel);
        mainContentPanel.add(sceneDescription);
        layeredPane.add(mainContentPanel, JLayeredPane.DEFAULT_LAYER);
    }

    private void initTabbedPanel() {
        tabbedPane = new JTabbedPane();
        inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBackground(Color.LIGHT_GRAY);
        tabbedPane.addTab("Inventory", inventoryPanel);
        statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.LIGHT_GRAY);
        initStatsPanel();
        tabbedPane.addTab("Stats", statsPanel);

        collectionsCardLayout = new CardLayout();
        collectionsCardPanel = new JPanel(collectionsCardLayout);
        collectionsPanels = new HashMap<>();
        initCollectionsPanels();
        collectionsScrollPane = new JScrollPane(collectionsCardPanel);
        tabbedPane.addTab("Collections", collectionsScrollPane);

        tabbedPane.setBounds(950, 20, 350, 700);
        tabbedPane.setBackground(Color.LIGHT_GRAY);
        tabbedPane.setOpaque(true);
        layeredPane.add(tabbedPane, JLayeredPane.DEFAULT_LAYER);
        initInventoryPanel();
    }

    private void initInventoryPanel() {
        JPanel slotsPanel = new JPanel(new GridBagLayout());
        inventory = new Inventory(slotsPanel, this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        for (int i = 0; i < 24; i++) {
            JPanel slot = new JPanel(new BorderLayout());
            Dimension size = new Dimension(70, 70);
            slot.setPreferredSize(size);
            slot.setMinimumSize(size);
            slot.setMaximumSize(size);
            slot.setSize(size);
            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            slotsPanel.add(slot, gbc);
        }
        JLabel inventoryTitle = new JLabel("Inventory", SwingConstants.CENTER);
        inventoryTitle.setFont(new Font("Serif", Font.BOLD, 18));
        inventoryTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inventoryPanel.add(inventoryTitle, BorderLayout.NORTH);
        inventoryPanel.add(slotsPanel, BorderLayout.CENTER);

        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void initStatsPanel() {
        JPanel foragingPanel = new JPanel(new BorderLayout());
        foragingLevelLabel = new JLabel("Foraging Level: " + foragingManager.getForagingLevel(), SwingConstants.CENTER);
        foragingLevelLabel.setFont(new Font("Serif", Font.BOLD, 18));

        foragingProgressBar = new JProgressBar(0, foragingManager.getForagingLevel() * 100);
        foragingProgressBar.setValue(foragingManager.getForagingExperience());
        foragingProgressBar.setStringPainted(true);
        foragingProgressBar.setForeground(Color.YELLOW); // Set the progress bar color to yellow

        // Customize the progress bar's appearance
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
        foragingProgressBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createBevelBorder(BevelBorder.RAISED)
        ));

        // Customizing the font and color of the progress bar string
        foragingProgressBar.setFont(new Font("Serif", Font.BOLD, 16));

        // Custom ProgressBarUI to change the text color
        foragingProgressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintString(Graphics g, int x, int y, int width, int height, int amountFull, Insets b) {
                Graphics2D g2 = (Graphics2D) g;
                String progressString = foragingProgressBar.getString();
                g2.setFont(foragingProgressBar.getFont());
                g2.setColor(Color.BLACK); // Set text color to black
                int stringWidth = g2.getFontMetrics().stringWidth(progressString);
                int stringHeight = g2.getFontMetrics().getHeight();
                int stringX = x + (width - stringWidth) / 2;
                int stringY = y + ((height + stringHeight) / 2) - g2.getFontMetrics().getDescent();
                g2.drawString(progressString, stringX, stringY);
            }
        });

        foragingPanel.add(foragingLevelLabel, BorderLayout.NORTH);
        foragingPanel.add(foragingProgressBar, BorderLayout.CENTER);
        statsPanel.add(foragingPanel, BorderLayout.NORTH);
    }



    private void initCollectionsPanels() {
        for (Scene scene : scenes.values()) {
            JPanel panel = createCollectionsPanel(scene);
            collectionsPanels.put(scene.getDescription(), panel);
            collectionsCardPanel.add(panel, scene.getDescription());
        }
    }

    private JPanel createCollectionsPanel(Scene scene) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (Item item : scene.getLootTable()) {
            JPanel itemPanel = createItemShadowPanel(item);
            panel.add(itemPanel, gbc);
            gbc.gridx++;
            if (gbc.gridx == 3) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        return panel;
    }

    private void updateCollectionsPanel(Scene scene) {
        JPanel panel = collectionsPanels.get(scene.getDescription());
        collectionsCardPanel.removeAll();
        collectionsCardPanel.add(panel, scene.getDescription());

        collectionsCardPanel.revalidate();
        collectionsCardPanel.repaint();

        // Ensure the scroll position is at the top
        SwingUtilities.invokeLater(() -> collectionsScrollPane.getVerticalScrollBar().setValue(0));
    }

    private void initButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(50, 680, 1200, 50);
        buttonPanel.setBackground(Color.BLACK); // Set background to black
        moveButton = new JButton("Move");
        moveButton.addActionListener(new MoveButtonListener(this));
        forageButton = new JButton("Forage");
        forageButton.addActionListener(new ForageButtonListener(foragingManager));
        bankButton = new JButton("Bank");
        bankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleBankWindow();
            }
        });
        depositAllButton = new JButton("Deposit All");
        depositAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                depositAllItemsToBank();
            }
        });
        depositAllButton.setVisible(false);

        buttonPanel.add(moveButton);
        buttonPanel.add(forageButton);
        buttonPanel.add(bankButton);
        buttonPanel.add(depositAllButton);

        layeredPane.add(buttonPanel, JLayeredPane.DEFAULT_LAYER);
    }

    private void toggleBankWindow() {
        boolean isBankWindowVisible = bankWindow.isVisible();
        bankWindow.setVisible(!isBankWindowVisible);
        updateButtonStates();
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public void refreshInventoryPanel() {
        inventory.refreshInventoryPanel();
    }

    public void updateForagingLevelLabel(int foragingLevel) {
        foragingLevelLabel.setText("Foraging Level: " + foragingLevel);
    }

    public void updateForagingProgressBar(int experience, int maxExperience) {
        foragingProgressBar.setMaximum(maxExperience);
        foragingProgressBar.setValue(experience);
        foragingProgressBar.setString(experience + " / " + maxExperience);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene scene) {
        currentScene = scene;
        updateCollectionsPanel(scene);
    }

    public Map<String, Scene> getScenes() {
        return scenes;
    }

    public void setSceneImage(String imagePath) {
        sceneImagePanel.setBackgroundImage(imagePath);
    }

    public void updateSceneDescription(String description) {
        sceneDescription.setText(description);
    }

    public void disableMoveButton() {
        moveButton.setEnabled(false);
    }

    public void disableForageButton() {
        forageButton.setEnabled(false);
    }

    public void enableMoveButton() {
        moveButton.setEnabled(true);
    }

    public void enableForageButton() {
        forageButton.setEnabled(true);
    }

    public void updateScene() {
        setSceneImage(currentScene.getImagePath());
        updateSceneDescription(currentScene.getDescription());
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean isBankScene = "You are in the bank.".equals(currentScene.getDescription());
        boolean isBankWindowVisible = bankWindow.isVisible();

        bankButton.setVisible(isBankScene);
        forageButton.setEnabled(!isBankScene);
        moveButton.setEnabled(!isBankWindowVisible);

        if (isBankScene) {
            bankButton.setEnabled(true);
            depositAllButton.setVisible(true);
        } else {
            depositAllButton.setVisible(false);
        }
    }

    public void addForagedItemToInventory(Item item) {
        if (inventory.isFull()) {
            JOptionPane.showMessageDialog(this, "Inventory is full. Cannot add more items.");
            return;
        }

        if (inventory.addItem(item)) {
            refreshInventoryPanel();
            revealCollectedItem(item);
            System.out.println("Item added to inventory: " + item.getName());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add item to inventory.");
        }
    }

    public JPanel getSceneImagePanel() {
        return sceneImagePanel;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void showLevelUpMessage(int newLevel) {
        String message = "Foraging Level Up! (You have reached level " + newLevel + " Foraging)";
        LevelUpPanel levelUpPanel = new LevelUpPanel(message);
        levelUpPanel.setOpaque(false);

        levelUpPanel.setSize(600, 80);

        int scenePanelWidth = sceneImagePanel.getWidth();
        int scenePanelHeight = sceneImagePanel.getHeight();
        int panelX = (scenePanelWidth - levelUpPanel.getWidth()) / 2;
        int panelY = (scenePanelHeight - levelUpPanel.getHeight()) / 2;

        levelUpPanel.setLocation(panelX, panelY);

        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.add(levelUpPanel, JLayeredPane.POPUP_LAYER);

        Timer animationTimer = new Timer(50, new ActionListener() {
            private float opacity = 0.0f;
            private int counter = 0;
            private boolean fadingIn = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fadingIn) {
                    if (opacity < 1.0f) {
                        opacity += 0.05f;
                        levelUpPanel.setOpacity(opacity);
                        levelUpPanel.repaint();
                    } else {
                        fadingIn = false;
                        counter = 0;
                    }
                } else {
                    if (counter < 40) {
                        counter++;
                    } else if (opacity > 0) {
                        opacity -= 0.03f;
                        levelUpPanel.setOpacity(opacity);
                        levelUpPanel.repaint();
                    } else {
                        ((Timer) e.getSource()).stop();
                        layeredPane.remove(levelUpPanel);
                        layeredPane.repaint();
                    }
                }
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }

    private void depositAllItemsToBank() {
        Map<String, Item> inventoryItems = new HashMap<>();
        for (Item item : inventory.getItems()) {
            inventoryItems.put(item.getName(), item);
        }
        for (Item item : inventoryItems.values()) {
            bankWindow.addItemToBank(new Item(item.getName(), item.getIconPath(), item.getWeight(), item.getExperience(), item.getLevelRequirement(), item.getCount()));
        }
        inventory.clear();
        refreshInventoryPanel();
        bankWindow.refreshBankPanel();
    }

    public void revealCollectedItem(Item item) {
        discoveredItems.add(item.getName());
        for (Scene scene : scenes.values()) {
            JPanel panel = collectionsPanels.get(scene.getDescription());
            for (Component innerComp : panel.getComponents()) {
                if (innerComp instanceof JPanel) {
                    JPanel itemPanel = (JPanel) innerComp;
                    for (Component labelComp : itemPanel.getComponents()) {
                        if (labelComp instanceof JLabel) {
                            JLabel label = (JLabel) labelComp;
                            if (label.getName() != null && label.getName().equals(item.getName())) {
                                ImageIcon revealedIcon = getPreloadedImage(item.getName());
                                Image scaledImage = revealedIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                                label.setIcon(new ImageIcon(scaledImage));
                                label.setText("");
                                itemPanel.revalidate();
                                itemPanel.repaint();
                            }
                        }
                    }
                }
            }
        }
    }

    private JPanel createItemShadowPanel(Item item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setPreferredSize(new Dimension(70, 70));

        ImageIcon originalIcon = getPreloadedImage(item.getName());
        Image shadowImage = createShadowImage(originalIcon.getImage());
        ImageIcon shadowIcon = new ImageIcon(shadowImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH));

        JLabel iconLabel = new JLabel(shadowIcon, JLabel.CENTER);
        iconLabel.setName(item.getName());

        if (discoveredItems.contains(item.getName())) {
            iconLabel.setIcon(new ImageIcon(originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
            iconLabel.setText("");
        }

        JLabel levelLabel = new JLabel("Level " + item.getLevelRequirement(), JLabel.CENTER);

        itemPanel.add(iconLabel, BorderLayout.CENTER);
        itemPanel.add(levelLabel, BorderLayout.SOUTH);

        return itemPanel;
    }

    private Image createShadowImage(Image originalImage) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        BufferedImage shadowImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = shadowImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();

        return shadowImage;
    }

    private ImageIcon getPreloadedImage(String itemName) {
        return preloadedImages.get(itemName);
    }
}
