package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/***********************************************************************************************************************
 * The logMessage class is in charge of writing output to the log. It does this in its own thread, but                 *
 * assumes that other threads will call the putMessage() in order to provide messages to log.                          *
 *                                                                                                                     *
 ***********************************************************************************************************************/
public class LogMessage implements Runnable
{

    private BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(100);
    private JTextArea logTextArea;

    private boolean runningStatus = true;


    public LogMessage(JTextArea logTextArea)
    {
        this.logTextArea = logTextArea;
    }


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
                // Get the next log message from blocking queue if message exist otherwise block
                String nextLogMessage = blockingQueue.take();

                // Will Run later on GUI Thread
                SwingUtilities.invokeLater(() ->
                {
                    logTextArea.append(" " + nextLogMessage + " \n");

                });
            }
        } catch (InterruptedException e)
        {
            runningStatus = false;
        }

    }

    /* Put New log Message into blocking Queue */
    public void putMessage(String logMessage)
    {
        try
        {
            blockingQueue.put(logMessage);

        } catch (InterruptedException exception)
        {
           runningStatus = false;
        }
    }

}
