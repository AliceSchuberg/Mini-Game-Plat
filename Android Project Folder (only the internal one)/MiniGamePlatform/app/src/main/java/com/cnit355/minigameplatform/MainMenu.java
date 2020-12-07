package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void MoveToCreateRoom (View view){
        Intent mIntent = new Intent(this, CreateRoom.class);
        startActivity(mIntent);
    }

    public void MoveToFindRoom (View view){
        Intent mIntent = new Intent(this, FindPublicRoom.class);
        startActivity(mIntent);
    }

    public void MoveToSearchRoom (View view){
        Intent mIntent = new Intent(this, SearchRoom.class);
        startActivity(mIntent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    //image free license: Photo by Philippe Donn from Pexels
}