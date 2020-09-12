package util.helper;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named(value = "themeCustomiser")
@SessionScoped
public class ThemeCustomiser implements Serializable {

    // The login page is customised to "Deep Purple" theme, feel free to change.
    private String componentTheme = "deeppurple";
    private String topbarColor = "purple";
    private String menuColor = "light";

    public String getComponentTheme() {
        return componentTheme;
    }

    // Perform theming based on access rights here, e.g., if admin, then set to bluegrey.
    public void setComponentTheme(String componentTheme) {
        this.componentTheme = componentTheme;
    }

    public String getTopbarColor() {
        return topbarColor;
    }

    public void setTopbarColor(String topbarColor) {
        this.topbarColor = topbarColor;
    }

    public String getMenuColor() {
        return menuColor;
    }

    public void setMenuColor(String menuColor) {
        this.menuColor = menuColor;
    }

}
