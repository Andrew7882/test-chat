package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Protocol {
    public final static String ARCHIVE_FILE_NAME = "c://temp//testChatArchive.txt";
    private static Map<String, PrintWriter> clients = Collections.synchronizedMap(new HashMap<>() );
    public static final String[] MESSAGES = {"Hello! What is your name?",
            "Connected.",
            "This name exists already. Enter another, please.",
            " entered the chat.",
            " leaves the chat."
    };

    static {
        try {
            clients.put("_systemArchive", new PrintWriter(new BufferedWriter(new FileWriter(ARCHIVE_FILE_NAME, true))) );
        } catch (IOException e) {
            System.out.println("Error when create file for saving chat's archive.");
            System.out.println(e.getMessage());
        }
    }

    private int step = 0;
    private String name = null;
    private final Socket socket;
    private static List<String> loadedArchive;

    public Protocol(Socket socket){
        super();
        this.socket = socket;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(ARCHIVE_FILE_NAME))){
            String str;
            ArrayList<String> tempArrList = new ArrayList<>();
            while ( (str = fileReader.readLine()) != null)
                tempArrList.add(str);
            loadedArchive = tempArrList.subList( tempArrList.size()>50 ? tempArrList.size()-50 : 0,
                    tempArrList.size());

        } catch (FileNotFoundException e) {
            System.out.println("Error opening the archive file.");
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println("Error when read data from archive.");
            System.out.println(e.getMessage());
        }
    }

    public String processMessage(String str){
        String result = null;
        if(step == 0){
            result = MESSAGES[0];
            step = 1;
        }else if(step == 1){
            if(clients.containsKey(str)){
                result = MESSAGES[2];
            }
            else {
                try {
                    clients.put(str, new PrintWriter(socket.getOutputStream()) );
                }catch (IOException e){
                    System.out.println("Error when got a PrintWriter for broadcasting.");
                    System.out.println(e.getMessage());
                }
                name = str;
                step = 2;
                sendArchive();
                broadcastMessage(name + MESSAGES[3]);
                result = "send";
            }
        }else if(step == 2) {
            broadcastMessage(name + ": " + str);
            if(str.equals("Bye.")){
                step = 3;
                broadcastMessage(name + MESSAGES[4]);
                result = "Bye.";
            }else
                result = "send";
        }
        else{
            result = "Bye.";
        }
        return result;
    }

    public void broadcastMessage(String message){
        for(Map.Entry<String, PrintWriter> entry : clients.entrySet()){
            entry.getValue().println(message);
            entry.getValue().flush();
        }
    }

    public void clearUserName(){
        clients.remove(name);
    }

    public void sendArchive(){
        clients.get(name).println(MESSAGES[1]);
        clients.get(name).println("------ Archived chat messages, last 50 ------");
        for(String str : loadedArchive){
            clients.get(name).println(str);
            clients.get(name).flush();
        }
        clients.get(name).println("------ End of archived messages ------");
    }
}
