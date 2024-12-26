package com.victoranderssen.dungeon_game.gfx;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private List<Sprite> sprites = new ArrayList<Sprite>();

    private static final int MAP_WIDTH = 64; // Must be a power of 2
    private static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    private int[] tiles = new int[MAP_WIDTH * MAP_WIDTH * 2];
    private int[] colors = new int[MAP_WIDTH * MAP_WIDTH * 3];
    private int[] databits = new int[MAP_WIDTH * MAP_WIDTH];
    private int xScroll;
    private int yScroll;

    public final int w, h;

    private SpriteSheet sheet;

    public Screen(int w, int h, SpriteSheet sheet) {
        this.sheet = sheet;
        this.w = w;
        this.h = h;
    }

    public void render(int[] pixels, int offs, int row) {
        for (int yt = xScroll << 3; yt <= (xScroll + 8) >> 3; yt++) {
            int y0 = yt + yScroll;
            int y1 = y0 + 8;
            if (y0 < 0)
                y0 = 0;
            if (y1 > h)
                y1 = h;

            for (int xt = yScroll << 3; xt <= (yScroll + 8) >> 3; xt++) {
                int x0 = xt - xScroll;
                int x1 = x0 + 8;
                if (x0 < 0)
                    x0 = 0;
                if (x1 > h)
                    x1 = h;

                int tileIndex = (xt & (MAP_WIDTH_MASK)) + (yt & (MAP_WIDTH_MASK)) * MAP_WIDTH;

                for (int y = 0; y < y1; y++) {
                    int sourcePointer = ((y - yScroll) & 7) * sheet.width + ((x0 - xScroll) & 7);
                    int targetPointer = offs + x0 + y * row;
                    for (int x = x0; x < x1; x++) {
                        pixels[targetPointer++] = colors[tileIndex * 4 + sheet.pixels[sourcePointer++]];
                    }
                }
            }
        }
    }
}
