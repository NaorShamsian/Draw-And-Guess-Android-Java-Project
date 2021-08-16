package com.example.draw_and_guess_naor_shamsian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class GameActivity extends Activity {
    private ProgressBar progressBar;
    private FireBaseGameData fireBaseGameData;
    private int countNumOfRooms = 0;
    private int countNumOfPlayersInRoom = 0;
    private String playerName;
    Player user;
    private TextView waitTextView;
    private TextView helloTextView;
    private Button searchGameBtn;
    private boolean isSearching = false; // If user is looking for a match
    boolean startGameAgain = false;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference rooms = mDatabase.getReference("GameRooms");
    DatabaseReference users = mDatabase.getReference("Users");
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        firebaseAuth = FirebaseAuth.getInstance();
        user = new Player(playerName, -1, false); // At this state the user is not logged to any room
        waitTextView = (TextView) findViewById(R.id.waitText);
        helloTextView = (TextView) findViewById(R.id.helloUserText);
        searchGameBtn = (Button) findViewById(R.id.btnStart);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);
        fireBaseGameData = new FireBaseGameData();

        Intent intent = getIntent();
        int roomNumber = intent.getIntExtra("roomNumber", -1);
        if (roomNumber != -1)
            startGameAgain = true;

        getUserNameByUid(new FireBasecallBack() {
            @Override
            public void OnCallback(int value) {

            }

            @Override
            public void OnCallback(String value) {
                user.name = value;
                searchGameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isSearching) {
                            isSearching = false;
                            searchGameBtn.setText("SEARCH FOR MATCH");
                            progressBar.setVisibility(View.GONE);
                            //Stop searching match
                            rooms.child("room " + user.roomNumber).child(FirebaseAuth.getInstance().getUid()).removeValue();
                            user.turnToDraw = false;
                            waitTextView.setText("");

                        } else {
                            isSearching = true;
                            // Starts searching match
                            user.roomNumber = -1;
                            fireBaseGameData.EnterRoomEvent(getApplicationContext(), user, progressBar,startGameAgain);
                            searchGameBtn.setText("STOP SEARCHING MATCH");
                            waitTextView.setVisibility(View.VISIBLE);
                            user.turnToDraw = false;
                            waitTextView.setText("Wating to opponent ...");
                        }
                    }


                });
            }

            @Override
            public void OnCallback(Player value) {

            }
        });

        findViewById(R.id.btnOut).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user != null) {
                            fireBaseGameData.DeletePlayer(user.roomNumber);
                            Intent intent = new Intent(GameActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });


        findViewById(R.id.btnProfile).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GameActivity.this, MyProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }

    // This method find out the username by the current UID of user
    private void getUserNameByUid(final FireBasecallBack fireBasecallBack) {
        users.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                playerName = dataSnapshot.getValue(String.class);
                helloTextView.setText(helloTextView.getText() + playerName);
                fireBasecallBack.OnCallback(playerName);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (user != null) {
            fireBaseGameData.DeletePlayer(user.roomNumber);
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

//    @Override
//    protected void onDestroy() {
//        if(user != null) {
//            fireBaseGameData.DeletePlayer(user.roomNumber);
//            Intent intent = new Intent(GameActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
//        super.onDestroy();
//    }


}

