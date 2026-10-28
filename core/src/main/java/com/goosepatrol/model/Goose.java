package com.goosepatrol.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * Goose movement (FR_GOOSE_MOVE),
 * Each goose will move at a constant speed towards the player from the position they spawn at,
 * Each goose has its own area it occupies and cannot be overlapping with other geese. 
 */
public class Goose {

    /** The width of the geese's box, in map pixels. */
    public static final float WIDTH = 12;

    /** The height of the geese's box, in map pixels. */
    public static final float HEIGHT = 12;

    private final Rectangle bounds;
    private final Vector2 velocity = new Vector2();
    private final Vector2 direction = new Vector2();
    private Facing facing = Facing.RIGHT;

    /**
     * Makes a goose, standing still and facing right.
     * 
     * @param centre the centre of the goos's starting place, such as one of the map's goose spawns
     */
    public Goose(Vector2 centre){
        bounds = new Rectangle(centre.x - WIDTH / 2, centre.y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    /**
     * Moves the goose by one frame. The goose turns to face the target, then walks towards it at
     * {@link GameConfig#GOOSE_VELOCITY}, moves across and up, stopping at the map's edges and at walls.
     * 
     * @param target the point to walk toward
     * @param flock every goose on the map
     * @param delta the frame's length in seconds
     * @param map the map, for its edges and walls
     */
    public void update(Vector2 target, List<Goose> flock, float delta, CampusMap map){
        direction.set(target).sub(bounds.x + WIDTH / 2, bounds.y + HEIGHT / 2);
        if (direction.len() < 1){
            velocity.setZero();
            return;
        }
        direction.nor();
        velocity.set(direction).scl(GameConfig.GOOSE_VELOCITY);
        moveX(velocity.x * delta, map, flock);
        moveY(velocity.y * delta, map, flock);
        if (velocity.x != 0) {
            facing = velocity.x < 0 ? Facing.LEFT : Facing.RIGHT;
        }
    }

        // Moves across, keeping the goose inside the map, out of walls and clear of other geese.
    private void moveX(float dx, CampusMap map, List<Goose> flock) {
        if (dx == 0) {
            return;
        }
        float oldX = bounds.x;
        float wantedX = oldX + dx;
        bounds.x = MathUtils.clamp(wantedX, 0, map.getWidth() - bounds.width);
        Rectangle wall = map.wallOverlapping(bounds);
        if (wall != null) {
            bounds.x = dx > 0 ? wall.x - bounds.width : wall.x + wall.width; // right against the wall's near side
            if (map.wallOverlapping(bounds) != null) {
                bounds.x = oldX; // squeezed between two walls: stay where it was
        }
        }
        if (overlapsAnotherGoose(flock)) {
            bounds.x = oldX; // another goose is in the way: don't move across this frame
        }
        if (bounds.x != wantedX) {
            velocity.x = 0; // blocked on this axis only, so the goose slides along what stopped it
        }
    }
    
    // Moves up or down, the same way as moveX.
    private void moveY(float dy, CampusMap map, List<Goose> flock) {
        if (dy == 0) {
            return;
        }
        float oldY = bounds.y;
        float wantedY = oldY + dy;
        bounds.y = MathUtils.clamp(wantedY, 0, map.getHeight() - bounds.height);
        Rectangle wall = map.wallOverlapping(bounds);
        if (wall != null) {
            bounds.y = dy > 0 ? wall.y - bounds.height : wall.y + wall.height;
            if (map.wallOverlapping(bounds) != null) {
                bounds.y = oldY;
            }
        }
        if (overlapsAnotherGoose(flock)) {
            bounds.y = oldY;
        }
        if (bounds.y != wantedY) {
            velocity.y = 0;
        }
    }

    // True if this goose's box overlaps any other goose's. Boxes that only touch at an edge don't overlap.
    private boolean overlapsAnotherGoose(List<Goose> flock) {
        for (Goose other : flock) {
            if (other != this && bounds.overlaps(other.bounds)) {
                return true;
            }
        }
        return false;
    }
     
    /**
     * Says whether this goose's box overlaps a box, such as a new goose's spawn place (FR_SPAWN).
     *
     * @param box the box to check
     * @return true if the two boxes overlap
     */
    public boolean overlaps(Rectangle box) {
        return bounds.overlaps(box);
    }
    
    /**
     * Returns the goose's box.
     *
     * @return a copy of the box, in map pixels
     */
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }
    
    /**
     * Returns the goose's speed.
     *
     * @return a copy of the speed, in map pixels per second
     */
    public Vector2 getVelocity() {
        return new Vector2(velocity);
    }
    
    /**
     * Returns which way the goose faces, which the view draws the goose by.
     *
     * @return LEFT or RIGHT
     */
    public Facing getFacing() {
        return facing;
    }

}