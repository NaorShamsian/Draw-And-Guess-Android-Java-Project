package com.example.draw_and_guess_naor_shamsian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import petrov.kristiyan.colorpicker.ColorPicker;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.UUID;

public class OnGameActivity extends Activity {
    static final int NUM_OF_ROUNDS = 1;
    double currentRound;
    boolean active;
    boolean gameDone = false;
    boolean roundAgain = false;
    int roomNumber;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FireBaseGameData fireBaseGameData;
    Player player, secondPlayer;
    Words words;
    ScrollView oprionsScrollView;
    TextView pointTextView;
    boolean finishDrawing;
    String wordSelected;
    int mDefaultColor;
    ProgressBar progressBar;
    WordGuessed wordGuessed;
    ImageView imageSent;
    LinearLayout actionLayout;
    LinearLayout myLayout;
    LinearLayout optionsLayout, optionsLayout2;
    LinearLayout linearLayoutScrollView;
    PaintView paintView;
    ValueEventListener listener1 = null;
    TextView nameAndStatus, waitText, strokeText, roundText;
    SeekBar seekBar;
    Button sendBtn;
    RadioGroup wordsGroup;
    List<String> randomWords = new ArrayList<String>();
    ChildEventListener listener = null;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference rooms = mDatabase.getReference("GameRooms");
    DatabaseReference masters = mDatabase.getReference("masters");
    DatabaseReference users = mDatabase.getReference("Users");
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_game);
        roomNumber = getIntent().getIntExtra("roomNumberPlayer", 0);
        ref = rooms.child("room " + roomNumber);
        roundAgain = getIntent().getBooleanExtra("gameState",false);
        getNumOfUsers();
        switchPlayersTurn();
        myLayout = (LinearLayout) findViewById(R.id.myL);
        optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
        optionsLayout2 = (LinearLayout) findViewById(R.id.optionsLayout2);
        oprionsScrollView = findViewById(R.id.optionsScrollView);
        roundText = findViewById(R.id.roundTextView);
        actionLayout = findViewById(R.id.actionLayout);
        imageSent = findViewById(R.id.paintSent);
        linearLayoutScrollView = findViewById(R.id.linearLayoutScrollView);
        pointTextView = findViewById(R.id.points);
        mDefaultColor = ContextCompat.getColor(OnGameActivity.this, R.color.colorPrimary);
        strokeText = findViewById(R.id.strokeText);
        progressBar = findViewById(R.id.progress_horizontal);
        seekBar = findViewById(R.id.seekBar);
        fireBaseGameData = new FireBaseGameData();
        firebaseAuth = FirebaseAuth.getInstance();
        wordsGroup = (RadioGroup) findViewById(R.id.radioGroup);
        words = new Words();
        loadWordsList();
        randomWords = words.return3RandomWords();

        // set RadioButtons text
        for (int i = 0; i < wordsGroup.getChildCount(); i++) {
            ((RadioButton) wordsGroup.getChildAt(i)).setText(randomWords.get(i));
        }

        // init word
        wordSelected = randomWords.get(0);
        sendBtn = new Button(this);
        sendBtn.setVisibility(View.VISIBLE);

        getPlayerDetails(new FireBasecallBack() {
            @Override
            public void OnCallback(int value) {

            }

            @Override
            public void OnCallback(String value) {

            }

            @Override
            public void OnCallback(Player value) {
                if (player != null)
                    player = value;
                getOpponentDetails(new FireBasecallBack() {
                    @Override
                    public void OnCallback(int value) {

                    }

                    @Override
                    public void OnCallback(String value) {

                    }

                    @Override
                    public void OnCallback(final Player player2) {
                        if (player != null && player2 != null) {
                            secondPlayer = player2;
                            currentRound = getIntent().getDoubleExtra("round", 1);
                            pointTextView.setText("POINTS: " + player.points
                                    + "\n" + "RIVAL POINTS: " + player2.points);
                            if (((int) currentRound % NUM_OF_ROUNDS) == 0)
                                roundText.setText("ROUND " + NUM_OF_ROUNDS + " / " + NUM_OF_ROUNDS);
                            else
                                roundText.setText("ROUND " + ((int) currentRound % NUM_OF_ROUNDS) + " / " + NUM_OF_ROUNDS);
                            if (((int) currentRound) > NUM_OF_ROUNDS) {
                                Intent intent = new Intent(OnGameActivity.this, GameEndActivity.class);
                                intent.putExtra("my_points", player.points);
                                intent.putExtra("rival_points", player2.points);
                                intent.putExtra("game_room", player.roomNumber);
                                // for second user who lates exiting game
                                rooms.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        masters.child("master "+roomNumber).child("my_points").setValue(player2.points);
                                        masters.child("master "+roomNumber).child("rival_points").setValue(player.points);
                                        masters.child("master "+roomNumber).child("game_room").setValue(player.roomNumber);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                ref.removeEventListener(listener);
                                fireBaseGameData.DeletePlayer(player.roomNumber);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            nameAndStatus = (TextView) findViewById(R.id.nameText);
                            waitText = (TextView) findViewById(R.id.waitToPlayer);
                            Toast.makeText(getApplicationContext(), "check: " + player2.name, Toast.LENGTH_SHORT).show();
                            //OrderPlayersTurn(); // This method gives initial values about the players info. who's drawing and who's guessing
                            if (player == null) {
                                ref.removeEventListener(listener);
                                fireBaseGameData.DeletePlayer(roomNumber);
                                Intent intent = new Intent(OnGameActivity.this, GameEndActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (player.turnToDraw) {
                                    LinearLayout contextL = new LinearLayout(OnGameActivity.this);
                                    contextL.setOrientation(LinearLayout.VERTICAL);
                                    // send button
                                    sendBtn.setText("SEND");
                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150, 1.0f);
                                    sendBtn.setBackgroundResource(R.drawable.custom_button3);
                                    sendBtn.setTextSize(12.0f);
                                    sendBtn.setLayoutParams(params1);
                                    // myLayout.addView(sendBtn);
                                    // paint to draw
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f);
                                    // params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    paintView = new PaintView(getApplicationContext(), displayMetrics);
                                    paintView.setBackgroundColor(Color.WHITE);
                                    params.bottomMargin = 10;
                                    paintView.setLayoutParams(params);
                                    // myLayout.addView(paintView);
                                    contextL.addView(paintView);
                                    contextL.addView(sendBtn);
                                    myLayout.addView(contextL);
                                    seekBar.setVisibility(View.VISIBLE);
                                    strokeText.setVisibility(View.VISIBLE);
                                    actionLayout.setVisibility(View.VISIBLE);
                                    wordsGroup.setVisibility(View.VISIBLE);
                                    sendBtn.setVisibility(View.VISIBLE);
                                    waitText.setVisibility(View.GONE);
                                    nameAndStatus.setText(player.name + " Draw time !");

                                    // paint actions
                                    ImageButton removeBtn, undoBtn, redoBtn, changeColorBtn;
                                    removeBtn = findViewById(R.id.remove);
                                    undoBtn = findViewById(R.id.undo);
                                    redoBtn = findViewById(R.id.redo);
                                    changeColorBtn = findViewById(R.id.changeColor);

                                    removeBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!finishDrawing)
                                                paintView.clear();
                                        }
                                    });
                                    undoBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!finishDrawing)
                                                paintView.undo();
                                        }
                                    });
                                    redoBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!finishDrawing)
                                                paintView.redo();
                                        }
                                    });
                                    changeColorBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!finishDrawing)
                                                openColorPicker();
                                        }
                                    });


                                    sendBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (paintView.paths.size() > 0) {
                                                paintView.setDrawingCacheEnabled(true);
                                                paintView.buildDrawingCache();
                                                Bitmap bitmap = paintView.getDrawingCache();
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                                paintView.setDrawingCacheEnabled(false);
                                                byte[] data = baos.toByteArray();
                                                String path = "room_" + player.roomNumber + "/" + UUID.randomUUID() + ".png";
                                                final StorageReference storageReference = storage.getReference(path);
                                                UploadTask uploadTask = storageReference.putBytes(data);
                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(final Uri uri) {
                                                                Handler handler = new Handler();
                                                                handler.postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        progressBar.setProgress(0);
                                                                        final String url = uri.toString();
                                                                        rooms.child("draws " + player.roomNumber).child("draw").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                wordGuessed = new WordGuessed(url, wordSelected);
                                                                                rooms.child("draws " + player.roomNumber).child("draw").setValue(wordGuessed);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                        Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
                                                                        waitText.setText("WATING TO OPPONENT GUESS..");
                                                                        waitText.setVisibility(View.VISIBLE);

                                                                    }
                                                                }, 1000);
                                                            }
                                                        });
                                                    }
                                                });


                                                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                                        progressBar.setVisibility(View.VISIBLE);
                                                        sendBtn.setVisibility(View.GONE);
                                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                        progressBar.setProgress((int) progress);
                                                        waitText.setVisibility(View.VISIBLE);
                                                        waitText.setText("SENDING DRAW..");
                                                        finishDrawing = true;
                                                        paintView.canDraw = false;
                                                    }
                                                })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressBar.setVisibility(View.GONE);
                                                                sendBtn.setVisibility(View.VISIBLE);
                                                                Toast.makeText(getApplicationContext(), "Error while sending draw", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
//                                        if(player2.wordGuesserGuessed.equals(wordSelected))
//                                            waitText.setText("OPPONENT JUST GUEESED RIGHT");
//                                        else if(player2.wordGuesserGuessed != "")
//                                            waitText.setText("OPPONENT FAILED TO GUEES RIGHT");
                                            } else
                                                Toast.makeText(getApplicationContext(), "Can't send empty draw", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    waitText.setText("WATING TO OPPONET ..");
                                    waitText.setVisibility(View.VISIBLE);
                                    wordsGroup.setVisibility(View.GONE);
                                    sendBtn.setVisibility(View.GONE);
                                    nameAndStatus.setText(player.name + " Guess time !");
                                    rooms.child("draws " + player.roomNumber).child("draw").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                imageSent.setVisibility(View.VISIBLE);
                                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                                int height = displayMetrics.heightPixels;
                                                if (height <= 1800) {
                                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650);
                                                    imageSent.setLayoutParams(params);
                                                    optionsLayout.setPadding(50, 05, 50, 50);
                                                }
                                                waitText.setText("YOUR OPPONENT SENT YOU HIS DRAW, WHAT YOU THINK HE DREW ?");
                                                wordGuessed = dataSnapshot.getValue(WordGuessed.class);
                                                Glide.with(getApplicationContext())
                                                        .load(wordGuessed.url)
                                                        .into(imageSent);
                                                oprionsScrollView.setVisibility(View.VISIBLE);
                                                optionsLayout.setVisibility(View.VISIBLE);
                                                optionsLayout2.setVisibility(View.VISIBLE);

                                                final List<Button> optionsButtons = new ArrayList<Button>();
                                                List<String> options = words.getOptionsForGuesser(wordGuessed.value);
                                                for (int i = 0; i < options.size(); i++) {
                                                    optionsButtons.add(new Button(getApplicationContext()));
                                                    optionsButtons.get(i).setText(options.get(i).toString().trim());
                                                    optionsButtons.get(i).setBackgroundResource(R.drawable.custom_button3);
                                                    if (i % options.size() <= (options.size() - 1) / 2)
                                                        optionsLayout.addView(optionsButtons.get(i));
                                                    else
                                                        optionsLayout2.addView(optionsButtons.get(i));
                                                }
                                                for (int i = 0; i < optionsButtons.size(); i++) {
                                                    final int current = i;

                                                    optionsButtons.get(i).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (wordGuessed.value.equals(optionsButtons.get(current).getText())) {
                                                                Toast.makeText(getApplicationContext(), "Well done " + player.name + ", you guessed right", Toast.LENGTH_SHORT).show();
                                                                player.points += 1;
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Oops .. wrong answer", Toast.LENGTH_SHORT).show();

                                                            }
                                                            player.wordGuesserGuessed = optionsButtons.get(current).getText().toString();
                                                            player.turnToDraw = true;
                                                            player2.turnToDraw = false;
                                                            player.firstRound = false;
                                                            player2.firstRound = false;
                                                            fireBaseGameData.updateDataBasePlayers(player);
                                                            fireBaseGameData.updateDataBasePlayers(player2);


                                                        }
                                                    });
                                                }
//                                finishBtn.setVisibility(View.VISIBLE);
//                                finishBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if (wordGuessed.value.equals(wordGuessed.value)) {
//                                            Toast.makeText(getApplicationContext(), "Well done " + player.name + ", you guessed right", Toast.LENGTH_SHORT).show();
//                                            // switch game rules
//                                            player.points += 1;
//                                            player.turnToDraw = true;
//                                            fireBaseGameData.updateDataBasePlayers(player);
////                                            player2.turnToDraw=false;
////                                            fireBaseGameData.updateDataBasePlayers(player2);
//                                            finish();
//                                            startActivity(getIntent());
//
//                                        } else
//                                            Toast.makeText(getApplicationContext(), "Oops .. wrong answer", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        }
                    }
                });
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                paintView.setStrokeWidth(seekBar.getProgress());
                strokeText.setText("Pen size: " + seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void loadWordsList() {
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        String wordString = settings.getString("words", "");
        String[] itemWords = wordString.split(",");
        for (int i = 0; i < itemWords.length; i++) {
            words.wordsList.add(itemWords[i]);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_one:
                if (checked)
                    wordSelected = ((RadioButton) view).getText().toString();
                break;
            case R.id.radio_two:
                if (checked)
                    wordSelected = ((RadioButton) view).getText().toString();
                break;
            case R.id.radio_three:
                if (checked)
                    wordSelected = ((RadioButton) view).getText().toString();
                break;
        }
    }


    // This method stops the current game because only 1 player left on game
    private void StopGameInRoom() {
        //   fireBaseGameData.DeletePlayer(player);
        AlertDialog.Builder ad = new AlertDialog.Builder(OnGameActivity.this);
        ad.setTitle("Opponent left");
        ad.setMessage("your opponent has left the match, you won");
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                player.turnToDraw = false;
                Intent intent = new Intent(OnGameActivity.this, GameActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        AlertDialog al = ad.create();
        ad.show();


    }


    private void getPlayerDetails(final FireBasecallBack fireBasecallBack) {
        int roomNumber = getIntent().getIntExtra("roomNumberPlayer", 0);
        rooms.child("room " + roomNumber).child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                player = dataSnapshot.getValue(Player.class);
                fireBasecallBack.OnCallback(player);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error while loading player details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOpponentDetails(final FireBasecallBack fireBasecallBack) {
        int roomNumber = getIntent().getIntExtra("roomNumberPlayer", 0);
        rooms.child("room " + roomNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!(ds.getKey().equals(firebaseAuth.getUid()))) {
                        Player p = ds.getValue(Player.class);
                        p.firstRound = false;
                        fireBasecallBack.OnCallback(p);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                mDefaultColor = color;
                paintView.setColor(mDefaultColor);
            }

            @Override
            public void onCancel() {
                // put code
            }
        })

                .setColumns(5)
                .show();
    }

    private void switchPlayersTurn() {
        final int roomNumber = getIntent().getIntExtra("roomNumberPlayer", 0);
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                rooms.child("room " + roomNumber).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            rooms.child("draws " + player.roomNumber).removeValue();
                            getIntent().putExtra("round", currentRound + 0.5);
                            startActivity(getIntent());
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (active && !gameDone) {
                    Player p = dataSnapshot.getValue(Player.class);
                    if (!p.name.equals(player.name)) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(OnGameActivity.this);
                        ad.setTitle("Opponent left");
                        ad.setMessage("your opponent has left the match, you won");
                        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                player.turnToDraw = false;
                                Intent intent = new Intent(OnGameActivity.this, GameActivity.class);
                                startActivity(intent);
                                fireBaseGameData.DeletePlayer(player);
                                //finish();
                            }
                        });
                        Dialog al = ad.create();
                        al.setCanceledOnTouchOutside(false);
                        al.setCancelable(false);
                        al.show();

                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad = new AlertDialog.Builder(OnGameActivity.this);
        ad.setTitle("Are you sure");
        ad.setMessage("if you leave the match. you'll be lost");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!player.firstRound) {
                    player.turnToDraw = false;
                    Intent intent = new Intent(OnGameActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    fireBaseGameData.DeletePlayer(player);
                    player.turnToDraw = false;
                    Intent intent = new Intent(OnGameActivity.this, GameActivity.class);
                    startActivity(intent);
                }
                //finish();

            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog al = ad.create();
        al.show();
        //super.onBackPressed();
    }

    public void getNumOfUsers() {
        final int roomNumber = getIntent().getIntExtra("roomNumberPlayer", 0);
        listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (player != null) {
                    if (!player.firstRound)
                        if (dataSnapshot.getChildrenCount() == 1) {
                            ref.removeEventListener(listener);
                            ref.removeEventListener(listener1);
                            fireBaseGameData.DeletePlayer(roomNumber);
                            Intent intent = new Intent(OnGameActivity.this, GameEndActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else
                            Toast.makeText(OnGameActivity.this,"game doest done yet 1",Toast.LENGTH_SHORT).show();
                }
                else
                {
                        if (dataSnapshot.getChildrenCount() == 0 || dataSnapshot.getChildrenCount() == 1) {
                            masters.child("master " + roomNumber).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    gameDone = true;
                                    Toast.makeText(OnGameActivity.this, "game doesnt done yet 2", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(OnGameActivity.this, GameEndActivity.class);
                                    intent.putExtra("game_room", dataSnapshot.child("game_room").getValue(int.class));
                                    intent.putExtra("my_points", dataSnapshot.child("my_points").getValue(int.class));
                                    intent.putExtra("rival_points", dataSnapshot.child("rival_points").getValue(int.class));
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }

                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        if(roundAgain)
            ref.removeEventListener(listener1);
        else
        ref.addValueEventListener(listener1);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
       ref.addChildEventListener(listener);
       ref.addValueEventListener(listener1);
    }

    @Override
    public void onStop() {
        super.onStop();
        ref.removeEventListener(listener);
        ref.removeEventListener(listener1);
        active = false;
    }

}

