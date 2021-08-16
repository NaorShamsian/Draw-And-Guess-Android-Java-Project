package com.example.draw_and_guess_naor_shamsian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;

public class FireBaseGameData  {
    public int countNumOfRooms;
    public FirebaseDatabase mDatabase;
    public DatabaseReference rooms;
    public DatabaseReference users;

    public FireBaseGameData ()
    {
         countNumOfRooms = 0;
         mDatabase = FirebaseDatabase.getInstance();
         rooms = mDatabase.getReference("GameRooms");
         users = mDatabase.getReference("Users");
    }
    private void enterToGameIntent (final Context context, final int roomID, final Player user, final ProgressBar progressBar, final boolean gameState) {
        rooms.child("room "+roomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                Toast.makeText(context, "Action room " + user.roomNumber+": "+count+" players", Toast.LENGTH_SHORT).show();


                if(count == 2)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent (context,OnGameActivity.class);
                   // intent.putExtra("playerName",user.name);
                    intent.putExtra("roomNumberPlayer",user.roomNumber);
                    intent.putExtra("firstRound",true);
                    intent.putExtra("turnToDraw",user.turnToDraw);
                    intent.putExtra("gameState",gameState);
                    intent.putExtra("uid",user.uid);
                    if(user.firstRound)
                        intent.putExtra("round",1);
                   // if(dataSnapshot.child(FirebaseAuth.getInstance().getUid()).child("firstRound").getValue(Boolean.class) == true)
                  //  updateDataBasePlayers(user);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    //((Activity) context).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void EnterRoomEvent (final Context context , final Player user, final ProgressBar progressBar, final boolean gameState) {
        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                int roomID = 0;
                boolean breaked = false;
                boolean exsit = false;
                countNumOfRooms = (int) dataSnapshot.getChildrenCount();
                for(int i=0;i<=countNumOfRooms;i++) {
                    if(dataSnapshot.hasChild("room "+i)) {
                        roomID = i;
                        break;
                    }
                }
                Toast.makeText(context, countNumOfRooms + " rooms", Toast.LENGTH_LONG).show();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!exsit) {
                        if (ds.hasChild(FirebaseAuth.getInstance().getUid()))
                            exsit = true;

                        if ((int) ds.getChildrenCount() <= 1 && !exsit) {
                            Toast.makeText(context , "room " + roomID+": "+(int)ds.getChildrenCount()+" players", Toast.LENGTH_LONG).show();
                            user.roomNumber = roomID;
                            user.firstRound = true;
                            user.uid = FirebaseAuth.getInstance().getUid();
                            if((int) ds.getChildrenCount() == 1)
                                user.turnToDraw = true;
                            rooms.child("room " + roomID).child(FirebaseAuth.getInstance().getUid()).setValue(user);
                            enterToGameIntent(context,roomID,user,progressBar,gameState);
                            breaked = true;
                            break;
                        }
                        roomID++;
                    }
                }
                if (!breaked && !exsit) {
                    user.roomNumber = roomID;
                    user.firstRound = true;
                    user.uid = FirebaseAuth.getInstance().getUid();
                    rooms.child("room " + roomID).child(FirebaseAuth.getInstance().getUid()).setValue(user);
                    enterToGameIntent(context,roomID,user,progressBar,gameState);
                }
                //fireBasecallBack.OnCallback(countNumOfRooms);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }


    public void DeletePlayer (final Player user) {
        rooms.child("room " + user.roomNumber).child(user.uid).removeValue();
        rooms.child("draws "+ user.roomNumber).child("draw").removeValue();
        user.roomNumber = -1;
    }
    public void DeletePlayer (int roomNumber) {
        rooms.child("room " + roomNumber).child(FirebaseAuth.getInstance().getUid()).removeValue();
        rooms.child("draws "+ roomNumber).child("draw").removeValue();
    }
    public void updateDataBasePlayers(Player player) {
        rooms.child("room " + player.roomNumber).child(player.uid).setValue(player);
    }

}
