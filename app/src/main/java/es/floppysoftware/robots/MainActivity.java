/**
 * MainActivity.java
 *
 * Copyright (c) 2015 Miguel I. Garcia Lopez / FloppySoftware, Spain
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation;
 * either version 2, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package es.floppysoftware.robots;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Main class for Robots game.
 *
 * @version 1.00
 * @since   24 Mar 2015
 *
 * (c) 2015 Miguel I. Garcia Lopez / FloppySoftware.
 *
 * www.floppysoftware.es
 * floppysoftware@gmail.com
 */
public class MainActivity extends ActionBarActivity {

    static private final int BOARD_ROWS = 9;  // # of board rows
    static private final int BOARD_COLS = 9;  // # of board columns

    static private final int ROBOTS = 4;      // # of robots in board

    static private final int TEL_UNITS = 3;   // # of teletransporting units on start

    // Board layout
    private TableLayout boardLayout;  //
    private TableRow[] boardRowLayout = new TableRow[BOARD_ROWS]; //

    // Board cells
    private Cell[][] boardCells = new Cell[BOARD_ROWS][BOARD_COLS];

    // Human
    private int human_row;  // Row position on board
    private int human_col;  // Column position on board

    // Needed for random numbers
    private Random myRandom = new Random();

    // Declare Buttons
    private Button btnUpLeft, btnUp, btnUpRight, btnLeft, btnRight,
            btnDownLeft, btnDown, btnDownRight,
            btnTel, btnReset;

    // Declare TextViews
    private TextView tvTitle;

    // Some globals
    private int telUnits;      // Teletransporting units left
    private int robots;        // Robots left in board
    private boolean playing;   // True if we are playing, else false

    // Sound
    private SoundPool soundPool;
    private int soundGameOver;
    private int soundMove;
    private int soundTel;
    private int soundReset;

    /**
     * Method called when the App starts.
     *
     * @param savedInstanceState   Some data (not used by now)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Save some data (not used by now)
        super.onCreate(savedInstanceState);

        // Set layout
        setContentView(R.layout.activity_main);

        // Link to board layout
        boardLayout = (TableLayout) findViewById(R.id.tableLayout);

        // Link to text views
        tvTitle = (TextView) findViewById(R.id.textViewTitle);

        // Link to buttons
        btnUpLeft = (Button) findViewById(R.id.btnUpLeft);
        btnUp = (Button) findViewById(R.id.btnUp);
        btnUpRight = (Button) findViewById(R.id.btnUpRight);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnDownLeft = (Button) findViewById(R.id.btnDownLeft);
        btnDown = (Button) findViewById(R.id.btnDown);
        btnDownRight = (Button) findViewById(R.id.btnDownRight);

        btnTel = (Button) findViewById(R.id.btnTel);

        btnReset = (Button) findViewById(R.id.btnReset);

        // OnClick listener for buttons
        btnUpLeft.setOnClickListener(onClickListenerForArrows);
        btnUp.setOnClickListener(onClickListenerForArrows);
        btnUpRight.setOnClickListener(onClickListenerForArrows);
        btnLeft.setOnClickListener(onClickListenerForArrows);
        btnRight.setOnClickListener(onClickListenerForArrows);
        btnDownLeft.setOnClickListener(onClickListenerForArrows);
        btnDown.setOnClickListener(onClickListenerForArrows);
        btnDownRight.setOnClickListener(onClickListenerForArrows);

        btnTel.setOnClickListener(onClickListenerForTel);

        btnReset.setOnClickListener(onClickListenerForReset);

        // Setup the sound
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundGameOver = soundPool.load(this, R.raw.game_over, 1);
        soundMove = soundPool.load(this, R.raw.move, 1);
        soundTel = soundPool.load(this, R.raw.teletr, 1);
        soundReset = soundPool.load(this, R.raw.reset, 1);

        // Setup the board
        setUpBoard();

        // Clear board to start playing
        clearBoard();
    }

    /**
     * Method called when constructing the action bar menu.
     *
     * @param menu  Menu
     * @return      True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Bye, bye
        return true;
    }

    /**
     * Method called when the user selects a menu item.
     *
     * @param item   Selected item
     * @return       True or false, it depends!
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the option id
        int id = item.getItemId();

        // Do an action according to the selected option
        switch(id) {

            // Settings
            case R.id.action_settings :
                Toast.makeText(this, R.string.msg_not_implemented, Toast.LENGTH_SHORT).show();
                return true;

            // About of Robots
            case R.id.action_about :
                dialogAbout();
                return true;
        }

        // Item not handled
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the board for the first time.
     */
    public void setUpBoard() {

        // Link board to cells and layout
        for(int r = 0; r < BOARD_ROWS; ++r) {

            // For each row, we link the TableRow
            TableRow tr = new TableRow(this);
            boardRowLayout[r] = tr;

            // For each row, we link each column
            for(int c = 0; c < BOARD_COLS; ++c) {

                // Create ImageView
                ImageView iv = new ImageView(this);

                // Assign the image resource to GROUND (it's useless because
                // when we create the Cell later, it will be do the same again, and
                // when we will call clearBoard() too!!!)
                iv.setImageResource(R.drawable.cell_ground);

                // Add ImageView to TableRow
                tr.addView(iv);

                // Link board cell to a new Cell object
                boardCells[r][c] = new Cell(Cell.CELL_GROUND, iv);
            }

            // Add the TableRow to the TableLayout
            boardLayout.addView(tr);
        }
    }

    /**
     * Clear the board. It resets the board to start playing (again).
     */
    public void clearBoard() {

        // Set all board cells as GROUND
        for(int r = 0; r < BOARD_ROWS; ++r) {
            for (int c = 0; c < BOARD_COLS; ++c) {

                // Set this cell as GROUND
                boardCells[r][c].setType(Cell.CELL_GROUND);
            }
        }

        // Set some WALLs
        boardCells[2][2].setType(Cell.CELL_WALL);

        // Set HUMAN
        human_row = BOARD_ROWS / 2;
        human_col = BOARD_COLS / 2;

        boardCells[human_row][human_col].setType(Cell.CELL_HUMAN);

        // Set some ROBOTs
        for(int i = 0; i < ROBOTS; ++i) {

            // Set a ROBOT in a GROUND random position
            while(true) {

                int r = myRandom.nextInt(BOARD_ROWS);  // Row
                int c = myRandom.nextInt(BOARD_COLS);  // Column

                // Put the robot there if the position is empty (ground)
                if (boardCells[r][c].getType() == Cell.CELL_GROUND) {

                    // Put the robot there.
                    boardCells[r][c].setType(Cell.CELL_ROBOT);

                    // Next, please
                    break;
                }
            }
        }

        // Reset some globals
        robots = ROBOTS;       // # of robots in board
        telUnits = TEL_UNITS;  // # of teletransporting units left

        // Update some things on screen
        updateTelUnits();                    // # of teletransporting units left
        tvTitle.setText(R.string.app_name);  // App title

        // Play the song
        soundPool.play(soundReset, 1.0f, 1.0f, 0, 0, 1.0f);

        // We are playing, now!!!
        playing = true;
    }

    /**
     * Listener for arrow buttons.
     */
    OnClickListener onClickListenerForArrows = new OnClickListener() {

        /**
         * Method called when a button is clicked.
         *
         * @param view   Related view (Button)
         */
        @Override
        public void onClick(View view) {

            // Do nothing if we are not playing
            if(playing == false)
                return;

            // Shifts for row and column
            int sh_row, sh_col;

            // Set the shifts, according to the clicked button
            switch (view.getId()) {

                case R.id.btnUpLeft:
                    sh_row = -1;
                    sh_col = -1;
                    break;

                case R.id.btnUp:
                    sh_row = -1;
                    sh_col = 0;
                    break;

                case R.id.btnUpRight:
                    sh_row = -1;
                    sh_col = 1;
                    break;

                case R.id.btnLeft:
                    sh_row = 0;
                    sh_col = -1;
                    break;
                case R.id.btnRight:
                    sh_row = 0;
                    sh_col = 1;
                    break;

                case R.id.btnDownLeft:
                    sh_row = 1;
                    sh_col = -1;
                    break;

                case R.id.btnDown:
                    sh_row = 1;
                    sh_col = 0;
                    break;

                default:         //case R.id.btnDownRight:
                    sh_row = 1;
                    sh_col = 1;
                    break;
            }

            // Calculate new position
            int to_row = human_row + sh_row;
            int to_col = human_col + sh_col;

            // Do things if the new position is legal (board inside)
            if (to_row >= 0 && to_row < BOARD_ROWS && to_col >= 0 && to_col < BOARD_COLS) {

                // Involved cells
                Cell from_cell = boardCells[human_row][human_col];  // Human cell
                Cell to_cell = boardCells[to_row][to_col];          // Destination cell

                // Check destination cell contents
                switch(to_cell.getType()) {

                    // Ground: Ok, move the human there
                    case Cell.CELL_GROUND :

                        // Play the song
                        soundPool.play(soundMove, 1.0f, 1.0f, 0, 0, 1.0f);

                        from_cell.setType(Cell.CELL_GROUND);
                        to_cell.setType(Cell.CELL_HUMAN);

                        human_row = to_row;
                        human_col = to_col;

                        actRobots();
                        break;

                    // Robot: Kill the human
                    case Cell.CELL_ROBOT :

                        to_cell.setType(Cell.CELL_ROBOT_WIN);

                        youAreDead();
                        break;
                }
            }
        }
    };

    /**
     * Listener for teletransporting button
     */
    OnClickListener onClickListenerForTel = new OnClickListener() {

        /**
         * Method called when the button is clicked.
         *
         * @param view   Related view (Button)
         */
        @Override
        public void onClick(View view) {

            // Do nothing if we are not playing
            // or we have not any teletransporting units
            if (playing == false || telUnits == 0)
                return;

            // Search for an empty cell (ground) in a random position
            while (true) {

                // Get a random position
                int r = myRandom.nextInt(BOARD_ROWS);  // Row
                int c = myRandom.nextInt(BOARD_COLS);  // Col

                // Move the human there, if it is a ground cell
                if (boardCells[r][c].getType() == Cell.CELL_GROUND) {

                    // Play the song
                    soundPool.play(soundTel, 1.0f, 1.0f, 0, 0, 1.0f);

                    boardCells[human_row][human_col].setType(Cell.CELL_GROUND);

                    boardCells[r][c].setType(Cell.CELL_HUMAN);

                    human_row = r;
                    human_col = c;

                    --telUnits;

                    updateTelUnits();

                    // Done
                    break;
                }
            }
        }
    };

    /**
     * Listener for reset button
     */
    OnClickListener onClickListenerForReset = new OnClickListener() {

        /**
         * Method called when the button is clicked.
         *
         * @param view   Related view (Button)
         */
        @Override
        public void onClick(View view) {

            // Restart game
            clearBoard();
        }
    };

    /**
     * Robots play
     */
    public void actRobots() {

        // Find all the robots in the board
        for(int r = 0; r < BOARD_ROWS; ++r) {
            for (int c = 0; c < BOARD_COLS; ++c) {

                // Get this cell
                Cell cell = boardCells[r][c];

                // Proceed if the cell has a robot
                if (cell.getType() == Cell.CELL_ROBOT) {

                    // Calculate destination position
                    int to_row = r;
                    int to_col = c;

                    // Adjust row
                    if (r < human_row)
                        ++to_row;
                    else if (r > human_row)
                        --to_row;

                    // Adjust column
                    if (c < human_col)
                        ++to_col;
                    else if (c > human_col)
                        --to_col;

                    // Get the destination cell
                    Cell toCell = boardCells[to_row][to_col];

                    // Get the destination cell contents
                    int type = toCell.getType();

                    // If the destination cell is ground,
                    // move the robot there.

                    // If the destination cell is a human,
                    // kill the human.

                    // If the destination cell is a wall or
                    // another robot, kill the robot(s).

                    if (type == Cell.CELL_GROUND) {

                        // Ground: Move the robot there
                        cell.setType(Cell.CELL_GROUND);
                        toCell.setType(Cell.CELL_ROBOT_TEMP);

                    } else if(type == Cell.CELL_HUMAN) {

                        // Human: Kill the human
                        cell.setType(Cell.CELL_ROBOT_WIN);

                        youAreDead();

                        // Done
                        return;

                    } else {

                        // Another: Robot is dead
                        cell.setType(Cell.CELL_SCRAP);

                        --robots;

                        // If there is another robot,
                        // kill that robot too
                        if (type == Cell.CELL_ROBOT || type == Cell.CELL_ROBOT_TEMP) {

                            toCell.setType(Cell.CELL_SCRAP);

                            --robots;
                        }

                        // Check if the human wins (no more robots)
                        if(robots == 0) {

                            youWin();

                            // Done
                            return;
                        }
                    }
                }
            }
        }

        // Persist the new locations of the robots
        for(int r = 0; r < BOARD_ROWS; ++r) {
            for (int c = 0; c < BOARD_COLS; ++c) {

                // Get this cell
                Cell cell = boardCells[r][c];

                // If the cell contents is a temporary robot,
                // change it to simple robot
                if (cell.getType() == Cell.CELL_ROBOT_TEMP)
                    cell.setType(Cell.CELL_ROBOT);
            }
        }
    }

    /**
     * Update teletransporting units left on the screen.
     */
    public void updateTelUnits() {

        // Change the text in the teletransporting button
        btnTel.setText("" + telUnits);
    }

    /**
     * The human is dead.
     */
    public void youAreDead() {

        // Update the HUMAN cell contents
        boardCells[human_row][human_col].setType(Cell.CELL_HUMAN_DEAD);

        // Set the App title
        tvTitle.setText(R.string.title_you_are_dead);

        // Play the song
        soundPool.play(soundGameOver, 1.0f, 1.0f, 0, 0, 1.0f);

        // We are NOT playing
        playing = false;
    }

    /**
     * All robots are dead.
     */
    public void youWin() {

        // Update the HUMAN cell contents
        boardCells[human_row][human_col].setType(Cell.CELL_HUMAN_WIN);

        // Set the App title
        tvTitle.setText(R.string.title_you_win);

        // Play the song
        soundPool.play(soundGameOver, 1.0f, 1.0f, 0, 0, 1.0f);

        // We are NOT playing
        playing = false;
    }

    /**
     * About of dialog
     */

    private void dialogAbout() {

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Build the dialog
        builder.setTitle(getResources().getString(R.string.about_title))
            .setMessage(getResources().getString(R.string.about_text))
            .setCancelable(false)
            .setPositiveButton(getResources().getString(R.string.btn_ok),

                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            // Nothing to do
                        }
                    });

        // Create AlertDialog
        AlertDialog alert = builder.create();

        // Show the dialog
        alert.show();
    }


}
