package chatClient;

import java.io.*;

public class MessageReceiver implements Runnable{
    InputStream inputStream;

    public MessageReceiver(InputStream inputReader){
        this.inputStream = inputReader;
    }

    @Override
    public void run() {
        String inputStr;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream) ) ){
            while( !Thread.currentThread().isInterrupted() && (inputStr = reader.readLine()) != null){
                System.out.println(inputStr);
                if(inputStr.equals("Bye."))
                    break;
            }
        }catch (IOException e){
            System.out.println("Error when got server message.");
            System.out.println(e.getMessage());
        }
    }
}