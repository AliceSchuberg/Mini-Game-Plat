package com.cnit355.minigameplatform;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import operations.AuthNMessage;
import operations.CreateRoomMsg;
import operations.GameResultMsg;
import operations.JoinMessage;
import operations.OperationType;
import operations.PublicRoomListMessage;
import operations.StartMessage;

public class SocketService extends Service {
    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_SERVICE = "CallService";
    private final static String ServerIP = "192.168.4.24";
    private final static int portNumber = 8189;
    IndividualSession iSession;
    private final static int LOGIN = 1, SIGNUP = 2, CREATE = 3, PUBLIC = 4, JOIN =5,
            START = 6, GAMING =7, RESULT = 8 , SEARCH = 9;

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OperationType ot = (OperationType) intent.getSerializableExtra("type");
            switch (ot.getType()){
                case LOGIN:
                case SIGNUP:
                    iSession = new IndividualSession();
                    (new Thread(iSession)).start();
                    iSession.sendMessage(ot,intent.getSerializableExtra("AuthNMessage"));
                    break;
                case CREATE:
                    iSession.sendMessage(ot,intent.getSerializableExtra("CreateRoomMsg"));
                    break;
                case PUBLIC:
                    iSession.sendMessage(ot,intent.getSerializableExtra("PublicRoomListMessage"));
                    break;
                case JOIN:
                case SEARCH:
                    iSession.sendMessage(ot,intent.getSerializableExtra("JoinMessage"));
                    break;
                case START:
                    iSession.sendMessage(ot,intent.getSerializableExtra("StartMessage"));
                    break;
                case RESULT:
                    iSession.sendMessage(ot,intent.getSerializableExtra("GameResultMsg"));
                    break;
                case GAMING:
                    iSession.sendMessage(ot,intent.getSerializableExtra("GamingData"));
                    break;
            }

        }
    };

    public SocketService() {

    }

    class IndividualSession implements Runnable {
        //socket-related
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        private final BlockingQueue<List<Object>> MsgQueue;

        private boolean authenticated;
        //private final static int LOGIN = 1, SIGNUP = 2, CREATE = 3, PUBLIC = 4, JOIN = 5, START = 6, GAMING = 7;

        public IndividualSession(){
            MsgQueue = new LinkedBlockingQueue<>();
            authenticated = true;
        }

        public void run() {
            try {
                //establish connection with the server 192.168.2.165 or 4.24
                socket = new Socket(ServerIP, portNumber);//pre-defined ip-address
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                //separate thread to send out message object
                Thread sendThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while(authenticated){
                                List<Object> listMsg = MsgQueue.take();
                                out.writeObject(listMsg);
                                out.reset();
                                //out.flush();
                            }
                        } catch(InterruptedException | IOException v) {
                            System.out.println(v);
                        }
                    }
                };
                sendThread.start();

                //proceed with specific operation
                do {
                    @SuppressWarnings("unchecked")
                    //get the header object out to determine which type of operation is requested
                    List<Object> listOfMessages = (List<Object>)in.readObject();
                    OperationType opt = (OperationType) listOfMessages.get(0);
                    int requestType = opt.getType();

                    switch (requestType) {
                        //request for login
                        case LOGIN:
                            AuthNMessage AuMsg = (AuthNMessage) listOfMessages.get(1);
                            sendBroadcast(prepareIntent("CallMainActivity", opt, AuMsg));
                            if(!AuMsg.isAuthorizedResult()){
                                authenticated = false;
                            }
                            break;
                        //request for sign-up
                        case SIGNUP:
                            AuthNMessage SignUpMsg = (AuthNMessage) listOfMessages.get(1);
                            Log.i("Received:",String.valueOf(SignUpMsg.isAuthorizedResult()));
                            sendBroadcast(prepareIntent("CallSignUpPage", opt, SignUpMsg));
                            if(!SignUpMsg.isAuthorizedResult()){
                                authenticated = false;
                            }
                            break;
                        //request for create room
                        case CREATE:
                            //receive Game Room ID
                            CreateRoomMsg cMsg = (CreateRoomMsg) listOfMessages.get(1);
                            sendBroadcast(prepareIntent("CallCreateRoom",opt,cMsg));
                            break;
                        //request for a list of public room
                        case PUBLIC:
                            PublicRoomListMessage prlm = (PublicRoomListMessage) listOfMessages.get(1);
                            sendBroadcast(prepareIntent("CallPublicRoom",opt,prlm));
                            break;
                        case START:
                            StartMessage sMessage = (StartMessage) listOfMessages.get(1);
                            if(sMessage.getReadyList() != null){
                                Log.i("Service received such: ",sMessage.getReadyList().keySet().toString());
                            }
                            sendBroadcast(prepareIntent("CallWaitRoom",opt,sMessage));
                            break;
                        case RESULT:
                            GameResultMsg grm = (GameResultMsg) listOfMessages.get(1);
                            sendBroadcast(prepareIntent("CallGameRoom",opt,grm));
                            break;
                        case SEARCH:
                            JoinMessage searchMsg = (JoinMessage) listOfMessages.get(1);
                            sendBroadcast(prepareIntent("CallSearchRoom",opt,searchMsg));
                            break;
                    }
                }while (authenticated);
            }catch (IOException |ClassNotFoundException ex) {
                ex.printStackTrace();}
            finally{
                try {
                    if(socket!=null)socket.close();
                    if(in!=null)in.close();
                    if(out!=null)out.close();}
                catch (IOException ex){
                    ex.printStackTrace();}
            }
        }
        public Intent prepareIntent(String activityToCall, Serializable type, Serializable msg){
            Intent mIntent = new Intent();
            mIntent.setAction(activityToCall);
            mIntent.putExtra("type",type);
            String className = msg.getClass().getName();
            mIntent.putExtra(className.substring(className.indexOf(".")+1),msg);
            return mIntent;
        }
        //Sender Method which put object/message list into the queue
        private void sendMessage(OperationType ot, Object o){
            List<Object> listMsg = new ArrayList<>();
            listMsg.add(ot);
            listMsg.add(o);
            MsgQueue.add(listMsg);
        }
    }

    @Override
    public void onCreate(){
        //once the service is created, initiate an IndividualSession
        iSession = new IndividualSession();
        if (serviceReceiver != null){
            IntentFilter iFilter = new IntentFilter(ACTION_SERVICE);
            registerReceiver(serviceReceiver,iFilter);
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(serviceReceiver);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
