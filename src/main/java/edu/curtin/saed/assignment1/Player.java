package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
@SuppressWarnings("PMD.ConfusingTernary") // To fulfill the logic, a complex ternary expression is necessary
public class Player implements Runnable
{
    private Object mutex1;
    private Object mutex2;
    private boolean runningStatus;
    private BlockingQueue<FortressWall> fortressWallQueue;
    private int currScore;
    private int queueCount;
    private Thread newThread1 = null;
    private Thread newThread2 = null;



    private final RobotArena robotArena;
    private JLabel scoreLabel;
    private JLabel queueLabel;
    private final FortressWallArena fortressWallArena;


    public Player(RobotArena robotArena, JLabel scoreLabel,
                  FortressWallArena fortressWallArena, JLabel queueLabel)
    {
        this.robotArena = robotArena;
        this.scoreLabel = scoreLabel;
        this.queueLabel = queueLabel;
        this.fortressWallArena = fortressWallArena;

        mutex1 = new Object();
        mutex2 = new Object();

        runningStatus = true;
        fortressWallQueue = new ArrayBlockingQueue<>(10);

        currScore = 0;
        queueCount = 0;
    }

    /*
    Construct a fortress wall on an empty square
    * */
    public void buildWall(FortressWall fortressWall)
    {
        try
        {
            fortressWallQueue.put(fortressWall);

            // Increase the QueueUP Count
            int newCount = getQueueCount() + 1;
            setQueueCount(newCount);
        }
        catch (InterruptedException e)
        {
            runningStatus = false;
        }
    }


    /*
    Update the Score
    */
    public void modifyScore(int newScore)
    {
        // Critical section protected by mutex1
        synchronized (mutex1)
        {
            SwingUtilities.invokeLater(() ->
            {
                int totalScore = newScore + currScore;
                String strScore = String.format("%20s : %-15d","Score",totalScore);

                scoreLabel.setText(strScore);

                currScore = totalScore;
            });
        }
    }


   /*
    Update the count of queued wall-building commands
    */
    private void modifyNumberOfQueue()
    {

            SwingUtilities.invokeLater(() ->
            {
                String strQueueLabel = String.format("%20s : %-15d","Queued Wall-Building Operations",getQueueCount());

                queueLabel.setText(strQueueLabel);

            });
    }




    /* Stop the Running Status */
    public void stopThread()
    {
        runningStatus = false;

        if(newThread1 != null)
        {
            newThread1.interrupt();
        }

        if(newThread2 != null)
        {
            newThread2.interrupt();
        }

    }

    @Override
    public void run()
    {
        Runnable runnable1 = generatePlayerScore();

        // Thread to manage score
        newThread1 = new Thread(runnable1, "Score of Player");
        newThread1.start();

        Runnable runnable2 = updateNumberOfQueuedWallBuilding();

        // Thread to manage the Count of Queued Wall
        newThread2 = new Thread(runnable2, "Update Number Of Queued Wall");
        newThread2.start();


        try
        {
            while(runningStatus)
            {
                if ( !robotArena.isOccupied() )
                {
                    // Get the Wall from Blocking Queue

                    FortressWall fortressWall = fortressWallQueue.take();

                    // Decrease the QueueUP Count
                    int newCount = getQueueCount() - 1;
                    setQueueCount(newCount);

                    fortressWallArena.updateWallPosition(fortressWall);

                }
                else
                {
                    runningStatus = false;
                }

                // Build only one fortress wall every 2000 milliseconds
                Thread.sleep(2000);

            }
        }
        catch (InterruptedException e)
        {
            runningStatus = false;
        }
    }


    private Runnable generatePlayerScore()
    {
        return () ->
        {
            try
            {
                while (runningStatus)
                {
                    // Check whether game is still ongoing
                    if( !robotArena.isOccupied() )
                    {
                        /*
                        For each second that passes(until the game ends),
                        the score increases by 10 points.
                         */
                        modifyScore(10);
                        Thread.sleep(1000); // 1 second
                    }
                    else
                    {
                        runningStatus = false;
                    }
                }

            }
            catch (InterruptedException e)
            {
                //System.out.println(e);
                runningStatus = false;
            }
        };
    }

    /*
    Update the present count of queued wall-building commands
     */
    private Runnable updateNumberOfQueuedWallBuilding()
    {
        return () ->
        {
            try
            {
                while (runningStatus)
                {
                    // Check whether game is still ongoing
                    if( !robotArena.isOccupied() )
                    {
                        modifyNumberOfQueue();

                        Thread.sleep(10);
                    }
                    else
                    {
                        runningStatus = false;
                    }
                }
            }
            catch (InterruptedException e)
            {
                //System.out.println(e);
                runningStatus = false;
            }
        };
    }

    public int getQueueCount()
    {
        // Critical section protected by mutex2
        synchronized (mutex2)
        {
            return queueCount;
        }

    }

    public void setQueueCount(int queueCount)
    {
        // Critical section protected by mutex2
        synchronized (mutex2)
        {
            this.queueCount = queueCount;
        }
    }

    public int getCurrScore()
    {
        synchronized (mutex1)
        {
            return currScore;
        }
    }
}
