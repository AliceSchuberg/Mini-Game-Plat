package com.cnit355.minigameplatform;

//This is a class where the platform will hold the relevant information about to the players
//for the purpose of being available to all activities
public class PlayerSingleton {
    private static PlayerSingleton player = null;

    private String playerID;
    private String roomID;

    //private constructor
    private PlayerSingleton()
    {

    }

    //get the static instance to this
    public static PlayerSingleton getInstance()
    {
        if (player == null)
        {
            player = new PlayerSingleton();
        }
        return player;
    }


    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
