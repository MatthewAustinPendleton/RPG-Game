import javax.swing.*;
import java.awt.*;

public class FontUtil {
    public static int calculatePreferredWidth(JButton button) {
        FontMetrics fontMetrics = button.getFontMetrics(button.getFont());
        int textWidth = fontMetrics.stringWidth(button.getText());
        Insets insets = button.getInsets();
        return textWidth + insets.left + insets.right + 10; // Adding some padding
    }
}