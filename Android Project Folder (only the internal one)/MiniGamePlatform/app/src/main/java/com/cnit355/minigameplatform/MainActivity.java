package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import operations.AuthNMessage;
import operations.OperationType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
    private final static String ACTION_MAIN_ACTIVITY = "CallMainActivity";
    private final static String ACTION_SERVICE = "CallService";
    private String id;

    private BroadcastReceiver mainActReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 1:
                    AuthNMessage aMsg = (AuthNMessage) intent.getSerializableExtra("AuthNMessage");
                    if(aMsg.isAuthorizedResult()){
                        Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_LONG).show();
                        Intent mIntent = new Intent(getApplicationContext(), MainMenu.class);
                        PlayerSingleton.getInstance().setPlayerID(id);
                        startActivity(mIntent);
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Login failed!\nPlease check username & password!",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mainActReceiver!= null){
            IntentFilter iFilter = new IntentFilter(ACTION_MAIN_ACTIVITY);
            registerReceiver(mainActReceiver,iFilter);
        }

        startService(new Intent(this,SocketService.class));
    }

    public void btnSignInOnClick(View view){
        EditText txtID = findViewById(R.id.txtUserID);
        EditText txtPW = findViewById(R.id.txtPassword);
        //Request for Authentication
        AuthNMessage aMsg = new AuthNMessage(txtID.getText().toString(),txtPW.getText().toString());
        this.id = txtID.getText().toString();
        OperationType ot = new OperationType(1); //1 -> request to Auth.

        Intent mIntent = new Intent();
        mIntent.setAction(ACTION_SERVICE);
        mIntent.putExtra("type",ot);
        mIntent.putExtra("AuthNMessage",aMsg);
        sendBroadcast(mIntent);
    }

    public void btnSignUpOnClick(View view){
        Intent mIntent = new Intent(this,SignUpActivity.class);
        startActivity(mIntent);
    }

    private static final String TAG = "MainActivity";

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop()");
        unregisterReceiver(mainActReceiver);
        super.onStop();
    }
}