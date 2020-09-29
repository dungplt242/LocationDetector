package com.hfad.locationdetector;

import android.graphics.drawable.Icon;

public class AppOption {

    private Icon icon;
    private String name;

    public AppOption(Icon icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }
}
