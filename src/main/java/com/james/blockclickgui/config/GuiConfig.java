package com.james.blockclickgui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.james.blockclickgui.BlockClickGuiClient;
import com.james.blockclickgui.module.ModuleCategory;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GuiConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("client-ui.json");

	public static Data data = new Data();

	private GuiConfig() {
	}

	public static void load() {
		if (!Files.exists(FILE)) {
			return;
		}

		try {
			Data read = GSON.fromJson(Files.readString(FILE), Data.class);
			if (read != null) {
				data = read;
				if (data.panels == null) data.panels = new HashMap<>();
				if (data.modules == null) data.modules = new HashMap<>();
				if (data.settings == null) data.settings = new HashMap<>();
				if (data.hud == null) data.hud = new HashMap<>();
			}
		} catch (Exception e) {
			BlockClickGuiClient.LOGGER.warn("Could not load click gui config", e);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, GSON.toJson(data));
		} catch (IOException e) {
			BlockClickGuiClient.LOGGER.warn("Could not save click gui config", e);
		}
	}

	public static PanelSpot panel(ModuleCategory category, int fallbackX, int fallbackY) {
		String name = category.name();
		PanelSpot spot = data.panels.get(name);
		if (spot == null) {
			spot = new PanelSpot(fallbackX, fallbackY, true);
			data.panels.put(name, spot);
		}
		return spot;
	}

	public static HudSpot hud(String id, int fallbackX, int fallbackY, boolean visible) {
		HudSpot spot = data.hud.get(id);
		if (spot == null) {
			spot = new HudSpot(fallbackX, fallbackY, visible);
			data.hud.put(id, spot);
		}
		return spot;
	}

	public static String setting(String moduleId, String key) {
		Map<String, String> values = data.settings.get(moduleId);
		return values == null ? null : values.get(key);
	}

	public static void setting(String moduleId, String key, String value) {
		data.settings.computeIfAbsent(moduleId, ignored -> new HashMap<>()).put(key, value);
	}

	public static class Data {
		public Map<String, PanelSpot> panels = new HashMap<>();
		public Map<String, Boolean> modules = new HashMap<>();
		public Map<String, Map<String, String>> settings = new HashMap<>();
		public Map<String, HudSpot> hud = new HashMap<>();
	}

	public static class PanelSpot {
		public int x;
		public int y;
		public boolean open = true;

		public PanelSpot() {
		}

		public PanelSpot(int x, int y, boolean open) {
			this.x = x;
			this.y = y;
			this.open = open;
		}
	}

	public static class HudSpot {
		public int x;
		public int y;
		public boolean visible = true;

		public HudSpot() {
		}

		public HudSpot(int x, int y, boolean visible) {
			this.x = x;
			this.y = y;
			this.visible = visible;
		}
	}
}
