import java.util.List;

/**
 * Scene represents a location in the game, including its description, image, adjacent scenes, loot table, and difficulty.
 */
public class Scene {
    private String name;
    private String description;
    private String imagePath;
    private List<String> adjacentScenes;
    private List<Item> lootTable;
    private int difficulty;

    /**
     * Constructs a Scene with the specified attributes.
     *
     * @param name            the name of the scene
     * @param description     the description of the scene
     * @param imagePath       the path to the scene's image
     * @param adjacentScenes  the list of adjacent scenes
     * @param lootTable       the list of items that can be found in the scene
     * @param difficulty      the difficulty level of the scene
     */
    public Scene(String name, String description, String imagePath, List<String> adjacentScenes, List<Item> lootTable, int difficulty) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.adjacentScenes = adjacentScenes;
        this.lootTable = lootTable;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public List<String> getAdjacentScenes() {
        return adjacentScenes;
    }

    public List<Item> getLootTable() {
        return lootTable;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
