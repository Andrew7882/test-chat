package chatClient;

import java.io.*;

public class MessageSender implements Runnable{
    PrintWriter printWriter;

    public MessageSender(PrintWriter printWriter){
        this.printWriter = printWriter;
    }

    @Override
    public void run() {
        try(BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("User's input catcher started.");
            String clientInput;
            while (!Thread.currentThread().isInterrupted() && (clientInput = sysIn.readLine()) != null) {
                printWriter.println(clientInput);
                if (clientInput.equals("Bye.")) {
                    break;
                }
            }
            System.out.println("Catcher stopped.");
        }catch (IOException e){
            System.out.println("Error when got user's input.");
            System.out.println(e.getMessage());
        }
    }
}