package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;

public class Main {
    JFrame frame;
    JTextArea outgoing;
    JTextArea incText;
    JTextArea usersList;
    JButton sendButton;
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;
    ArrayList<String> IPs;

    public static void main(String[] args) {
        Main main = new Main();
        main.go();
    }

    public void go() {
        IPs = new ArrayList<String>();
        initGUI();
        initConnection();
        Thread thread = new Thread(new BackgroundReader());
        thread.start();
    }


    public void initGUI() {
        frame = new JFrame("Simple chat");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        JPanel mainPanel = new JPanel();
        JPanel viewPanel = new JPanel();

        JLabel userListLbl = new JLabel("Список пользователей");
        userListLbl.setHorizontalAlignment(SwingConstants.CENTER);
        usersList = new JTextArea(20, 12);
        usersList.setEditable(false);
        JScrollPane userScroller = new JScrollPane(usersList);
        userScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        userScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        viewPanel.setLayout(new BorderLayout());
        viewPanel.add(userListLbl, BorderLayout.NORTH);
        viewPanel.add(userScroller, BorderLayout.CENTER);

        incText = new JTextArea(20, 25);
        incText.setLineWrap(true);
        incText.setWrapStyleWord(true);
        incText.setEditable(false);
        JScrollPane scroller = new JScrollPane(incText);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outgoing = new JTextArea(2, 25);
        outgoing.setLineWrap(true);
        outgoing.setToolTipText("Введите ваше сообщение");
        outgoing.addKeyListener(new sendKeyListener());

        sendButton = new JButton("SEND MESSAGE");
        sendButton.addActionListener(new ButtonListener());
        sendButton.setPreferredSize(new Dimension(50, 38));


        mainPanel.setLayout(new BorderLayout());

        JLabel mainLbl = new JLabel("Тестовая версия чат-клиента");

        mainLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.add(mainLbl, BorderLayout.NORTH);
        subPanel.add(scroller, BorderLayout.CENTER);
        //subPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));

        mainPanel.add(subPanel);
        mainPanel.add(outgoing, BorderLayout.SOUTH);
        viewPanel.add(sendButton, BorderLayout.SOUTH);

        incText.setFont(new Font("TimesNewRoman", Font.PLAIN, 12));
        outgoing.setFont(new Font("TimesNewRoman", Font.BOLD, 14));
        viewPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        subPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        frame.getContentPane().add(viewPanel, BorderLayout.EAST);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
        outgoing.grabFocus();
    }



    public void initConnection() {
        try {
            sock = new Socket("192.168.1.68", 6969);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class sendKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if(keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                keyEvent.consume();
                sendButton.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {

        }
    }

    public class BackgroundReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("client read " + message);
                    if(message.contains("NEW USER JOINED:")) {
                        String[] strMas = message.split("/");
                        String str = strMas[strMas.length-1];
                        if(!IPs.contains(str)) {
                            usersList.append(str + "\n");
                            IPs.add(str);
                        }
                        continue;
                    }
                    incText.append(sock.getRemoteSocketAddress().toString().split("/")[1] + ": " + message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            writer.println(outgoing.getText());
            writer.flush();
            outgoing.setText("");
        }
    }
}
