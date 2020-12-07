package com.cnit355.minigameplatform;

import androidx.appcompat.app.AppCompatActivity;
import operations.AuthNMessage;
import operations.OperationType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    private String id;

    private BroadcastReceiver SignUpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case 2:
                    AuthNMessage aMsg = (AuthNMessage) intent.getSerializableExtra("AuthNMessage");
                    Log.i("Msg","Received");
                    if(aMsg.isAuthorizedResult()){
                        Toast.makeText(SignUpActivity.this,"Sign up successful!" +
                                "\nMain Menu will pop up in 1 sec",Toast.LENGTH_SHORT).show();
                        Intent mIntent = new Intent(getApplicationContext(), MainMenu.class);
                        PlayerSingleton.getInstance().setPlayerID(id);
                        startActivity(mIntent);
                    }else{
                        Toast.makeText(SignUpActivity.this,
                                "Sign up failed, please try again!",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(SignUpReceiver!= null){
            IntentFilter iFilter = new IntentFilter("CallSignUpPage");
            registerReceiver(SignUpReceiver,iFilter);
        }
    }

    public void buttonSignUpOnClick(View view){
        EditText txtID = findViewById(R.id.txtUserID1);
        EditText txtPW = findViewById(R.id.txtPassword1);
        id = txtID.getText().toString();

        //Request for Authentication
        AuthNMessage aMsg = new AuthNMessage(txtID.getText().toString(),txtPW.getText().toString());
        OperationType ot = new OperationType(2); //2 -> request to Sign Up.

        Intent mIntent = new Intent();
        mIntent.setAction("CallService");
        mIntent.putExtra("type",ot);
        mIntent.putExtra("AuthNMessage",aMsg);
        sendBroadcast(mIntent);
    }

    @Override
    protected void onStop() {
        Log.d("SignUp","onStop()");
        unregisterReceiver(SignUpReceiver);
        super.onStop();
    }
}