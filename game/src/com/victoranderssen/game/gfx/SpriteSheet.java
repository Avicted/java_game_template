package com.victoranderssen.game.gfx;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    public int width = 256;
    public int height = 256;

    public int[] pixels;

    public SpriteSheet(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();
        pixels = image.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (pixels[i] & 0xff) / 64;
        }
    }
}
