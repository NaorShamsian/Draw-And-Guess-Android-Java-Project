package com.example.draw_and_guess_naor_shamsian;



public class Player {
    public int roomNumber;
    public String name;
    public int points;
    public boolean firstRound;
    public boolean turnToDraw;
    public String uid;
    public String wordGuesserGuessed;

    public Player () {}
    public Player(String name ,int roomNumber,boolean firstRound) {
        this.name = name;
        this.roomNumber=roomNumber;
        this.firstRound=firstRound;
    }


}
