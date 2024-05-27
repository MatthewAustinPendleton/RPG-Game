import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LevelUpPanel extends JPanel {
    private String text;
    private float opacity = 0.0f; // Start with 0 opacity for fade-in effect

    public LevelUpPanel(String text) {
        this.text = text;
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
        g2d.setFont(new Font("Serif", Font.BOLD, 30)); // Adjusted font size to 30
        FontMetrics fm = g2d.getFontMetrics();

        // Set the opacity for the entire component
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Calculate panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Split text into lines
        List<String> lines = getWrappedLines(text, fm, panelWidth - 20);

        // Calculate text starting position for centered alignment
        int totalTextHeight = lines.size() * fm.getHeight();
        int textY = (panelHeight - totalTextHeight) / 2 + fm.getAscent();

        // Draw each line with green text
        for (String line : lines) {
            int textX = (panelWidth - fm.stringWidth(line)) / 2; // Center text horizontally
            drawText(g2d, line, textX, textY);
            textY += fm.getHeight();
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

    private void drawText(Graphics2D g2d, String text, int x, int y) {
        // Draw the text with a slight shadow for better visibility
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + 2, y + 2); // Shadow
        g2d.setColor(Color.GREEN);
        g2d.drawString(text, x, y);
    }
}
