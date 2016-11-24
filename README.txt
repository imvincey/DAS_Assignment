1. Compile the file by typing javac *.java in a new terminal

2. Start RMI by typing (Windows: start rmiregistry || Linux: rmiregistry)
(if you're using windows, you may use the zCompile.bat given to perform step 2-3)

3. Start server by typing java Server (default) in a new terminal
For specific ipaddress and port, the syntax would be java Server <ipaddress> <port>
(if you're using windows, you may start the default server by using zStartServer.bat)

4. Start the client by typing java Client (default) in a new terminal
For specific ipaddress and port, the syntax would be java Client <ipaddress> <port>
(if you're using windows, you may start the default server by using zStartClient.bat)

5. In the client terminal, you may enter your username, after which you'll be able to enter to the system. You may create an auction item (1), to bid an item (2) by following the instructions given by the system prompt. Enter the legitimate value. You can also list the test bid item that I have listed in the auction (3). If you want to exit the client, you may key 4 in the client terminal.

6. You may also bid on my existing item, however I do not know when this program will be tested, and thus I am unable to do a real-time notify / callback. The expiry of the program is on 2016-12-10 02:30:27.43.

7. You may also want to test the performance of the system by starting java AuctionTest <Client Name>
(if you're using windows, you may run TESTING.bat)