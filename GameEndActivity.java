package com.example.draw_and_guess_naor_shamsian;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GameEndActivity extends Activity {
    int myPoints, rivalPoints;
    pl.droidsonroids.gif.GifImageView gifImageView;
    FireBaseGameData fireBaseGameData = new FireBaseGameData();
    int roomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        if (getIntent() != null) {
                roomNumber = getIntent().getIntExtra("game_room", -2);
                gifImageView = findViewById(R.id.resultGif);
                myPoints = getIntent().getIntExtra("my_points", -1);
                rivalPoints = getIntent().getIntExtra("rival_points", -1);
            }



        if (myPoints > rivalPoints)
            gifImageView.setBackgroundResource(R.drawable.winner);
        else if (myPoints < rivalPoints)
            gifImageView.setBackgroundResource(R.drawable.loser);
        else gifImageView.setBackgroundResource(R.drawable.tie);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameEndActivity.this, GameActivity.class);
                intent.putExtra("roomNumber", roomNumber);
                startActivity(intent);
                //finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

//    @Override
//    protected void onPause() {
//        Intent intent = new Intent(GameEndActivity.this, GameActivity.class);
//        intent.putExtra("flag", "A");
//        startActivity(intent);
//        finish();
//        super.onPause();
//    }
}
