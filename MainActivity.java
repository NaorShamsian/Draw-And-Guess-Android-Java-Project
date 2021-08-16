package com.example.draw_and_guess_naor_shamsian;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {
    private TextView title;
    private Words words;
    private Button registerButton , playButton, wordsButton ,logOutButton;
    private ArrayList<Object> gameObjects = new ArrayList<Object>();
    private BackgroundMusic backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // backgroundMusic = new BackgroundMusic(new Intent(this,BackgroundMusicService.class),R.raw.background_music);
        words = new Words();
        words.insertWordsToDataBase(getApplicationContext());
        registerButton=(Button)findViewById(R.id.registerBtn);
        playButton = (Button) findViewById(R.id.playBtn);
        wordsButton = (Button) findViewById(R.id.wordsBtn);
        logOutButton = (Button)findViewById(R.id.btnLogOut);
        title = (TextView) findViewById(R.id.title);
        initObjectsList();
        startAnimation();
        saveWordsList();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        wordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordsActivity.class);
                intent.putStringArrayListExtra("words",((ArrayList<String>)words.wordsList)); 
                startActivity(intent);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(logOutButton.getVisibility() == View.VISIBLE) {
                    FirebaseAuth.getInstance().signOut();
                    logOutButton.setVisibility(View.GONE);
                }
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        logOutButton.setVisibility(View.VISIBLE);

    }

    public void initObjectsList () {
        gameObjects.add(registerButton);
        gameObjects.add(playButton);
        gameObjects.add(wordsButton);
        gameObjects.add(logOutButton);
        gameObjects.add(title);
    }

    public void startAnimation() {
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.sample_anim);
        Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.bounce);
        for(int i=0;i<gameObjects.size();i++) {
            if(gameObjects.get(i) instanceof  Button) {
                ((Button) gameObjects.get(i)).startAnimation(animation2);
            }
            else {
                if (gameObjects.get(i) instanceof TextView) {
                    ((TextView) gameObjects.get(i)).startAnimation(animation1);
                }
            }

        }

    }
    public void saveWordsList () {
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: words.wordsList) {
            stringBuilder.append(s);
            stringBuilder.append(",");
        }
        SharedPreferences settings = getSharedPreferences("PREFS",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("words",stringBuilder.toString());
        editor.commit();
    }




}

