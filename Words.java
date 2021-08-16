package com.example.draw_and_guess_naor_shamsian;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Words {
    public static int NUM_OF_OPTIONS_FOR_GUESSER = 8;
    public List<String> wordsList;
    public FirebaseDatabase mDatabase;
    public DatabaseReference words;

    public Words() {
        mDatabase = FirebaseDatabase.getInstance();
        words = mDatabase.getReference("Words");
        wordsList = new ArrayList<String>();
    }

    public void insertWordsToDataBase(final Context context) {
        wordsList.add("chair");
        wordsList.add("bottle");
        wordsList.add("phone");
        wordsList.add("table");
        wordsList.add("cup");
        wordsList.add("leg");
        wordsList.add("monster");
        wordsList.add("computer");
        wordsList.add("child");
        wordsList.add("plane");
        wordsList.add("notebook");
        wordsList.add("sky");
        wordsList.add("clouds");
        wordsList.add("wall");
        wordsList.add("bag");
        wordsList.add("bed");
        wordsList.add("ball");
        wordsList.add("tree");
        wordsList.add("flower");
        wordsList.add("television");
        wordsList.add("floor");
        wordsList.add("basketball");
        wordsList.add("dest");
        wordsList.add("window");
        wordsList.add("lamp");
        wordsList.add("book");
        wordsList.add("umbrella");
        wordsList.add("bicycle");
        wordsList.add("house");
        wordsList.add("moon");
        wordsList.add("sun");
        wordsList.add("shoes");
        wordsList.add("kitchen");
        wordsList.add("chicken");
        wordsList.add("dog");
        wordsList.add("chicken");
        wordsList.add("garden");

        words.setValue(wordsList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(context.getApplicationContext(), "Words uploaded to database", Toast.LENGTH_LONG).show();
            }
        });
    }

    public List<String> return3RandomWords() {
        List<String> cloned_list = new ArrayList<String>(wordsList);
        List<String> temp = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            String element = getRandomElement(cloned_list);
            temp.add(element);
            cloned_list.remove(new String(element));
        }
        return temp;
    }

    public List<String> getOptionsForGuesser(String name) {
        List<String> cloned_list = new ArrayList<String>(wordsList);
        List<String> temp = new ArrayList<String>();
        Random rnd = new Random();
        for (int i = 0; i < NUM_OF_OPTIONS_FOR_GUESSER; i++) {
            String element = getRandomElement(cloned_list,name);
            temp.add(element);
            cloned_list.remove(new String(element));
        }
        temp.set(rnd.nextInt(NUM_OF_OPTIONS_FOR_GUESSER),name);
        return temp;
    }

    public String getRandomElement(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public String getRandomElement(List<String> list, String name) {
        Random rand = new Random();
        int n = rand.nextInt(list.size());
        while (list.get(rand.nextInt(list.size())).equals(name)) {
            n = rand.nextInt(list.size());
        }
        return list.get(n);
    }

}
