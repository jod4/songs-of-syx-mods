package view.tool;

import com.github.jod4.improvedlinetool.LineRasterizer;
import settlement.main.SETT;
import snake2d.LOG;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.sets.LIST;
import util.GUTIL;
import view.keyboard.KEYS;
import view.subview.GameWindow;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

/**
 * Wraps the vanilla {@link PlacableMultiTool} and replaces the area painted by the line tool
 * with a Bresenham line. Holding the MOD key (left ctrl by default) keeps the vanilla 45°/90° line.
 *
 * Lives in the games {@code view.tool} package, because {@link ToolPlacer.placeFunc}, {@link PlacerArea}
 * and {@link PLACER_TYPE#paint} are package-private. Mod jars are loaded by the same class loader as the game,
 * so this works at runtime.
 */
public final class ImprovedLineMultiTool extends ToolPlacer.placeFunc {

	private static final VarHandle TYPE;
	private static final VarHandle SIZE;
	private static final VarHandle HOVERED_TILE;
	private static final VarHandle CLICKED_TILE;
	private static final VarHandle PLACABLE;

	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(PlacableMultiTool.class, MethodHandles.lookup());
			TYPE = lookup.findVarHandle(PlacableMultiTool.class, "type", PLACER_TYPE.class);
			SIZE = lookup.findVarHandle(PlacableMultiTool.class, "size", int.class);
			HOVERED_TILE = lookup.findVarHandle(PlacableMultiTool.class, "hTile", snake2d.util.datatypes.Coo.class);
			CLICKED_TILE = lookup.findVarHandle(PlacableMultiTool.class, "clickedTile", snake2d.util.datatypes.Coo.class);
			PLACABLE = lookup.findVarHandle(PlacableMultiTool.class, "placable", PlacableMulti.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final PlacableMultiTool vanilla;

	private ImprovedLineMultiTool(PlacableMultiTool vanilla) {
		this.vanilla = vanilla;
	}

	/**
	 * Replaces the multi tile tool of the given tool manager with this one.
	 *
	 * @return whether the tool was replaced
	 */
	public static boolean install(ToolManager tools) throws ReflectiveOperationException {
		Field multi = ToolPlacer.class.getDeclaredField("multi");
		multi.setAccessible(true);
		Object current = multi.get(tools.placer);
		if (current instanceof ImprovedLineMultiTool) {
			return false;
		}
		if (!(current instanceof PlacableMultiTool)) {
			LOG.ln("[Improved Line Tool] Unexpected multi tool " + current.getClass().getName() + ", not replacing it");
			return false;
		}
		multi.set(tools.placer, new ImprovedLineMultiTool((PlacableMultiTool) current));
		return true;
	}

	@Override
	void updateHovered(float ds, GameWindow window, boolean pressed) {
		vanilla.updateHovered(ds, window, pressed);

		if (!pressed || TYPE.get(vanilla) != PLACER_TYPE.LINE || KEYS.MAIN().MOD.isPressed()) {
			return;
		}

		COORDINATE from = (COORDINATE) CLICKED_TILE.get(vanilla);
		COORDINATE to = (COORDINATE) HOVERED_TILE.get(vanilla);
		PlacerArea area = PlacerArea.self;
		area.clear();
		LineRasterizer.line(from.x(), from.y(), to.x(), to.y(), (int) SIZE.get(vanilla), area.set::set);
		expand(area, (PlacableMulti) PLACABLE.get(vanilla));
	}

	/**
	 * Same expansion the vanilla tool applies after painting, e.g. for placables spanning multiple tiles.
	 */
	private void expand(PlacerArea area, PlacableMulti placable) {
		GUTIL.filler().init(this);
		for (COORDINATE c : area.body()) {
			if (area.is(c)) {
				GUTIL.filler().fill(c);
			}
		}
		while (GUTIL.filler().hasMore()) {
			COORDINATE c = GUTIL.filler().poll();
			for (int i = 0; i < DIR.ALL.size(); i++) {
				DIR d = DIR.ALL.get(i);
				int dx = c.x() + d.x();
				int dy = c.y() + d.y();
				if (SETT.IN_BOUNDS(dx, dy) && placable.expandsTo(c.x(), c.y(), dx, dy)) {
					GUTIL.filler().fill(dx, dy);
					area.set.set(dx, dy);
				}
			}
		}
		GUTIL.filler().done();
	}

	@Override
	void update(float ds, GameWindow window, boolean pressed) {
		vanilla.update(ds, window, pressed);
	}

	@Override
	void render(SPRITE_RENDERER r, float ds, GameWindow window) {
		vanilla.render(r, ds, window);
	}

	@Override
	void click(GameWindow window) {
		vanilla.click(window);
	}

	@Override
	void clickRelease(GameWindow window) {
		vanilla.clickRelease(window);
	}

	@Override
	void activate(PLACABLE placer, GameWindow window) {
		vanilla.activate(placer, window);
	}

	@Override
	LIST<CLICKABLE> gui() {
		return vanilla.gui();
	}
}
