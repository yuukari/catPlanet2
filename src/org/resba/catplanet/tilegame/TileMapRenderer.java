package org.resba.catplanet.tilegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.resba.catplanet.graphics.Sprite;
import org.resba.catplanet.tilegame.sprites.Entity;
import org.resba.catplanet.util.CatCounter;



/**
    The TileMapRenderer class draws a TileMap on the screen.
    It draws all tiles, sprites, and an optional background image
    centered around the position of the player.

    <p>If the width of background image is smaller the width of
    the tile map, the background image will appear to move
    slowly, creating a parallax background effect.

    <p>Also, three static methods are provided to convert pixels
    to tile positions, and vice-versa.

    <p>This TileMapRender uses a tile size of 64.
*/
public class TileMapRenderer {

    
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 6;
    private static final int TILE_SIZE = (int) Math.pow(2, TILE_SIZE_BITS);
    private Image background;
    private String label;
    private float ex;
    private float ey;
    private boolean flip;
    private ArrayList<String> s;
    private ArrayList<String> xn;
    private ArrayList<String> yn;
    private long timer;
    public String extra;
    
    public TileMapRenderer(){
        s = new ArrayList<String>();
        xn = new ArrayList<String>();
        yn = new ArrayList<String>();
        flip = false;
        extra = "";
    }
    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        //return pixels >> TILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two,
        // use the floor function:
        return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**                             
        Converts a tile position to a pixel position.
    */
    public static int tilesToPixels(int numTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;  

        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }


    /**
        Sets the background to draw.
    */
    public void setBackground(Image background) {
        this.background = background;
    }

    public void flip(boolean b){
       this.flip = b;
    }
    public void addText(String label, int nx, int ny){
         s.add(label);
         String ix = ""+nx;
         String iy = ""+ny;
         xn.add(ix);
         yn.add(iy);
    }
    public void removeAllText(){
        s.removeAll(s);
        xn.removeAll(xn);
        yn.removeAll(yn);
    }
    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g, TileMap map,
        int screenWidth, int screenHeight)
    {
        Sprite player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());
        // get the scrolling position of the map
        // based on player's position
        int offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.max(offsetX, screenWidth - mapWidth);
        // get the y offset to draw all sprites and tiles
        int offsetY = screenHeight -
            tilesToPixels(map.getHeight());

        // draw black background, if needed
        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }

        // draw parallax background image
        if (background != null) {
            int x = offsetX * (screenWidth - background.getWidth(null)) / (screenWidth + mapWidth);
            int y = screenHeight - background.getHeight(null);

            g.drawImage(background, x, y, null);
        }

        // draw the visible tiles
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX +
            pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<map.getHeight(); y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }
        // draw player
        g.drawImage(player.getImage(),
            Math.round(player.getX()) + offsetX,
            Math.round(player.getY()) + offsetY,
            null);

        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);
            // wake up the creature when it's on screen
            if (sprite instanceof Entity &&
                x >= 0 && x < screenWidth)
            {
                ((Entity)sprite).wakeUp();
            }
        }
        g.setColor(Color.black);
        g.setBackground(Color.WHITE);
        g.drawString("cats x"+ CatCounter.getCATS(),tilesToPixels(1),tilesToPixels(2));
        g.drawString("player velocity x: "+player.getVelocityX(), tilesToPixels(1), tilesToPixels(3));
        g.drawString("player velocity y: "+player.getVelocityY(), tilesToPixels(1), tilesToPixels(4));
        g.drawString("player x: "+pixelsToTiles(Math.round(player.getX()))+ offsetX, tilesToPixels(1), tilesToPixels(5));
        g.drawString("player y: "+pixelsToTiles(Math.round(player.getY()))+ offsetY, tilesToPixels(1), tilesToPixels(6));
        if(s.size() > 0){
            for(int in = 0; s.size() > in; in++){
                        g.drawString(s.get(in),tilesToPixels(Integer.parseInt(xn.get(in)))+offsetX,tilesToPixels(Integer.parseInt(yn.get(in)))+offsetY);
                }
            }
    }

}
