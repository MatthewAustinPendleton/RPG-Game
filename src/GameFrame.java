import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameFrame extends JFrame {

    public java.util.List<JButton> mainButtons;
    public JPanel selectionBox;
    private int selectedButtonIndex = 0;

    ButtonPanelInitializer buttonPanelInitializer;
    private BankWindow bankWindow;
    private BackgroundPanel sceneImagePanel;
    private JLabel sceneDescription;
    private JPanel inventoryPanel;
    private Inventory inventory;
    private JButton moveButton;
    private JButton forageButton;
    private JButton bankButton;
    private JButton depositAllButton;
    private JButton farmButton;
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
    private int farmPlotAmount = 10;
    boolean showPercentage = true;
    private int currentFarmPage = 0;
    private Scene previousScene;

    public GameFrame(Map<String, Scene> scenes) {
        this.scenes = scenes;
        this.currentScene = scenes.get("forest");
        this.previousScene = null; // Start with no previous scene
        this.discoveredItems = new HashSet<>();
        this.collectionsPanels = new HashMap<>();

        // Initialize the foraging manager first
        this.foragingManager = new ForagingManager(this);

        mainButtons = new java.util.ArrayList<>(); // Explicitly use java.util.ArrayList
        selectionBox = new JPanel();
        selectionBox.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        selectionBox.setOpaque(false);

        // Set up the JFrame
        setTitle("Java GUI Game");
        setSize(1400, 800);
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

        // Initialize ButtonPanelInitializer and set the button panel
        this.buttonPanelInitializer = new ButtonPanelInitializer(this);
        buttonPanelInitializer.initButtonPanel(layeredPane);

        // Initialize buttons before calling updateButtonStates
        if (moveButton == null) {
            System.err.println("MoveButton is not initialized before setting.");
        }
        if (forageButton == null) {
            System.err.println("ForageButton is not initialized before setting.");
        }
        if (bankButton == null) {
            System.err.println("BankButton is not initialized before setting.");
        }
        if (depositAllButton == null) {
            System.err.println("DepositAllButton is not initialized before setting.");
        }
        if (farmButton == null) {
            System.err.println("FarmButton is not initialized before setting.");
        }

        mainButtons.add(moveButton);
        mainButtons.add(forageButton);
        mainButtons.add(bankButton);
        mainButtons.add(depositAllButton);
        mainButtons.add(farmButton);

        initKeyBindings(); // Ensure key bindings are initialized

        bankWindow = new BankWindow(this);
        bankWindow.setVisible(false);
        bankWindow.setBounds(200, 100, 600, 400);
        layeredPane.add(bankWindow, JLayeredPane.POPUP_LAYER);
        layeredPane.add(selectionBox, JLayeredPane.PALETTE_LAYER);
        contentPane.add(layeredPane, BorderLayout.CENTER);

        updateButtonStates();
        setVisible(true);

        // Ensure the collections tab is scrolled to the top initially
        SwingUtilities.invokeLater(() -> collectionsScrollPane.getVerticalScrollBar().setValue(0));

        // Ensure the focus starts on the button panel and set the focus traversal policy
        SwingUtilities.invokeLater(() -> {
            layeredPane.requestFocusInWindow();
            buttonPanelInitializer.getButtonPanel().requestFocusInWindow();
            setFocusTraversalPolicy(new FocusTraversalPolicy() {
                @Override
                public Component getComponentAfter(Container aContainer, Component aComponent) {
                    return buttonPanelInitializer.getButtonPanel();
                }

                @Override
                public Component getComponentBefore(Container aContainer, Component aComponent) {
                    return buttonPanelInitializer.getButtonPanel();
                }

                @Override
                public Component getFirstComponent(Container aContainer) {
                    return buttonPanelInitializer.getButtonPanel();
                }

                @Override
                public Component getLastComponent(Container aContainer) {
                    return buttonPanelInitializer.getButtonPanel();
                }

                @Override
                public Component getDefaultComponent(Container aContainer) {
                    return buttonPanelInitializer.getButtonPanel();
                }
            });
        });

        tabbedPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Whenever the tabbedPane loses focus, request focus for the button panel
                buttonPanelInitializer.getButtonPanel().requestFocusInWindow();
            }
        });

        updateSelectionBox(mainButtons);

        // Debug prints
        System.out.println("GameFrame initialized!");
        System.out.println("Main buttons: " + mainButtons.size());
        mainButtons.forEach(button -> {
            System.out.println("Button: " + button.getText() + " Bounds: " + button.getBounds());
            System.out.println("Button: " + button.getText() + " Location: " + button.getLocation());
            System.out.println("Button: " + button.getText() + " Size: " + button.getSize());
        });

        setFarmPlotAmount(10);

        // Ensure the farm button visibility is updated based on farmPlotAmount
        updateFarmButtonVisibility();

        // Ensure the layout is validated and repainted
        validate();
        repaint();

        // Call updateSelectionBox again after validation and repaint
        SwingUtilities.invokeLater(() -> updateSelectionBox(mainButtons));
    }

    public void drawFarmPlots(int farmPlotAmount) {
        if (sceneImagePanel == null) {
            System.err.println("Error: sceneImagePanel is null!");
        }

        JPanel farmPanel = new JPanel();
        farmPanel.setLayout(null);
        farmPanel.setBounds(0, 0, sceneImagePanel.getWidth(), sceneImagePanel.getHeight());
        farmPanel.setOpaque(false);

        int plotsPerPage = 6;
        int totalPages = (int) Math.ceil(farmPlotAmount / (double) plotsPerPage);
        int startPlot = currentFarmPage * plotsPerPage;
        int endPlot = Math.min(startPlot + plotsPerPage, farmPlotAmount);

        int gridWidth = 3;
        int gridHeight = 2;
        int plotSize = 90;
        int horizontalGap = 50;
        int xOffset = (sceneImagePanel.getWidth() - (gridWidth * plotSize + (gridWidth - 1) * horizontalGap)) / 2;
        int yOffset = sceneImagePanel.getHeight() - gridHeight * plotSize - 10;

        // Add farm plots to the farmPanel
        for (int i = startPlot; i < endPlot; i++) {
            int plotIndex = i - startPlot;
            int x = xOffset + (plotIndex % gridWidth) * (plotSize + horizontalGap);
            int y = yOffset + (plotIndex / gridWidth) * plotSize;
            JLabel plotLabel = new JLabel();
            plotLabel.setBounds(x, y, plotSize, plotSize);
            plotLabel.setOpaque(false);

            ImageIcon plotIcon = new ImageIcon(getClass().getResource("/farmplot-transparent.png"));
            Image scaledImage = plotIcon.getImage().getScaledInstance(plotSize, plotSize, Image.SCALE_SMOOTH);
            plotLabel.setIcon(new ImageIcon(scaledImage));

            farmPanel.add(plotLabel);

            System.out.println("Farm plot: " + (i + 1) + " drawn at x: " + x + ", y: " + y);
        }

        // Add back button if applicable
        if (currentFarmPage > 0) {
            JButton backButton = new JButton("<--");
            backButton.setBounds(10, sceneImagePanel.getHeight() - 40, 50, 30);
            backButton.addActionListener(e -> {
                currentFarmPage--;
                drawFarmPlots(farmPlotAmount);
            });
            farmPanel.add(backButton);
        }

        // Add next button if applicable
        if (currentFarmPage < totalPages - 1) {
            JButton nextButton = new JButton("-->");
            nextButton.setBounds(sceneImagePanel.getWidth() - 60, sceneImagePanel.getHeight() - 40, 50, 30);
            nextButton.addActionListener(e -> {
                currentFarmPage++;
                drawFarmPlots(farmPlotAmount);
            });
            farmPanel.add(nextButton);
        }

        // Ensure all components are added to the sceneImagePanel
        sceneImagePanel.removeAll();
        sceneImagePanel.setLayout(null);
        sceneImagePanel.add(farmPanel);
        sceneImagePanel.revalidate();
        sceneImagePanel.repaint();

        System.out.println("Farm plots drawn. FarmPanel bounds: " + farmPanel.getBounds());
    }

    public void moveAction() {
        Scene currentScene = getCurrentScene();
        System.out.println("Current Scene: " + currentScene.getName());
        List<String> adjacentScenes = currentScene.getAdjacentScenes();

        System.out.println("Attempting to move from Scene: " + currentScene.getName());
        System.out.println("Adjacent Scenes: " + adjacentScenes);
        System.out.println("Previous Scene: " + (previousScene != null ? previousScene.getName() : "null"));

        if (currentScene.getName().equals("farm")) {
            if (previousScene != null) {
                System.out.println("Returning to previous scene: " + previousScene.getName());
                setCurrentScene(previousScene);
                return;
            } else {
                System.out.println("Error: previousScene is null.");
            }
        }

        if (adjacentScenes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No adjacent scenes available.");
            return;
        }

        if (adjacentScenes.size() == 1) {
            previousScene = currentScene;
            System.out.println("Setting previousScene to: " + previousScene.getName());
            setCurrentScene(scenes.get(adjacentScenes.get(0)));
        } else {
            String nextSceneName = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a scene to move to:",
                    "Move",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    adjacentScenes.toArray(),
                    adjacentScenes.get(0)
            );

            if (nextSceneName != null && scenes.containsKey(nextSceneName)) {
                previousScene = currentScene;
                System.out.println("Setting previousScene to: " + previousScene.getName());
                setCurrentScene(scenes.get(nextSceneName));
            } else {
                JOptionPane.showMessageDialog(this, "Scene not found: " + nextSceneName);
            }
        }
    }

    public void forageAction() {
        if (!foragingManager.getIsForagingBoolean()) {
            foragingManager.startForaging();
        }
    }

    public void depositAllItemsToBank() {
        if (bankWindow != null) {
            bankWindow.depositAllItemsToBank();
        }
    }

    private void initKeyBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "selectButton");
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "selectButton");

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<JButton> visibleButtons = mainButtons.stream()
                        .filter(Component::isVisible)
                        .sorted(Comparator.comparingInt(b -> b.getLocation().x))
                        .collect(Collectors.toList());
                if (visibleButtons.isEmpty()) return;
                if (selectedButtonIndex > 0) {
                    selectedButtonIndex--;
                } else {
                    selectedButtonIndex = visibleButtons.size() - 1;
                }
                updateSelectionBox(visibleButtons);
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<JButton> visibleButtons = mainButtons.stream()
                        .filter(Component::isVisible)
                        .sorted(Comparator.comparingInt(b -> b.getLocation().x))
                        .collect(Collectors.toList());
                if (visibleButtons.isEmpty()) return;
                if (selectedButtonIndex < visibleButtons.size() - 1) {
                    selectedButtonIndex++;
                } else {
                    selectedButtonIndex = 0;
                }
                updateSelectionBox(visibleButtons);
            }
        });

        actionMap.put("selectButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<JButton> visibleButtons = mainButtons.stream()
                        .filter(Component::isVisible)
                        .sorted(Comparator.comparingInt(b -> b.getLocation().x))
                        .collect(Collectors.toList());
                if (visibleButtons.isEmpty()) return;
                JButton selectedButton = visibleButtons.get(selectedButtonIndex);
                selectedButton.doClick();
            }
        });

        // Prevent focus traversal in tabbedPane
        Set<AWTKeyStroke> emptySet = Collections.emptySet();
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, emptySet);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, emptySet);

        System.out.println("Key bindings initialized!");
    }

    public void updateSelectionBox(java.util.List<JButton> visibleButtons) {
        if (visibleButtons.isEmpty()) return;

        // Ensure the selectedButtonIndex is within the range of visible buttons
        selectedButtonIndex = Math.min(selectedButtonIndex, visibleButtons.size() - 1);
        JButton selectedButton = visibleButtons.get(selectedButtonIndex);
        Rectangle bounds = selectedButton.getBounds();
        Point buttonLocation = SwingUtilities.convertPoint(selectedButton.getParent(), bounds.getLocation(), layeredPane);
        selectionBox.setBounds(buttonLocation.x - 2, buttonLocation.y - 2, bounds.width + 4, bounds.height + 4);
        getLayeredPane().repaint();

        // Debug prints
        System.out.println("SelectionBox updated to bounds: " + selectionBox.getBounds());
    }

    public void setMoveButton(JButton moveButton) {
        this.moveButton = moveButton;
        System.out.println("MoveButton set with Bounds: " + moveButton.getBounds());
    }

    public void setForageButton(JButton forageButton) {
        this.forageButton = forageButton;
        System.out.println("ForageButton set with Bounds: " + forageButton.getBounds());
    }

    public void setBankButton(JButton bankButton) {
        this.bankButton = bankButton;
        System.out.println("BankButton set with Bounds: " + bankButton.getBounds());
    }

    public void setDepositAllButton(JButton depositAllButton) {
        this.depositAllButton = depositAllButton;
        System.out.println("DepositAllButton set with Bounds: " + depositAllButton.getBounds());
    }

    public void setFarmButton(JButton farmButton) {
        this.farmButton = farmButton;
        System.out.println("FarmButton set with Bounds: " + farmButton.getBounds());
    }

    public void updateButtonStates() {
        if (currentScene == null) {
            System.err.println("Error: currentScene is null.");
            return;
        }

        boolean isBankScene = "You are in the bank.".equals(currentScene.getDescription());
        boolean isBankWindowVisible = bankWindow != null && bankWindow.isVisible();
        boolean isFarmScene = "You are at your farm.".equals(currentScene.getDescription());

        if (bankButton == null) {
            System.err.println("Error: bankButton is null.");
        } else {
            bankButton.setVisible(isBankScene);
        }

        if (forageButton == null) {
            System.err.println("Error: forageButton is null.");
        } else {
            forageButton.setEnabled(!isBankScene);
            forageButton.setVisible(!isFarmScene);
        }

        if (moveButton == null) {
            System.err.println("Error: moveButton is null.");
        } else {
            moveButton.setEnabled(!isBankWindowVisible);
        }

        if (farmButton == null) {
            System.err.println("Error: farmButton is null.");
        } else {
            farmButton.setVisible(!isFarmScene);
        }

        if (depositAllButton == null) {
            System.err.println("Error: depositAllButton is null.");
        } else {
            if (isBankScene) {
                depositAllButton.setVisible(true);
            } else {
                depositAllButton.setVisible(false);
            }
        }

        JPanel buttonPanel = buttonPanelInitializer.getButtonPanel();
        if (buttonPanel == null) {
            System.err.println("Error: buttonPanel is null.");
        } else {
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }

        System.out.println("Button states updated!");
        if (bankButton != null) {
            System.out.println("BankButton visible: " + bankButton.isVisible());
        }
        if (forageButton != null) {
            System.out.println("ForageButton enabled: " + forageButton.isEnabled());
        }
        if (moveButton != null) {
            System.out.println("MoveButton enabled: " + moveButton.isEnabled());
        }
        if (depositAllButton != null) {
            System.out.println("DepositAllButton visible: " + depositAllButton.isVisible());
        }
        if (farmButton != null) {
            System.out.println("FarmButton visible: " + farmButton.isVisible());
        }
        updateSelectionBox(mainButtons.stream().filter(Component::isVisible).sorted(Comparator.comparingInt(b -> b.getLocation().x)).collect(Collectors.toList()));
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

    public void refreshInventoryPanel() {
        inventory.refreshInventoryPanel();
    }

    public void updateForagingLevelLabel(int foragingLevel) {
        if (foragingLevelLabel != null) {
            foragingLevelLabel.setText("Foraging Level: " + foragingLevel);
            foragingLevelLabel.repaint(); // Ensure the label is repainted immediately
        }
    }

    public JProgressBar getForagingProgressBar() {
        return foragingProgressBar;
    }

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

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene scene) {
        System.out.println("Setting current scene. Current: " + (currentScene != null ? currentScene.getName() : "null") + ", New: " + scene.getName());

        // Always set previousScene before changing the current scene
        if (currentScene != null && !currentScene.getName().equals(scene.getName())) {
            previousScene = currentScene;
            System.out.println("Setting previousScene to: " + previousScene.getName());
        }

        System.out.println("Changing current scene to: " + scene.getName());
        clearFarmElements();
        currentScene = scene;
        updateScene();
        System.out.println("Updated current scene to: " + currentScene.getName());

        if ("farm".equals(scene.getName())) {
            drawFarmPlots(farmPlotAmount);
        }

        updateButtonStates();
        updateCollectionsPanel(currentScene);
        validate();
        repaint();
    }


    public void clearFarmElements() {
        System.out.println("Clearing farm elements...");

        if (sceneImagePanel != null) {
            sceneImagePanel.removeAll();
            sceneImagePanel.revalidate();
            sceneImagePanel.repaint();
            System.out.println("All components removed from sceneImagePanel.");
        }

        if (layeredPane != null) {
            List<Component> componentsToRemove = new ArrayList<>();
            for (Component comp : layeredPane.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if ("<--".equals(button.getText()) || "-->".equals(button.getText())) {
                        componentsToRemove.add(button);
                    }
                }
            }
            for (Component comp : componentsToRemove) {
                layeredPane.remove(comp);
                System.out.println("Removed button: " + ((JButton) comp).getText());
            }
            layeredPane.revalidate();
            layeredPane.repaint();
        }
    }

    public Map<String, Scene> getScenes() {
        return scenes;
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

    public void toggleBankWindow() {
        boolean isBankWindowVisible = bankWindow.isVisible();
        bankWindow.setVisible(!isBankWindowVisible);
        updateButtonStates();
        SwingUtilities.invokeLater(() -> updateSelectionBox(mainButtons.stream().filter(Component::isVisible).sorted(Comparator.comparingInt(b -> b.getLocation().x)).collect(Collectors.toList())));
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

    public JPanel createItemShadowPanel(Item item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        itemPanel.setPreferredSize(new Dimension(70, 70));

        ImageIcon originalIcon = getPreloadedImage(item.getName());
        Image shadowImage = createBlackShadowImage(originalIcon.getImage());
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

    private Image createBlackShadowImage(Image originalImage) {
        BufferedImage blackShadowImage = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = blackShadowImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, null);
        graphics2D.setComposite(AlphaComposite.SrcIn);
        graphics2D.setColor(new Color(0, 0, 0, 255));
        graphics2D.fillRect(0, 0, originalImage.getWidth(null), originalImage.getHeight(null));
        graphics2D.dispose();
        return blackShadowImage;
    }

    public void disableMoveButton() {
        if (moveButton != null) {
            moveButton.setEnabled(false);
        }
    }

    public void disableFarmButton() {
        if (farmButton != null) {
            farmButton.setEnabled(false);
        }
    }

    public void disableForageButton() {
        if (forageButton != null) {
            forageButton.setEnabled(false);
        }
    }

    public void disableBankButton() {
        if (bankButton != null) {
            bankButton.setEnabled(false);
        }
    }

    public ImageIcon getPreloadedImage(String itemName) {
        return preloadedImages.getOrDefault(itemName, null);
    }

    public void setFarmPlotAmount(int farmPlotAmount) {
        this.farmPlotAmount = farmPlotAmount;
        updateFarmButtonVisibility(); // Ensure the farm button visibility is updated
    }

    void updateFarmButtonVisibility() {
        if (farmButton != null) {
            farmButton.setVisible(farmPlotAmount > 0);
        }
    }

    public void refreshUI() {
        if (bankWindow != null && bankWindow.isVisible()) {
            refreshInventoryPanel();
        }
        refreshInventoryPanel();
        updateButtonStates();
        validate();
        repaint();
    }

    public JPanel getSceneImagePanel() {
        return sceneImagePanel;
    }

    public void updateScene() {
        if (sceneImagePanel != null && currentScene != null) {
            sceneImagePanel.setBackgroundImage(currentScene.getImagePath());
        }
        if (sceneDescription != null && currentScene != null) {
            sceneDescription.setText(currentScene.getDescription());
        }
        updateButtonStates();
        SwingUtilities.invokeLater(() -> updateSelectionBox(mainButtons.stream()
                .filter(Component::isVisible)
                .sorted(Comparator.comparingInt(b -> b.getLocation().x))
                .collect(Collectors.toList())));
        System.out.println("Scene updated to: " + currentScene.getName());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void enableMoveButton() {
        if (moveButton != null) {
            moveButton.setEnabled(true);
        }
    }

    public void enableForageButton() {
        if (forageButton != null) {
            forageButton.setEnabled(true);
        }
    }

    public void enableFarmButton() {
        if (farmButton != null) {
            farmButton.setEnabled(true);
        }
    }

    public void showLevelUpMessage(int newLevel) {
        LevelUpPanel levelUpPanel = new LevelUpPanel("Congratulations! You reached level " + newLevel + "!");
        levelUpPanel.setSize(400, 100);

        int centerX = (getWidth() - levelUpPanel.getWidth()) / 2 - 210;
        int centerY = (getHeight() - levelUpPanel.getHeight()) / 2 - 65;
        levelUpPanel.setLocation(centerX, centerY);

        layeredPane.add(levelUpPanel, JLayeredPane.POPUP_LAYER);

        Timer fadeTimer = new Timer(50, new ActionListener() {
            private float opacity = 0.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1.0f) {
                    opacity += 0.05f;
                    levelUpPanel.setOpacity(opacity);
                    levelUpPanel.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                    Timer fadeOutTimer = new Timer(50, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (opacity > 0.0f) {
                                opacity -= 0.05f;
                                levelUpPanel.setOpacity(opacity);
                                levelUpPanel.repaint();
                            } else {
                                ((Timer) e.getSource()).stop();
                                layeredPane.remove(levelUpPanel);
                                layeredPane.repaint();
                            }
                        }
                    });
                    fadeOutTimer.start();
                }
            }
        });
        fadeTimer.start();
    }

    public void addForagedItemToInventory(Item foragedItem) {
        if (inventory.addItem(foragedItem)) {
            refreshInventoryPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Inventory is full. Cannot add foraged item.");
        }
    }

}