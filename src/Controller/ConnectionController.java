/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daan
 */
public class ConnectionController {

    private Socket socket;
    private OutputStream outputStream;
    private Writer outputStreamWriter;
    private BufferedWriter bufferedWriter;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private final String userName;
    private final String serverIp;
    private final Integer serverPort;
    private final String serverRoom;
    private final String serverPass;
    public final int BUFFER_SIZE = 256;
    public boolean VOICE_ENABLE = true;
    private Integer PING_TIMEOUT;

    public ConnectionController(String userName, String serverIp, Integer serverPort, String serverRoom, String userPass) {
        this.userName = userName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.serverRoom = serverRoom;
        this.serverPass = userPass;
        this.PING_TIMEOUT = 3000;
    }

    public boolean connect() {
        String clientAuth = "";

        try {
            /**
             * Creates a new socket with the server, also creates the output
             * streams and prepares the buffer *
             */
            InetAddress address = InetAddress.getByName(serverIp);
            if(address.isReachable(PING_TIMEOUT)){
                socket = new Socket(serverIp, this.serverPort);
                outputStream = socket.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                //clientAuth = userName+"|"+Arrays.toString(userPass)+"|"+userRoom+"|"+this.getNetworkInterface();
                clientAuth = userName + "|" + this.getNetworkInterface();
                this.sendMessage("connect", clientAuth);
            }else{
                throw new IOException();
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;

    }

    /**
     * private String getNetworkInterface() Get localhost hardware addres (MAC
     * Address) and format in hexadecimal
     *
     * @return MAC Address of this client in Hexadecimal format
     */
    private String getNetworkInterface() {
        String mac = "";
        try {
            InetAddress address = InetAddress.getLocalHost();

            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            byte[] harwareAddress = ni.getHardwareAddress();

            /* Here begins some magic that transforms bytes into a formatted string*/
            for (int i = 0; i < harwareAddress.length; i++) {
                mac += String.format("%02X%s", harwareAddress[i], (i < harwareAddress.length - 1) ? "-" : "");
            }
        } catch (SocketException | UnknownHostException e) {
            e.getStackTrace();
        }

        return mac;

    }

    /**
     * *
     * It validates the message (doesn't allow empty messages) and send it to
     * server. Also reset the textfield
     *
     * @param operation String [!connect][!message][!standby][!disconnect]
     * @param msg String
     * @return true if message was sent OR false if it wasn't
     */
    public boolean sendMessage(String operation, String msg) {

        try {
            this.writeBufferedWriter("!" + operation + "|" + msg + "\r\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sends a message to server through a BufferedWriter (that is how occurs
     * the "client > server" communication)
     *
     * @param msg
     * @throws java.io.IOException
     */
    public void writeBufferedWriter(String msg) throws IOException {
        this.bufferedWriter.write(msg);
        this.bufferedWriter.flush();
    }

    /**
     * *
     * Close all connections. Sends a "disconnect" message to the server, closes
     * everything
     */
    public void close() {

        try {
            this.sendMessage("disconnect", "");
            socket.close();//closing the socket will also close socket's buffer writer/reader and IOstream
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getUserName() {
        return userName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getServerRoom() {
        return serverRoom;
    }

    public String getServerPass() {
        return serverPass;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public BufferedReader getBufferedReader() {
        return this.bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return this.bufferedWriter;
    }

    public void toggleVoiceCapture() {
        this.VOICE_ENABLE = !this.VOICE_ENABLE;
    }
}
