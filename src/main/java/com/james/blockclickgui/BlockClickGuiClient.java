package com.james.blockclickgui;

import com.james.blockclickgui.config.GuiConfig;
import com.james.blockclickgui.gui.ClickGuiScreen;
import com.james.blockclickgui.gui.HudEditorScreen;
import com.james.blockclickgui.gui.HudManager;
import com.james.blockclickgui.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockClickGuiClient implements ClientModInitializer {
	public static final String MOD_ID = "clientui";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath(MOD_ID, "gui")
	);

	private static final KeyMapping OPEN_CLICK_GUI = KeyBindingHelper.registerKeyBinding(new KeyMapping(
		"key.clientui.open_gui",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_RIGHT_SHIFT,
		KEY_CATEGORY
	));

	private static final KeyMapping OPEN_HUD_EDITOR = KeyBindingHelper.registerKeyBinding(new KeyMapping(
		"key.clientui.open_hud_editor",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_BACKSLASH,
		KEY_CATEGORY
	));

	@Override
	public void onInitializeClient() {
		ModuleManager.makeModules();
		GuiConfig.load();
		ModuleManager.loadSavedStates();
		HudManager.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			HudManager.tick();

			while (OPEN_CLICK_GUI.consumeClick()) {
				client.setScreen(new ClickGuiScreen());
			}

			while (OPEN_HUD_EDITOR.consumeClick()) {
				client.setScreen(new HudEditorScreen());
			}
		});
	}
}
