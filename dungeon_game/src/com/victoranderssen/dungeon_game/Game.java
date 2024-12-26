package com.victoranderssen.dungeon_game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "Dungeon Game";
	public static final int SCALE = 2;
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

	public void start() {
		running = true;
		new Thread(this).start();
	}

	public void stop() {
		running = false;
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
	}

	// Render the game
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		// Simple pixel manipulation for rendering
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = i + tickCount;
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
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
		frame.add(game);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.start();
	}
}
