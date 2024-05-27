import java.util.List;

public class Scene {
    private String description;
    private String imagePath;
    private List<String> adjacentScenes;
    private List<Item> lootTable;
    private int difficulty;

    public Scene(String description, String imagePath, List<String> adjacentScenes, List<Item> lootTable, int difficulty) {
        this.description = description;
        this.imagePath = imagePath;
        this.adjacentScenes = adjacentScenes;
        this.lootTable = lootTable;
        this.difficulty = difficulty;
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
