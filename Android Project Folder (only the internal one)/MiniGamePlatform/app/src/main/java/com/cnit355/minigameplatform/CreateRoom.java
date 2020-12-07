package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;
import operations.AuthNMessage;
import operations.CreateRoomMsg;
import operations.OperationType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateRoom extends AppCompatActivity {
    String[] game = {"Paper Rock Scissor", "Dice Roller"};

    private BroadcastReceiver createRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 3:
                    Intent mIntent = new Intent(getApplicationContext(), WaitRoom.class);
                    CreateRoomMsg cMsg = (CreateRoomMsg) intent.getSerializableExtra("CreateRoomMsg");
                    mIntent.putExtra("RoomID",cMsg.getRoomID());
                    startActivity(mIntent);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, game);
        Spinner spinner = findViewById(R.id.spinnerGameType);
        spinner.setAdapter(adapter);

        if(createRoomReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallCreateRoom");
            registerReceiver(createRoomReceiver,iFilter);
        }
    }

    public void btnCreateOnClick(View view){
        Spinner spinner = findViewById(R.id.spinnerGameType);
        String game = spinner.getSelectedItem().toString();
        OperationType ot = new OperationType(3);// 3 -> create a game room
        CreateRoomMsg cMsg = new CreateRoomMsg(game.replaceAll(" ",""));
        Intent mIntent = new Intent();
        mIntent.setAction("CallService");
        mIntent.putExtra("type",ot).putExtra("CreateRoomMsg",cMsg);
        sendBroadcast(mIntent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(createRoomReceiver);
    }
}