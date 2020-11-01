package chatClient;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Client {

    public static void main(String[] args) {
        if( args.length != 2){
            System.out.println("Client usage: java Client <hostname> <port>");
            System.exit(-1);
        }

        int portNumber = Integer.parseInt(args[1]);
        int reconnectionsCounter = 0;
        //Thread serverMessages = null;
        ThreadPoolExecutor usersInput = new ThreadPoolExecutor(1, 1, 200, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(6, false) );

        do {
            System.out.println("Connecting to chat server...");
            try (Socket socket = new Socket(args[0], portNumber);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()) )
            ) {
                //serverMessages = new Thread(new MessageSender(new PrintWriter(socket.getOutputStream(), true)));
                //serverMessages.setName("Server sender");
                //serverMessages.start();
                usersInput.execute(new MessageSender(new PrintWriter(socket.getOutputStream(), true)));
                String inputStr;
                while(  (inputStr = reader.readLine()) != null){
                    System.out.println(inputStr);
                    if(inputStr.equals("Bye.")) {
                        //serverMessages.interrupt();
                        usersInput.shutdownNow();
                        break;
                    }
                }
                //serverMessages.join();
            } catch (SocketException socEx) {
                reconnectionsCounter++;
                System.out.println("Connection failed. Try reconnect. Attempt " + reconnectionsCounter + " from 10.");

                try {
                    /*if(serverMessages != null && serverMessages.isAlive()) {
                        serverMessages.interrupt();
                        System.out.println("Interrupt command was send to user's input catcher. Now wait.");
                        serverMessages.join();
                        System.out.println("join() completed.");
                    }*/
                    usersInput.shutdownNow();
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    System.out.println("Common interrupt in socket listener.");
                    System.out.println(e.getMessage());
                }

            } catch (IOException e) {
                System.out.println("Error when read client input and send to server.");
                System.out.println(e.getMessage());
            }
        } while (reconnectionsCounter > 0 && reconnectionsCounter < 10);
    }
}
