/**
 * Represents an item in the game.
 */
public class Item {
    private String name;
    private String iconPath;
    private int weight;
    private int experience;
    private int levelRequirement;
    private int count;

    /**
     * Constructs an item with the specified attributes.
     *
     * @param name             the name of the item
     * @param iconPath         the path to the item's icon
     * @param weight           the weight of the item
     * @param experience       the experience value of the item
     * @param levelRequirement the level requirement to use the item
     */
    public Item(String name, String iconPath, int weight, int experience, int levelRequirement) {
        this.name = name;
        this.iconPath = iconPath;
        this.weight = weight;
        this.experience = experience;
        this.levelRequirement = levelRequirement;
        this.count = 1;
    }

    /**
     * Constructs an item with the specified attributes and count.
     *
     * @param name             the name of the item
     * @param iconPath         the path to the item's icon
     * @param weight           the weight of the item
     * @param experience       the experience value of the item
     * @param levelRequirement the level requirement to use the item
     * @param count            the count of the item
     */
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

    /**
     * Increments the count of the item.
     *
     * @param increment the amount to increment
     */
    public void incrementCount(int increment) {
        this.count += increment;
    }

    /**
     * Decrements the count of the item.
     *
     * @param decrement the amount to decrement
     */
    public void decrementCount(int decrement) {
        this.count -= decrement;
    }
}