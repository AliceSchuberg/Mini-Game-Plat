package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;
import games.P_R_S_Data;
import operations.CreateRoomMsg;
import operations.GameResultMsg;
import operations.GamingData;
import operations.JoinMessage;
import operations.OperationType;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;

public class RockPaperScissor extends AppCompatActivity {
    private String roomID;
    private BroadcastReceiver gameRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 8:
                    GameResultMsg grm = (GameResultMsg) intent.getSerializableExtra("GameResultMsg");
                    switch (grm.getResult()){
                        case 0:
                            findViewById(R.id.btnPaper).setEnabled(false);
                            findViewById(R.id.btnRock).setEnabled(false);
                            findViewById(R.id.btnScissor).setEnabled(false);
                            Toast.makeText(RockPaperScissor.this,
                                    "Input has been recorded, please wait for another player",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            AlertDialog aDialog = new AlertDialog.Builder(RockPaperScissor.this)
                                    .setNegativeButton("Return to Main Menu", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //tell the server the player is exiting the room
                                            Intent nIntent = (new Intent()).setAction("CallService");
                                            nIntent.putExtra("type",new OperationType(8));
                                            nIntent.putExtra("GameResultMsg",new GameResultMsg(2));//2 -> exit the game
                                            sendBroadcast(nIntent);

                                            Intent mIntent = new Intent(getApplicationContext(), MainMenu.class);
                                            startActivity(mIntent);
                                        }
                                    })
                                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //tell the server to restart the game
                                            Intent nIntent = (new Intent()).setAction("CallService");
                                            nIntent.putExtra("type",new OperationType(8));
                                            nIntent.putExtra("GameResultMsg",new GameResultMsg(3));//3 -> restart the game
                                            sendBroadcast(nIntent);

                                            Intent mIntent = new Intent(getApplicationContext(), WaitRoom.class)
                                                    .putExtra("RoomID",roomID);
                                            startActivity(mIntent);
                                        }
                                    }).create();
                            aDialog.setCanceledOnTouchOutside(false);
                            //iterate through the array and check if the player has won
                            boolean found = false;
                            for(String i:grm.getWinners()){
                                if(i.equals(PlayerSingleton.getInstance().getPlayerID())){
                                    found = true;
                                }
                            }
                            if(found){
                                aDialog.setTitle("Congrats! You have won!");
                                aDialog.setMessage("Do you wanna play again or exit to main menu?");
                            }else{
                                aDialog.setTitle("Sad! You lost!");
                                aDialog.setMessage("Do you wanna play again or exit to main menu?");
                            }
                            if(grm.getWinners().length>1){
                                aDialog.setTitle("It is a tie!");
                            }
                            //show the dialog
                            aDialog.show();
                            break;
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rock_paper_scissors);
        roomID = getIntent().getStringExtra("RoomID");
        if(gameRoomReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallGameRoom");
            registerReceiver(gameRoomReceiver,iFilter);
        }
    }

    public void btnOnClick(View view){
        switch (view.getId()){
            case R.id.btnPaper:
                sendBroadcast(prepareIntent("PAPER"));
                break;
            case R.id.btnRock:
                sendBroadcast(prepareIntent("ROCK"));
                break;
            case R.id.btnScissor:
                sendBroadcast(prepareIntent("SCISSOR"));
                break;
        }
    }

    public Intent prepareIntent(String data){
        Intent mIntent = new Intent();
        mIntent.setAction("CallService");
        mIntent.putExtra("type",new OperationType(7));// 7 -> gaming

        P_R_S_Data gd = new P_R_S_Data();
        gd.setMove(data);
        mIntent.putExtra("GamingData",gd);
        return mIntent;
    }
    @Override
    protected void onStop(){
        unregisterReceiver(gameRoomReceiver);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //finish the game first
        Toast.makeText(this,"Please finish the game first.",Toast.LENGTH_LONG).show();
    }
    //image credit: https://freesvg.org/rock-paper-scissors
}