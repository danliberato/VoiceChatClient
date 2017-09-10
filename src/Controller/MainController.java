package Controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import View.MainView;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author daan
 */
public class MainController implements ActionListener {

    private static final long serialVersionUID = 1L;
    protected final MainView mainView;
    private String userName;
    private String serverIp;
    private Integer userPort;
    private String userRoom;
    private char[] userPass;
    private ConnectionController connection;

    /**
     * Constructor
     */
    public MainController() {
        this.mainView = new MainView();
        this.initComponents();
    }

    /**
     * *
     * Method used to connect to the server socket, retorns an IO Exception.
     *
     * @return
     */
    public boolean connect() {

        this.userName = mainView.getTxtName().getText();
        this.serverIp = mainView.getTxtIp().getText();
        this.userPort = Integer.parseInt(mainView.getTxtPort().getText());
        this.userRoom = mainView.getTxtRoom().getText();
        this.userPass = mainView.getTxtPass().getPassword();

        this.connection = new ConnectionController(userName, serverIp, userPort, userRoom, userName);

        return connection.connect();

    }

    private void initComponents() {
        mainView.getTxtName().requestFocus();
        mainView.setVisible(true);
        mainView.setLocationRelativeTo(null);
        //add the listener to make things (events) work
        mainView.getBtnConnect().addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if ((e.getSource() == this.mainView.getBtnConnect()) && this.checkFieldsMainView()) {

            if (this.connect()) {
                //Initiates ChatController
                ChatController chat = new ChatController(connection, userName);
                mainView.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(new Frame(), "Connection error! (timed out)");

            }
        }
    }

    private boolean checkFieldsMainView() {
        if (mainView.getTxtIp().getText().isEmpty()) {
            JOptionPane.showMessageDialog(new Frame(), "Server IP cannot be empty.");
            mainView.getTxtIp().setFocusable(true);
            return false;
        } else if (mainView.getTxtPort().getText().isEmpty()) {
            JOptionPane.showMessageDialog(new Frame(), "Port cannot be empty.");
            mainView.getTxtPort().setFocusable(true);
            return false;
        } else if (mainView.getTxtName().getText().isEmpty()) {
            JOptionPane.showMessageDialog(new Frame(), "Name cannot be empty.");
            mainView.getTxtName().setFocusable(true);
            return false;
        }
        return true;
    }

}
