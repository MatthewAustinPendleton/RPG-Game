import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForageButtonListener implements ActionListener {

    private ForagingManager foragingManager;

    public ForageButtonListener(ForagingManager foragingManager) {
        this.foragingManager = foragingManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        foragingManager.startForaging();
    }
}