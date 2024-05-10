package edu.curtin.saed.assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// To fulfill the logic, a complex ternary expression is necessary
//@SuppressWarnings({"PMD.ConfusingTernary", "PMD.DoNotTerminateVM"})
@SuppressWarnings({"PMD.ConfusingTernary"})
public class RobotHandling implements Runnable
{
    private ExecutorService executorService; // For Thread Pool
    private boolean runningStatus;
    private boolean initiated;

    private final LogMessage logMessage;
    private final RobotArena robotArena;
    private final RobotFormation robotFormation;
    private final Player player;


    public RobotHandling(LogMessage logMessage, RobotArena robotArena,
                         RobotFormation robotFormation, Player player)
    {
        this.logMessage = logMessage;
        this.robotArena = robotArena;
        this.robotFormation = robotFormation;
        this.player = player;

        // Creates a Thread Pool
        executorService = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        runningStatus = true;
        initiated = false;
    }


    /*
    Start the Threads
     */
    public void startThreads()
    {
        if(!initiated)
        {
            // Create threads
            Thread robotFormThread = new Thread(robotFormation, " Robot Formation");
            Thread logMessagesThread = new Thread(logMessage, "Log Messages");
            Thread playerThread = new Thread(player, "Player");


            // start threads
            robotFormThread.start();
            logMessagesThread.start();
            playerThread.start();

            // change the status of initiated to true
            initiated = true;
        }
        else
        {
            stopThreads();
        }
    }


    /*
    Stop and exit all the threads smoothly
     */
    public void stopThreads()
    {
        try
        {
            runningStatus = false;

            robotFormation.stopThread();
            logMessage.stopThread();
            player.stopThread();



            executorService.shutdownNow();

            if( !executorService.awaitTermination(200, TimeUnit.MICROSECONDS) )
            {
                System.out.println(" Waiting for the threads to End gracefully.");
            }

        }
        catch (InterruptedException e)
        {
            runningStatus = false;
        }

    }




    @Override
    public void run()
    {
        try
        {
            while (runningStatus)
            {

                if ( !robotArena.isOccupied() )  //  Verify that the game is still in progress
                {
                    // Create RobotMove Object
                    RobotMove robotMove = new RobotMove(robotFormation,
                            robotFormation.retrieveSubsequentRobot(),robotArena);


                   // Run it using the ExecutorService
                    executorService.execute(robotMove);

                    /*
                    The robot will take 400 Milli seconds to complete its move
                    additional 100 Milli seconds for other checking
                     */
                    Thread.sleep(500);
                }
                else
                {
                    String gameOverMessage = " \n\n  -----------------   Game over!!! Give it another shot   ----------------- \n\n";
                    logMessage.putMessage(gameOverMessage);

                    String finalScore = "\n ++++++++++++++++   Your Final Score: " + player.getCurrScore() + " ++++++++++++++++ ";
                    logMessage.putMessage(finalScore);

                    runningStatus = false;
                }
            }
        }
        catch (InterruptedException e)
        {

            runningStatus = false;
        }
    }
}
