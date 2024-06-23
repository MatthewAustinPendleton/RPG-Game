import javax.swing.*;

public class FarmingGUI {
    public static void showHarvestDialog(int harvestedAmount, String itemName) {
        String message = "You harvested " + harvestedAmount + " " + itemName + "(s).";
        JOptionPane.showMessageDialog(null, message, "Harvest", JOptionPane.INFORMATION_MESSAGE);
    }
}
