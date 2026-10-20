package com.github.jod4.improvedlinetool;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LineRasterizerTest {

	private static List<String> line(int x0, int y0, int x1, int y1, int size) {
		List<String> tiles = new ArrayList<>();
		LineRasterizer.line(x0, y0, x1, y1, size, (x, y) -> tiles.add(x + "," + y));
		return tiles;
	}

	@Test
	void singleTile() {
		assertThat(line(3, 4, 3, 4, 0)).containsExactly("3,4");
	}

	@Test
	void horizontalAndDiagonal() {
		assertThat(line(0, 0, 3, 0, 0)).containsExactly("0,0", "1,0", "2,0", "3,0");
		assertThat(line(0, 0, -2, -2, 0)).containsExactly("0,0", "-1,-1", "-2,-2");
	}

	@Test
	void shallowAngleStaysOnTheIdealLine() {
		List<int[]> tiles = new ArrayList<>();
		LineRasterizer.line(0, 0, 9, 4, 0, (x, y) -> tiles.add(new int[]{x, y}));

		assertThat(tiles).hasSize(10);
		for (int i = 0; i < tiles.size(); i++) {
			int[] t = tiles.get(i);
			assertThat(t[0]).isEqualTo(i);
			assertThat(Math.abs(t[1] - t[0] * 4.0 / 9.0)).isLessThanOrEqualTo(0.5);
		}
	}

	@Test
	void steepAngleHasOneTilePerRow() {
		List<String> tiles = line(0, 0, 2, 7, 0);
		assertThat(tiles).hasSize(8).startsWith("0,0").endsWith("2,7");
	}

	@Test
	void thicknessIsSizePlusOneAlongMinorAxis() {
		assertThat(line(0, 0, 2, 0, 1)).containsExactlyInAnyOrder("0,0", "0,1", "1,0", "1,1", "2,0", "2,1");
		assertThat(line(0, 0, 0, 1, 2)).containsExactlyInAnyOrder("-1,0", "0,0", "1,0", "-1,1", "0,1", "1,1");
	}

	@Test
	void lineIsSymmetricInLength() {
		Set<String> forward = new LinkedHashSet<>(line(0, 0, 9, 4, 0));
		Set<String> backward = new LinkedHashSet<>(line(9, 4, 0, 0, 0));
		assertThat(forward).hasSize(10);
		assertThat(backward).hasSize(10);
	}
}
