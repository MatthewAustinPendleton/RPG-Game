import javax.swing.*;
import java.awt.*;

/**
 * BackgroundPanel is a custom JPanel that displays an image as the background.
 * It allows setting and updating the background image using a specified image path.
 */
public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    /**
     * Constructs a BackgroundPanel with the specified image path.
     *
     * @param imagePath the path to the background image
     */
    public BackgroundPanel(String imagePath) {
        setBackgroundImage(imagePath);
    }

    /**
     * Sets the background image from the specified image path.
     * The method updates the current background image and repaints the panel.
     *
     * @param imagePath the path to the background image
     */
    public void setBackgroundImage(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        repaint();
    }

    /**
     * Overrides the paintComponent method to draw the background image.
     * The background image is drawn to fill the entire panel.
     *
     * @param g the Graphics object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}