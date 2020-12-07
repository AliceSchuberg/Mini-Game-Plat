package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;
import operations.GameResultMsg;
import operations.JoinMessage;
import operations.OperationType;
import operations.StartMessage;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WaitRoom extends AppCompatActivity {
    private String roomID;
    private ArrayList<String> playerList;
    private ArrayAdapter<String> adapter;
    private boolean btnStartPressed = false;
    private BroadcastReceiver waitRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 6:
                    StartMessage sMsg = (StartMessage) intent.getSerializableExtra("StartMessage");
                    if (sMsg.isReady()){
                        switch(sMsg.getGameType()){
                            case "PaperRockScissor":
                                Intent prsIntent = new Intent(WaitRoom.this, RockPaperScissor.class)
                                        .putExtra("RoomID",roomID);
                                startActivity(prsIntent);
                                break;
                            case "DiceRoller":
                                Intent drIntent = new Intent(WaitRoom.this, DiceRoller.class)
                                        .putExtra("RoomID",roomID);
                                drIntent.putExtra("NumOfPlayer",playerList.size());
                                drIntent.putExtra("PlayerList",playerList);
                                startActivity(drIntent);
                                break;
                        }
                    }else{
                        //update waiting-players list
                        int count = 0;
                        ListView listView = findViewById(R.id.listView);
                        playerList.clear();
                        HashMap<String,Boolean> hs = sMsg.getReadyList();
                        Log.i("???", hs.keySet().toString());
                        for (HashMap.Entry<String, Boolean> entry: sMsg.getReadyList().entrySet()){
                            playerList.add(entry.getKey());
                            adapter.notifyDataSetChanged();
                            listView.setItemChecked(count,entry.getValue());
                            count++;
                        }

                        //Change Button if the player has prepared
                        Button btnStart = findViewById(R.id.btnStartGame);
                        Button btnCancel = findViewById(R.id.btnCancel);
                        if (btnStartPressed){
                            btnStart.setVisibility(View.INVISIBLE);
                            btnCancel.setVisibility(View.VISIBLE);
                        }else {
                            btnStart.setVisibility(View.VISIBLE);
                            btnCancel.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_room);
        //display the room ID on top
        TextView txtRoomID = findViewById(R.id.txtRoomID);
        roomID = getIntent().getStringExtra("RoomID");
        txtRoomID.setText(txtRoomID.getText().toString().concat(roomID));

        //set Button cancel to invisible
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.INVISIBLE);

        //register the Broadcast Receiver
        if(waitRoomReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallWaitRoom");
            registerReceiver(waitRoomReceiver,iFilter);
        }

        //prepare the listView to display the players who are in the room
        ListView listView = findViewById(R.id.listView);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        playerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, playerList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setEnabled(false);

        //send out message to let the server know the player is in wait room
        prepare(false);
    }

    public void btnStartOnClick(View view){
        //send out message to let the server know the player is ready
        prepare(true);
        btnStartPressed = true;
    }

    //if true, let the server the player is ready; if false, let the server update the current list
    public void prepare(boolean p){
        //send out message to let the server know the current status
        StartMessage sMsg = new StartMessage(p);
        OperationType ot = new OperationType(6);//6 -> player is ready to start
        //prepare the intent to broadcast
        Intent mIntent = new Intent();
        mIntent.setAction("CallService");
        mIntent.putExtra("type",ot).putExtra("StartMessage",sMsg);
        sendBroadcast(mIntent);
    }
    @Override
    protected void onStop(){
        unregisterReceiver(waitRoomReceiver);
        super.onStop();
    }

    public void btnCancelOnClick(View view){
        prepare(false);
        btnStartPressed = false;
    }

    @Override
    public void onBackPressed() {
        //tell the server the player is exiting the room
        Intent nIntent = (new Intent()).setAction("CallService");
        nIntent.putExtra("type",new OperationType(8));
        nIntent.putExtra("GameResultMsg",new GameResultMsg(2));//2 -> exit the game
        sendBroadcast(nIntent);

        Intent mIntent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(mIntent);
    }
    //image credit:
    // <a href="https://www.vecteezy.com/vector-art/225050-night-time-beach">Night Time Beach Vectors by Vecteezy</a>
}