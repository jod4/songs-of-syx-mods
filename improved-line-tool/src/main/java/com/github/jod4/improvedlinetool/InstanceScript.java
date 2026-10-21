package com.github.jod4.improvedlinetool;

import script.SCRIPT;
import snake2d.LOG;
import snake2d.Renderer;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import view.main.VIEW;
import view.tool.ImprovedLineMultiTool;
import view.tool.ToolManager;
import view.world.generator.WorldViewGenerator;

import java.lang.reflect.Field;

/**
 * Installs the improved line tool as soon as the game views exist.
 */
final class InstanceScript implements SCRIPT.SCRIPT_INSTANCE {

	private boolean installed;
	private WorldViewGenerator generator;

	@Override
	public void save(FilePutter file) {
	}

	@Override
	public void load(FileGetter file) {
	}

	@Override
	public void update(double deltaSeconds) {
		install();
	}

	/**
	 * Also install here, since the game might start paused and not call {@link #update(double)}
	 */
	@Override
	public void render(Renderer renderer, float deltaSeconds) {
		install();
	}

	private void install() {
		installGenerator();
		if (installed) {
			return;
		}
		installed = true;

		install("settlement", () -> VIEW.s().tools);
		install("world", () -> VIEW.world().tools);
		install("world editor", () -> fieldValue(VIEW.world().editor, "tools", ToolManager.class));
	}

	/**
	 * The world generator (edit stage of a new game) is only created on demand and not stored in a field,
	 * so it can only be reached while it is the current view.
	 */
	private void installGenerator() {
		if (VIEW.current() instanceof WorldViewGenerator generator && generator != this.generator) {
			this.generator = generator;
			install("world generator", () -> fieldValue(generator, "tools", ToolManager.class));
		}
	}

	/**
	 * Each view is installed separately, so a failure in one keeps the improved tool in the others
	 */
	private static void install(String view, ToolManagerGetter tools) {
		try {
			if (ImprovedLineMultiTool.install(tools.get())) {
				LOG.ln("[Improved Line Tool] Installed in " + view + " view");
			}
		} catch (ReflectiveOperationException | RuntimeException e) {
			LOG.err("[Improved Line Tool] Could not install in " + view + " view, it uses the vanilla line tool");
			e.printStackTrace();
		}
	}

	private static <T> T fieldValue(Object owner, String name, Class<T> type) throws ReflectiveOperationException {
		Field field = owner.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return type.cast(field.get(owner));
	}

	@FunctionalInterface
	private interface ToolManagerGetter {
		ToolManager get() throws ReflectiveOperationException;
	}
}
