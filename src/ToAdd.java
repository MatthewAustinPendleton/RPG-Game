public class ToAdd {
    /**
     * To add:
     * 6) I now have images of a seed buried, and subsequent images that show how that seed grows over time. What I want
     * now is a system where you have a seed in your inventory, for the time being let's just say it's a cabbage seed.
     * The idea is that when you're in the farm scene, if you right-click on an "empty" farm plot, that is one where
     * nothing is currently growing, which they all start out as, then you're given the option of planting seeds that are
     * in your inventory. The list will list all unique seeds currently in your inventory. If you select that seed in the
     * empty plot, it will then overlap the farm plot you selected with an image of the seed being planted, the first
     * in a succession of images relating to the growing of a cabbage plant. These will be referred to as
     * "/cabbageGrowing1.png", "/cabbageGrowing2.png", and so on, all the way to a planted-but-fully-grown cabbage.
     * Now the idea is that based on the thing planted, there is an associated timer, like cabbages have 5 real minutes.
     * So you wait five real minutes, and carve that up into the amount of growth stages of the cabbage seed. In this case let's say that the
     * cabbage has 5 stages: seed, sprout, plant, near-grown, and fully-grown. That means 5 minutes is split into 5, 1 minute a piece.
     * That means every minute the image should change from seed -> sprout -> plant -> near-grown -> fully-grown.
     * That means "/cabbageGrowing1.png" to "/cabbageGrowning2.png", etc.
     *
     *
     *
     * forage experience bar animating from current exp to where the bar should be at after exp is added
     * make the level up animated in such a way that the level changes in front of the player from skillLevel to
     * skillLevel+1, i kinda want it to have like a square outline appear behind just the
     * level number, and have that kinda become '3d' in the sense that it rotates in 3D space and behind the square is
     * the skill level number + 1, if that makes sense
     */
}