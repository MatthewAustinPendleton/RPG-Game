public class CropData {
    private String cropName;
    private String cropImagePath;
    private int baseYield;
    private int baseExperience;

    public CropData(String cropName, String cropImagePath, int baseYield, int baseExperience) {
        this.cropName = cropName;
        this.cropImagePath = cropImagePath;
        this.baseYield = baseYield;
        this.baseExperience = baseExperience;
    }

    public String getCropName() {
        return cropName;
    }

    public String getCropImagePath() {
        return cropImagePath;
    }

    public int getBaseYield() {
        return baseYield;
    }

    public int getBaseExperience() {
        return baseExperience;
    }
}