# Mini-Game-Plat
CNIT 355 Project: A practice of Internet Communication between Android Apps and Server.

## WARNING

Unfortunately, our team has yet not able to find a proper public server (such as AWS) to run our server. 

Due to this reason, this project is currently only LAN enabled, which means you have to follow the steps below and manually configure the ip address and port number to fit in your network environment.



## Prerequisites

0. Internet connection are required!!!!

1. NetBeans IDE 8.2 or newer (If NetBeans failed to install, please follow the steps in the end of this file to proceed)

2. Android Studio 4.0.1 or newer

## Compiling & Running

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

4. You are free to explore the features of the platform!

```
Testing Account:
Username: player         Username: player1
Password: player         Password: player1
```


## Program Behavior

### Server part

1. When the program runs, it attempts to locate users.db. If not found, it will create a new one.

2. Program continues in a loop to accept client connection and put it in a thread.

3. For each connection, the server communicates with client using Message object defined in the package.

4. Based on the operations.OperationType object, the server determine which action to perform (possible actions as followed)

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

### Client Part

1. When the program runs, it displays a dialog for login authentication

2. When authen is successful, the client displays the main GUI with a list of available file on Cloud Drive & WL weather

3. User can choose to upload or download files from server.

## Authors

* **Haowen Lei** 
* **Steven Wang**
* **Eric Malone** 
