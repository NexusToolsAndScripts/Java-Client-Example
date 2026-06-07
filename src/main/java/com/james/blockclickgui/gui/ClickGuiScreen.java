package com.james.blockclickgui.gui;

import com.james.blockclickgui.config.GuiConfig;
import com.james.blockclickgui.module.Module;
import com.james.blockclickgui.module.ModuleCategory;
import com.james.blockclickgui.module.ModuleManager;
import com.james.blockclickgui.module.ModuleSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
	private static final int PANEL_W = 142;
	private static final int HEADER_H = 18;
	private static final int ROW_H = 16;
	private static final int SET_H = 14;

	private final List<Panel> panels = new ArrayList<>();
	private Panel grabbed;
	private int grabX;
	private int grabY;

	public ClickGuiScreen() {
		super(Component.literal("Click GUI"));
	}

	@Override
	protected void init() {
		panels.clear();
		Map<ModuleCategory, List<Module>> byCat = ModuleManager.byCategory();
		int x = 18;
		int y = 24;

		for (ModuleCategory category : ModuleCategory.values()) {
			List<Module> modules = byCat.get(category);
			if (modules == null || modules.isEmpty()) {
				continue;
			}

			GuiConfig.PanelSpot spot = GuiConfig.panel(category, x, y);
			panels.add(new Panel(category, modules, spot.x, spot.y, spot.open));
			x += PANEL_W + 8;
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, width, height, 0x66000000);
		graphics.drawString(font, "Click GUI", 8, 7, 0xFFFFFFFF, false);
		graphics.drawString(font, "credit: jamie.local", 8, height - 14, 0xFFAAAAAA, false);

		for (Panel panel : panels) {
			drawPanel(graphics, panel, mouseX, mouseY);
		}

		super.render(graphics, mouseX, mouseY, delta);
	}

	private void drawPanel(GuiGraphics graphics, Panel p, int mouseX, int mouseY) {
		int h = p.height();
		graphics.fill(p.x - 1, p.y - 1, p.x + PANEL_W + 1, p.y + h + 1, 0xFF050505);
		graphics.fill(p.x, p.y, p.x + PANEL_W, p.y + HEADER_H, p.category.color);
		graphics.drawString(font, p.category.title, p.x + 5, p.y + 5, 0xFFFFFFFF, false);
		graphics.drawString(font, p.open ? "-" : "+", p.x + PANEL_W - 12, p.y + 5, 0xFFFFFFFF, false);

		if (!p.open) {
			return;
		}

		int rowY = p.y + HEADER_H;
		for (Module module : p.modules) {
			boolean hover = mouseX >= p.x && mouseX <= p.x + PANEL_W && mouseY >= rowY && mouseY <= rowY + ROW_H;
			int bg = module.enabled ? 0xFF2F7F55 : 0xFF181818;
			if (hover) {
				bg = module.enabled ? 0xFF3C9A6A : 0xFF272727;
			}

			graphics.fill(p.x, rowY, p.x + PANEL_W, rowY + ROW_H, bg);
			graphics.fill(p.x, rowY + ROW_H - 1, p.x + PANEL_W, rowY + ROW_H, 0xFF090909);
			graphics.drawString(font, fit(module.name, 82), p.x + 5, rowY + 4, 0xFFFFFFFF, false);

			String on = module.hasAction() ? "OPEN" : (module.enabled ? "ON" : "OFF");
			if (!module.settings.isEmpty()) {
				graphics.drawString(font, module.settingsOpen ? "v" : ">", p.x + PANEL_W - 13, rowY + 4, 0xFFD8D8D8, false);
			}
			int statusRightPad = module.settings.isEmpty() ? 5 : 18;
			graphics.drawString(font, on, p.x + PANEL_W - font.width(on) - statusRightPad, rowY + 4, 0xFFD8D8D8, false);
			rowY += ROW_H;

			if (module.settingsOpen) {
				for (ModuleSetting setting : module.settings) {
					boolean setHover = mouseX >= p.x + 4 && mouseX <= p.x + PANEL_W - 4 && mouseY >= rowY && mouseY <= rowY + SET_H;
					graphics.fill(p.x + 4, rowY, p.x + PANEL_W - 4, rowY + SET_H, setHover ? 0xFF262626 : 0xFF141414);
					graphics.drawString(font, fit(setting.name, 74), p.x + 9, rowY + 3, 0xFFCCCCCC, false);
					String value = fit(setting.display(), 48);
					graphics.drawString(font, value, p.x + PANEL_W - font.width(value) - 9, rowY + 3, 0xFFFFFFFF, false);
					rowY += SET_H;
				}
			}
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();

		for (int i = panels.size() - 1; i >= 0; i--) {
			Panel p = panels.get(i);

			if (p.header(mouseX, mouseY)) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					grabbed = p;
					grabX = (int) mouseX - p.x;
					grabY = (int) mouseY - p.y;
					panels.remove(p);
					panels.add(p);
					return true;
				}

				if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					p.open = !p.open;
					savePanel(p);
					return true;
				}
			}

			if (p.open) {
				int rowY = p.y + HEADER_H;
				for (Module module : p.modules) {
					if (inside(mouseX, mouseY, p.x, rowY, PANEL_W, ROW_H)) {
						if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
							module.leftClick();
							return true;
						}

						if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !module.settings.isEmpty()) {
							module.settingsOpen = !module.settingsOpen;
							return true;
						}
					}

					rowY += ROW_H;

					if (module.settingsOpen) {
						for (ModuleSetting setting : module.settings) {
							if (inside(mouseX, mouseY, p.x + 4, rowY, PANEL_W - 8, SET_H)) {
								module.clickSetting(setting, button);
								return true;
							}
							rowY += SET_H;
						}
					}
				}
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();

		if (grabbed != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			grabbed.x = clamp((int) mouseX - grabX, 2, Math.max(2, width - PANEL_W - 2));
			grabbed.y = clamp((int) mouseY - grabY, 2, Math.max(2, height - HEADER_H - 2));
			return true;
		}

		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (grabbed != null) {
			savePanel(grabbed);
			grabbed = null;
			return true;
		}

		return super.mouseReleased(event);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			onClose();
			return true;
		}

		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		for (Panel panel : panels) {
			savePanel(panel);
		}
		GuiConfig.save();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void savePanel(Panel panel) {
		GuiConfig.PanelSpot spot = GuiConfig.panel(panel.category, panel.x, panel.y);
		spot.x = panel.x;
		spot.y = panel.y;
		spot.open = panel.open;
		GuiConfig.save();
	}

	private int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private boolean inside(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx <= x + w && my >= y && my <= y + h;
	}

	private String fit(String text, int maxWidth) {
		if (font.width(text) <= maxWidth) {
			return text;
		}

		String trimmed = text;
		while (trimmed.length() > 1 && font.width(trimmed + ".") > maxWidth) {
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed + ".";
	}

	private static class Panel {
		final ModuleCategory category;
		final List<Module> modules;
		int x;
		int y;
		boolean open;

		Panel(ModuleCategory category, List<Module> modules, int x, int y, boolean open) {
			this.category = category;
			this.modules = modules;
			this.x = x;
			this.y = y;
			this.open = open;
		}

		int height() {
			if (!open) {
				return HEADER_H;
			}

			int h = HEADER_H;
			for (Module module : modules) {
				h += ROW_H;
				if (module.settingsOpen) {
					h += module.settings.size() * SET_H;
				}
			}
			return h;
		}

		boolean header(double mx, double my) {
			return mx >= x && mx <= x + PANEL_W && my >= y && my <= y + HEADER_H;
		}

	}
}
