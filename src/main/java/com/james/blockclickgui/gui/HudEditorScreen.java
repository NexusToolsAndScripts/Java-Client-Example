package com.james.blockclickgui.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class HudEditorScreen extends Screen {
	private HudManager.HudWidget grabbed;
	private int grabX;
	private int grabY;

	public HudEditorScreen() {
		super(Component.literal("HUD Editor"));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, width, height, 0x77000000);
		graphics.drawString(font, "HUD Editor", 8, 8, 0xFFFFFFFF, false);
		graphics.drawString(font, "credit: jamie.local", 8, height - 14, 0xFFAAAAAA, false);

		for (HudManager.HudWidget widget : HudManager.widgets()) {
			HudManager.drawWidget(graphics, widget, true, widget == grabbed || widget.contains(mouseX, mouseY));
		}

		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		HudManager.HudWidget widget = HudManager.widgetAt(event.x(), event.y());
		if (widget == null) {
			return super.mouseClicked(event, doubleClick);
		}

		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			widget.visible = !widget.visible;
			HudManager.save(widget);
			return true;
		}

		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			grabbed = widget;
			grabX = (int) event.x() - widget.x;
			grabY = (int) event.y() - widget.y;
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (grabbed != null && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			grabbed.x = clamp((int) event.x() - grabX, 2, Math.max(2, width - grabbed.w - 2));
			grabbed.y = clamp((int) event.y() - grabY, 2, Math.max(2, height - grabbed.h - 2));
			return true;
		}

		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (grabbed != null) {
			HudManager.save(grabbed);
			grabbed = null;
			return true;
		}

		return super.mouseReleased(event);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT || event.key() == GLFW.GLFW_KEY_BACKSLASH) {
			onClose();
			return true;
		}

		return super.keyPressed(event);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
