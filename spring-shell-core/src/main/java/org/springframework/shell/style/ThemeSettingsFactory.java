package org.springframework.shell.style;

public class ThemeSettingsFactory {


    /**
     * Creates an instance of a default settings.
     *
     * @return a default theme settings
     */
    public static ThemeSettings themeSettings() {
        return new DefaultThemeSettings();
    }
}
