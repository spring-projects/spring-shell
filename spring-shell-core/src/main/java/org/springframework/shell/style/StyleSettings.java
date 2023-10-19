/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.style;

/**
 * Base class defining a settings for styles.
 *
 * @author Janne Valkealahti
 */
public abstract class StyleSettings {

	/**
	 * Represents some arbitrary {@code title}.
	 */
	public final static String TAG_TITLE = "style-title";

	/**
	 * Represents some arbitrary {@code value}.
	 */
	public final static String TAG_VALUE = "style-value";

	/**
	 * Styling for keys or names in a lists:
	 * <list key1> : list value1
	 * <list key2> : list value2
	 */
	public final static String TAG_LIST_KEY = "style-list-key";

	/**
	 * Styling for keys or names in a lists:
	 * list key1 : <list value1>
	 * list key2 : <list value2>
	 */
	public final static String TAG_LIST_VALUE = "style-list-value";

	/**
	 * Styling for some arbitrary content indicating {@code INFO} level.
	 */
	public final static String TAG_LEVEL_INFO = "style-level-info";

	/**
	 * Styling for some arbitrary content indicating {@code WARN} level.
	 */
	public final static String TAG_LEVEL_WARN = "style-level-warn";

	/**
	 * Styling for some arbitrary content indicating {@code ERROR} level.
	 */
	public final static String TAG_LEVEL_ERROR = "style-level-error";

	/**
	 * Styling for something i.e. in selectors when item is selectable.
	 */
	public final static String TAG_ITEM_ENABLED = "style-item-enabled";

	/**
	 * Styling for something i.e. in selectors when item can't be selected.
	 */
	public final static String TAG_ITEM_DISABLED = "style-item-disabled";

	/**
	 * Styling for something i.e. in selectors when item is selected.
	 */
	public final static String TAG_ITEM_SELECTED = "style-item-selected";

	/**
	 * Styling for something i.e. in selectors when item is not selected.
	 */
	public final static String TAG_ITEM_UNSELECTED = "style-item-unselected";

	/**
	 * Styling for selector i.e. arrow in selectors.
	 */
	public final static String TAG_ITEM_SELECTOR = "style-item-selector";

	/**
	 * Styling for something which is highlighted.
	 */
	public final static String TAG_HIGHLIGHT = "style-highlight";

	/**
	 * Styling for something which has a background.
	 */
	public final static String TAG_BACKGROUND = "style-background";

	/**
	 * Styling for dialog background.
	 */
	public final static String TAG_DIALOG_BACKGROUND = "style-dialog-background";

	/**
	 * Styling for button background.
	 */
	public final static String TAG_BUTTON_BACKGROUND = "style-button-background";

	/**
	 * Styling for menubar background.
	 */
	public final static String TAG_MENUBAR_BACKGROUND = "style-menubar-background";

	/**
	 * Styling for statusbar background.
	 */
	public final static String TAG_STATUSBAR_BACKGROUND = "style-statusbar-background";

	public String title() {
		return "bold";
	}

	public String value() {
		return "fg:blue";
	}

	public String listKey() {
		return "default";
	}

	public String listValue() {
		return "bold,fg:green";
	}

	public String listLevelInfo() {
		return "fg:green";
	}

	public String listLevelWarn() {
		return "fg:yellow";
	}

	public String listLevelError() {
		return "fg:red";
	}

	public String itemEnabled() {
		return "bold";
	}

	public String itemDisabled() {
		return "faint";
	}

	public String itemSelected() {
		return "fg:green";
	}

	public String itemUnselected() {
		return "bold";
	}

	public String itemSelector() {
		return "bold,fg:bright-cyan";
	}

	public String highlight() {
		return "bold";
	}

	public String background() {
		return "default";
	}

	public String dialogBackground() {
		return "default";
	}

	public String buttonBackground() {
		return "default";
	}

	public String menubarBackground() {
		return "default";
	}

	public String statusbarBackground() {
		return "default";
	}

	/**
	 * Resolve a theme setting from a given tag.
	 *
	 * @param tag the tag
	 * @return a theme setting
	 */
	public String resolveTag(String tag) {
		switch (tag) {
			case TAG_TITLE:
				return title();
			case TAG_VALUE:
				return value();
			case TAG_LIST_KEY:
				return listKey();
			case TAG_LIST_VALUE:
				return listValue();
			case TAG_LEVEL_INFO:
				return listLevelInfo();
			case TAG_LEVEL_WARN:
				return listLevelWarn();
			case TAG_LEVEL_ERROR:
				return listLevelError();
			case TAG_ITEM_ENABLED:
				return itemEnabled();
			case TAG_ITEM_DISABLED:
				return itemDisabled();
			case TAG_ITEM_SELECTED:
				return itemSelected();
			case TAG_ITEM_UNSELECTED:
				return itemUnselected();
			case TAG_ITEM_SELECTOR:
				return itemSelector();
			case TAG_HIGHLIGHT:
				return highlight();
			case TAG_BACKGROUND:
				return background();
			case TAG_DIALOG_BACKGROUND:
				return dialogBackground();
			case TAG_BUTTON_BACKGROUND:
				return buttonBackground();
			case TAG_MENUBAR_BACKGROUND:
				return menubarBackground();
			case TAG_STATUSBAR_BACKGROUND:
				return statusbarBackground();
		}
		throw new IllegalArgumentException(String.format("Unknown tag '%s'", tag));
	}

	/**
	 * Creates an instance of a default settings.
	 *
	 * @return a default theme settings
	 */
	public static StyleSettings defaults() {
		return new DefaultStyleSettings();
	}

	public static StyleSettings dump() {
		return new DumpStyleSettings();
	}

	/**
	 * Gets all tags.
	 *
	 * @return array of all tags
	 */
	public static String[] tags() {
		return new String[] {
				TAG_TITLE,
				TAG_VALUE,
				TAG_LIST_KEY,
				TAG_LIST_VALUE,
				TAG_LEVEL_INFO,
				TAG_LEVEL_WARN,
				TAG_LEVEL_ERROR,
				TAG_ITEM_ENABLED,
				TAG_ITEM_DISABLED,
				TAG_ITEM_SELECTED,
				TAG_ITEM_UNSELECTED,
				TAG_ITEM_SELECTOR,
				TAG_HIGHLIGHT,
				TAG_BACKGROUND,
				TAG_DIALOG_BACKGROUND,
				TAG_BUTTON_BACKGROUND,
				TAG_MENUBAR_BACKGROUND,
				TAG_STATUSBAR_BACKGROUND
		};
	}

	private static class DefaultStyleSettings extends StyleSettings {
	}

	private static class DumpStyleSettings extends StyleSettings {

		@Override
		public String title() {
			return "default";
		}

		@Override
		public String value() {
			return "default";
		}

		@Override
		public String listKey() {
			return "default";
		}

		@Override
		public String listValue() {
			return "default";
		}

		@Override
		public String listLevelInfo() {
			return "default";
		}

		@Override
		public String listLevelWarn() {
			return "default";
		}

		@Override
		public String listLevelError() {
			return "default";
		}

		@Override
		public String itemEnabled() {
			return "default";
		}

		@Override
		public String itemDisabled() {
			return "default";
		}

		@Override
		public String itemSelected() {
			return "default";
		}

		@Override
		public String itemUnselected() {
			return "default";
		}

		@Override
		public String itemSelector() {
			return "default";
		}

		@Override
		public String highlight() {
			return "default";
		}
	}
}
