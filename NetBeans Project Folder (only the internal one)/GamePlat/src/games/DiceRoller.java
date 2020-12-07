package games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class DiceRoller implements gameInterface{
    private final HashMap<String, Integer> user_input;
    private int hasEnd;
    private final static int MAXSIZE = 4;
    
    public DiceRoller(){
        user_input = new HashMap<>();
        hasEnd = -1;
    }
    
    @Override
    public int hasEnded(){
        return this.hasEnd;
    }
    
    @Override
    public boolean inputData(Object obj){
        D_R_Data data = (D_R_Data) obj;
        data.getPlayerID();
        Random rand =new Random();
        
        if(user_input.containsKey(data.getPlayerID())){
            if(user_input.get(data.getPlayerID())!=null){
                return false;
            }
            user_input.put(data.getPlayerID(), rand.nextInt(6)+1);
            //while all users and inputs are recorded, mark it the game has ended
            if (!user_input.containsValue(null)){
                hasEnd = 1;
            }
            if (user_input.containsValue(null)){
                hasEnd = 0;
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int joinRoom(String str){
        user_input.put(str, null);
        return 0;
    }
    
    @Override
    public void exitRoom(String str){
        reStart();
        user_input.remove(str);
    }
    
    @Override
    public String[] getGamers(){
        return (String[])user_input.keySet().toArray();
    }
    
    @Override
    public synchronized void reStart(){
        hasEnd = -1;
        for(String i : Arrays.copyOf(user_input.keySet().toArray(), user_input.size(), String[].class)){
            user_input.put(i, null);
        }
    }

    @Override
    public String[] getResult(){
        //get the max of the players
        int max = Collections.max(user_input.values());
        //iterate the inputs and find the players who matches
        List<String> winners = new ArrayList<>();
        for (Entry<String, Integer> entry : user_input.entrySet()) {
            if (entry.getValue()==max) {
                winners.add(entry.getKey());
            }
        }
        return  Arrays.copyOf(winners.toArray(), winners.size(), String[].class);
    }

    /**
     * @return the user_input
     */
    @Override
    public gameDataInterface getGameData() {
        return new D_R_Data(user_input) ;
    }
}
