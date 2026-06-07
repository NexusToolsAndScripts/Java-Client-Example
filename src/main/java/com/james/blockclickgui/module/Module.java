package com.james.blockclickgui.module;

import com.james.blockclickgui.config.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Module {
	public final String name;
	public final String desc;
	public final ModuleCategory category;
	public final List<ModuleSetting> settings = new ArrayList<>();
	public boolean enabled;
	public boolean settingsOpen;
	private Runnable action;

	public Module(String name, String desc, ModuleCategory category) {
		this.name = name;
		this.desc = desc;
		this.category = category;
	}

	public String id() {
		return category.name().toLowerCase() + "." + name.toLowerCase().replace(" ", "_");
	}

	public Module setting(ModuleSetting setting) {
		settings.add(setting);
		return this;
	}

	public Module action(Runnable action) {
		this.action = action;
		return this;
	}

	public boolean hasAction() {
		return action != null;
	}

	public void leftClick() {
		if (action != null) {
			action.run();
		} else {
			toggle();
		}
	}

	public void loadSettings() {
		for (ModuleSetting setting : settings) {
			String saved = GuiConfig.setting(id(), setting.key);
			if (saved != null) {
				setting.load(saved);
			}
		}
	}

	public void clickSetting(ModuleSetting setting, int button) {
		setting.click(button);
		GuiConfig.setting(id(), setting.key, setting.save());
		GuiConfig.save();
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;
		GuiConfig.data.modules.put(id(), enabled);
		GuiConfig.save();

		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			String state = enabled ? "on" : "off";
			mc.player.displayClientMessage(Component.literal(name + " toggled " + state), false);
		}
	}

	public void toggle() {
		setEnabled(!enabled);
	}
}
