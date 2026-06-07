package com.james.blockclickgui.module;

import com.james.blockclickgui.config.GuiConfig;
import com.james.blockclickgui.gui.HudEditorScreen;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
	public static final List<Module> MODULES = new ArrayList<>();

	private ModuleManager() {
	}

	public static void makeModules() {
		if (!MODULES.isEmpty()) {
			return;
		}

		add("Counter", "Fake number box for testing the HUD.")
			.setting(new ModuleSetting.Number("start", "Start", 0, 0, 999, 1))
			.setting(new ModuleSetting.Number("step", "Step", 1, 1, 25, 1))
			.setting(new ModuleSetting.Mode("style", "Style", "plain", "box", "tiny"));

		add("Sticky Note", "Tiny pretend note widget.")
			.setting(new ModuleSetting.Mode("note", "Note", "todo", "brb", "empty"))
			.setting(new ModuleSetting.Bool("shadow", "Shadow", true));

		add("HUD Editor", "Move the demo HUD boxes around.", ModuleCategory.DISPLAY)
			.setting(new ModuleSetting.Bool("grid", "Grid", true))
			.setting(new ModuleSetting.Number("snap", "Snap", 4, 1, 16, 1))
			.setting(new ModuleSetting.Bool("ghosts", "Ghosts", true))
			.action(() -> Minecraft.getInstance().setScreen(new HudEditorScreen()));

		add("Status Line", "Simple HUD status label.", ModuleCategory.DISPLAY)
			.setting(new ModuleSetting.Mode("tone", "Tone", "quiet", "loud", "flat"))
			.setting(new ModuleSetting.Bool("brackets", "Brackets", true));

		add("Tiny Clock", "Small clock label for the HUD.", ModuleCategory.DISPLAY)
			.setting(new ModuleSetting.Mode("format", "Format", "24h", "12h", "ticks"))
			.setting(new ModuleSetting.Bool("seconds", "Seconds", false));

		add("Dice Roller", "Random-looking number toy.", ModuleCategory.TOYS)
			.setting(new ModuleSetting.Number("sides", "Sides", 6, 2, 20, 1))
			.setting(new ModuleSetting.Mode("mood", "Mood", "normal", "dramatic", "sleepy"));

		add("Color Box", "A colored block for layout testing.", ModuleCategory.TOYS)
			.setting(new ModuleSetting.Mode("color", "Color", "green", "blue", "red", "gray"))
			.setting(new ModuleSetting.Number("size", "Size", 12, 8, 40, 2));

		add("Fake Packets", "Debug counter that does nothing real.", ModuleCategory.DEBUG)
			.setting(new ModuleSetting.Number("rate", "Rate", 3, 0, 99, 1))
			.setting(new ModuleSetting.Bool("blink", "Blink", false));

		add("Frame Readout", "Pretend frame meter for mockups.", ModuleCategory.DEBUG)
			.setting(new ModuleSetting.Mode("unit", "Unit", "fps", "ms", "bars"))
			.setting(new ModuleSetting.Bool("warn", "Warn", true));

		add("Mood Tag", "Random label for testing text widths.", ModuleCategory.ODDS)
			.setting(new ModuleSetting.Mode("tag", "Tag", "ok", "busy", "lost", "coffee"))
			.setting(new ModuleSetting.Bool("caps", "Caps", false));
	}

	private static Module add(String name, String desc) {
		return add(name, desc, ModuleCategory.TOOLS);
	}

	private static Module add(String name, String desc, ModuleCategory category) {
		Module module = new Module(name, desc, category);
		MODULES.add(module);
		return module;
	}

	public static void loadSavedStates() {
		for (Module module : MODULES) {
			Boolean enabled = GuiConfig.data.modules.get(module.id());
			if (enabled != null) {
				module.enabled = enabled;
			}
			module.loadSettings();
		}
	}

	public static Map<ModuleCategory, List<Module>> byCategory() {
		Map<ModuleCategory, List<Module>> map = new EnumMap<>(ModuleCategory.class);
		for (ModuleCategory category : ModuleCategory.values()) {
			map.put(category, new ArrayList<>());
		}

		for (Module module : MODULES) {
			map.get(module.category).add(module);
		}
		return map;
	}
}
