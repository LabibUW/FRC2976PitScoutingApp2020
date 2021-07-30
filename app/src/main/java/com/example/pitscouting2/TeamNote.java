package com.example.pitscouting2;

import android.util.Log;

public class TeamNote {
    private String name;
    private String number;

    public TeamNote(){

    }

    public TeamNote(String teamname, String teamnumber) {
        this.name = teamname;
        this.number = teamnumber;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

}
