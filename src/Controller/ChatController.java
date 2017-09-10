/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Controller.Runnables.ListenerConnection;
import View.ChatView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author daan
 */
public class ChatController implements ActionListener {

    private final ChatView chatView;
    private ConnectionController con;
    private String userName;
    private VoiceController voiceThread;

    public ChatController(ConnectionController c, String name) {
        this.chatView = new ChatView();
        this.con = c;
        this.userName = name;
        this.initComponents();
    }

    private void initComponents() {
        this.listenerConnectionThread();
        chatView.setLocationRelativeTo(null);
        chatView.getTxtChat().append(userName + " -> Connected\r\n");
        chatView.setTitle("VoiceChatClient - " + userName);
        chatView.getTxtMessage().requestFocus();
        chatView.setVisible(true);
        chatView.getTxtMessage().addActionListener(this);
        chatView.getItemPreferences().addActionListener(this);
        chatView.getItemNewConnection().addActionListener(this);
        chatView.getBtnVoice().addActionListener(this);
        //Set autoscroll for chat area
        DefaultCaret chatCaret = (DefaultCaret) chatView.getTxtChat().getCaret();
        chatCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        chatView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        voiceThread = new VoiceController(this.con);
        chatView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                con.close();
                System.exit(0);
                chatView.setVisible(false);
                chatView.getTxtChat().setText("");
                chatView.getTxtFriends().setText("");
            }
        });

    }

    /**
     * Starts a thread that listen server's message
     */
    public void listenerConnectionThread() {

        ListenerConnection listener = new ListenerConnection(con, chatView);
        Thread t = new Thread(listener);
        t.start();
    }

    /**
     * *
     * It validates the message (doesn't allow empty messages) and send it to
     * server. Also reset the textfield
     *
     * @param operation String [connect][message][standby][disconnect]
     * @param msg String
     */
    public void sendMessage(String operation, String msg) {

        if (!msg.isEmpty()) {
            con.sendMessage(operation, msg);
            chatView.getTxtChat().append("Me -> " + chatView.getTxtMessage().getText() + "\r\n");
        }
        chatView.getTxtMessage().setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == chatView.getTxtMessage()) {
            this.sendMessage("message", chatView.getTxtMessage().getText());
        } else if (e.getSource() == chatView.getItemPreferences()) {
            //JPanel preferences = new InputPanel();
        } else if (e.getSource() == chatView.getBtnVoice()) {
            con.toggleVoiceCapture();
        }

    }
}
