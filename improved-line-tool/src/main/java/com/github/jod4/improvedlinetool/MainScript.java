package com.github.jod4.improvedlinetool;

import script.SCRIPT;
import util.info.INFO;

/**
 * Entry point of the mod, found and instantiated by the game.
 */
@SuppressWarnings("unused") // used by the game via reflection
public final class MainScript implements SCRIPT {

	private final INFO info = new INFO("Improved Line Tool", "Draws lines in any angle. Hold the MOD key (left ctrl) for the vanilla line.");

	@Override
	public CharSequence name() {
		return info.name;
	}

	@Override
	public CharSequence desc() {
		return info.desc;
	}

	/**
	 * Not shown in the script selection, since it's always active via {@link #forceInit()}
	 */
	@Override
	public boolean isSelectable() {
		return false;
	}

	/**
	 * Load into every new and existing game while the mod is enabled
	 */
	@Override
	public boolean forceInit() {
		return true;
	}

	@Override
	public SCRIPT_INSTANCE createInstance() {
		return new InstanceScript();
	}
}
