# Mini-Game-Plat
CNIT 355 Project: A practise of Internet Communication between Android Apps and Server.

## WARNING
Unfortunately, our team has yet not able to find a proper public server (such as AWS) to run our server. 
Due to this reason, this project is currently only LAN enabled, which means you have to follow the steps below and manually configure the ip address and port number to fit in your network environment.

##

## Prerequisites

0. Internet connection are required!!!!

1. NetBeans IDE 8.2 or newer

2. Android Studio 4.0.1 or newer

## Compiling & Running

### Run the Server

1. Load project folder in NetBeans

2. Run the entire project


### Run the Android Apps

1. Load the entire Android Project folder into Android Studio, if failed, proceed to Step 1.1

#### Step 1.1

1.1.1 Create a new Android project with empty activity 

1.1.2 Delete the src folder of the project you just created

1.1.3 copy and paste the src folder from step 1

####

2. locate SocketService.java in com/cnit355/minigameplatform, configure

3. Run the entire project on two seperate devices

4. In order to test the functionality of the entire project, 

```
Username: Pete
Password: hailpurdue
```


## Program Behavior

### Server Part

1. When the program runs, it search for "users.txt" in same directory to load all users' info into HashMap.

2. Program continues in a loop to accept client connection and put in a thread (Multi-Connection Enabled!)

3. For each connection, the server communicates with client using Message object defined in the package.

4. Based on the Message.requestType attribute, the server determine which action to perform (possible actions as followed)

```
0: login Authentication
1: request for downloading
2: request for uploading
3: request for registering (note: only upon registering, the users' info would be written into "users.txt")
```

5. IF the client request for 2/3, the server will prepare a new port and thread dedicated to transfer the file

### Client Part

1. When the program runs, it displays a dialog for login authentication

2. When authen is successful, the client displays the main GUI with a list of available file on Cloud Drive & WL weather

3. User can choose to upload or download files from server.

## Authors

* **Haowen Lei** 
* **Steven Wang**
* **Eric Malone** 
