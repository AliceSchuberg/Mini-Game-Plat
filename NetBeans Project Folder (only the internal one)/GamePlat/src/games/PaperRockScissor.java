package games;

import java.util.HashMap;

public class PaperRockScissor implements gameInterface {
    private String id1;
    private String id2;
    private final HashMap<String, String> user_input;
    private int count1;
    private int count2;
    private int hasEnd;
    private final static int MAXSIZE = 2;
    
    public PaperRockScissor(){
        user_input = new HashMap<>();
        id1 = null;
        id2 = null;
        hasEnd = -1;
    }
    
    @Override
    public int hasEnded(){
        return this.hasEnd;
    }
    
    @Override
    public boolean inputData(Object d){
        P_R_S_Data data = (P_R_S_Data) d;
        System.out.println(getUser_input().keySet().toString());
        if(getUser_input().containsKey(data.getPlayerID())){
            if(getUser_input().get(data.getPlayerID())!=null){
                return false;
            }
            getUser_input().put(data.getPlayerID(), data.getMove());
            //while both users and inputs are recorded, mark it the game has ended
            if (!user_input.containsValue(null) && getUser_input().size() == MAXSIZE){
                hasEnd = 1;
            }
            if (getUser_input().containsValue(null) && getUser_input().size() == MAXSIZE){
                hasEnd = 0;
            }
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public String[] getResult () {
        //if have the same result, return "Tie";
        if (getUser_input().get(id1).equals(getUser_input().get(id2))){
            return new String[]{id1,id2};
        } else {
            switch (getUser_input().get(id1)) {
                case "ROCK":
                    if (getUser_input().get(id2).equals("SCISSOR")){
                        count1++;
                        return new String[]{id1};
                    }else {
                        count2++;
                        return new String[]{id2};
                    }
                case "SCISSOR":
                    if (getUser_input().get(id2).equals("PAPER")) {
                        count1++;
                        return new String[]{id1};
                    }else {
                        count2++;
                        return new String[]{id2};
                    }
                case "PAPER":
                    if (getUser_input().get(id2).equals("ROCK")) {
                        count1++;
                        return new String[]{id1};
                    }else {
                        count2++;
                        return new String[]{id2};
                    }
            }
        }
        return null;
    }
    
    @Override
    public void reStart(){
        hasEnd = -1;
        if(id1!=null){
            getUser_input().put(id1, null);
        }
        if(id2!=null){
            getUser_input().put(id2, null);
        }
    }

    @Override
    public int joinRoom(String str){
        if (id1 == null){
            id1 = str;
            getUser_input().put(str, null);
            return 1;
        }else if(id2 == null){
            id2 = str;
            getUser_input().put(str, null);
            return 2;
        }else{
            return -1;
        }
    }
    
    @Override
    public void exitRoom(String str){
        reStart();
        if (id1 != null && id1.equals(str)){
            id1 = null;
            getUser_input().remove(str);
        }else if(id2 != null && id2.equals(str)){
            id2 = null;
            getUser_input().remove(str);
        }
    }
    /**
     * @return the id1
     */
    public String getId1() {
        return id1;
    }

    /**
     * @param id1 the id1 to set
     */
    public void setId1(String id1) {
        this.id1 = id1;
    }

    /**
     * @return the id2
     */
    public String getId2() {
        return id2;
    }

    /**
     * @param id2 the id2 to set
     */
    public void setId2(String id2) {
        this.id2 = id2;
    }
    
    /**
     * @return the count1
     */
    public int getCount1() {
        return count1;
    }

    /**
     * @return the count2
     */
    public int getCount2() {
        return count2;
    }
    
    @Override
    public String[] getGamers(){
        return (String[]) getUser_input().keySet().toArray();
    }

    /**
     * @return the user_input
     */
    public HashMap<String, String> getUser_input() {
        return user_input;
    }
    
    @Override
    public gameDataInterface getGameData() {
        return new D_R_Data() ;
    }
}
