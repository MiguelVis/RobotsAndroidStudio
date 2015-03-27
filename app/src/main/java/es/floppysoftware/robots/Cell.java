/**
 * Cell.java
 */
package es.floppysoftware.robots;

import android.widget.ImageView;

/**
 * Implements a board cell.
 *
 * Created by Miguel on 24/03/2015.
 */
public class Cell {

    private int type;         // Contents type
    private ImageView image;  // Related ImageView

    // Contents type
    static public final int CELL_GROUND = 0;
    static public final int CELL_WALL = 1;
    static public final int CELL_HUMAN = 2;
    static public final int CELL_ROBOT = 3;
    static public final int CELL_SCRAP = 4;
    static public final int CELL_HUMAN_DEAD = 5;
    static public final int CELL_ROBOT_WIN = 6;
    static public final int CELL_HUMAN_WIN = 7;
    static public final int CELL_ROBOT_TEMP = 8;  // Used only when moving robots

    // Image resources, related to contents type in the same order
    static private final int imageResources[] = {
            R.drawable.cell_ground, R.drawable.cell_wall, R.drawable.cell_human,
            R.drawable.cell_robot, R.drawable.cell_scrap, R.drawable.cell_dead,
            R.drawable.cell_robot_win, R.drawable.cell_human_win,
            R.drawable.cell_robot
    };

    /**
     * Constructor
     *
     * @param type  Contents type
     * @param image Related ImageView
     */
    public Cell(int type, ImageView image) {

        // Set ImageView
        this.image = image;

        // Set contents type
        setType(type);

    }

    /**
     * Return cell contents type
     *
     * @return  Contents type
     */
    public int getType() {

        return this.type;
    }

    /**
     * Set cell contents type
     *
     * @param type  Contents type
     */
    public void setType(int type) {

        // Set contents type
        this.type = type;

        // Change image accordingly
        this.image.setImageResource(imageResources[type]);
    }
}
