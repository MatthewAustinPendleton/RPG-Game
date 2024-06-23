public class FarmPlot {
    private String cropName;
    private int growthStage;
    private int maxGrowthStage;
    private SeedInfo seedInfo;

    public FarmPlot(SeedInfo seedInfo) {
        this.seedInfo = seedInfo;
        this.cropName = seedInfo.getSeedName();
        this.growthStage = 0;
        this.maxGrowthStage = seedInfo.getMaxStage();
    }

    public String getCropName() {
        return cropName;
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public void incrementGrowthStage() {
        if (growthStage < maxGrowthStage) {
            growthStage++;
        }
    }

    public boolean isFulllyGrown() {
        return growthStage == maxGrowthStage;
    }

    public void resetPlot() {
        cropName = null;
        growthStage = 0;
    }
}
