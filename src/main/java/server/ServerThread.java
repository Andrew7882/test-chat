package server;

import java.io.*;
import java.net.*;

public class ServerThread implements Runnable{

    private Socket socket;

    public ServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try(
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String inputLine, outputLine;
            Protocol prt = new Protocol(socket);
            outputLine = prt.processMessage(null);
            out.println(outputLine);

            while( (inputLine = in.readLine()) != null){
                outputLine = prt.processMessage(inputLine);
                if( !outputLine.equals("send") ) {
                    out.println(outputLine);
                }
                if(outputLine.equals("Bye.")){
                    break;
                }
            }
                prt.clearUserName();
                socket.close();
                System.out.println("Socket was close, thread is out.");
        }catch (IOException e){
            System.out.println("Error when read from or print to socket in thread " + Thread.currentThread().getName());
            System.out.println(e.getMessage());
        }
    }
}
