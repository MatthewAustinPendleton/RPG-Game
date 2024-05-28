public class Item {
    private String name;
    private String iconPath;
    private int weight;
    private int experience;
    private int levelRequirement;
    private int count;

    public Item(String name, String iconPath, int weight, int experience, int levelRequirement) {
        this.name = name;
        this.iconPath = iconPath;
        this.weight = weight;
        this.experience = experience;
        this.levelRequirement = levelRequirement;
        this.count = 1;
    }

    public Item(String name, String iconPath, int weight, int experience, int levelRequirement, int count) {
        this.name = name;
        this.iconPath = iconPath;
        this.weight = weight;
        this.experience = experience;
        this.levelRequirement = levelRequirement;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public int getWeight() {
        return weight;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount(int increment) {
        this.count += increment;
    }

    public void decrementCount(int decrement) {
        this.count -= decrement;
    }
}