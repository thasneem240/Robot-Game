package edu.curtin.saed.assignment1;

import javax.swing.*;
import java.net.URL;

/***********************************************************************************************************************
 * FortressWall class responsible for store the cordinates and health of the wall                                      *
 *                                                                                                                     *
 ***********************************************************************************************************************/

public class FortressWall
{
    private static final String WALL_IMAGE_FILE = "181478.png";
    private static final String DAMAGED_WALL_IMAGE_FILE = "181479.png";

    private String wallName;
    private int wallX;
    private int wallY;

    private int wallHealth;
    private ImageIcon wallImageIcon;

    public FortressWall(int wallX, int wallY, int wallId)
    {
        this.wallX = wallX;
        this.wallY = wallY;

        wallHealth = 100;
        wallImageIcon = getImageIcon(WALL_IMAGE_FILE);
        wallName = "Wall_No " + wallId;

    }

    public int getWallHealth()
    {
        return wallHealth;
    }

    public int getWallX()
    {
        return wallX;
    }

    public int getWallY()
    {
        return wallY;
    }

    public String getWallName()
    {
        return wallName;
    }

    /* Change the Health and Image Icon for Walls */
    public void setWallHealth(int wallHealth)
    {
        this.wallHealth = wallHealth;

        if(this.wallHealth == 50)
        {
            // change the Image Icon to Damage Wall Image Icon
            wallImageIcon = getImageIcon(DAMAGED_WALL_IMAGE_FILE);
        }
    }

    /*
        To Get the Image Icon
        * */
    private ImageIcon getImageIcon(String imageFile)
    {
        URL url = getClass().getClassLoader().getResource(imageFile);
        if(url == null)
        {
            throw new AssertionError("Cannot find image file " + imageFile);
        }

        ImageIcon wallImageIcon = new ImageIcon(url);

        return wallImageIcon;
    }

    public ImageIcon getWallImageIcon()
    {
        return wallImageIcon;
    }
}
