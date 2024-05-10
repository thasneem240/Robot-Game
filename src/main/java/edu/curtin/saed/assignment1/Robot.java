package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.util.Random;

/***********************************************************************************************************************
 * The Robot class is responsible for Robots and their characteristics                                                 *
 *                                                                                                                     *
 ***********************************************************************************************************************/

public class Robot
{

    /*  Variable Declarations */

    private int robotId;
    private String robotName;
    private boolean isAlive;
    private ImageIcon imageIcon;

    /* Robot Coordinates */
    private double robotX;
    private double robotY;


    /* Robot Current Coordinates */

    private double currX;
    private double currY;


    /* Robot Old Coordinates */
    private double oldX;
    private double oldY;

    /* Robot Direction */
    private String movDirection = null;
    private int delay = getRandomDelay();;

    public Robot(int robotX, int robotY, int robotId, ImageIcon imageIcon)
    {
        this.robotId = robotId;
        this.robotName = "Robot " + robotId;
        this.isAlive = true;
        this.robotX = robotX;
        this.robotY = robotY;
        this.imageIcon = imageIcon;

    }

    /* Generate a random delay value between 500 and 2000 milliseconds */
    public int getRandomDelay()
    {

        // Create a Random object
        Random random = new Random();

        int minDelay = 500; // Minimum delay in milliseconds
        int maxDelay = 2000; // Maximum delay in milliseconds
        int randomDelay = random.nextInt(maxDelay - minDelay + 1) + minDelay;

        return randomDelay;
    }

    public int getRobotId()
    {
        return robotId;
    }


    public String getRobotName()
    {
        return robotName;
    }


    public int getDelay()
    {
        return delay;
    }


    public boolean isAlive()
    {
        return isAlive;
    }

    public void setRobotHealthStatus(double healthValue)
    {
        if(healthValue <= 0)
        {
            isAlive = false;
        }

    }

    public double getRobotX()
    {
        return robotX;
    }

    public void setRobotX(double robotX)
    {
        this.robotX = robotX;
    }

    public double getRobotY()
    {
        return robotY;
    }

    public void setRobotY(double robotY)
    {
        this.robotY = robotY;
    }

    public double getCurrX()
    {
        return currX;
    }

    public double getCurrY()
    {
        return currY;
    }

    public void setCurrX(double currX)
    {
        this.currX = currX;
    }

    public void setCurrY(double currY)
    {
        this.currY = currY;
    }

    public void setOldX(double oldX)
    {
        this.oldX = oldX;
    }

    public void setOldY(double oldY)
    {
        this.oldY = oldY;
    }

    public double getOldX()
    {
        return oldX;
    }

    public double getOldY()
    {
        return oldY;
    }

    public ImageIcon getImageIcon()
    {
        return imageIcon;
    }

    public String getMovDirection()
    {
        return movDirection;
    }

    public void setMovDirection(String movDirection)
    {
        this.movDirection = movDirection;
    }
}
