package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import operations.JoinMessage;
import operations.OperationType;
import operations.PublicRoomListMessage;
import operations.StartMessage;

public class FindPublicRoom extends AppCompatActivity {
    private ArrayList<String> roomList;
    private ArrayAdapter<String> adapter;
    private String roomIDselected;

    private BroadcastReceiver findPublicRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 4:
                    Log.i("Public Room","Received Info");
                    PublicRoomListMessage prlm = (PublicRoomListMessage) intent.getSerializableExtra("PublicRoomListMessage");
                    for(HashMap.Entry<String,String> entry: prlm.getRoomMap().entrySet()){
                        roomList.add("Game Type: "+ entry.getValue()+";\n Room ID: "+entry.getKey());
                    }
                    if(roomList.size() == 0){
                        Button btnSearch = findViewById(R.id.btnSearch);
                        btnSearch.setEnabled(false);
                        Toast.makeText(FindPublicRoom.this,"Sorry, no public room available currently!" ,Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_public_room);

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setEnabled(true);

        //register the Broadcast Receiver
        if(findPublicRoomReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallPublicRoom");
            registerReceiver(findPublicRoomReceiver,iFilter);
        }

        roomList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice, roomList);
        //prepare the listView to display the players who are in the room
        ListView listView = findViewById(R.id.listViewFind);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                roomIDselected = roomList.get(i).substring(roomList.get(i).length()-5);
            }
        });

        PublicRoomListMessage prlm = new PublicRoomListMessage();
        OperationType ot = new OperationType(4);
        sendBroadcast((new Intent()).setAction("CallService")
                .putExtra("type",ot).putExtra("PublicRoomListMessage",prlm));
    }

    public void btnJoinOnClick(View view){
        ListView listView = findViewById(R.id.listViewFind);
        JoinMessage jMsg = new JoinMessage();
        jMsg.setRoomID(roomIDselected);
        OperationType ot = new OperationType(5);//5 -> request to join the room

        //send out the request to join the room
        Intent jIntent = new Intent();
        jIntent.setAction("CallService");
        jIntent.putExtra("type",ot).putExtra("JoinMessage",jMsg);
        sendBroadcast(jIntent);

        Intent mIntent = new Intent(getApplicationContext(), WaitRoom.class);
        mIntent.putExtra("RoomID",roomIDselected);
        startActivity(mIntent);
    }

    @Override
    protected void onStop(){
        unregisterReceiver(findPublicRoomReceiver);
        super.onStop();
    }
}