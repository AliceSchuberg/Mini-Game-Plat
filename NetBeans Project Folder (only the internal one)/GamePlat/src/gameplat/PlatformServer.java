package gameplat;

import games.D_R_Data;
import games.DiceRoller;
import games.P_R_S_Data;
import operations.CreateRoomMsg;
import operations.AuthNMessage;
import operations.JoinMessage;
import operations.OperationType;
import games.PaperRockScissor;
import games.gameDataInterface;
import games.gameInterface;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import operations.GameResultMsg;
import operations.PublicRoomListMessage;
import operations.StartMessage;

/**
 *
 * @author Rory
 */
public class PlatformServer {
    ServerSocket s;
    HashMap<String, IndividualSession> users;
    HashMap<String, GameSession> gameRooms;
    private final static int PORT_NUM = 8189;
    
    public PlatformServer() throws IOException{
        //initialize the server
        s = new ServerSocket(PORT_NUM);
        users = new HashMap();
        gameRooms = new HashMap();
        Connection dataBaseConnection = null;
        //create database if not existed
        try{
            dataBaseConnection = DriverManager.getConnection("jdbc:sqlite:users.db");
            Statement statement = dataBaseConnection.createStatement();
            statement.executeUpdate("create table if not exists UserRecord("
                                    + "id char(5),"
                                    + "password char(30));");
            //statement.executeUpdate("INSERT INTO UserRecord(id,password) values('admin','admin')");
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }finally{
            if (dataBaseConnection != null){
                try{dataBaseConnection.close();}catch(SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    
    public void AcceptConnection() throws IOException {
        boolean over = false;
        while (!over) {
            Socket incoming = this.s.accept();
            System.out.println("Accepted Connection!");
            (new Thread(new IndividualSession(incoming))).start();   
        }
    }
    
    //Individual Class to handle Authentication and Operation Calls
    public class IndividualSession implements Runnable{
        private final Socket socket;            //the socket dedicated for individual connectivity
        private boolean authenticated;   //the boolean indicating whether Authen. is successful
        private String id;                      //the user id
        ObjectInputStream in;                   //the input receiver
        ObjectOutputStream out;                 //the output sender
        Thread sendThread;                      //the dedicated thread to send object/message out
        String roomID;                          //record the room Id in which the user is
        //a queue used for sending object/messages out
        final BlockingQueue<List<Object>> MsgQueue;
        //pre-defined int to indicate what operation each message is for
        private final static int LOGIN = 1, SIGNUP = 2, CREATE = 3, PUBLIC = 4, JOIN =5,
                                START = 6, GAMING =7,RESULT = 8 , SERACH = 9;
        
        IndividualSession(Socket s) { 
            this.socket = s;
            in = null;
            out = null;
            id = null;
            authenticated = true;
            MsgQueue = new LinkedBlockingQueue<>();
        }
        
        @Override
        public void run() {
            try{
                //set up communication channel as Object Input/Output Stream
                in = new ObjectInputStream(this.socket.getInputStream());
                out = new ObjectOutputStream(this.socket.getOutputStream()); 
                
                //separate thread to send out message object
                sendThread = new Thread() {
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
                            v.printStackTrace();
                        }finally{
                            try {
                                if(socket!=null)socket.close();
                            }
                            catch (IOException ex){
                                Logger.getLogger(PlatformServer.class.getName()).log(Level.SEVERE, null, ex);}
                        }
                    }  
                };
                sendThread.start();
                
                //proceed with specific operation
                do {
                    //get the header object out to determine which type of operation is requested
                    List<Object> listOfMessages = (List<Object>)in.readObject();
                    OperationType opt = (OperationType) listOfMessages.get(0);
                    int requestType = opt.getType();

                    switch (requestType) {
                        //request for login
                        case LOGIN:
                            AuthNMessage AuMsg = (AuthNMessage) listOfMessages.get(1);
                            if (authenticate(AuMsg)){
                                this.id = AuMsg.getId();
                                System.out.println("The id is "+this.id);
                                PlatformServer.this.users.put(id, this);
                            }else{
                                authenticated = false;
                            }
                            break;
                        //request for sign-up
                        case SIGNUP:
                            AuthNMessage SignUpMsg = (AuthNMessage) listOfMessages.get(1);
                            if (signUp(SignUpMsg)){
                                this.id = SignUpMsg.getId();
                                System.out.println("ID signed up:"+this.id);
                                PlatformServer.this.users.put(id, this);
                            }else{
                                authenticated = false;
                            }
                            break;
                        //request for create room
                        case CREATE:
                            //create a room session and store it into HashTable
                            CreateRoomMsg cMsg = (CreateRoomMsg) listOfMessages.get(1);
                            GameSession gs = new GameSession(cMsg, this.id);
                            PlatformServer.this.gameRooms.put(gs.getRoomID(), gs);
                            roomID = gs.getRoomID();
                            //reply to the room creator with the roomID
                            cMsg.setRoomID(gs.getRoomID());
                            cMsg.setCreatorID(this.id);
                            sendMessage(opt,cMsg);
                            break;
                        //request for a list of public room
                        case PUBLIC:
                            PublicRoomListMessage prlm = (PublicRoomListMessage) listOfMessages.get(1);
                            HashMap<String, String> hMap = new HashMap<>();
                            for (HashMap.Entry<String, GameSession> entry: PlatformServer.this.gameRooms.entrySet()){
                                if(!entry.getValue().isRoomFull){
                                    hMap.put(entry.getKey(), entry.getValue().gameType);
                                    System.out.println(entry.getKey());
                                }
                            }
                            prlm.setRoomMap(hMap);
                            sendMessage(opt,prlm);
                            break;
                        //request to join a specific room
                        case JOIN:
                            //retrieve room ID and request to join it
                            JoinMessage jMsg = (JoinMessage) listOfMessages.get(1);
                            GameSession jGs = PlatformServer.this.gameRooms.get(jMsg.getRoomID());
                            int joinResult = jGs.joinRoom(this.id);
                            jMsg.setJoinOrder(joinResult);
                            if(joinResult != -1){
                                roomID = jMsg.getRoomID();
                            }
                            //reply to the user whether the join was successful: -1 if not success
                            sendMessage(opt,jMsg);
                            break;
                        //request to start the game in a specific room
                        case START:
                            StartMessage sMsg = (StartMessage) listOfMessages.get(1);
                            GameSession sGs = PlatformServer.this.gameRooms.get(roomID);
                            sGs.ready(id, sMsg.isReady());
                            break;
                        case GAMING:
                            gameDataInterface gamingData = (gameDataInterface) listOfMessages.get(1);
                            gamingData.setPlayerID(this.id);
                            GameSession gamingGs = PlatformServer.this.gameRooms.get(roomID);
                            gamingGs.inputGameData(gamingData);
                            break;
                        //search if a room with given ID exists on the server
                        case SERACH:
                            JoinMessage searchMsg = (JoinMessage) listOfMessages.get(1);
                            System.out.println("SearchRoomID is "+searchMsg.getRoomID());
                            if(PlatformServer.this.gameRooms.containsKey(searchMsg.getRoomID())){
                                searchMsg.setRoomExist(!PlatformServer.this.gameRooms.get(searchMsg.getRoomID()).isRoomFull);
                            }else{
                                searchMsg.setRoomExist(false);
                            }
                            sendMessage(opt,searchMsg);
                            break;
                        case RESULT:
                            GameResultMsg grm = (GameResultMsg)  listOfMessages.get(1);
                            GameSession gameResultGs = PlatformServer.this.gameRooms.get(roomID);
                            switch(grm.getResult()){
                                case 2:
                                    gameResultGs.exitGame(id);
                                    roomID = null;
                                    break;
                                case 3:
                                    gameResultGs.reStart();
                                    break;
                            }
                            break;
                        }
                }while (authenticated);
            }catch(EOFException ex){
                if(PlatformServer.this.users.containsKey(id)){
                    authenticated = false;
                    PlatformServer.this.users.remove(id);
                    System.out.println("The user: "+id+" has exited. EOFException has been catched");
                    
                }
            }catch (IOException|ClassNotFoundException ex) {
                Logger.getLogger(PlatformServer.class.getName()).log(Level.SEVERE, null, ex);}
            
        }
        
        //Authentication Method
        private boolean authenticate(AuthNMessage m) throws IOException, ClassNotFoundException{
            //Authenticate the user with AuthNMessage object received
            Connection dataBaseConnection = null;
            try{
                dataBaseConnection = DriverManager.getConnection("jdbc:sqlite:users.db");
                Statement statement = dataBaseConnection.createStatement();
                //statement.executeQuery("EXISTS(select * from UserRecord where id = "+m.getId()+")");
                //check if id record exists
                String query = "SELECT (count(*) > 0) as found FROM UserRecord WHERE id = ?";
                PreparedStatement pst = dataBaseConnection.prepareStatement(query);
                pst.setString(1, m.getId());
                try (ResultSet rs = pst.executeQuery()) {
                    // Only expecting a single result
                    if (rs.next()) {
                        boolean found = rs.getBoolean(1); // "found" column
                        if (found) {
                            ResultSet rs1 = statement.executeQuery("select * from UserRecord where id = '"+m.getId()+"'");
                            //reply true if password correct, false if not correct
                            return authNResultSend(1,m.getPassword().equals(rs1.getString("password")));
                        } else {
                            return authNResultSend(1,false);
                        }
                    }
                }
                /*
                if(statement.execute("select * from UserRecord where id = '"+m.getId()+"'")){
                    ResultSet rs = statement.executeQuery("select * from UserRecord where id = '"+m.getId()+"'");
                    //reply true if password correct, false if not correct
                    return authNResultSend(1,m.getPassword().equals(rs.getString("password")));
                }else{
                    return authNResultSend(1,false);
                }*/
            }catch(SQLException e){
                e.printStackTrace();
            }finally{
                if (dataBaseConnection != null){
                    try{
                        dataBaseConnection.close();
                    }catch(SQLException e){
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
        
        //Authentication Method
        private synchronized boolean signUp(AuthNMessage m) throws IOException, ClassNotFoundException{
            //Authenticate the user with AuthNMessage object received
            Connection dataBaseConnection = null;
            try{
                dataBaseConnection = DriverManager.getConnection("jdbc:sqlite:users.db");
                Statement statement = dataBaseConnection.createStatement();
                ResultSet rs = statement.executeQuery("select count(*) as count from UserRecord where id = '"+m.getId()+"'");
                rs.next();
                //check if id record exists
                if(rs.getBoolean(1)){
                    return authNResultSend(2,false);
                }else{
                    statement.executeUpdate(String.format("INSERT INTO UserRecord(id,password) values('%s','%s')"
                            ,m.getId(),m.getPassword()));
                    //reply true if password correct, false if not correct
                    return authNResultSend(2, true);
                }
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }finally{
                if (dataBaseConnection != null){
                    try{
                        dataBaseConnection.close();
                    }catch(SQLException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
            return authNResultSend(2,false);
        }
        
        //send out and return the Auth. result
        private boolean authNResultSend(int i,boolean r) throws IOException{
            AuthNMessage m = new AuthNMessage("","");
            m.setAuthorizedResult(r);
            
            OperationType ot = new OperationType();
            ot.setType(i);
            
            sendMessage(ot,m);
            return r;
        }
        
        //Sender Method which put object/message list into the queue
        private void sendMessage(OperationType ot, Object o) throws IOException{
            List<Object> listMsg = new ArrayList<>();
            listMsg.add(ot);
            listMsg.add(o);
            MsgQueue.add(listMsg);
        }
    }//IndividualSession
    
    public class GameSession implements Runnable{
        private CreateRoomMsg roomInfo; //room info object
        private String[] gamers;        //a list of users
        private gameInterface game;     //the game object
        private int roomCapacity;       //room capacity
        public boolean isRoomFull;     //if room full
        public String gameType;
        private HashMap<String,Boolean> GamerReady;
        final BlockingQueue<gameDataInterface> DataQueue;
        
        //initiate room and record gametype & room creator id
        public GameSession(CreateRoomMsg ri, String u1){
            //store room info
            this.roomInfo = ri;
            //randomly create GameRoom ID
            String AlphaNumString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder(5);
            for (int i = 0;i<5;i++){
                int index = (int) (AlphaNumString.length()*Math.random());
                sb.append(AlphaNumString.charAt(index));
            }
            this.roomInfo.setRoomID(sb.toString());
            //depending on the game, create different size of array to record user
            gameType = roomInfo.getGameType();
            switch(gameType){
                case "PaperRockScissor":
                    game = new PaperRockScissor();
                    this.gamers  = new String[2];
                    roomCapacity = 2;
                    break;
                case "DiceRoller":
                    game = new DiceRoller();
                    this.gamers = new String[4];
                    roomCapacity = 4;
                    break;
            }
            //record creator id in position 0
            this.gamers[0] = u1;
            this.game.joinRoom(u1);
            GamerReady = new HashMap<>();
            GamerReady.put(u1, false);
            DataQueue = new LinkedBlockingQueue<>();
        }
        
        @Override
        public void run(){
            boolean end = false;
            do{
                try{
                    gameDataInterface gd = DataQueue.take();
                    game.inputData(gd);
                    int progress = game.hasEnded();//-1 = not end, 0 = wait for other user, 1 = end
                    //if the game has ended, request for the results (which is a list of winners)
                    //retrieve the userSession and send out a Message to them
                    switch(progress){
                        // the game has not ended, skip
                        case -1:
                            break;
                        // the game needs to wait for another input, tell user to wait
                        case 0:
                            GameResultMsg grm0 = new GameResultMsg(progress);
                            if(gameType.equals("DiceRoller")){
                                grm0.setProgress(game.getGameData());
                                sendProgress(grm0);
                                break;
                            }
                            PlatformServer.this.users.get(gd.getPlayerID()).sendMessage(new OperationType(8), grm0);
                            break;
                        // the game has ended, send a list of winner to users
                        case 1:
                            if(gameType.equals("DiceRoller")){
                                GameResultMsg grm10 = new GameResultMsg(0);
                                grm10.setProgress(game.getGameData());
                                sendProgress(grm10);
                            }
                            String[] winners = game.getResult();
                            GameResultMsg grm1 = new GameResultMsg(progress,winners);
                            sendProgress(grm1);
                            end = true;
                            break;
                    }  
                }catch(InterruptedException | IOException e){
                    System.out.println(e.getMessage());
                }
            }while (!end);
        }
        
        //the specified player is exiting the room
        public void exitGame(String player) throws IOException{
            for (int i = 0;i<gamers.length;i++){
                if(gamers[i]!=null && gamers[i].equals(player)){
                    gamers[i] =null;
                    //
                    int countNull = 0;
                    for (String gamer : gamers) {
                        if (gamer == null) {
                            countNull++;
                        }
                    }
                    if(countNull == gamers.length){
                        PlatformServer.this.gameRooms.remove(roomInfo.getRoomID());
                    }
                    GamerReady.remove(player);
                    System.out.println("After Exit():"+GamerReady.keySet().toString());
                    game.exitRoom(player);
                    isRoomFull = false;
                    updateList();
                    return;
                }
            }
        }
        
        //restart the game
        public void reStart() throws IOException{
            game.reStart();
            System.out.println("reStart()");
            for (String i:gamers){
                if(i != null){
                    GamerReady.put(i, false);
                    updateList();
                }
            }
        }
        
        //add the gameData to the queue and wait for run() execution
        public void inputGameData(gameDataInterface obj){
            DataQueue.add(obj);
        }
        
        //set the specified player status to ready, and start the game if all ready
        public void ready(String player, boolean ready) throws IOException{
            //record the user's status as ready
            GamerReady.put(player, ready);
            //depending on whether every player is ready, 
            //start the game OR reply to the user with the list
            if(!GamerReady.containsValue(false) && GamerReady.size()>1){
                (new Thread(this)).start();
                StartMessage sMsg = new StartMessage(true);//tell to start the game
                sMsg.setGameType(gameType);
                OperationType ot = new OperationType();
                ot.setType(6); //6 - > start related info
                
                for (String i: gamers){
                    if(i!=null){
                        PlatformServer.this.users.get(i).sendMessage(ot, sMsg);
                    }
                }
            }else{
                updateList();
            }
        }
        
        //broadcast the list of player and status to everyone in the room
        public void updateList() throws IOException{
            StartMessage sMsg = new StartMessage(false);//tell not to start the game
            sMsg.setReadyList(GamerReady);
            OperationType ot = new OperationType();
            ot.setType(6); //6 - > start related info
           
            for (String i: gamers){
                if(i!=null){
                    PlatformServer.this.users.get(i).sendMessage(ot, sMsg);
                }
            }
        }
        
        //send the game results / progress to each player
        public void sendProgress(GameResultMsg Msg) throws IOException{
            OperationType ot = new OperationType();
            ot.setType(8);//8 -> gameResult
            for (String i: gamers){
                if(i!= null){
                    PlatformServer.this.users.get(i).sendMessage(ot, Msg);
                }
            }
        }
        
        //get the roomID of this game session
        public String getRoomID(){
            return roomInfo.getRoomID();
        }
        
        //request to join the room, based on situations, reply with specified int
        public int joinRoom(String u) throws IOException{
            //if room is full, return -1
            if (!isRoomFull){
                System.out.println(u+" reached!");
                //loop and add the user to the first available position in the array
                for (int i = 0;i<roomCapacity;i++){
                    if (gamers[i] == null){
                        gamers[i] = u;
                        GamerReady.put(u, false);
                        game.joinRoom(u);
                        System.out.println(u+" 1 reached!");
                        if(i == (roomCapacity-1)){isRoomFull = true;System.out.println(u+"2 reached!");}
                        return i;
                    }
                }
            }
            System.out.println(u + "The room capacity is :"+String.valueOf(isRoomFull));
            return -1;
        }
    }//GameSession
}
