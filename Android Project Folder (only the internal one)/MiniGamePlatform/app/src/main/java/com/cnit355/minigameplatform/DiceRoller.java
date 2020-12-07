package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import games.D_R_Data;
import games.P_R_S_Data;
import operations.GameResultMsg;
import operations.OperationType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DiceRoller extends AppCompatActivity {
    private String roomID;
    private String playerID;
    private int playerNum;

    private BroadcastReceiver gameRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            assert ot != null;
            switch (ot.getType()){
                case 8:
                    GameResultMsg grm = (GameResultMsg) intent.getSerializableExtra("GameResultMsg");
                    switch (grm.getResult()){
                        case 0:
                            D_R_Data drData = (D_R_Data) grm.getProgress();
                            displayCard(drData);
                            break;
                        case 1:
                            findViewById(R.id.btnDiceRoll).setVisibility(Button.INVISIBLE);
                            findViewById(R.id.btnDiceExit).setVisibility(Button.VISIBLE);
                            findViewById(R.id.btnDiceRePlay).setVisibility(Button.VISIBLE);
                            TextView txtResult = findViewById(R.id.txtDiceResult);
                            //iterate through the array and check if the player has won
                            boolean found = false;
                            for(String i:grm.getWinners()){
                                if(i.equals(PlayerSingleton.getInstance().getPlayerID())){
                                    found = true;
                                }
                            }
                            if(found){
                                txtResult.setText("Congrats! You have won!");
                            }else{
                                txtResult.setText("Sad! You lost!");
                                txtResult.append(String.format("\nWinners: %s", Arrays.toString(grm.getWinners())));
                            }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_roller);
        //register the game room receiver to receive broadcast
        roomID = getIntent().getStringExtra("RoomID");
        if(gameRoomReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallGameRoom");
            registerReceiver(gameRoomReceiver,iFilter);
        }

        playerID = PlayerSingleton.getInstance().getPlayerID();

        //set return button invisible to players
        findViewById(R.id.btnDiceExit).setVisibility(Button.INVISIBLE);
        findViewById(R.id.btnDiceRePlay).setVisibility(Button.INVISIBLE);

        //prepare the user's layout
        TextView txtPlayer = findViewById(R.id.txtDicePlayer);
        txtPlayer.setText(playerID);

        //retrieve data and determine how many players needed to be displayed
        Intent mIntent = getIntent();
        playerNum = mIntent.getIntExtra("NumOfPlayer",2);
        ArrayList<String> playList = mIntent.getStringArrayListExtra("PlayerList");
        playList.remove(playerID);
        switch (playerNum){
            case 2:
                displayPlayer(LinearLayout.INVISIBLE,LinearLayout.VISIBLE,LinearLayout.INVISIBLE);
                displayPlayerID(R.id.txtDicePlayer2,playList.get(0));
                break;
            case 3:
                displayPlayer(LinearLayout.VISIBLE,LinearLayout.INVISIBLE,LinearLayout.VISIBLE);
                displayPlayerID(R.id.txtDicePlayer1,playList.get(0));
                displayPlayerID(R.id.txtDicePlayer3,playList.get(1));
                break;
            case 4:
                displayPlayer(LinearLayout.VISIBLE,LinearLayout.VISIBLE,LinearLayout.VISIBLE);
                displayPlayerID(R.id.txtDicePlayer1,playList.get(0));
                displayPlayerID(R.id.txtDicePlayer2,playList.get(1));
                displayPlayerID(R.id.txtDicePlayer3,playList.get(2));
                break;
        }
    }

    public void displayPlayer(int one, int two, int three){
        LinearLayout lLayout1 = findViewById(R.id.LinearLayoutDice1);
        LinearLayout lLayout2 = findViewById(R.id.LinearLayoutDice2);
        LinearLayout lLayout3 = findViewById(R.id.LinearLayoutDice3);

        lLayout1.setVisibility(one);
        lLayout2.setVisibility(two);
        lLayout3.setVisibility(three);
    }

    public void displayPlayerID(int viewID, String id){
        TextView txtView = findViewById(viewID);
        txtView.setText(id);
    }

    public void displayCard(D_R_Data d_r_data){
        HashMap<String, Integer> progress = d_r_data.getProgress();
        //depends on different num of players, set images differently
        TextView txtDicePlayer = findViewById(R.id.txtDicePlayer);
        TextView txtDicePlayer1 = findViewById(R.id.txtDicePlayer1);
        TextView txtDicePlayer2 = findViewById(R.id.txtDicePlayer2);
        TextView txtDicePlayer3 = findViewById(R.id.txtDicePlayer3);
        switch(playerNum){
            case 2:
                for (HashMap.Entry<String, Integer> entry: progress.entrySet()){
                    if(entry.getKey().equals(txtDicePlayer.getText().toString())){
                        setCard(R.id.imgDicePlayer,(entry.getValue()!=null)?entry.getValue():0);
                    }else{
                        setCard(R.id.imgDicePlayer2,(entry.getValue()!=null)?entry.getValue():0);
                    }
                }
                break;
            case 3: //same logic for case 4, if the entry key == textView, update its correspondent imgView
                for (HashMap.Entry<String, Integer> entry: progress.entrySet()){
                    if(entry.getKey().equals(txtDicePlayer.getText().toString())){
                        setCard(R.id.imgDicePlayer,(entry.getValue()!=null)?entry.getValue():0);
                    }else if(entry.getKey().equals(txtDicePlayer1.getText().toString())){
                        setCard(R.id.imgDicePlayer1,(entry.getValue()!=null)?entry.getValue():0);
                    }else if(entry.getKey().equals(txtDicePlayer3.getText().toString())) {
                        setCard(R.id.imgDicePlayer3,(entry.getValue()!=null)?entry.getValue():0);
                    }
                }
                break;
            case 4:
                for (HashMap.Entry<String, Integer> entry: progress.entrySet()){
                    if(entry.getKey().equals(txtDicePlayer.getText().toString())){
                        setCard(R.id.imgDicePlayer,(entry.getValue()!=null)?entry.getValue():0);
                    }else if(entry.getKey().equals(txtDicePlayer1.getText().toString())){
                        setCard(R.id.imgDicePlayer1,(entry.getValue()!=null)?entry.getValue():0);
                    }else if(entry.getKey().equals(txtDicePlayer2.getText().toString())) {
                        setCard(R.id.imgDicePlayer2,(entry.getValue()!=null)?entry.getValue():0);
                    }else if(entry.getKey().equals(txtDicePlayer3.getText().toString())) {
                        setCard(R.id.imgDicePlayer3,(entry.getValue()!=null)?entry.getValue():0);
                    }
                }
                break;
        }
    }

    public void setCard(int viewID, int card){
        ImageView imgPlayer = findViewById(viewID);
        Drawable myDrawable = ResourcesCompat.getDrawable(getResources(), findCard(card), null);
        imgPlayer.setImageDrawable(myDrawable);
    }

    public int findCard(int i){
        switch (i){
            case 0:
                return R.drawable.question;
            case 1:
                return R.drawable.one;
            case 2:
                return R.drawable.two;
            case 3:
                return R.drawable.three;
            case 4:
                return R.drawable.four;
            case 5:
                return R.drawable.five;
            case 6:
                return R.drawable.six;
        }
        return R.drawable.question;
    }

    public void btnDiceRollOnClick(View view){
        Intent mIntent = new Intent();
        mIntent.setAction("CallService");
        mIntent.putExtra("type",new OperationType(7));// 7 -> gaming

        D_R_Data gd = new D_R_Data();
        mIntent.putExtra("GamingData",gd);
        sendBroadcast(mIntent);

        Button btnRoll = findViewById(R.id.btnDiceRoll);
        btnRoll.setEnabled(false);
        Toast.makeText(this,"Request has been processed, please wait.",Toast.LENGTH_SHORT).show();
    }

    public void btnDiceExitOnClick(View view){
        //tell the server the player is exiting the room
        Intent nIntent = (new Intent()).setAction("CallService");
        nIntent.putExtra("type",new OperationType(8));
        nIntent.putExtra("GameResultMsg",new GameResultMsg(2));//2 -> exit the game
        sendBroadcast(nIntent);

        Intent mIntent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(mIntent);
    }

    public void btnDiceReplayOnClick(View view){
        //tell the server to restart the game
        Intent nIntent = (new Intent()).setAction("CallService");
        nIntent.putExtra("type",new OperationType(8));
        nIntent.putExtra("GameResultMsg",new GameResultMsg(3));//3 -> restart the game
        sendBroadcast(nIntent);

        Intent mIntent = new Intent(getApplicationContext(), WaitRoom.class)
                .putExtra("RoomID",roomID);
        startActivity(mIntent);
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
}