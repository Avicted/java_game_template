package com.victoranderssen.dungeon_game.gfx;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private List<Sprite> sprites = new ArrayList<Sprite>();

    private static final int MAP_WIDTH = 64; // Must be a power of 2
    private static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH * 2];
    public int[] colors = new int[MAP_WIDTH * MAP_WIDTH * 4];
    public int[] databits = new int[MAP_WIDTH * MAP_WIDTH];
    public int xScroll;
    public int yScroll;

    public static final int BIT_MIRROR_X = 0x01;
    public static final int BIT_MIRROR_Y = 0x02;

    public final int w, h;

    private SpriteSheet sheet;

    public Screen(int w, int h, SpriteSheet sheet) {
        this.sheet = sheet;
        this.w = w;
        this.h = h;

        for (int i = 0; i < MAP_WIDTH * MAP_WIDTH; i++) {
            colors[i * 4 + 0] = 0xff00ff;
            colors[i * 4 + 1] = 0x00ffff;
            colors[i * 4 + 2] = 0xffff00;
            colors[i * 4 + 3] = 0xffffff;

            if (i % 2 == 0) {
                databits[i] += 1;
            }
            if (i / MAP_WIDTH % 2 == 0) {
                databits[i] += 2;
            }
        }
    }

    public void render(int[] pixels, int offs, int row) {
        for (int yt = yScroll >> 3; yt <= (yScroll + h) >> 3; yt++) {
            int yp = yt * 8 - yScroll;

            for (int xt = xScroll >> 3; xt <= (xScroll + w) >> 3; xt++) {
                int xp = xt * 8 - xScroll;

                int tileIndex = (xt & (MAP_WIDTH_MASK)) + (yt & (MAP_WIDTH_MASK)) * MAP_WIDTH;
                int bits = databits[tileIndex] & 3;

                boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
                boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

                for (int y = 0; y < 8; y++) {
                    int ys = y;
                    if (mirrorY) {
                        ys = 7 - y;
                    }
                    if (y + yp < 0) {
                        continue;
                    }
                    if (y + yp >= h) {
                        continue;
                    }

                    for (int x = 0; x < 8; x++) {
                        if (x + xp < 0) {
                            continue;
                        }
                        if (x + xp >= w) {
                            continue;
                        }

                        int xs = x;
                        if (mirrorX) {
                            xs = 7 - x;
                        }

                        int col = tileIndex * 4 + sheet.pixels[xs + ys * sheet.width];
                        pixels[(x + xp) + (y + yp) * row + offs] = colors[col];

                    }
                }
            }
        }
    }
}
