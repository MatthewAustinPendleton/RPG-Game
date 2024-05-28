import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomPanel extends JPanel {
    private String text;
    private Image image;
    private float opacity = 1.0f;

    public CustomPanel(String text, Image image) {
        this.text = text;
        this.image = image;
        setOpaque(false); // Ensure the panel background is transparent
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity)); // Ensure opacity is within the valid range
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Do not call super.paintComponent(g) to prevent the JPanel from drawing the background itself
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Set font and calculate font metrics
        g2d.setFont(new Font("Serif", Font.BOLD, 20)); // Adjusted font size to 20
        FontMetrics fm = g2d.getFontMetrics();

        // Set the opacity for the entire component
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Split text into lines
        List<String> lines = getWrappedLines(text, fm, getWidth() - 80); // Adjust width to accommodate the larger image

        // Calculate text starting position
        int textX = 60; // Adjusted to leave more space for the larger image
        int textY = 10 + fm.getAscent();

        // Draw each line with shadow and fill
        for (String line : lines) {
            drawTextShadow(g2d, line, textX, textY);
            g2d.setColor(Color.WHITE);
            g2d.drawString(line, textX, textY);
            textY += fm.getHeight();
        }

        // Draw the image next to the text
        if (image != null) {
            int imageX = 20; // Move to the right
            int imageY = textY - (2 * fm.getHeight()) - 7; // Move up and align with text
            g2d.drawImage(image, imageX, imageY, 50, 50, this); // Increased image size to 50x50
        }

        g2d.dispose();
    }

    private List<String> getWrappedLines(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (fm.stringWidth(line.toString() + word) > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }
        lines.add(line.toString());
        return lines;
    }

    private void drawTextShadow(Graphics2D g2d, String text, int x, int y) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + 1, y + 1);
    }
}