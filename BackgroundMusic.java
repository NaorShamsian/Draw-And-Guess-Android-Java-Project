package com.example.draw_and_guess_naor_shamsian;

import android.content.Intent;

public class BackgroundMusic {
    Intent serviceIntent;
    String musicURL;

    public BackgroundMusic(Intent serviceIntent , String musicURL) {
        this.serviceIntent = serviceIntent;
        this.musicURL=musicURL;
    }
}
