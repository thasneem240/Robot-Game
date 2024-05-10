package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/***********************************************************************************************************************
 * The class responsible for generating new robots ensures that a fresh robot appears randomly                         *
 * in one of the four corners of the grid every 1500 milliseconds                                                      *
 *                                                                                                                     *
 ***********************************************************************************************************************/

public class RobotFormation implements Runnable
{
    private final LogMessage logMessage;
    private List<Robot> robotList;
    private final RobotArena robotArena;
    private BlockingQueue<Robot> robotBlockingQueue;
    private boolean runningStatus;
    private Object mutex;
    private int sequentialID;
    private int row;
    private int column;


    /* Constructor */
    public RobotFormation(LogMessage logMessage, RobotArena robotArena)
    {
        this.logMessage = logMessage;
        robotList = new LinkedList<>();
        this.robotArena = robotArena;
        robotBlockingQueue =  new ArrayBlockingQueue<>(100);
        mutex = new Object();
        runningStatus = true;
        sequentialID  = 1;

        SwingArena swingArena = robotArena.getSwingArena();

        row = swingArena.getGridHeight() - 1; // Default height is 9 so, row = 8
        column = swingArena.getGridWidth() - 1; // Default width is 9 so, column = 8

    }


    /* Get Next Robot From robot Blocking Queue*/
    public Robot retrieveSubsequentRobot() throws InterruptedException
    {
        Robot robot = robotBlockingQueue.take();

        return robot;
    }


    /* Stop the Running Status */
    public void stopThread()
    {
        runningStatus = false;
    }


    @Override
    public void run()
    {
        try
        {
            while (runningStatus)
            {
                if(!robotArena.isOccupied()) // Verify that the game is still ongoing.
                {
                    Robot robot = generateRobot();
                    refresh(robot);
                }

                // Generate a new robot every 1500 milliseconds.
                Thread.sleep(1500);
            }
        }
        catch (InterruptedException e)
        {
            runningStatus = false;
        }
    }

    /*
    After creating the robot, it is necessary to synchronize its information
    with other classes. This involves incrementing the sequentialID,
    adding the robot to a list, updating the RobotArena,
    and enqueueing it for further processing
     */
    private void refresh(Robot robot)
    {
        // lock the code

        synchronized (mutex)
        {
            if( robot != null)
            {
                String message = " The Robot has been Created $$$  ( Robot ID = " + robot.getRobotName() + " ) " ;
                logMessage.putMessage(message);

                // Increment the ID
                sequentialID = sequentialID + 1;

                // Add the newly created robot into the Linked list
                robotList.add(robot);

                // Add the newly created robot into the Blocking Queue
                robotBlockingQueue.add(robot);

                // Update and Repaint
                robotArena.updateRobotPosition(robot.getRobotX(),robot.getRobotY(), robot);

            }
        }
    }

    /*
    Generate a new robot, select a number from 1-4, and use a switch statement
    to determine a corner. Ensure that there are no other robots at
    that corner before creating a new one
    */
    private Robot generateRobot()
    {
        Robot newRobot = null;
        // Get a random number from 1 to 4
        int randomNo = getRandomNumber();

        // Get a random Image Icon for Robots
        ImageIcon robotImageIcon = getDifferentRobotIcon();

        switch (randomNo)
        {
            case 1:
                // Upper-left corner

                if( !robotArena.isRobotAtGrid(0,0) )
                {
                    // Create a new robot when the corner is free
                    newRobot = new Robot(0,0,sequentialID,robotImageIcon);
                }
                break;


            case 2:
                // Lower -left corner

                if( !robotArena.isRobotAtGrid(0,row) )
                {
                    // Create a new robot when the corner is free
                    newRobot = new Robot(0,row,sequentialID,robotImageIcon);
                }
                break;

            case 3:

                // Upper-Right corner

                if( !robotArena.isRobotAtGrid(column,0) )
                {
                    // Create a new robot when the corner is free
                    newRobot = new Robot(column,0,sequentialID,robotImageIcon);
                }
                break;

            case 4:
                // Lower -Right corner

                if( !robotArena.isRobotAtGrid(column,row) )
                {
                    // Create a new robot when the corner is free
                    newRobot = new Robot(column,row,sequentialID,robotImageIcon);
                }
                break;

            default:
                break;
        }

        return newRobot;
    }


    /* Add a robot back to the
     *  Robot Blocking queue
     */
    public void addRobot(Robot robot)
    {
        if ( robot.isAlive() ) // Verify the robot's state of being alive.
        {
            robotBlockingQueue.add(robot);
        }
    }

    private int getRandomNumber()
    {
        // Create a Random object
        Random random = new Random();

        // Generate a random number from 1 to 4
        int randomNumber = random.nextInt(4) + 1;

        return randomNumber;
    }


    private ImageIcon getDifferentRobotIcon()
    {
        String robotImageFile = null;

        String robot1ImageFile = "1554047213.png";
        String robot2ImageFile = "rg1024-robot-carrying-things-4.png";
        String robot3ImageFile = "droid2.png";

        // Create a Random object
        Random random = new Random();

        // Generate a random number between 1 and 3 (inclusive)
        int randomNumber = random.nextInt(3) + 1;

        switch (randomNumber)
        {
            case 1:
                robotImageFile = robot1ImageFile;
                break;

            case 2:
                robotImageFile = robot2ImageFile;
                break;

            case 3:
                robotImageFile = robot3ImageFile;
                break;

            default:
                break;

        }

        ImageIcon robotImageIcon = getImageIcon(robotImageFile);

        return robotImageIcon;

    }


    private ImageIcon getImageIcon(String imageFile)
    {
        URL url = getClass().getClassLoader().getResource(imageFile);
        if(url == null)
        {
            throw new AssertionError("Cannot find image file " + imageFile);
        }

        ImageIcon robotImageIcon = new ImageIcon(url);

        return robotImageIcon;
    }

    public List<Robot> getRobotList()
    {
        return robotList;
    }

    public BlockingQueue<Robot> getRobotBlockingQueue()
    {
        return robotBlockingQueue;
    }
}
