package com.victoranderssen.dungeon_game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.victoranderssen.dungeon_game.gfx.Screen;
import com.victoranderssen.dungeon_game.gfx.SpriteSheet;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "Dungeon Game";
	public static final int SCALE = 3;
	public static final int HEIGHT = 360;
	public static final int WIDTH = HEIGHT * 16 / 9;

	public static final int TARGET_FPS = 144; // Adjustable frame rate
	public static final int TARGET_TICK_RATE = 60; // Fixed tick rate

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private DataBufferInt dataBufferInt = ((DataBufferInt) image.getRaster().getDataBuffer());
	private int[] pixels = dataBufferInt.getData();

	private boolean running = false;
	private int tickCount;
	private long lastTime, timer;
	private int ticks, frames;

	// Time in nanoseconds per tick and per frame
	private double nsPerTick;
	private double nsPerFrame;

	private Screen screen;

	private int[] colors = new int[256];

	public void start() {
		running = true;
		new Thread(this).start();
	}

	public void stop() {
		running = false;
	}

	private void init() {
		int pp = 0;

		// Loop through all combinations of red (r), green (g), and blue (b) values
		// Each of r, g, b will range from 0 to 5, creating a palette with 6 levels per
		// color
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					// Generate a color by combining the RGB values and store it in the 'colors'
					// array
					// The color intensity is scaled from 0 to 255, divided by 5 (since there are 6
					// levels)
					// The final color is composed of:
					// - Red: (r * 255 / 5) shifted 16 bits (into the red channel)
					// - Green: (g * 255 / 5) shifted 8 bits (into the green channel)
					// - Blue: (b * 255 / 5) directly in the blue channel
					// This will create a total of 6 * 6 * 6 = 216 distinct colors in the 'colors'
					// array
					colors[pp++] = (r * 255 / 5) << 16 | (g * 255 / 5) << 8 | (b * 255 / 5);
				}
			}
		}

		try {
			// Print the path of the resource
			System.out.println(
					"\n\tSpriteSheet path: " + Game.class.getResource("/SpriteSheet.png") + "\n");

			screen = new Screen(WIDTH, HEIGHT,
					new SpriteSheet(ImageIO.read(
							getClass().getResourceAsStream("/SpriteSheet.png"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// Initialize timing values
		lastTime = System.nanoTime();
		timer = System.currentTimeMillis();
		nsPerTick = 1000000000.0 / TARGET_TICK_RATE; // Nanoseconds per tick
		nsPerFrame = 1000000000.0 / TARGET_FPS; // Nanoseconds per frame

		// Initialize time tracking for ticks and frames
		double tickDelta = 0;
		double frameDelta = 0;

		init();

		while (running) {
			long now = System.nanoTime();
			tickDelta += (now - lastTime) / nsPerTick;
			frameDelta += (now - lastTime) / nsPerFrame;
			lastTime = now;

			// Update ticks at a fixed rate
			while (tickDelta >= 1) {
				ticks++;
				tick(); // Perform the game logic update
				tickDelta--;
			}

			// Render the game at the fixed FPS rate
			if (frameDelta >= 1) {
				frames++;
				render(); // Perform the rendering update
				frameDelta--;
			}

			// Avoid consuming too much CPU time
			try {
				Thread.sleep(1); // Sleep to limit CPU usage
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Print ticks and frames per second every second
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(ticks + " ticks, " + frames + " FPS");
				ticks = 0;
				frames = 0;
			}
		}
	}

	// Game logic update
	public void tick() {
		tickCount++;
		screen.xScroll++;
		// screen.yScroll++;
	}

	// Render the game
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		screen.render();
		for (int y = 0; y < screen.h; y++) {
			for (int x = 0; x < screen.w; x++) {
				pixels[x + y * WIDTH] = colors[screen.pixels[x + y * screen.w]];
			}
		}

		Graphics g = bs.getDrawGraphics();

		int ww = WIDTH * SCALE;
		int hh = HEIGHT * SCALE;
		int xo = (getWidth() - ww) / 2;
		int yo = (getHeight() - hh) / 2;
		g.drawImage(image, xo, yo, ww, hh, null);
		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		JFrame frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(game, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.start();
	}
}
