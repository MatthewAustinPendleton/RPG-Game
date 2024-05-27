import javax.swing.*;
import java.util.*;

public class Main {

    public static Item apple, strawberry, blackberry, berry, raspberry, blueberry, truffle, elderberry, mulberry, gooseberry, huckleberry, currant, cd_rom,
            chanterelle, oystermushroom, morel, porcini, henofthewoodsmushroom, turkeytailmushroom, puffballmushroom, russulamushroom,
            mint, thyme, sage, oregano, chamomile, dandelion, plantain, yarrow, stingingnettle, wildgarlic, acorn, twigs, pinecone,
            wildrose, bluebell, trillium, woodanemone, foxglove, buttercup, violet, daisy, forgetmenot, primrose, bluegrass,
            fescue, ryegrass, timothygrass, granitepebble, limestonechipping, flintnodule, quartzpebble, ant, beetle, monarchbutterfly,
            swallowtailbutterfly, paintedladybutterfly, bluemorphobutterfly, redadmiralbutterfly, viceroybutterfly,
            peacockbutterfly, cabbagewhitebutterfly, moth, grasshopper, orbweaverspider, jumpingspider, wolfspider,
            blackwidowspider, tarantula, dragonfly, bee, caterpillar, cricket, grovemint, ballofmoss, brambleberry;
    public static List<Item> forestLootTable = new ArrayList<>();
    public static List<Item> forestClearingLootTable = new ArrayList<>();
    public static List<Item> whisperingThicketLootTable = new ArrayList<>();

    public static void generateItemList() {
        apple = new Item("Apple", "/apple-transparent.png", 50, 20, 1);
        blackberry = new Item("Blackberry", "/blackberry-transparent.png", 50, 20, 1);
        berry = new Item("Berry","/berry-transparent.png", 50, 20, 1);
        strawberry = new Item("Strawberry", "/strawberry-transparent.png", 50, 20, 1);
        raspberry = new Item("Raspberry", "/raspberry-transparent.png", 50, 20, 1);
        blueberry = new Item("Blueberry","/blueberry-transparent.png", 50, 20, 1);
        elderberry = new Item("Elderberry", "/elderberry-transparent.png", 50, 20, 1);
        mulberry = new Item("Mulberry", "/mulberry-transparent.png", 50, 20, 1);
        gooseberry = new Item("Gooseberry", "/gooseberry-transparent.png", 50, 20, 1);
        huckleberry = new Item("Huckleberry","/huckleberry-transparent.png", 50, 20, 1);
        chanterelle = new Item("Chanterelle","/chanterelle-transparent.png", 50, 20, 1);
        turkeytailmushroom = new Item("Turkey Tail", "/turkeytail-transparent.png", 50, 20, 1);
        currant = new Item("Currant","/currant-transparent.png", 50, 20, 1);
        oystermushroom = new Item("Oyster Mushroom", "/oystermushroom-transparent.png", 50, 20, 1);
        henofthewoodsmushroom = new Item("Hen of the Woods", "/henofthewoods-transparent.png", 50, 20, 1);
        morel = new Item("Morel","/morel-transparent.png",50, 20,1);
        puffballmushroom = new Item("Puffball","/puffball-transparent.png",50,20,1);
        russulamushroom = new Item("Russula","/russula-transparent.png", 50, 20, 1);
        porcini = new Item("Porcini","/porcini-transparent.png",50,20,1);
        truffle = new Item("Truffle","/truffle-transparent.png", 25, 180, 2);
        cd_rom = new Item("CD Rom", "/prof. cd-transparent.png", 10, 1250, 3);
        mint = new Item("Mint", "/mint-transparent.png", 50, 21, 1);
        thyme = new Item("Thyme","/thyme-transparent.png", 50, 21, 1);
        sage = new Item("Sage","/sage-transparent.png",50,21,1);
        oregano = new Item("Oregano","/oregano-transparent.png",50,21,1);
        chamomile = new Item("Chamomile","/chamomile-transparent.png",50, 21,1);
        dandelion = new Item("Dandelion", "/dandelion-transparent.png", 50, 21, 1);
        plantain = new Item("Plantain", "/plantain-transparent.png", 50, 21, 1);
        yarrow = new Item("Yarrow","/yarrow-transparent.png", 50, 21, 1);
        stingingnettle = new Item("Stinging Nettle", "/stingingnettle-transparent.png", 50, 21, 1);
        wildgarlic = new Item("Wild Garlic","/wildgarlic-transparent.png", 50, 21, 1);
        acorn = new Item("Acorn","/acorn-transparent.png", 50, 21, 1);
        twigs = new Item("Twigs", "/twigs-transparent.png", 50, 21, 1);
        pinecone = new Item("Pinecone", "/pinecone-transparent.png", 50, 21, 1);
        wildrose = new Item("Wild Rose", "/wildrose-transparent.png", 50, 25, 1);
        bluebell = new Item("Blue Bell", "bluebell-transparent.png", 50, 25, 1);
        trillium = new Item("Trillium", "/trillium-transparent.png", 50, 25, 1);
        woodanemone = new Item("Wood Anemone", "woodanemone-transparent.png", 50, 25, 1);
        foxglove = new Item("Foxglove", "foxglove-transparent.png",50, 25,1);
        buttercup = new Item("Buttercup", "/buttercup-transparent.png", 50, 25, 1);
        violet = new Item("Violet","/violet-transparent.png", 50, 25, 1);
        daisy = new Item("Daisy", "/daisy-transparent.png", 50, 25, 1);
        forgetmenot = new Item("Forget-Me-Not", "/forgetmenot-transparent.png", 50, 25, 1);
        primrose = new Item("Prim Rose", "/primrose-transparent.png", 50, 25, 1);
        bluegrass = new Item("Bluegrass", "/bluegrass-transparent.png", 50, 18, 1);
        fescue = new Item("Fescue", "/fescue-transparent.png", 50, 18, 1);
        ryegrass = new Item("Ryegrass", "/ryegrass-transparent.png", 50, 18, 1);
        timothygrass = new Item("Timothy Grass", "/timothygrass-transparent.png", 50, 18, 1);
        granitepebble = new Item("Granite Pebble","/granitepebble-transparent.png", 50, 10, 1);
        limestonechipping = new Item("Limestone Chipping","/limestonechipping-transparent.png", 50, 10, 1);
        flintnodule = new Item("Flint Nodule", "/flintnodule-transparent.png", 50, 10, 1);
        quartzpebble = new Item("Quartz Pebble","/quartzpebble-transparent.png", 50, 10, 1);
        ant = new Item("Ant", "/ant-transparent.png",50, 35, 1);
        beetle = new Item("Beetle","/beetle-transparent.png", 50, 40,1);
        monarchbutterfly = new Item("Monarch Butterfly","/monarchbutterfly-transparent.png", 50, 50, 1);
        swallowtailbutterfly = new Item("Swallowtail Butterfly","/swallowtailbutterfly-transparent.png", 50, 50, 1);
        paintedladybutterfly = new Item("Painted Lady Butterfly","/paintedladybutterfly-transparent.png", 50, 50, 1);
        bluemorphobutterfly = new Item("Blue Morpho Butterfly","/bluemorphobutterfly-transparent.png", 50, 50, 1);
        redadmiralbutterfly = new Item("Red Admiral Butterfly", "/redadmiralbutterfly-transparent.png", 50, 50, 1);
        viceroybutterfly = new Item("Viceroy Butterfly", "/viceroybutterfly-transparent.png", 50, 50, 1);
        peacockbutterfly = new Item("Peacock Buttefly","/peacockbutterfly-transparent.png",50,50, 1);
        cabbagewhitebutterfly = new Item("Cabbage White Butterfly", "/cabbagewhitebutterfly-transparent.png", 50, 50, 1);
        moth = new Item("Moth","/moth-transparent.png", 50, 45, 1);
        grasshopper = new Item("Grasshopper","/grasshopper-transparent.png", 50, 55,1);
        orbweaverspider = new Item("Orb-Weaver Spider","/orbweaverspider-transparent.png",50,60,1);
        jumpingspider = new Item("Jumping Spider","/jumpingspider-transparent.png", 50, 60, 1);
        wolfspider = new Item("Wolf Spider","/wolfspider-transparent.png", 50, 60, 1);
        blackwidowspider = new Item("Black Widow Spider","/blackwidow-transparent.png",50,60,1);
        tarantula = new Item("Tarantula","/tarantula-transparent.png",50,60,1);
        dragonfly = new Item("Dragonfly","/dragonfly-transparent.png",50,80,1);
        bee = new Item("Bee","/bee-transparent.png",50,80,1);
        caterpillar = new Item("Caterpillar", "/caterpillar-transparent.png", 50, 85, 1);
        cricket = new Item("Cricket","/cricket-transparent.png", 50, 83, 1);
        grovemint = new Item("Grove Mint","/grovemint-transparent.png", 50, 35, 1);
        ballofmoss = new Item("Ball of Moss","/ballofmoss-transparent.png", 50, 35, 1);
        brambleberry = new Item("Bramble Berry","/brambleberrie-transparent.png", 50, 36, 2);
    }

    public static void populateLootTables() {
        forestLootTable.add(apple);
        forestLootTable.add(berry);
        forestLootTable.add(blackberry);
        forestLootTable.add(raspberry);
        forestLootTable.add(blueberry);
        forestLootTable.add(strawberry);
        forestLootTable.add(twigs);
        forestLootTable.add(elderberry);
        forestLootTable.add(mulberry);
        forestLootTable.add(gooseberry);
        forestLootTable.add(huckleberry);
        forestLootTable.add(currant);
        forestLootTable.add(chanterelle);
        forestLootTable.add(oystermushroom);
        forestLootTable.add(morel);
        forestLootTable.add(porcini);
        forestLootTable.add(henofthewoodsmushroom);
        forestLootTable.add(turkeytailmushroom);
        forestLootTable.add(puffballmushroom);
        forestLootTable.add(russulamushroom);
        forestLootTable.add(mint);
        forestLootTable.add(wildgarlic);
        forestLootTable.add(thyme);
        forestLootTable.add(sage);
        forestLootTable.add(oregano);
        forestLootTable.add(chamomile);
        forestLootTable.add(dandelion);
        forestLootTable.add(plantain);
        forestLootTable.add(yarrow);
        forestLootTable.add(stingingnettle);
        forestLootTable.add(acorn);
        forestLootTable.add(pinecone);
        forestLootTable.add(wildrose);
        forestLootTable.add(bluebell);
        forestLootTable.add(trillium);
        forestLootTable.add(woodanemone);
        forestLootTable.add(foxglove);
        forestLootTable.add(buttercup);
        forestLootTable.add(violet);
        forestLootTable.add(daisy);
        forestLootTable.add(forgetmenot);
        forestLootTable.add(primrose);
        forestLootTable.add(bluegrass);
        forestLootTable.add(fescue);
        forestLootTable.add(ryegrass);
        forestLootTable.add(timothygrass);
        forestLootTable.add(granitepebble);
        forestLootTable.add(limestonechipping);
        forestLootTable.add(flintnodule);
        forestLootTable.add(quartzpebble);
        forestLootTable.add(ant);
        forestLootTable.add(beetle);
        forestLootTable.add(monarchbutterfly);
        forestLootTable.add(swallowtailbutterfly);
        forestLootTable.add(paintedladybutterfly);
        forestLootTable.add(bluemorphobutterfly);
        forestLootTable.add(redadmiralbutterfly);
        forestLootTable.add(viceroybutterfly);
        forestLootTable.add(peacockbutterfly);
        forestLootTable.add(cabbagewhitebutterfly);
        forestLootTable.add(moth);
        forestLootTable.add(grasshopper);
        forestLootTable.add(orbweaverspider);
        forestLootTable.add(jumpingspider);
        forestLootTable.add(wolfspider);
        forestLootTable.add(blackwidowspider);
        forestLootTable.add(tarantula);
        forestLootTable.add(dragonfly);
        forestLootTable.add(bee);
        forestLootTable.add(caterpillar);
        forestLootTable.add(cricket);

        forestClearingLootTable.addAll(forestLootTable);
        forestClearingLootTable.add(truffle);
        forestClearingLootTable.add(cd_rom);

        whisperingThicketLootTable.add(grovemint);
        whisperingThicketLootTable.add(pinecone);
        whisperingThicketLootTable.add(acorn);
        whisperingThicketLootTable.add(ballofmoss);
        whisperingThicketLootTable.add(twigs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<String, Scene> scenes = loadScenes();
            if (scenes.containsKey("forest")) {
                new GameFrame(scenes);
            } else {
                System.err.println("Error: Initial scene 'forest' not found in scenes map.");
            }
        });
    }

    private static Map<String, Scene> loadScenes() {
        Map<String, Scene> scenes = new HashMap<>();
        generateItemList();
        populateLootTables();

        List<String> forestAdjacent = Collections.singletonList("forest clearing");
        List<String> forestClearingAdjacent = Arrays.asList("forest", "bank", "whispering thicket");
        List<String> whisperingThicketAdjacent = Collections.singletonList("forest clearing");
        List<String> bankAdjacent = Collections.singletonList("forest clearing");

        Scene forest = new Scene("You are in a forest.", "/forestScene.png", forestAdjacent, forestLootTable, 1);
        Scene forestClearing = new Scene("You are in a forest clearing.", "/forestClearing.png", forestClearingAdjacent, forestClearingLootTable, 2);
        Scene bank = new Scene("You are in the bank.", "/bankScene.png", bankAdjacent, new ArrayList<>(), 0);
        Scene whisperingThicket = new Scene("Quiet whispers mysteriously echo around you.", "/whisperingThicket.png", whisperingThicketAdjacent, whisperingThicketLootTable, 3);

        scenes.put("forest", forest);
        scenes.put("forest clearing", forestClearing);
        scenes.put("bank", bank);
        scenes.put("whispering thicket", whisperingThicket);

        System.out.println("Scenes loaded: " + scenes.keySet());

        return scenes;
    }
}
