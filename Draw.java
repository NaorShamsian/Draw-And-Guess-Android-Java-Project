package com.example.draw_and_guess_naor_shamsian;

import android.graphics.Path;

public class Draw {

    public int color;
    public int strokeWidth;
    public Path path;

    public Draw(int color, int strokeWidth, Path path) {

        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;

    }

}