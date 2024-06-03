import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Initializes the tabbed panel of the game.
 */
public class TabbedPanelInitializer {

    private GameFrame gameFrame;

    public TabbedPanelInitializer(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    /**
     * Initializes the tabbed panel with the specified layered pane, scenes, and foraging manager.
     *
     * @param layeredPane     the layered pane to add the tabbed panel to
     * @param scenes          the scenes of the game
     * @param foragingManager the foraging manager
     */
    public void initTabbedPanel(JLayeredPane layeredPane, Map<String, Scene> scenes, ForagingManager foragingManager) {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBackground(Color.LIGHT_GRAY);
        Inventory inventory = new Inventory(inventoryPanel, gameFrame); // Initialize Inventory here
        tabbedPane.addTab("Inventory", inventoryPanel);
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.LIGHT_GRAY);
        initStatsPanel(statsPanel, foragingManager);
        tabbedPane.addTab("Stats", statsPanel);

        CardLayout collectionsCardLayout = new CardLayout();
        JPanel collectionsCardPanel = new JPanel(collectionsCardLayout);
        Map<String, JPanel> collectionsPanels = gameFrame.getCollectionsPanels();
        initCollectionsPanels(scenes, collectionsCardPanel, collectionsPanels);
        JScrollPane collectionsScrollPane = new JScrollPane(collectionsCardPanel);
        tabbedPane.addTab("Collections", collectionsScrollPane);

        tabbedPane.setBounds(950, 20, 350, 700);
        tabbedPane.setBackground(Color.LIGHT_GRAY);
        tabbedPane.setOpaque(true);
        layeredPane.add(tabbedPane, JLayeredPane.DEFAULT_LAYER);

        gameFrame.setInventory(inventory); // Set Inventory in GameFrame
        gameFrame.setTabbedPane(tabbedPane);
        gameFrame.setInventoryPanel(inventoryPanel);
        gameFrame.setStatsPanel(statsPanel);
        gameFrame.setCollectionsCardPanel(collectionsCardPanel);
        gameFrame.setCollectionsScrollPane(collectionsScrollPane);
    }

    private void initStatsPanel(JPanel statsPanel, ForagingManager foragingManager) {
        JPanel foragingPanel = new JPanel(new BorderLayout());
        JLabel foragingLevelLabel = new JLabel("Foraging Level: " + foragingManager.getForagingLevel(), SwingConstants.CENTER);
        foragingLevelLabel.setFont(new Font("Serif", Font.BOLD, 18));

        JProgressBar foragingProgressBar = new JProgressBar(0, (int) foragingManager.getForagingLevel() * 100);
        foragingProgressBar.setValue((int) foragingManager.getForagingExperience());
        foragingProgressBar.setStringPainted(true);
        foragingProgressBar.setForeground(Color.YELLOW);

        // Customize the progress bar's appearance
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
        foragingProgressBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1)
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

        // Add a MouseListener to toggle the display between ratio and percentage
        foragingProgressBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    gameFrame.showPercentage = !gameFrame.showPercentage; // Toggle the state
                    gameFrame.updateForagingProgressBar(foragingManager.getForagingExperience(), foragingProgressBar.getMaximum());
                }
            }
        });

        // Initial update to set the string
        gameFrame.updateForagingProgressBar(foragingManager.getForagingExperience(), foragingProgressBar.getMaximum());

        foragingPanel.add(foragingLevelLabel, BorderLayout.NORTH);
        foragingPanel.add(foragingProgressBar, BorderLayout.CENTER);
        statsPanel.add(foragingPanel, BorderLayout.NORTH);

        gameFrame.setForagingLevelLabel(foragingLevelLabel);
        gameFrame.setForagingProgressBar(foragingProgressBar);
    }

    private void initCollectionsPanels(Map<String, Scene> scenes, JPanel collectionsCardPanel, Map<String, JPanel> collectionsPanels) {
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
            JPanel itemPanel = gameFrame.createItemShadowPanel(item);
            panel.add(itemPanel, gbc);
            gbc.gridx++;
            if (gbc.gridx == 3) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        return panel;
    }
}