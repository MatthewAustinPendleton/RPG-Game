import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ForageButtonListener handles the action of foraging when the forage button is clicked.
 */
public class ForageButtonListener implements ActionListener {

    private ForagingManager foragingManager;

    /**
     * Constructs a ForageButtonListener with the specified ForagingManager.
     *
     * @param foragingManager the foraging manager
     */
    public ForageButtonListener(ForagingManager foragingManager) {
        this.foragingManager = foragingManager;
    }

    /**
     * Invoked when an action occurs. This method starts the foraging process
     * by calling the startForaging method on the ForagingManager.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        foragingManager.startForaging();
    }
}