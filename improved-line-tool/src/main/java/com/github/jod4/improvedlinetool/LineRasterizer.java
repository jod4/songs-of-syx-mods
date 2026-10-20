package com.github.jod4.improvedlinetool;

/**
 * Rasterizes lines onto the tile grid with Bresenham's algorithm.
 * Free of game dependencies, so it can be unit tested.
 */
public final class LineRasterizer {

	@FunctionalInterface
	public interface TileSetter {
		void set(int x, int y);
	}

	private LineRasterizer() {
	}

	/**
	 * Draws a line from (x0, y0) to (x1, y1), both ends inclusive.
	 *
	 * @param size thickness like the vanilla line tool: the line is {@code size + 1} tiles wide,
	 *             measured along the minor axis, so thick lines have no gaps
	 */
	public static void line(int x0, int y0, int x1, int y1, int size, TileSetter out) {
		int dx = Math.abs(x1 - x0);
		int dy = -Math.abs(y1 - y0);
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		boolean xMajor = dx >= -dy;
		int from = -(size / 2);
		int to = size - size / 2;
		int err = dx + dy;

		while (true) {
			for (int k = from; k <= to; k++) {
				if (xMajor) {
					out.set(x0, y0 + k);
				} else {
					out.set(x0 + k, y0);
				}
			}
			if (x0 == x1 && y0 == y1) {
				return;
			}
			int e2 = 2 * err;
			if (e2 >= dy) {
				err += dy;
				x0 += sx;
			}
			if (e2 <= dx) {
				err += dx;
				y0 += sy;
			}
		}
	}
}
