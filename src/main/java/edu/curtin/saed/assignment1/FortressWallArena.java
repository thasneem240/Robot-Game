package edu.curtin.saed.assignment1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"PMD.ConfusingTernary"}) // To fulfill the logic, a complex ternary expression is necessary
public class FortressWallArena
{
    private Map<String, FortressWall> wallMap;
    private final LogMessage logMessage;
    private final RobotArena robotArena;
    private final SwingArena swingArena;


    private boolean[][] wallGrid;


    public FortressWallArena(LogMessage logMessage, RobotArena robotArena)
    {
        this.logMessage = logMessage;
        this.robotArena = robotArena;

        swingArena = robotArena.getSwingArena();

        wallMap = new ConcurrentHashMap<>();

        int gridWidth = swingArena.getGridWidth(); // columns
        int gridHeight = swingArena.getGridHeight(); // rows

        wallGrid = new boolean[gridWidth][gridHeight];

    }


    /**
     * First, update the Wall's position within both the ForestWallArena's hashmap and the grid,
     * and then initiate a UI update in the SwingArena.
     */
    public boolean updateWallPosition( FortressWall fortressWall)
    {
        int wallX = fortressWall.getWallX();
        int wallY = fortressWall.getWallY();

        boolean isWallBuilt = false;


        // Confirm the limit of building up to 10 fortress walls
        if(wallMap.size() < 10)
        {
            // Check whether the Block is already occupied by a Robot or not
            if(!robotArena.isRobotAtGrid(wallX,wallY))
            {
                // Check whether the block is presently occupied by another wall.
                if(!isWallAtGrid(wallX,wallY))
                {
                    wallGrid[wallX][wallY] = true; // Now the Block is Occupied by the Wall
                    wallMap.put(fortressWall.getWallName(),fortressWall); // Put the Wall into the Wall Map

                    String message = String.format(" The %s has been created $$$ at ( %d, %d )",
                            fortressWall.getWallName(),wallX,wallY);

                    logMessage.putMessage(message);

                    // This method is called in order to redraw the screen
                    swingArena.repaint();

                    isWallBuilt = true; // Wall successfully built
                }
                else
                {
                    System.out.println("Wall Block is Occupied By Another WALL");
                }

            }
            else
            {
                System.out.println("Wall Block is Occupied By Another ROBOT");
            }
        }
        else
        {
            System.out.println(" Maximum Limit Reached!!! Only up to 10 fortress walls can be built" +
                    " at any one time \n And any extra wall-building commands will be Ignored. \n");
        }

    return isWallBuilt;

    }

    public Map<String, FortressWall> getWallMap()
    {
        return wallMap;
    }


    /**
     * CHECK THE BLOCK ALREADY OCCUPIED BY ANOTHER Wall
     */
    public boolean isWallAtGrid(int wallX,int wallY)
    {
        boolean isAtGrid = wallGrid[wallX][wallY];
        return isAtGrid;
    }


    /**
     * Damage the Wall when the robot run into the wall
     * @param fortressWall
     */
    public void damageTheWall(FortressWall fortressWall)
    {
        fortressWall.setWallHealth(50);

        String message = String.format(" The %s has been Damaged ### at ( %d, %d )",
                fortressWall.getWallName(),fortressWall.getWallX(),fortressWall.getWallY());

        logMessage.putMessage(message);

//        // This method is called in order to redraw the screen
//        swingArena.repaint();
    }


    /**
     * Destroy the Wall  upon the robot's second collision with it.
     * @param fortressWall
     */

    public void destroyTheWall(FortressWall fortressWall)
    {
        // Old spot are now Available to build a new Fortress wall
        wallGrid[fortressWall.getWallX()][fortressWall.getWallY()] = false;

        // remove the wall from wall map
        wallMap.remove(fortressWall.getWallName());

        String message = String.format(" The %s has been Destroyed !!! at ( %d, %d )",
                fortressWall.getWallName(),fortressWall.getWallX(),fortressWall.getWallY());

        logMessage.putMessage(message);

//        // This method is called in order to redraw the screen
//        swingArena.repaint();
    }


}
