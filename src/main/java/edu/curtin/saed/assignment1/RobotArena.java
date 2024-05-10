package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/***********************************************************************************************************************
 * RobotArena class responsible for Update and Remove Robots from the Grid                                             *
 * and also put the relevant log messages                                                                              *
 *                                                                                                                     *
 ***********************************************************************************************************************/
@SuppressWarnings("PMD.ConfusingTernary") // To fulfill the logic, a complex ternary expression is necessary
public class RobotArena
{
    private Map<String, Robot> robotsMap;

    private final SwingArena swingArena;
    private final FortressWallArena fortressWallArena;
    private boolean[][] grid;
    private int gridWidth;
    private int gridHeight;
    private List<Robot> robotList = null;
    private BlockingQueue<Robot> robotBlockingQueue = null;
    private Player player = null;

    private boolean occupied;
    private final LogMessage messageLogger;
    private Object mutex = new Object();

    private static final int ANIMATION_INTERVAL = 40; // Milliseconds per animation frame



    public RobotArena(LogMessage messageLogger, int gridWidth, int gridHeight)
    {
        this.messageLogger = messageLogger;

        occupied = false;
        robotsMap =  new ConcurrentHashMap<>();

        swingArena =  new SwingArena(this,gridWidth,gridHeight);

        this.gridWidth = gridWidth; // columns
        this.gridHeight = gridHeight; // rows

        grid = new boolean[gridWidth][gridHeight];

        fortressWallArena =  new FortressWallArena(this.messageLogger,this);

    }



    /**
     * First, update the robot's position within both the RobotArena's hashmap and the grid,
     * and then initiate a UI update in the SwingArena.
     */
    public void updateRobotPosition(double oldRobotX, double oldRobotY, Robot robot)
    {
        robot.setOldX(oldRobotX);
        robot.setOldY(oldRobotY);

        robot.setCurrX(oldRobotX);
        robot.setCurrY(oldRobotY);

        // to get the coordinates of the citadel(Fortress)
        int centerX = gridWidth / 2;  // Column
        int centerY = gridHeight / 2; // row


        robotsMap.put(robot.getRobotName(), robot);


        // new robot that generated in the corners
         if( oldRobotX == robot.getRobotX() && oldRobotY == robot.getRobotY() )
        {
            if(!occupied)
            {
                // The old spots become available once the robot completes its move
                grid[(int) robot.getOldX()][(int) robot.getOldY()] = false;

                // Occupied the new spot
                grid[(int)robot.getRobotX()][(int)robot.getRobotY()] = true;

                // This method is called in order to redraw the screen
                swingArena.repaint();
            }
            else
            {
                swingArena.repaint();
            }

         }
         else // for moving robots
         {
             if(!occupied)
             {
                 updateMovingRobotPosition(robot);
             }
             else
             {
                 swingArena.repaint();
             }
         }

         checkFortressWallHit(robot);

        // Check if the robot has reached the citadel; if it has, declare the game over
        if (robot.getCurrX() == centerX && robot.getCurrY() == centerY)
        {
            this.occupied = true;

            ImageIcon emptyIcon = new ImageIcon();
            swingArena.setCitadel(emptyIcon);
            swingArena.setLabel("");

        }

    }

    private void updateMovingRobotPosition(Robot robot)
    {

        // Occupied the new spot
         grid[(int) robot.getRobotX()][(int) robot.getRobotY()] = true;

         try
         {

             if (robot.getMovDirection().equals("UP"))
             {

                 while (robot.getRobotY() != robot.getCurrY())
                 {
                     double newY = getRoundValue(robot.getCurrY() - 0.1);

                     robot.setCurrY(newY);

                     doAnimation();
                 }

                 // The old spots become available once the robot completes its move
                 grid[(int) robot.getOldX()][(int) robot.getOldY()] = false;



             }
             else
             {
                 if (robot.getMovDirection().equals("DOWN"))
                 {
                     while (robot.getRobotY() != robot.getCurrY())
                     {
                         double newY = getRoundValue(robot.getCurrY() + 0.1);

                         robot.setCurrY(newY);

                         doAnimation();
                     }

                     // The old spots become available once the robot completes its move
                     grid[(int) robot.getOldX()][(int) robot.getOldY()] = false;

                 }
                 else
                 {
                     if (robot.getMovDirection().equals("RIGHT"))
                     {
                         while (robot.getRobotX() != robot.getCurrX())
                         {
                             double newX = getRoundValue(robot.getCurrX() + 0.1);

                             robot.setCurrX(newX);

                             doAnimation();
                         }

                         // The old spots become available once the robot completes its move
                         grid[(int) robot.getOldX()][(int) robot.getOldY()] = false;

                     }
                     else
                     {
                         if (robot.getMovDirection().equals("LEFT"))
                         {
                             while (robot.getRobotX() != robot.getCurrX())
                             {
                                 double newX = getRoundValue(robot.getCurrX() - 0.1);

                                 robot.setCurrX(newX);

                                 doAnimation();
                             }

                             // The old spots become available once the robot completes its move
                             grid[(int) robot.getOldX()][(int) robot.getOldY()] = false;
                         }
                         else
                         {
                             System.out.println("SOMETHING WRONG");
                         }
                     }
                 }
             }
         }
         catch (InterruptedException e)
         {
             System.out.println(e);
         }




    }



    /*
    Perform the move from the starting point to the endpoint, and
    animate it at intervals of 40 milliseconds.
     */

    private void doAnimation() throws InterruptedException
    {
        // redraw the screen
        swingArena.repaint();

        Thread.sleep(ANIMATION_INTERVAL);
    }




    /**
     * Eliminate the robot from the RobotArena and synchronize the update with the SwingArena
     */
    public void eliminateRobot(Robot robot)
    {

        grid[(int)robot.getRobotX()][(int)robot.getRobotY()] = false; // Old spot are now Available
        robotsMap.remove(robot.getRobotName());

        String logMessage = " The Robot has been destroyed!!!  ( Robot ID = " + robot.getRobotName() + " ) " ;

        messageLogger.putMessage(logMessage);

//        // This method is called in order to redraw the screen
//        swingArena.repaint();
    }

    public Map<String, Robot> getRobotsMap()
    {
        return robotsMap;
    }

    public SwingArena getSwingArena()
    {
        return swingArena;
    }

    public FortressWallArena getFortressWallArena()
    {
        return fortressWallArena;
    }

    public boolean isOccupied()
    {
        return occupied;
    }


    /**
     * CHECK THE BLOCK ALREADY OCCUPIED BY ANOTHER ROBOT
     */
    public boolean isRobotAtGrid(double robotX, double robotY)
    {
        boolean isAtGrid = grid[(int)robotX][(int)robotY];
        return isAtGrid;
    }


    /**
     * Ensure that the robot remains inside the grid
     */
    public boolean insideGrid(double column, double row)
    {
        boolean isInside = true;

        // column: RobotX,  row: RobotY

        if( ( column >= gridWidth )  ||  ( row >= gridHeight )  )
        {
            isInside = false;
        }

        if( ( column < 0  || ( row < 0 ) ) )
        {
            isInside = false;
        }

        return isInside;
    }

    private double getRoundValue(double value)
    {
        double roundedValue = Math.round(value * 10.0) / 10.0;

        return roundedValue;
    }


    /**
     * Check if any robot is currently located at the
     * position of the fortress wall
     */

    public void checkFortressWallHit(Robot robot)
    {
        synchronized (mutex)
        {
            Map<String, FortressWall> wallMap = fortressWallArena.getWallMap();

            // Check wall map is empty or not
            if (wallMap.size() != 0)
            {
                Iterator<Map.Entry<String, FortressWall>> iterator = wallMap.entrySet().iterator();

                while (iterator.hasNext())
                {
                    Map.Entry<String, FortressWall> entry = iterator.next();

                    FortressWall fortressWall = entry.getValue();

                    int wallX = fortressWall.getWallX();
                    int wallY = fortressWall.getWallY();

                    if (robot.getCurrX() == wallX && robot.getCurrY() == wallY)
                    {
                        // Then 1. Robot will be Destroyed

                        robot.setRobotHealthStatus(0.0);
                        robotBlockingQueue.remove(robot);
                        robotList.remove(robot);
                        eliminateRobot(robot);

                        player.modifyScore(100);

                        messageLogger.putMessage(" *** Score Increased by 100 *** ");

                        // Then 2. The wall may weaken or be destroyed

                        if (fortressWall.getWallHealth() == 100) // Strong or New Wall
                        {
                            fortressWallArena.damageTheWall(fortressWall); // Damage the Wall

                        } else // Damaged Wall
                        {
                            fortressWallArena.destroyTheWall(fortressWall); // Destroy the wall
                        }

                        // This method is called in order to redraw the screen
                        swingArena.repaint();
                    }

                }
            }
        }
    }


    public void setRobotList(List<Robot> robotList)
    {
        this.robotList = robotList;
    }

    public void setRobotBlockingQueue(BlockingQueue<Robot> robotBlockingQueue)
    {
        this.robotBlockingQueue = robotBlockingQueue;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
