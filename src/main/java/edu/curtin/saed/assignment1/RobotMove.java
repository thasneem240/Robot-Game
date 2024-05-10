package edu.curtin.saed.assignment1;

import java.util.Random;

@SuppressWarnings("PMD.ConfusingTernary") // To fulfill the logic, a complex ternary expression is necessary
public class RobotMove implements Runnable
{

    private final RobotFormation robotFormation;
    private final Robot robot;
    private final RobotArena robotArena;

    /* Coordinates of Citadel Post */
    private int citadelX;
    private int citadelY;

    public RobotMove(RobotFormation robotFormation, Robot robot, RobotArena robotArena)
    {
        this.robotFormation = robotFormation;
        this.robot = robot;
        this.robotArena = robotArena;

        SwingArena swingArena = robotArena.getSwingArena();

        int gridWidth = swingArena.getGridWidth();
        int gridHeight = swingArena.getGridHeight();

        // to get the coordinates of the single square at the center

        citadelX = gridWidth / 2;
        citadelY = gridHeight / 2;
    }

    /*
    These directional methods facilitate robot movement. Within these methods,
    checks are performed to determine if the move is feasible.
    If so, the coordinates are updated, and the scene is synchronized.
    These methods apply to: moveUp, moveDown, moveRight, and moveLeft
     */

    public boolean moveUp()
    {
        boolean isMoveUp = false;

        double robotX = robot.getRobotX();
        double robotY = robot.getRobotY();

        double newRobotY = robotY - 1; // Go Up

        /*
         Verify if the robot is inside the grid and there
         are no other robots present at that location.
         */

        if( robotArena.insideGrid(robotX,newRobotY) )
        {
            if( !robotArena.isRobotAtGrid(robotX,newRobotY) )
            {
                isMoveUp = true; // Successfully moved Up

                robot.setRobotY(newRobotY);
                robot.setMovDirection("UP");

                // Update the position and repaint the grid
                robotArena.updateRobotPosition(robotX,robotY,robot);
            }
        }


        return isMoveUp;
    }


    public boolean moveDown()
    {
        boolean isMoveDown = false;

        double robotX = robot.getRobotX();
        double robotY = robot.getRobotY();

        double newRobotY = robotY + 1; // Go Down

        /*
         Verify if the robot is inside the grid and there
         are no other robots present at that location.
         */

        if(robotArena.insideGrid(robotX,newRobotY))
        {
            if(!robotArena.isRobotAtGrid(robotX,newRobotY))
            {
                isMoveDown = true; // Successfully moved Down

                robot.setRobotY(newRobotY);
                robot.setMovDirection("DOWN");

                // Update the position and repaint the grid
                robotArena.updateRobotPosition(robotX,robotY,robot);
            }
        }

        return isMoveDown;
    }



    public boolean moveRight()
    {
        boolean isMoveRight = false;

        double robotX = robot.getRobotX();
        double robotY = robot.getRobotY();

        double newRobotX = robotX + 1; // Go Right

        /*
         Verify if the robot is inside the grid and there
         are no other robots present at that location.
         */

        if( robotArena.insideGrid(newRobotX,robotY) )
        {
            if( !robotArena.isRobotAtGrid(newRobotX,robotY) )
            {
                isMoveRight = true; // Successfully moved Right

                robot.setRobotX(newRobotX);
                robot.setMovDirection("RIGHT");

                // Update the position and repaint the grid
                robotArena.updateRobotPosition(robotX,robotY,robot);
            }
        }

        return isMoveRight;
    }


    public boolean moveLeft()
    {
        boolean isMoveLeft = false;

        double robotX = robot.getRobotX();
        double robotY = robot.getRobotY();

        double newRobotX = robotX - 1; // Go Right

        /*
         Verify if the robot is inside the grid and there
         are no other robots present at that location.
         */

        if( robotArena.insideGrid(newRobotX,robotY) )
        {
            if (!robotArena.isRobotAtGrid(newRobotX, robotY))
            {
                isMoveLeft = true; // Successfully moved Left

                robot.setRobotX(newRobotX);
                robot.setMovDirection("LEFT");

                // Update the position and repaint the grid
                robotArena.updateRobotPosition(robotX, robotY, robot);
            }
        }

        return isMoveLeft;
    }


    private int getRandomNumber()
    {
        // Create a Random object
        Random random = new Random();

        // Generate a random number from 1 to 4
        int randomNumber = random.nextInt(4) + 1;

        return randomNumber;
    }



    /*
    Obtains the next move by selecting a number from 1 to 4.
    Then, using a switch statement, determines a direction
    while ensuring there are no other robots in that location.
    If occupied, it attempts an alternative direction.
     */

    public void randomMove()
    {
        // Generate a random number from 1 to 4
        int randomNo = getRandomNumber();

        // move towards the citadel either horizontally or vertically
        boolean moveDone = predictMove();


        // move randomly if the predicted move fails ( When Block already occupied by other Robots)

        if( !moveDone )
        {

            switch(randomNo)
            {
                case 1:
                    moveDone = moveUp();

                    if( !moveDone )
                    {
                        moveDone = moveLeft();
                    }

                    if( !moveDone )
                    {
                        moveDone = moveRight();
                    }

                    if( !moveDone )
                    {
                        moveDown();
                    }

                    break;

                case 2:
                    moveDone = moveDown();

                    if( !moveDone )
                    {
                        moveDone = moveLeft();
                    }

                    if( !moveDone )
                    {
                        moveDone = moveRight();
                    }

                    if( !moveDone )
                    {
                        moveUp();
                    }

                    break;

                case 3:
                    moveDone = moveRight();

                    if( !moveDone )
                    {
                        moveDone = moveDown();
                    }

                    if( !moveDone )
                    {
                        moveDone = moveLeft();
                    }

                    if( !moveDone )
                    {
                        moveUp();
                    }

                    break;


                case 4:
                    moveDone = moveLeft();

                    if( !moveDone )
                    {
                        moveDone = moveUp();
                    }

                    if( !moveDone )
                    {
                        moveDone = moveDown();
                    }

                    if( !moveDone )
                    {
                        moveRight();
                    }

                    break;

                default:
                    break;
            }
        }

    }


    @Override
    public void run()
    {
        try
        {

            if (robot.isAlive()) // Confirm the robot's life status
            {
                if (!robotArena.isOccupied()) // Ensure the game is ongoing
                {
                    // Delay 500–2000 milliseconds before move
                    Thread.sleep(robot.getDelay());

                    randomMove(); // move randomly

                    robotFormation.addRobot(robot);
                }
            }
            else
            {
                // remove the robot
                robotArena.eliminateRobot(robot);
                System.out.println("I am AT RobotMove Eliminate Robot");
            }

        } catch (InterruptedException e)
        {
            System.out.println(e);
        }
    }

    /*
    move towards the citadel either horizontally or vertically
     */
    public boolean predictMove()
    {
        boolean moveDone = false;

        double robotX = robot.getRobotX();
        double robotY = robot.getRobotY();


        if( ( ( citadelX > robotX || citadelX < robotX )   &&  citadelY > robotY ) )
        {
            moveDone = moveDown();
        }
        else
        {
            if( ( ( citadelX > robotX || citadelX < robotX )   &&  citadelY < robotY ) )
            {
                moveDone = moveUp();
            }
            else
            {
                if( citadelX > robotX  &&  citadelY == robotY )
                {
                    moveDone = moveRight();
                }
                else
                {
                    if( citadelX < robotX  &&  citadelY == robotY )
                    {
                        moveDone = moveLeft();
                    }
                    else
                    {
                        if( citadelX  == robotX  &&  citadelY > robotY )
                        {
                            moveDone = moveDown();
                        }
                        else
                        {
                            if( citadelX  == robotX  &&  citadelY < robotY )
                            {
                                moveDone = moveUp();
                            }
                        }
                    }
                }
            }
        }

        return moveDone;
    }
}
