package com.james.blockclickgui.module;

public enum ModuleCategory {
	TOOLS("Tools", 0xFF4C7A62),
	DISPLAY("Display", 0xFF4A6C91),
	TOYS("Toys", 0xFF8A6252),
	DEBUG("Debug", 0xFF6D617E),
	ODDS("Odds", 0xFF77734F);

	public final String title;
	public final int color;

	ModuleCategory(String title, int color) {
		this.title = title;
		this.color = color;
	}
}
