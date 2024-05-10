package edu.curtin.saed.assignment1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List; // So that 'List' means java.util.List and not java.awt.List.
import java.net.URL ;

/**
 * A Swing GUI element that displays a grid on which you can draw images, text and lines.
 */
public class SwingArena extends JPanel
{
    // Represents the image to draw. You can modify this to introduce multiple images.
    private static final String ROBOT1_IMAGE_FILE = "1554047213.png";
    private static final String FORTRESS_IMAGE_FILE = "rg1024-isometric-tower.png";
    private ImageIcon robot1;
    private ImageIcon citadel; // Fortress icon, always stays at the center of the grid
    private String label = "CITADEL";

    // The following values are arbitrary, and you may need to modify them according to the 
    // requirements of your application.

    private int gridWidth ;  // column
    private int gridHeight; // rows


    private double gridSquareSize; // Auto-calculated
    
    private List<ArenaListener> listeners = null;

    private RobotArena robotArena;



    /**
     * Creates a new arena object, loading the robot image.
     */
    public SwingArena(RobotArena robotArena, int gridWidth, int gridHeight)
    {
        // Here's how (in Swing) you get an Image object from an image file that's part of the 
        // project's "resources". If you need multiple different images, you can modify this code
        // accordingly.
        
        // (NOTE: _DO NOT_ use ordinary file-reading operations here, and in particular do not try
        // to specify the file's path/location. That will ruin things if you try to create a 
        // distributable version of your code with './gradlew build'. The approach below is how a 
        // project is supposed to read its own internal resources, and should work both for 
        // './gradlew run' and './gradlew build'.)
        
        URL url = getClass().getClassLoader().getResource(ROBOT1_IMAGE_FILE);
        if(url == null)
        {
            throw new AssertionError("Cannot find image file " + ROBOT1_IMAGE_FILE);
        }
        robot1 = new ImageIcon(url);



        /* GET FORTRESS IMAGE FILE */

        URL url2 = getClass().getClassLoader().getResource(FORTRESS_IMAGE_FILE);
        if(url2 == null)
        {
            throw new AssertionError("Cannot find image file " + FORTRESS_IMAGE_FILE);
        }
        citadel = new ImageIcon(url2);


        this.robotArena = robotArena;

        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;


    }
    

    /**
     * Adds a callback for when the user clicks on a grid square within the arena. The callback 
     * (of type ArenaListener) receives the grid (x,y) coordinates as parameters to the 
     * 'squareClicked()' method.
     */
    public void addListener(ArenaListener newListener)
    {
        if(listeners == null)
        {
            listeners = new LinkedList<>();
            addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent event)
                {
                    int gridX = (int)((double)event.getX() / gridSquareSize);
                    int gridY = (int)((double)event.getY() / gridSquareSize);
                    
                    if(gridX < gridWidth && gridY < gridHeight)
                    {
                        for(ArenaListener listener : listeners)
                        {   
                            listener.squareClicked(gridX, gridY);
                        }
                    }
                }
            });
        }
        listeners.add(newListener);
    }
    
    
    
    /**
     * This method is called in order to redraw the screen, either because the user is manipulating 
     * the window, OR because you've called 'repaint()'.
     *
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);
        Graphics2D gfx = (Graphics2D) g;
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
                (double) getWidth() / (double) gridWidth,
                (double) getHeight() / (double) gridHeight);

        int arenaPixelWidth = (int) ((double) gridWidth * gridSquareSize);
        int arenaPixelHeight = (int) ((double) gridHeight * gridSquareSize);


        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setColor(Color.GRAY);
        gfx.drawRect(0, 0, arenaPixelWidth - 1, arenaPixelHeight - 1); // Outer edge

        for (int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            int x = (int) ((double) gridX * gridSquareSize);
            gfx.drawLine(x, 0, x, arenaPixelHeight);
        }

        for (int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            int y = (int) ((double) gridY * gridSquareSize);
            gfx.drawLine(0, y, arenaPixelWidth, y);
        }


        // Invoke helper methods to draw things at the current location.
        // ** You will need to adapt this to the requirements of your application. **


        // to get the coordinates of the single square at the center
        int centerX = gridWidth / 2;
        int centerY = gridHeight / 2;

        // Draw the Fortress at the center of the grid
        drawImage(gfx, citadel, centerX, centerY);
        drawLabel(gfx, label, centerX, centerY);

        FortressWallArena fortressWallArena = robotArena.getFortressWallArena();


        /* Draw the Robots from Hash Map */

        Map<String, Robot> robotsMap = robotArena.getRobotsMap();

        Iterator<Map.Entry<String, Robot>> iteratorRobot = robotsMap.entrySet().iterator();

        while (iteratorRobot.hasNext())
        {
            Map.Entry<String, Robot> entry = iteratorRobot.next();

            Robot robot = entry.getValue();

            drawImage(gfx, robot.getImageIcon(), robot.getCurrX(), robot.getCurrY());
            drawLabel(gfx, robot.getRobotName(), robot.getCurrX(), robot.getCurrY());

        }


        /* Draw the Walls from Hash Map */

        Map<String, FortressWall> wallMap = fortressWallArena.getWallMap();

        Iterator<Map.Entry<String, FortressWall>> iteratorWall = wallMap.entrySet().iterator();


        while (iteratorWall.hasNext())
        {
            Map.Entry<String, FortressWall> entry = iteratorWall.next();

            FortressWall fortressWall = entry.getValue();

            drawImage(gfx, fortressWall.getWallImageIcon(), fortressWall.getWallX(), fortressWall.getWallY());
        }

    }
    
    
    /** 
     * Draw an image in a specific grid location. *Only* call this from within paintComponent(). 
     *
     * Note that the grid location can be fractional, so that (for instance), you can draw an image 
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     *     
     * You shouldn't need to modify this method.
     */
    private void drawImage(Graphics2D gfx, ImageIcon icon, double gridX, double gridY)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn. 
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;
        
        // We also need to know how "big" to make the image. The image file has a natural width 
        // and height, but that's not necessarily the size we want to draw it on the screen. We 
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = (double) robot1.getIconWidth();
        double fullSizePixelHeight = (double) robot1.getIconHeight();
        
        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Here, the image is wider than it is high, so we'll display it such that it's as 
            // wide as a full grid cell, and the height will be set to preserve the aspect 
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Otherwise, it's the other way around -- full height, and width is set to 
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(icon.getImage(), 
            (int) (x - displayedPixelWidth / 2.0),  // Top-left pixel coordinates.
            (int) (y - displayedPixelHeight / 2.0), 
            (int) displayedPixelWidth,              // Size of displayed image.
            (int) displayedPixelHeight, 
            null);
    }
    
    
    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within 
     * paintComponent(). 
     *
     * You shouldn't need to modify this method.
     */
    private void drawLabel(Graphics2D gfx, String label, double gridX, double gridY)
    {
        gfx.setColor(Color.BLUE);
        FontMetrics fm = gfx.getFontMetrics();
        gfx.drawString(label, 
            (int) ((gridX + 0.5) * gridSquareSize - (double) fm.stringWidth(label) / 2.0), 
            (int) ((gridY + 1.0) * gridSquareSize) + fm.getHeight());
    }


    /**
     * Draws a (slightly clipped) line between two grid coordinates. 
     *
     * You shouldn't need to modify this method.
     */

//    private void drawLine(Graphics2D gfx, double gridX1, double gridY1,
//                                          double gridX2, double gridY2)
//    {
//        gfx.setColor(Color.RED);
//
//        // Recalculate the starting coordinate to be one unit closer to the destination, so that it
//        // doesn't overlap with any image appearing in the starting grid cell.
//        final double radius = 0.5;
//        double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
//        double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
//        double clippedGridY1 = gridY1 + Math.sin(angle) * radius;
//
//        gfx.drawLine((int) ((clippedGridX1 + 0.5) * gridSquareSize),
//                     (int) ((clippedGridY1 + 0.5) * gridSquareSize),
//                     (int) ((gridX2 + 0.5) * gridSquareSize),
//                     (int) ((gridY2 + 0.5) * gridSquareSize));
//    }


    /* Setters and Getters for Grids */

    public int getGridWidth()
    {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth)
    {
        this.gridWidth = gridWidth;
    }

    public int getGridHeight()
    {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight)
    {
        this.gridHeight = gridHeight;
    }

    public void setCitadel(ImageIcon citadel)
    {
        this.citadel = citadel;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
