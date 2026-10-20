package com.github.jod4.improvedlinetool;

import script.SCRIPT;
import snake2d.LOG;
import snake2d.Renderer;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import view.main.VIEW;
import view.tool.ImprovedLineMultiTool;

/**
 * Installs the improved line tool as soon as the game views exist.
 */
final class InstanceScript implements SCRIPT.SCRIPT_INSTANCE {

	private boolean installed;

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
		if (installed) {
			return;
		}
		installed = true;

		try {
			ImprovedLineMultiTool.install(VIEW.s().tools);
			ImprovedLineMultiTool.install(VIEW.world().tools);
			LOG.ln("[Improved Line Tool] Installed");
		} catch (ReflectiveOperationException | RuntimeException e) {
			LOG.err("[Improved Line Tool] Could not install, the game uses the vanilla line tool");
			e.printStackTrace();
		}
	}
}
