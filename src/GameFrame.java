import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * GameFrame is the main frame for the game, containing all UI elements and
 * managing the game state.
 */
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
    boolean showPercentage = true;

    /**
     * Constructs a GameFrame with the specified scenes.
     *
     * @param scenes the scenes of the game
     */
    public GameFrame(Map<String, Scene> scenes) {
        this.scenes = scenes;
        this.currentScene = scenes.get("forest");
        this.discoveredItems = new HashSet<>();
        this.collectionsPanels = new HashMap<>();

        // Initialize the foraging manager first
        this.foragingManager = new ForagingManager(this);

        // Set up the JFrame
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

        new MainContentPanelInitializer(this).initMainContentPanel(layeredPane, currentScene);
        new TabbedPanelInitializer(this).initTabbedPanel(layeredPane, scenes, foragingManager);
        new ButtonPanelInitializer(this).initButtonPanel(layeredPane);

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

    /**
     * Preloads images for faster access during the game.
     */
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

    /**
     * Returns the bank window of the game.
     *
     * @return the bank window
     */
    public BankWindow getBankWindow() {
        return bankWindow;
    }

    /**
     * Refreshes the inventory panel.
     */
    public void refreshInventoryPanel() {
        inventory.refreshInventoryPanel();
    }

    /**
     * Updates the foraging level label with the specified level.
     *
     * @param foragingLevel the new foraging level
     */
    public void updateForagingLevelLabel(int foragingLevel) {
        if (foragingLevelLabel != null) {
            foragingLevelLabel.setText("Foraging Level: " + foragingLevel);
            foragingLevelLabel.repaint(); // Ensure the label is repainted immediately
        }
    }

    /**
     * Returns the foraging progress bar.
     *
     * @return the foraging progress bar
     */
    public JProgressBar getForagingProgressBar() {
        return foragingProgressBar;
    }

    /**
     * Updates the foraging progress bar with the specified experience and max experience.
     *
     * @param experience    the current experience
     * @param maxExperience the maximum experience
     */
    public void updateForagingProgressBar(long experience, long maxExperience) {
        if (foragingProgressBar != null) {
            foragingProgressBar.setMaximum((int) maxExperience);
            foragingProgressBar.setValue((int) experience);
            String displayText = showPercentage ? String.format("%.2f%%", (double) experience / maxExperience * 100) :
                    experience + " / " + maxExperience;
            foragingProgressBar.setString(displayText);
            foragingProgressBar.repaint(); // Ensure the progress bar is repainted immediately
        }
    }

    /**
     * Returns the current scene.
     *
     * @return the current scene
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Sets the current scene to the specified scene.
     *
     * @param scene the new scene
     */
    public void setCurrentScene(Scene scene) {
        currentScene = scene;
        updateCollectionsPanel(scene);
    }

    /**
     * Returns the scenes of the game.
     *
     * @return the scenes of the game
     */
    public Map<String, Scene> getScenes() {
        return scenes;
    }

    /**
     * Sets the background image of the scene to the specified image path.
     *
     * @param imagePath the path to the image
     */
    public void setSceneImage(String imagePath) {
        sceneImagePanel.setBackgroundImage(imagePath);
    }

    /**
     * Updates the description of the scene to the specified description.
     *
     * @param description the new scene description
     */
    public void updateSceneDescription(String description) {
        sceneDescription.setText(description);
    }

    /**
     * Disables the move button.
     */
    public void disableMoveButton() {
        moveButton.setEnabled(false);
    }

    /**
     * Disables the forage button.
     */
    public void disableForageButton() {
        forageButton.setEnabled(false);
    }

    /**
     * Enables the move button.
     */
    public void enableMoveButton() {
        moveButton.setEnabled(true);
    }

    /**
     * Enables the forage button.
     */
    public void enableForageButton() {
        forageButton.setEnabled(true);
    }

    /**
     * Updates the scene image and description, and button states.
     */
    public void updateScene() {
        setSceneImage(currentScene.getImagePath());
        updateSceneDescription(currentScene.getDescription());
        updateButtonStates();
    }

    /**
     * Updates the states of the buttons based on the current scene and bank window visibility.
     */
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

    /**
     * Adds a foraged item to the inventory.
     *
     * @param item the item to add
     */
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

    /**
     * Returns the scene image panel.
     *
     * @return the scene image panel
     */
    public JPanel getSceneImagePanel() {
        return sceneImagePanel;
    }

    /**
     * Returns the inventory of the game.
     *
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Shows a level-up message with the specified new level.
     *
     * @param newLevel the new level
     */
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
                    } else if ( opacity > 0) {
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

    /**
     * Reveals a collected item in the collections panel.
     *
     * @param item the collected item
     */
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

    /**
     * Creates a panel with a shadowed icon for the specified item.
     *
     * @param item the item for which to create a panel
     * @return the created panel
     */
    public JPanel createItemShadowPanel(Item item) {
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

    /**
     * Creates a shadow image from the specified original image.
     *
     * @param originalImage the original image
     * @return the shadow image
     */
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

    /**
     * Returns the preloaded image for the specified item name.
     *
     * @param itemName the name of the item
     * @return the preloaded image
     */
    private ImageIcon getPreloadedImage(String itemName) {
        return preloadedImages.get(itemName);
    }

    public void setSceneImagePanel(BackgroundPanel sceneImagePanel) {
        this.sceneImagePanel = sceneImagePanel;
    }

    public void setSceneDescription(JLabel sceneDescription) {
        this.sceneDescription = sceneDescription;
    }

    public void setInventoryPanel(JPanel inventoryPanel) {
        this.inventoryPanel = inventoryPanel;
    }

    public void setStatsPanel(JPanel statsPanel) {
        this.statsPanel = statsPanel;
    }

    public void setCollectionsCardPanel(JPanel collectionsCardPanel) {
        this.collectionsCardPanel = collectionsCardPanel;
    }

    public void setCollectionsScrollPane(JScrollPane collectionsScrollPane) {
        this.collectionsScrollPane = collectionsScrollPane;
    }

    public void setForagingLevelLabel(JLabel foragingLevelLabel) {
        this.foragingLevelLabel = foragingLevelLabel;
    }

    public void setForagingProgressBar(JProgressBar foragingProgressBar) {
        this.foragingProgressBar = foragingProgressBar;
    }

    public void setMoveButton(JButton moveButton) {
        this.moveButton = moveButton;
    }

    public void setForageButton(JButton forageButton) {
        this.forageButton = forageButton;
    }

    public void setBankButton(JButton bankButton) {
        this.bankButton = bankButton;
    }

    public void setDepositAllButton(JButton depositAllButton) {
        this.depositAllButton = depositAllButton;
    }

    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<String, JPanel> getCollectionsPanels() {
        return collectionsPanels;
    }

    public ForagingManager getForagingManager() {
        return foragingManager;
    }

    public boolean getBoolShowPercentage() {
        return showPercentage;
    }

    public void setBoolShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
    }

    /**
     * Toggles the visibility of the bank window.
     */
    public void toggleBankWindow() {
        boolean isBankWindowVisible = bankWindow.isVisible();
        bankWindow.setVisible(!isBankWindowVisible);
        updateButtonStates();
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Updates the collections panel for the specified scene.
     *
     * @param scene the scene to update the collections panel for
     */
    private void updateCollectionsPanel(Scene scene) {
        JPanel panel = collectionsPanels.get(scene.getDescription());
        collectionsCardPanel.removeAll();
        collectionsCardPanel.add(panel, scene.getDescription());

        collectionsCardPanel.revalidate();
        collectionsCardPanel.repaint();

        // Ensure the scroll position is at the top
        SwingUtilities.invokeLater(() -> collectionsScrollPane.getVerticalScrollBar().setValue(0));
    }
}
