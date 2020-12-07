# Mini-Game-Plat
CNIT 355 Project: A game platform of mini / simple games with a defined server and the correspondent Android clients for players to operate with.

## Motivation
The project was designed in a way to practice our programming skills on Android in terms of Android communication with a running server. The Android App itself was neither attempting to be commercial nor to be attractive to the Android market. 

## WARNING

Unfortunately, our team has yet not been able to find a proper public server (such as AWS) to run our server. 

Due to this reason, this project is currently only LAN enabled, which means you have to follow the steps below and manually configure the ip address and port number to fit in your network environment.

## Design & Analysis
In order to manage the following potential issues with our projects, we designed our app and server separately to suit the needs.
```
1. How to manage multiple players as well as multiple gaming session at the same time?
2. How to manage a consistent and stable Socket connection for Android App?
3. How to communicate between server and Android App except using only String or Integer?
4. How to communicate properly among Service and Activities?
```
### Server

#### Multi-Threading: 
We designed our server to maintain two global HashMaps to keep records of players and game rooms. And each of them will a single instance of Thread. This means once you have 5 players signed in to the server and 2 game rooms have been created, there will be 7 different separate Threads to handle all communications and processes.

#### Communication Packages: 
In order to communicate with the Android Apps, and since different operations (sign-in or create-a-room) will proceed with different data and information, we self-defined our own Message classes that will carry with the appropriate data for each purpose. For this, see `operations` package.

#### Games Packages: 
And for the purpose of preventing cheating, we also self-defined our own gaming data classes that will carry the data to the server, while the actual game classes which contains the gaming logics will be hidden from the players. For this, see `games` package. 

### Android 


## Prerequisites

0. Internet connection are required!!!!

1. NetBeans IDE 8.2 or newer (If NetBeans failed to install, please follow the steps in the end of this file to proceed)

2. Android Studio 4.0.1 or newer

## How to use?

### Run the Server

1. Load project folder in NetBeans

2. Locate SocketService.java in com/cnit355/minigameplatform

3. Configure PORT_NUM in line 34 to the port number you want your server listen to

4. Take note of your server's ip address (Tutorial: https://www.avg.com/en/signal/find-ip-address)

5. Run the entire project.


### Run the Android Apps

1. Load the entire Android Project folder into Android Studio, if failed, proceed to Step 1.1

```
Step 1.1

1.1.1 Create a new Android project with empty activity 

1.1.2 Delete the src folder of the project you just created

1.1.3 copy and paste the src folder from step 1
```

####

2. locate SocketService.java in com/cnit355/minigameplatform, configure IP and Port_NUM in line 32, 33 to the server you just ran

3. Run the entire project on two seperate devices

4. You are free to explore the features of the platform! (feel free to use the below accounts, both have been registered in server)

```
Account for testing purpose:
Username: player         Username: player1
Password: player         Password: player1
```


## Program Behavior Description

### Server part

1. When the program runs, it attempts to locate users.db. If not found, it will create a new one.

2. Program continues in a loop to accept Android client connection and put it in a thread.

3. For each connection, the server communicates with Android client using different objects defined in the package `games` and `operations`.

4. Based on the `operations.OperationType` object, the server determine which action to perform (possible actions as followed)
```
1 -> sign-in /authentication
2 -> sign-up
3 -> create room
4 -> find public room
5 -> join /search room
6 -> start game
7 -> gaming
8 -> result /exit room
9 -> search (if room exists)
```
5. Upon accepting request 3, it will create a GameSession instance running on an individual thread.

6. GameSession will continue to receive inputs / game data from Android client

7. Once all inputs are recorded, the server will proceed to calculate the final winners, and flush out the winner list (String[]) to everyone in the game room

### Client Part

1. When the Android App runs, it will ask for sign-in credentials to authenticate. Or the player can choose to sign up for a new account.

2. Once either Sign-In or Sign-Up is successful, the app will prompt to main menu.

3. The player can choose `create a new room`, `find a public room`, `search and join a room`.

4. Either way, the player will be sent to WaitRoom in which the player must wait for another player to join. On the WaitRoom activity, the user will be able to see and share the `roomID` to others.

5. Other players may now find the room that was just created on `find a public room`, or `search and join a room` with the shared room ID.

6. On the waitroom, all players must press `PREPARE` in order to proceed to next phase: Gaming.

7. On the actual gaming activity, depends on which game the player is in:

#### Paper Rock Scissor
1. The server will take inputs from each player. 

2. Once both players have entered their data, it will trigger the server to compare them to get the results. 

3. The server will send back the results to both players.

4. The players will see a dialog showing the result, the players can choose to `re-play` again or `return to main menu`.

#### Dice Roller
1. Ther server takes requests from each player.

2. The server then calculates a random number (1<=x<=6) for the player just requested. 

3. The server then flushes out the current result of all players (could be null since some may not have requested) to each player.

4. Once all players have their numbers, the server will then calculate and obtain winners-list whose have the highest scores, and flushes out the results again.

5. Once the game is over, the app will show the results and let the player either `re-play` again or `return to main menu`.

## Authors

* **Haowen Lei** 
* **Steven Wang**
* **Eric Malone** 
