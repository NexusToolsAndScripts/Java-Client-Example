package com.james.blockclickgui.module;

public abstract class ModuleSetting {
	public final String key;
	public final String name;

	protected ModuleSetting(String key, String name) {
		this.key = key;
		this.name = name;
	}

	public abstract String display();

	public abstract String save();

	public abstract void load(String value);

	public abstract void click(int button);

	public static class Bool extends ModuleSetting {
		public boolean value;

		public Bool(String key, String name, boolean value) {
			super(key, name);
			this.value = value;
		}

		@Override
		public String display() {
			return value ? "yes" : "no";
		}

		@Override
		public String save() {
			return Boolean.toString(value);
		}

		@Override
		public void load(String value) {
			this.value = Boolean.parseBoolean(value);
		}

		@Override
		public void click(int button) {
			value = !value;
		}
	}

	public static class Number extends ModuleSetting {
		public int value;
		private final int min;
		private final int max;
		private final int step;

		public Number(String key, String name, int value, int min, int max, int step) {
			super(key, name);
			this.value = value;
			this.min = min;
			this.max = max;
			this.step = step;
		}

		@Override
		public String display() {
			return Integer.toString(value);
		}

		@Override
		public String save() {
			return Integer.toString(value);
		}

		@Override
		public void load(String value) {
			try {
				this.value = clamp(Integer.parseInt(value));
			} catch (NumberFormatException ignored) {
			}
		}

		@Override
		public void click(int button) {
			value = clamp(value + (button == 1 ? -step : step));
		}

		private int clamp(int input) {
			return Math.max(min, Math.min(max, input));
		}
	}

	public static class Mode extends ModuleSetting {
		private final String[] values;
		private int index;

		public Mode(String key, String name, String... values) {
			super(key, name);
			this.values = values;
			this.index = 0;
		}

		@Override
		public String display() {
			return values[index];
		}

		@Override
		public String save() {
			return values[index];
		}

		@Override
		public void load(String value) {
			for (int i = 0; i < values.length; i++) {
				if (values[i].equalsIgnoreCase(value)) {
					index = i;
					return;
				}
			}
		}

		@Override
		public void click(int button) {
			index += button == 1 ? -1 : 1;
			if (index < 0) {
				index = values.length - 1;
			}
			if (index >= values.length) {
				index = 0;
			}
		}
	}
}
