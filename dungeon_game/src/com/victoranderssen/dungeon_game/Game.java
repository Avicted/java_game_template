package com.victoranderssen.dungeon_game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "Dungeon Game";
	public static final int HEIGHT = 360;
	public static final int WIDTH = HEIGHT * 16 / 9;
	public static final int TARGET_FPS = 144; // Adjustable frame rate
	public static final int TARGET_TICK_RATE = 60; // Fixed tick rate

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

	WritableRaster raster = image.getRaster();
	private DataBufferInt dataBufferInt = ((DataBufferInt) image.getRaster().getDataBuffer());
	private int[] pixels = dataBufferInt.getData();

	private boolean running = false;
	private int tickCount;

	public void start() {
		running = true;
		new Thread(this).start();
	}

	public void stop() {
		running = false;
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000.0 / TARGET_TICK_RATE; // 60 ticks per second
		double nsPerFrame = 1000000000.0 / TARGET_FPS; // Adjustable FPS
		double tickDelta = 0;
		double frameDelta = 0;
		int frames = 0;
		int ticks = 0;
		long lastTimer1 = System.currentTimeMillis();

		while (running) {
			long now = System.nanoTime();
			tickDelta += (now - lastTime) / nsPerTick;
			frameDelta += (now - lastTime) / nsPerFrame;
			lastTime = now;

			// Tick the game logic at the TARGET_TICK_RATE (60 ticks per second)
			while (tickDelta >= 1) {
				ticks++;
				tick();
				tickDelta--;
			}

			// Render the game at the TARGET_FPS (adjustable)
			if (frameDelta >= 1) {
				frames++;
				render();
				frameDelta--;
			}

			try {
				// Sleep to limit CPU usage (for FPS cap)
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Output ticks and FPS every second
			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				System.out.println(ticks + " ticks, " + frames + " FPS");
				frames = 0;
				ticks = 0;
			}
		}
	}

	public void tick() {
		tickCount++;
	}

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
		game.setMinimumSize(new Dimension(WIDTH * 2, HEIGHT * 2));
		game.setMaximumSize(new Dimension(WIDTH * 2, HEIGHT * 2));
		game.setPreferredSize(new Dimension(WIDTH * 2, HEIGHT * 2));

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
