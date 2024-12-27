package com.victoranderssen.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.victoranderssen.game.gfx.Renderer;
import com.victoranderssen.game.gfx.SpriteSheet;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "Dungeon Game";
	public static final int SCALE = 3;
	public static final int HEIGHT = 360;
	public static final int WIDTH = HEIGHT * 16 / 9;

	public static final int TARGET_FPS = 1000; // Adjustable frame rate
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

	private Renderer renderer;

	private int[] colors = new int[512];

	public void start() {
		running = true;
		new Thread(this).start();
	}

	public void stop() {
		running = false;
	}

	private void init() {
		int pp = 0;

		for (int r = 0; r < 8; r++) {
			for (int g = 0; g < 8; g++) {
				for (int b = 0; b < 8; b++) {
					colors[pp++] = (r * 255 / 7) << 16 | (g * 255 / 7) << 8 | (b * 255 / 7);
				}
			}
		}

		try {
			// Print the path of the resource
			System.out.println(
					"\n\tSpriteSheet path: " + getClass().getResource("/SpriteSheet.png") + "\n");

			renderer = new Renderer(WIDTH, HEIGHT,
					new SpriteSheet(ImageIO.read(getClass().getResourceAsStream("/SpriteSheet.png"))));
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
		renderer.xScroll++;
		renderer.yScroll++;
	}

	// Render the game
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		renderer.render();
		for (int y = 0; y < renderer.h; y++) {
			for (int x = 0; x < renderer.w; x++) {
				pixels[x + y * WIDTH] = colors[renderer.pixels[x + y * renderer.w]];
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
		Main game = new Main();
		game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		JFrame frame = new JFrame(Main.NAME);
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
