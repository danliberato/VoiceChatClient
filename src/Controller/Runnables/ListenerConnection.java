/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Runnables;

import Controller.ConnectionController;
import View.ChatView;
import java.awt.Frame;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author daan
 */
public class ListenerConnection implements Runnable {

    private final ConnectionController con;
    private final ChatView view;
    private SimpleDateFormat sdf;
    private ArrayList<String> friendsList;

    public ListenerConnection(ConnectionController c, ChatView chatView) {
        this.con = c;
        this.view = chatView;
        this.sdf = new SimpleDateFormat("HH:mm:ss");
    }

    @Override
    public void run() {

        try {

            String msg = "";
            String dataFormatada = "";

            while ((msg = con.getBufferedReader().readLine()) != null) {

                if (msg != null && !msg.equals("")) {
                    if (msg.equals("!kicked")) {
                        con.getSocket().close();
                        System.out.println("Client kicked by Server!");
                        msg = "You were kicked!";
                        view.getTxtChat().append(" " + msg + "\r\n");
                        JOptionPane.showMessageDialog(new Frame(), msg);
                        view.getTxtMessage().setEditable(false);
                        break;
                    } else if (msg.equals("!shutdown")) {
                        con.getSocket().close();
                        System.out.println("Server disconnected!");
                        msg = "Server was shutdown!";
                        JOptionPane.showMessageDialog(new Frame(), msg);
                        view.getTxtChat().append(" " + msg + "\r\n");
                        view.getTxtMessage().setEditable(false);
                        break;
                    } else if (msg.equals("!banned")) {
                        con.getSocket().close();
                        System.out.println("Client kicked by Server!");
                        msg = "Sorry, you have been banned from chat! \nContact server admin for more info.";
                        view.getTxtChat().append(" " + msg + "\r\n");
                        JOptionPane.showMessageDialog(new Frame(), msg);
                        view.getTxtMessage().setEditable(false);
                        break;
                    } else if (msg.substring(0,3).equals("!CL")) {
                        msg = msg.substring(4);
                        msg = msg.replace(",", "\r\n").replace("]", "");
                        view.getTxtFriends().setText(" "+msg);
                    } else {
                        Date hora = Calendar.getInstance().getTime();// format the date
                        dataFormatada = sdf.format(hora);
                        view.getTxtChat().append(" [" + dataFormatada + "] " + msg + "\r\n");
                    }
                }

            }
        } catch (SocketException se) {
            switch (se.getMessage()) {
                case "Connection reset":
                    view.getTxtChat().append("- Server is down! \r\n");
                    break;
                case "Socket closed":
                    view.getTxtChat().append("- Connection lost! \r\n");
                    break;
                default:
                    se.printStackTrace();
            }
        } catch (IOException ex) {
            Logger.getLogger(ListenerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
