/**
 * ExperienceCalculator provides methods to calculate experience points and levels.
 */
public class ExperienceCalculator {

    private static final long[] EXPERIENCE_TABLE = {
            0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523,
            3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247,
            20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127,
            83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886,
            273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257,
            992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087,
            2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558,
            9684577, 9684577, 10692629, 11805606, 13034431
    };

    /**
     * Returns the experience points required for the specified level.
     *
     * @param level the level
     * @return the experience points required
     */
    public static long getExperienceForLevel(int level) {
        if (level < 1 || level > EXPERIENCE_TABLE.length) {
            throw new IllegalArgumentException("Level must be between 1 and " + EXPERIENCE_TABLE.length);
        }
        return EXPERIENCE_TABLE[level - 1];
    }

    /**
     * Calculates the new level based on the experience points and current level.
     *
     * @param experience   the experience points
     * @param currentLevel the current level
     * @return the new level
     */
    public static int calculateNewLevel(long experience, int currentLevel) {
        for (int level = currentLevel; level < EXPERIENCE_TABLE.length; level++) {
            if (experience < EXPERIENCE_TABLE[level]) {
                return level;
            }
        }
        return EXPERIENCE_TABLE.length;
    }

    /**
     * Calculates the remaining experience points needed to reach the next level.
     *
     * @param experience the current experience points
     * @param level      the current level
     * @return the remaining experience points
     */
    public static long calculateRemainingExperience(long experience, int level) {
        if (level < 1 || level >= EXPERIENCE_TABLE.length) {
            throw new IllegalArgumentException("Level must be between 1 and " + EXPERIENCE_TABLE.length);
        }
        return EXPERIENCE_TABLE[level] - experience;
    }
}