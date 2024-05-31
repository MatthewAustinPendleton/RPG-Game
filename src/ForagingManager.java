import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * ForagingManager handles the foraging process, including the timer, experience calculation,
 * and animations for collected items.
 */
public class ForagingManager {

    private static final Logger LOGGER = Logger.getLogger(ForagingManager.class.getName());

    private GameFrame gameFrame;
    private Timer forageTimer;
    private Random random;
    private int foragingLevel;
    private long foragingExperience;
    private Clip forageSoundClip;
    private SoundManager soundManager;
    private boolean isForaging = false;

    /**
     * Constructs a ForagingManager with the specified GameFrame.
     *
     * @param gameFrame the main game frame
     */
    public ForagingManager(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.random = new Random();
        this.foragingLevel = 1;
        this.foragingExperience = 0;
        this.soundManager = new SoundManager();

        if (gameFrame.getForagingProgressBar() != null) {
            gameFrame.updateForagingProgressBar(foragingExperience, ExperienceCalculator.getExperienceForLevel(foragingLevel + 1));
        }
    }

    public boolean getIsForagingBoolean() {
        return isForaging;
    }

    /**
     * Starts the foraging process.
     */
    public synchronized void startForaging() {
        if (isForaging) return;
        isForaging = true;
        gameFrame.disableMoveButton();
        gameFrame.disableForageButton();
        soundManager.playSound("/foraging.wav");
        forageTimer = new Timer(getForagingTime(), new ForagingTimerListener());
        forageTimer.setRepeats(false);
        forageTimer.start();
    }

    private class ForagingTimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Item foragedItem = performForaging();
            handleForagedItem(foragedItem);
        }
    }

    private Item performForaging() {
        List<Item> lootTable = gameFrame.getCurrentScene().getLootTable();
        List<Item> validItems = new ArrayList<>();

        for (Item item : lootTable) {
            if (item.getLevelRequirement() <= foragingLevel) {
                validItems.add(item);
            }
        }

        if (validItems.isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame, "Your foraging level is too low to find any items in this area.");
            return null;
        }

        int totalWeight = validItems.stream().mapToInt(Item::getWeight).sum();
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (Item item : validItems) {
            currentWeight += item.getWeight();
            if (currentWeight > randomWeight) {
                return item;
            }
        }
        return null;
    }

    private void handleForagedItem(Item foragedItem) {
        if (foragedItem == null) {
            endForaging();
            return;
        }

        if (gameFrame.getInventory().isFull()) {
            showInventoryFullMessage();
            endForaging();
            return;
        }

        int expGained = foragedItem.getExperience();
        Item singleForagedItem = new Item(foragedItem.getName(), foragedItem.getIconPath(), foragedItem.getWeight(), expGained, foragedItem.getLevelRequirement(), 1);
        animateForagedItem(singleForagedItem, () -> {
            gainForagingExperience(expGained);
            showForagedItemInfo(singleForagedItem, expGained, calculateProgress());
            gameFrame.revealCollectedItem(singleForagedItem); // Reveal item in collections
            endForaging();
        });
    }

    private double calculateProgress() {
        long expNeeded = ExperienceCalculator.getExperienceForLevel(foragingLevel + 1);
        return ((double) foragingExperience / expNeeded) * 100;
    }

    private void endForaging() {
        soundManager.stopSound();
        gameFrame.enableMoveButton();
        gameFrame.enableForageButton();
        System.out.println("Foraging ended, sound stopping...");
        isForaging = false;
    }

    public int getForagingLevel() {
        return foragingLevel;
    }

    public long getForagingExperience() {
        return foragingExperience;
    }

    private synchronized void gainForagingExperience(int exp) {
        foragingExperience += exp;
        int newLevel = ExperienceCalculator.calculateNewLevel(foragingExperience, foragingLevel);
        boolean leveledUp = newLevel > foragingLevel;

        if (leveledUp) {
            foragingExperience -= ExperienceCalculator.getExperienceForLevel(newLevel);
            foragingLevel = newLevel;
            LOGGER.info("Leveled up! New Level: " + foragingLevel + " | Remaining Experience: " + foragingExperience + " | Next Level Experience: " + ExperienceCalculator.getExperienceForLevel(foragingLevel + 1));
        }

        final long finalExpNeeded = ExperienceCalculator.getExperienceForLevel(foragingLevel + 1);
        final boolean finalLeveledUp = leveledUp;

        LOGGER.info("Scheduling UI update for experience: " + foragingExperience + " / " + finalExpNeeded + " | Leveled Up: " + finalLeveledUp);

        SwingUtilities.invokeLater(() -> {
            LOGGER.info("Executing UI update for experience: " + foragingExperience + " / " + finalExpNeeded + " | Leveled Up: " + finalLeveledUp);
            updateForagingUI(finalExpNeeded, finalLeveledUp);
        });
    }

    private void updateForagingUI(long finalExpNeeded, boolean leveledUp) {
        gameFrame.updateForagingProgressBar(foragingExperience, finalExpNeeded);
        if (leveledUp) {
            gameFrame.updateForagingLevelLabel(foragingLevel);
            soundManager.playSound("/foragingLevelUp.wav");
            gameFrame.showLevelUpMessage(foragingLevel);
            LOGGER.info("Level-up notification shown for level: " + foragingLevel);
        }
    }

    private int getForagingTime() {
        int difficulty = gameFrame.getCurrentScene().getDifficulty();
        int levelDifference = difficulty - foragingLevel;
        int baseTime = 5000;
        int adjustedTime = baseTime + (levelDifference * 100);
        return Math.max(1000, adjustedTime);
    }

    public void animateForagedItem(Item foragedItem, Runnable onComplete) {
        JLabel itemLabel = new JLabel();
        ImageIcon itemIcon = new ImageIcon(getClass().getResource(foragedItem.getIconPath()));
        int initialSize = 90;
        itemLabel.setIcon(new ImageIcon(itemIcon.getImage().getScaledInstance(initialSize, initialSize, Image.SCALE_SMOOTH)));
        itemLabel.setSize(initialSize, initialSize);

        JLabel basketLabel = new JLabel();
        ImageIcon basketIcon = new ImageIcon(getClass().getResource("/basket-transparent.png"));
        int basketSize = 140;
        basketLabel.setIcon(new ImageIcon(basketIcon.getImage().getScaledInstance(basketSize, basketSize, Image.SCALE_SMOOTH)));
        basketLabel.setSize(basketSize, basketSize);

        int startX = (gameFrame.getSceneImagePanel().getWidth() - initialSize) / 2;
        int startY = (gameFrame.getSceneImagePanel().getHeight() - initialSize) / 2;
        itemLabel.setLocation(startX, startY);

        stopForagingSound();

        int basketX = gameFrame.getSceneImagePanel().getWidth() - basketSize - 50;
        int basketY = (gameFrame.getSceneImagePanel().getHeight() - basketSize) / 2;
        basketLabel.setLocation(basketX, basketY);

        gameFrame.getSceneImagePanel().setLayout(null);
        gameFrame.getSceneImagePanel().add(basketLabel);
        gameFrame.getSceneImagePanel().add(itemLabel);
        gameFrame.getSceneImagePanel().setComponentZOrder(basketLabel, 1);
        gameFrame.getSceneImagePanel().setComponentZOrder(itemLabel, 0);
        gameFrame.getSceneImagePanel().repaint();

        // Adjust the endpoint so the item goes further into the basket
        int endX = basketX + (basketSize - initialSize) / 2 + 35; // Move further into the basket
        int endY = basketY + (basketSize - initialSize) / 2 + 38; // Move further into the basket
        int controlX = (startX + endX) / 2;
        int controlY = startY - 150;

        Timer animationTimer = new Timer(20, new ActionListener() {
            private double t = 0.0;
            private double rotation = 0.0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (t <= 1.0) {
                    double u = 1 - t;
                    int x = (int) (u * u * startX + 2 * u * t * controlX + t * t * endX);
                    int y = (int) (u * u * startY + 2 * u * t * controlY + t * t * endY);

                    double scale = 1.0 - (t / 2.5);
                    int newSize = (int) (initialSize * scale);

                    rotation += Math.toRadians(10); // Rotate 10 degrees per step

                    // Apply transformations
                    BufferedImage originalImage = new BufferedImage(itemIcon.getIconWidth(), itemIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = originalImage.createGraphics();
                    g2d.drawImage(itemIcon.getImage(), 0, 0, null);
                    g2d.dispose();

                    BufferedImage transformedImage = new BufferedImage(initialSize, initialSize, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2dTransformed = transformedImage.createGraphics();
                    g2dTransformed.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2dTransformed.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2dTransformed.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    AffineTransform affineTransform = new AffineTransform();
                    affineTransform.translate(newSize / 2.0, newSize / 2.0);
                    affineTransform.rotate(rotation);
                    affineTransform.scale(scale, scale);
                    affineTransform.translate(-initialSize / 2.0, -initialSize / 2.0);
                    g2dTransformed.setTransform(affineTransform);
                    g2dTransformed.drawImage(originalImage, 0, 0, initialSize, initialSize, null);
                    g2dTransformed.dispose();

                    itemLabel.setIcon(new ImageIcon(transformedImage));
                    itemLabel.setSize(newSize, newSize);
                    itemLabel.setLocation(x, y);

                    t += 0.025;
                } else {
                    ((Timer) e.getSource()).stop();
                    gameFrame.getSceneImagePanel().remove(itemLabel);
                    gameFrame.getSceneImagePanel().remove(basketLabel);
                    gameFrame.getSceneImagePanel().repaint();
                    playCollectSound(foragedItem.getWeight());
                    gameFrame.addForagedItemToInventory(foragedItem);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
                itemLabel.repaint();
            }
        });
        animationTimer.start();
    }

    private void showForagedItemInfo(Item foragedItem, int expGained, double progress) {
        String infoText = "+1 " + foragedItem.getName() + " (" + String.format("%.2f", progress) + "% to next level)";
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(foragedItem.getIconPath()));
        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Increased image size to 40x40
        CustomPanel infoPanel = new CustomPanel(infoText, scaledImage);
        infoPanel.setOpaque(false); // Ensure the panel background is transparent

        // Increase the size of the infoPanel for better readability
        infoPanel.setSize(400, 100); // Adjust size as needed
        int panelX = 50;
        int panelY = gameFrame.getHeight() - infoPanel.getHeight() - 150; // Position above the bottom bar
        infoPanel.setLocation(panelX, panelY);

        // Add the infoPanel to the layeredPane at the POPUP_LAYER
        JLayeredPane layeredPane = gameFrame.getLayeredPane();
        layeredPane.add(infoPanel, JLayeredPane.POPUP_LAYER);

        // Set a timer to animate the infoPanel upwards and fade out
        Timer animationTimer = new Timer(50, new ActionListener() {
            private float opacity = 1.0f;
            private int y = panelY;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity > 0) {
                    y -= 4; // Move up by 4 pixels
                    infoPanel.setLocation(panelX, y);

                    opacity -= 0.03f; // Reduce opacity faster
                    infoPanel.setOpacity(opacity);
                    infoPanel.repaint();

                    layeredPane.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                    layeredPane.remove(infoPanel);
                    layeredPane.repaint();
                }
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }

    public void stopForagingSound() {
        soundManager.stopSound();
    }

    public void playCollectSound(int itemWeight) {
        soundManager.playCollectSound(itemWeight);
    }

    private void showInventoryFullMessage() {
        JLabel infoLabel = new JLabel("Inventory Full");
        infoLabel.setOpaque(false);
        infoLabel.setForeground(Color.RED);
        infoLabel.setFont(new Font("Serif", Font.BOLD, 24));

        // Set the size and location of the infoLabel
        infoLabel.setSize(infoLabel.getPreferredSize());
        int labelX = (gameFrame.getWidth() - infoLabel.getWidth()) / 2;
        int labelY = gameFrame.getHeight() / 2;
        infoLabel.setLocation(labelX, labelY);

        // Add the infoLabel to the layeredPane at the POPUP_LAYER
        JLayeredPane layeredPane = gameFrame.getLayeredPane();
        layeredPane.add(infoLabel, JLayeredPane.POPUP_LAYER);

        // Set a timer to animate the infoLabel upwards and fade out
        Timer animationTimer = new Timer(50, new ActionListener() {
            private float opacity = 1.0f;
            private int y = labelY;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity > 0) {
                    y -= 2;
                    infoLabel.setLocation(labelX, y);

                    opacity -= 0.05f;
                    infoLabel.setForeground(new Color(255, 0, 0, (int) (255 * opacity)));

                    layeredPane.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                    layeredPane.remove(infoLabel);
                    layeredPane.repaint();
                }
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }
}

