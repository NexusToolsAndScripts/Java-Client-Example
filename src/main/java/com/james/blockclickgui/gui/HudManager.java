package com.james.blockclickgui.gui;

import com.james.blockclickgui.BlockClickGuiClient;
import com.james.blockclickgui.config.GuiConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HudManager {
	private static final List<HudWidget> WIDGETS = new ArrayList<>();
	private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static int fakeCounter;
	private static int ticks;

	private HudManager() {
	}

	public static void init() {
		if (!WIDGETS.isEmpty()) {
			return;
		}

		widget("counter", "Counter", 10, 10, true);
		widget("clock", "Clock", 10, 28, true);
		widget("note", "Note", 10, 46, true);
		widget("debug", "Debug", 10, 64, false);

		HudElementRegistry.addLast(
			Identifier.fromNamespaceAndPath(BlockClickGuiClient.MOD_ID, "demo_hud"),
			HudManager::renderHud
		);
	}

	public static void tick() {
		ticks++;
		if (ticks % 20 == 0) {
			fakeCounter++;
		}
	}

	public static List<HudWidget> widgets() {
		return WIDGETS;
	}

	public static HudWidget widgetAt(double x, double y) {
		for (int i = WIDGETS.size() - 1; i >= 0; i--) {
			HudWidget widget = WIDGETS.get(i);
			if (widget.contains(x, y)) {
				return widget;
			}
		}
		return null;
	}

	public static void save(HudWidget widget) {
		GuiConfig.HudSpot spot = GuiConfig.hud(widget.id, widget.x, widget.y, widget.visible);
		spot.x = widget.x;
		spot.y = widget.y;
		spot.visible = widget.visible;
		GuiConfig.save();
	}

	public static void drawWidget(GuiGraphics graphics, HudWidget widget, boolean editor, boolean selected) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		String text = text(widget);
		widget.w = Math.max(76, font.width(text) + 10);
		widget.h = 15;

		if (!widget.visible && !editor) {
			return;
		}

		int fill = widget.visible ? 0xAA101010 : 0x55101010;
		int edge = selected ? 0xFFEED06B : (widget.visible ? 0xFF404040 : 0xFF252525);
		box(graphics, widget.x, widget.y, widget.w, widget.h, fill, edge);
		graphics.drawString(font, text, widget.x + 5, widget.y + 4, widget.visible ? 0xFFFFFFFF : 0xFF777777, false);
	}

	private static void renderHud(GuiGraphics graphics, DeltaTracker deltaTracker) {
		for (HudWidget widget : WIDGETS) {
			drawWidget(graphics, widget, false, false);
		}
	}

	private static void widget(String id, String title, int x, int y, boolean visible) {
		GuiConfig.HudSpot spot = GuiConfig.hud(id, x, y, visible);
		WIDGETS.add(new HudWidget(id, title, spot.x, spot.y, spot.visible));
	}

	private static String text(HudWidget widget) {
		return switch (widget.id) {
			case "counter" -> "Counter: " + fakeCounter;
			case "clock" -> "Clock: " + LocalTime.now().format(CLOCK);
			case "note" -> "Note: fake todo";
			case "debug" -> "Fake packets: " + (fakeCounter * 3 % 97);
			default -> widget.title;
		};
	}

	private static void box(GuiGraphics graphics, int x, int y, int w, int h, int fill, int edge) {
		graphics.fill(x, y, x + w, y + h, edge);
		graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
	}

	public static class HudWidget {
		public final String id;
		public final String title;
		public int x;
		public int y;
		public int w = 76;
		public int h = 15;
		public boolean visible;

		public HudWidget(String id, String title, int x, int y, boolean visible) {
			this.id = id;
			this.title = title;
			this.x = x;
			this.y = y;
			this.visible = visible;
		}

		public boolean contains(double mx, double my) {
			return mx >= x && mx <= x + w && my >= y && my <= y + h;
		}
	}
}
