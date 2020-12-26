package com.nerdytech.bemyvoice.model;

import java.util.ArrayList;
import java.util.List;

public class Favourites {
    List<String> text;

    public Favourites() {
        this.text = new ArrayList<String>();
    }

    public Favourites(List<String> text) {
        this.text = text;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
}
