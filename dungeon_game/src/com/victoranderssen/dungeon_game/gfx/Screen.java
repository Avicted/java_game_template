package com.victoranderssen.dungeon_game.gfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Screen {
    private List<Sprite> sprites = new ArrayList<Sprite>(); // List to store sprites (not used in this snippet)

    // Constants defining the width of the map (must be a power of 2)
    private static final int MAP_WIDTH = 64; // Size of the map (width and height)
    private static final int MAP_WIDTH_MASK = MAP_WIDTH - 1; // Mask for bitwise operations on map coordinates

    // Arrays to store tile data, color information, and additional bit flags for
    // each tile
    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH]; // Tile indices (probably referring to the tile sheet)
    public int[] colors = new int[MAP_WIDTH * MAP_WIDTH]; // Color information for each tile
    public int[] databits = new int[MAP_WIDTH * MAP_WIDTH]; // Extra data flags for each tile (e.g., mirror flags)

    // Scroll positions for x and y, determining where the visible area starts
    public int xScroll;
    public int yScroll;

    // Bit flags for mirroring tiles along the x and y axes
    public static final int BIT_MIRROR_X = 0x01;
    public static final int BIT_MIRROR_Y = 0x02;

    // Width and height of the screen (in pixels) and an array to store the screen's
    // pixel data
    public final int w, h;
    public int[] pixels;

    // Sprite sheet (contains tile data) used for rendering
    private SpriteSheet sheet;

    // Constructor that initializes the screen dimensions, sprite sheet, and random
    // color generation
    public Screen(int w, int h, SpriteSheet sheet) {
        this.sheet = sheet;
        this.w = w;
        this.h = h;

        // Initialize the pixel array to store the screen's pixel data
        pixels = new int[w * h];

        // Random number generator for initializing colors
        Random random = new Random();

        // Initialize colors and databits arrays with random values
        for (int i = 0; i < MAP_WIDTH * MAP_WIDTH; i++) {
            // Generate random RGB color values for each tile (using base 6 color system)
            colors[i] = (colors[i] << 8) + random.nextInt(6 * 6 * 6);
            colors[i] = (colors[i] << 8) + random.nextInt(6 * 6 * 6);
            colors[i] = (colors[i] << 8) + random.nextInt(6 * 6 * 6);
            colors[i] = (colors[i] << 8) + random.nextInt(6 * 6 * 6);

            // Alternate databits for tiles (setting flags for mirroring)
            if (i % 2 == 0) {
                databits[i] += 1; // Set the horizontal mirror bit for every other tile
            }
            if (i / MAP_WIDTH % 2 == 0) {
                databits[i] += 2; // Set the vertical mirror bit for every other row of tiles
            }
        }
    }

    // Main rendering function that iterates through the screen area and renders
    // visible tiles
    public void render() {
        // Iterate through tile rows in the vertical direction (y-axis)
        for (int yt = yScroll >> 3; yt <= (yScroll + h) >> 3; yt++) {
            int yp = yt * 8 - yScroll; // Calculate vertical pixel position based on scroll

            // Iterate through tile columns in the horizontal direction (x-axis)
            for (int xt = xScroll >> 3; xt <= (xScroll + w) >> 3; xt++) {
                int xp = xt * 8 - xScroll; // Calculate horizontal pixel position based on scroll
                // Calculate the index of the tile in the map (with wrapping based on map size)
                int tileIndex = (xt & (MAP_WIDTH_MASK)) + (yt & (MAP_WIDTH_MASK)) * MAP_WIDTH;
                // Call the render function to draw this tile at the appropriate position
                render(xp, yp, 0, colors[tileIndex], databits[tileIndex]);
            }
        }
    }

    // Helper function to render an individual tile at a specific position (xp, yp)
    private void render(int xp, int yp, int tileIndex, int colors, int bits) {
        // Check if the tile should be mirrored along the X or Y axis using bitwise
        // flags
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

        // Loop through all 8x8 pixels of the tile (assuming tiles are 8x8 in size)
        for (int y = 0; y < 8; y++) {
            int ys = y;
            if (mirrorY) {
                ys = 7 - y; // Flip the Y-coordinate if mirroring is enabled
            }
            // Skip rendering if the tile is outside the screen bounds vertically
            if (y + yp < 0 || y + yp >= h) {
                continue;
            }

            for (int x = 0; x < 8; x++) {
                // Skip rendering if the tile is outside the screen bounds horizontally
                if (x + xp < 0 || x + xp >= w) {
                    continue;
                }

                int xs = x;
                if (mirrorX) {
                    xs = 7 - x; // Flip the X-coordinate if mirroring is enabled
                }

                // Calculate the color for the current pixel based on the sprite sheet
                int col = (colors >> (sheet.pixels[xs + ys * sheet.width] * 8)) & 255;

                // If the color is not transparent (i.e., not equal to 255), set the pixel
                if (col < 255) {
                    pixels[(x + xp) + (y + yp) * w] = col;
                }
            }
        }
    }
}
