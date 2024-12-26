package com.victoranderssen.dungeon_game.gfx;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private List<Sprite> sprites = new ArrayList<Sprite>();

    private static final int MAP_WIDTH = 64; // Must be a power of 2
    private static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH];
    public int[] colors = new int[MAP_WIDTH * MAP_WIDTH];
    public int[] databits = new int[MAP_WIDTH * MAP_WIDTH];
    public int xScroll;
    public int yScroll;

    public static final int BIT_MIRROR_X = 0x01;
    public static final int BIT_MIRROR_Y = 0x02;

    public final int w, h;
    public int[] pixels;

    private SpriteSheet sheet;

    public Screen(int w, int h, SpriteSheet sheet) {
        this.sheet = sheet;
        this.w = w;
        this.h = h;

        pixels = new int[w * h];

        for (int i = 0; i < MAP_WIDTH * MAP_WIDTH; i++) {
            colors[i] = i & 511;

            if (i % 2 == 0) {
                databits[i] += 1;
            }
            if (i / MAP_WIDTH % 2 == 0) {
                databits[i] += 2;
            }
        }
    }

    public void render() {
        for (int yt = yScroll >> 3; yt <= (yScroll + h) >> 3; yt++) {
            int yp = yt * 8 - yScroll;

            for (int xt = xScroll >> 3; xt <= (xScroll + w) >> 3; xt++) {
                int xp = xt * 8 - xScroll;

                int tileIndex = (xt & (MAP_WIDTH_MASK)) + (yt & (MAP_WIDTH_MASK)) * MAP_WIDTH;
                int bits = databits[tileIndex] & 3;

                render(xp, yp, 0, colors[tileIndex], databits[tileIndex]);
            }
        }
    }

    private void render(int xp, int yp, int tileIndex, int colors, int bits) {
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

        for (int y = 0; y < 8; y++) {
            int ys = y;
            if (mirrorY) {
                ys = 7 - y;
            }
            if (y + yp < 0 || y + yp >= h) {
                continue;
            }

            for (int x = 0; x < 8; x++) {
                if (x + xp < 0 || x + xp >= w) {
                    continue;
                }

                int xs = x;
                if (mirrorX) {
                    xs = 7 - x;
                }

                int col = (colors >> (sheet.pixels[xs + ys * sheet.width] * 9)) & 511; // 9 is the number of bits per
                                                                                       // pixel
                pixels[(x + xp) + (y + yp) * w] = col;

            }
        }
    }
}
