package dev.risas.nokrooms.utilities.cuboid;

/**
 * Represents directions that can be applied to certain faces and actions of a Cuboid
 */
public enum CuboidDirection {

    NORTH,
    EASY,
    SOUTH,
    WEST,
    UP,
    DOWN,
    HORIZONTAL,
    VERTICAL,
    BOTH,
    UNKNOWN;

    public CuboidDirection opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EASY -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EASY;
            case HORIZONTAL -> VERTICAL;
            case VERTICAL -> HORIZONTAL;
            case UP -> DOWN;
            case DOWN -> UP;
            case BOTH -> BOTH;
            default -> UNKNOWN;
        };
    }
}