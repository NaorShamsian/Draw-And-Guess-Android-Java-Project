package com.example.draw_and_guess_naor_shamsian;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Vector;

public class WordsActivity extends Activity {
    private ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);
        words = getIntent().getStringArrayListExtra("words");
        LinearLayout llMain = findViewById(R.id.myL);
        for (int i = 0; i < words.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(words.get(i).toString());
            int dp1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                    getApplicationContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp1 * 60
            );
            textView.setLayoutParams(params);
            textView.setTextSize(23);
            textView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            llMain.addView(textView);
        }


    }

}

