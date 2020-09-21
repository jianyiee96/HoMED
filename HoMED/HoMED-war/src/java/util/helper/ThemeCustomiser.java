package util.helper;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named(value = "themeCustomiser")
@SessionScoped
public class ThemeCustomiser implements Serializable {

    private String componentTheme = "cyan";
    private String topbarColor = "cyan";

    private final String menuMode = "layout-overlay";
    private final String menuColor = "light";
    private final boolean lightLogo = true;
    private final String inputStyle = "filled";

    public String getComponentTheme() {
        return componentTheme;
    }

    public void setComponentTheme(String componentTheme) {
        this.componentTheme = componentTheme;
    }

    public String getTopbarColor() {
        return topbarColor;
    }

    public void setTopbarColor(String topbarColor) {
        this.topbarColor = topbarColor;
    }

    public String getMenuMode() {
        return menuMode;
    }

    public String getMenuColor() {
        return menuColor;
    }

    public boolean isLightLogo() {
        return lightLogo;
    }

    public String getInputStyle() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

}
