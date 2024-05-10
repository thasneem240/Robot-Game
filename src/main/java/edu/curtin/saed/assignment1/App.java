package edu.curtin.saed.assignment1;

import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;


@SuppressWarnings("PMD.ConfusingTernary") // To fulfill the logic, a complex ternary expression is necessary
public class App 
{
    private static Scanner scanner = new Scanner(System.in);
    private static int minWidth = 3;
    private static int minHeight = 3;

    private static int userInpWidth;
    private static int userInpHeight;


    public static void main(String[] args) 
    {

        showIntro();

        obtainUserInput();



        System.out.println("\n****************************************************************************************");
        System.out.println(String.format(" Game started With ( %d x %d ) Grid ",userInpWidth,userInpHeight) );
        System.out.println("****************************************************************************************");

        // Note: SwingUtilities.invokeLater() is equivalent to JavaFX's Platform.runLater().
        SwingUtilities.invokeLater(() ->
        {

            JFrame window = new JFrame(" GAME: ARMY OF KILLER ROBOTS  (Swing)  ");
            JTextArea loggerJTextArea = new JTextArea();

            JToolBar toolbar = new JToolBar();
            JButton startButton = new JButton("   Start   ");

            String strScore = String.format("%20s : %-15d","Score",0);
            JLabel scoreLabel = new JLabel(strScore);

            String strQueueLabel = String.format("%20s : %-15d","Queued Wall-Building Operations",0);
            JLabel queueLabel = new JLabel(strQueueLabel);

            toolbar.add(startButton);
            toolbar.add(scoreLabel);
            toolbar.add(queueLabel);


            AtomicBoolean isGameStarted = new AtomicBoolean(false);

            /* Initialize */
            LogMessage logMessage = new LogMessage(loggerJTextArea);

            /* Set User Input width and height */
            RobotArena robotArena = new RobotArena(logMessage,userInpWidth,userInpHeight);
            FortressWallArena fortressWallArena = robotArena.getFortressWallArena();


            RobotFormation robotFormation = new RobotFormation(logMessage,robotArena);
            Player player = new Player(robotArena,scoreLabel,fortressWallArena,queueLabel);
            RobotHandling robotHandling = new RobotHandling(logMessage,robotArena,
                    robotFormation,player);
            SwingArena swingArena = robotArena.getSwingArena();

            robotArena.setRobotList(robotFormation.getRobotList());
            robotArena.setRobotBlockingQueue(robotFormation.getRobotBlockingQueue());
            robotArena.setPlayer(player);


            int gridWidth = swingArena.getGridWidth(); // columns(X)
            int gridHeight = swingArena.getGridHeight(); // rows(Y)

            // to get the coordinates of the citadel(Fortress)
            int centerX = gridWidth / 2;  // column(X)
            int centerY = gridHeight / 2; // row(Y)

            final int[] wallID = {1};

            // Listen for Clicks and Build the walls
            swingArena.addListener((x, y) ->
            {

                // Check whether the game has started and if the Citadel is currently taken
                if(!robotArena.isOccupied() && isGameStarted.get())
                {
                    // Check if the user has clicked outside the citadel block
                    if ( !( x == centerX && y == centerY ) )
                    {
                        // Check whether the block is presently occupied by another wall.
                        if(!fortressWallArena.isWallAtGrid(x,y))
                        {

                            // Create Fortress Wall
                            FortressWall fortressWall = new FortressWall(x,y, wallID[0]);
                            wallID[0]++;

                            player.buildWall(fortressWall);

                        }
                        else
                        {
                            System.out.println("Occupied By Another Wall");
                        }

                    }
                    else
                    {
                        System.out.println("Clicked at Citadel");
                    }

                }

            });
            


             startButton.addActionListener((event) ->
             {

                 isGameStarted.set(true);

                 // will start  robotFormThread, logMessagesThread and playerThread
                 robotHandling.startThreads();

                 Thread robotHandlingThread = new Thread(robotHandling, "Robot Handling Thread");
                 robotHandlingThread.start();

                 if (startButton.getText().equals("   Start   "))
                 {
                     startButton.setText("   STOP   ");
                 }

             });

            

            JScrollPane loggerArea = new JScrollPane(loggerJTextArea);
            loggerArea.setBorder(BorderFactory.createEtchedBorder());
            
            JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, swingArena, loggerArea);
            loggerJTextArea.append("\n");

            Container contentPane = window.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(toolbar, BorderLayout.NORTH);
            contentPane.add(splitPane, BorderLayout.CENTER);
            
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setPreferredSize(new Dimension(1300, 950));
            window.pack();
            window.setVisible(true);
            
            splitPane.setDividerLocation(0.65);



        });
    }

    private static void showIntro()
    {
        System.out.println("\n ************************************************************************************ \n");

        System.out.println("                        DEFAULT_WIDTH = 9                 DEFAULT_HEIGHT = 9             " );
        System.out.println("                        MINIMUM_WIDTH = 3                 MINIMUM_HEIGHT = 3             " );

        System.out.println("\n ************************************************************************************ \n");
    }


    private static void obtainUserInput()
    {
        boolean gotWidth = false;
        boolean gotHeight = false;

        while ( !gotWidth || !gotHeight )
        {
            if(!gotWidth)
            {
                gotWidth = getGridWidthInput();
            }
            else
            {
                gotHeight = getGridHeightInput();
            }

        }

}

    private static boolean getGridWidthInput()
    {
        boolean gotWidth = false;

        System.out.println("\n Enter the grid width (Must be Greater than or Equal to " + minWidth + "): ");
        String input = scanner.nextLine();

        try
        {
            int w = Integer.parseInt(input);

            if( w < minWidth || w <= 0)
            {
                System.out.println("\n Grid width Must be Greater than or Equal to " + minWidth );
            }
            else
            {
                userInpWidth = w;
                System.out.println("\n Grid width = " + userInpWidth);
                gotWidth = true;
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("\n Invalid input !!!! ");
        }

        return gotWidth;
    }

    private static boolean getGridHeightInput()
    {
        boolean gotHeight = false;

        System.out.println("\n Enter the grid Height (Must be Greater than or Equal to " + minHeight + "): ");
        String input = scanner.nextLine();

        try
        {
            int h = Integer.parseInt(input);

            if( h < minHeight || h <= 0)
            {
                System.out.println("\n Grid Height Must be Greater than or Equal to " + minHeight );
            }
            else
            {
                userInpHeight = h;
                System.out.println("\n Grid Height = " + userInpHeight);
                gotHeight = true;
            }


        }
        catch (NumberFormatException e)
        {
            System.out.println("\n Invalid input !!!! ");
        }

        return gotHeight;

    }




}
