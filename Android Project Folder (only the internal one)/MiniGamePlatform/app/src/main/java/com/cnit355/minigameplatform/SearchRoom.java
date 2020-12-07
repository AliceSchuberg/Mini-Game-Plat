package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;
import operations.CreateRoomMsg;
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
import android.widget.EditText;
import android.widget.Toast;

public class SearchRoom extends AppCompatActivity {
    String searchRoom;
    private BroadcastReceiver searchRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 9:
                    JoinMessage searchMsg = (JoinMessage) intent.getSerializableExtra("JoinMessage");
                    //Toast.makeText(getApplicationContext(),"Room ID is"+ searchMsg.getRoomID(),Toast.LENGTH_LONG).show();
                    if(searchMsg.isRoomExist()){
                        new AlertDialog.Builder(SearchRoom.this)
                                .setTitle("Room "+searchRoom+" is Found!")
                                .setMessage("Do you want to join it?")
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        JoinMessage jMsg = new JoinMessage();
                                        jMsg.setRoomID(searchRoom);
                                        OperationType ot = new OperationType(5);//5 -> request to join the room
                                        //send out the request to join the room
                                        Intent jIntent = new Intent();
                                        jIntent.setAction("CallService");
                                        jIntent.putExtra("type",ot).putExtra("JoinMessage",jMsg);
                                        sendBroadcast(jIntent);

                                        Intent mIntent = new Intent(getApplicationContext(), WaitRoom.class);
                                        mIntent.putExtra("RoomID",searchRoom);
                                        startActivity(mIntent);
                                    }
                                }).create().show();
                    }else {
                        new AlertDialog.Builder(SearchRoom.this)
                                .setTitle("Room "+searchRoom+" does not exist!")
                                .setMessage("Please search again")
                                .setPositiveButton("Okay", null).create().show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        if(searchRoomReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallSearchRoom");
            registerReceiver(searchRoomReceiver,iFilter);
        }
    }

    public void btnSearchOnClick(View view){
        Toast.makeText(this,"Clicked",Toast.LENGTH_LONG).show();
        EditText txtSearchID = findViewById(R.id.txtSearchRoomID);
        searchRoom = txtSearchID.getText().toString();
        JoinMessage jMsg = new JoinMessage();
        jMsg.setRoomID(txtSearchID.getText().toString());
        OperationType ot = new OperationType(9);//9 -> search if the room exist

        Intent mIntent = new Intent();
        mIntent.setAction("CallService");
        mIntent.putExtra("type",ot).putExtra("JoinMessage",jMsg);
        sendBroadcast(mIntent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(searchRoomReceiver);
    }
}