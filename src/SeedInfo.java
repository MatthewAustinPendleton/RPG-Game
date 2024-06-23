import java.util.List;
public class SeedInfo {
    private String seedName;
    private int maxStage;
    private List<Integer> growthTimes; // List of growth times for each stage

    public SeedInfo(String seedName, int maxStage, List<Integer> growthTimes) {
        this.seedName = seedName;
        this.maxStage = maxStage;
        this.growthTimes = growthTimes;
    }

    public String getSeedName() {
        return seedName;
    }

    public int getMaxStage() {
        return maxStage;
    }

    public List<Integer> getGrowthTimes() {
        return growthTimes;
    }
}
