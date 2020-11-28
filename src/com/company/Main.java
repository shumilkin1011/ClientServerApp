package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    ArrayList<PrintWriter> clientsOutStreams;

    public static void main(String[] args) {
	    Main main = new Main();
	    main.go();
    }

    public void go() {
        clientsOutStreams = new ArrayList<PrintWriter>();
        try {
            ServerSocket serverSocket = new ServerSocket(6969);
            while(true) {
                Socket newClientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(newClientSocket.getOutputStream());
                clientsOutStreams.add(writer);
                sendMessageToEveryone("NEW USER JOINED:"+newClientSocket.getRemoteSocketAddress());

                Thread t = new Thread(new ClientHandler(newClientSocket));
                t.start();
                System.out.println("new connection!");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {
        Socket sock;
        BufferedReader reader;

        public ClientHandler(Socket newSocket) {
            try {
                this.sock = newSocket;
                InputStreamReader inputStreamReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            }
            catch (IOException ex) { ex.printStackTrace(); }

        }
        @Override
        public void run() {
            String message;
            try {

                while ( (message = reader.readLine()) != null) {
                    sendMessageToEveryone(message);
                }
            }
            catch (IOException ex) { ex.toString(); }

        }
    }

    public void sendMessageToEveryone(String message) {
        for(PrintWriter writer : clientsOutStreams) {
            writer.println(message);
            writer.flush();
        }
    }
}
