public class FarmingManager {
    private int farmingLevel;
    private long farmingExperience;
    private GameFrame gameFrame;

    public FarmingManager(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.farmingLevel = 1;
        this.farmingExperience = 0;
    }

    public int getFarmingLevel() {
        return farmingLevel;
    }

    public long getFarmingExperience() {
        return farmingExperience;
    }

    public void updateFarmingExperience(int experienceGained) {
        farmingExperience += experienceGained;
        checkForFarmingLevelUp();
    }

    private void checkForFarmingLevelUp() {
        int newLevel = ExperienceCalculator.calculateNewLevel(farmingExperience, farmingLevel);
        if (newLevel > farmingLevel) {
            farmingLevel = newLevel;
            farmingExperience -= ExperienceCalculator.getExperienceForLevel(farmingLevel);
            gameFrame.updateFarmingLevelLabel(farmingLevel);
            gameFrame.showLevelUpMessage(farmingLevel);
        }
        long maxExperience = ExperienceCalculator.getExperienceForLevel(farmingLevel + 1);
        gameFrame.updateFarmingProgressBar(farmingExperience, maxExperience);
    }
}
