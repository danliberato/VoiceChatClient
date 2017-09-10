/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Runnables;

import Controller.ConnectionController;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author daan
 */
public class VoiceCaptureThread implements Runnable {

    private ConnectionController con;
    private final AudioFormat format;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private Control[] captureControls;
    private TargetDataLine lineIn;

    public VoiceCaptureThread(ConnectionController c, AudioFormat af) {
        this.format = af;
        this.con = c;
    }

    @Override
    public void run() {

        try {
            //Inicializa o microfone
            lineIn = AudioSystem.getTargetDataLine(format);
            lineIn.open(format);
            this.captureControls = lineIn.getControls();
            System.out.println("---------- Line In controls:");
            for (Control c : captureControls) {
                System.out.println("\t" + c.getType() + ": " + lineIn.getControl(c.getType()).toString());
            }
            lineIn.start();

            int numBytesRead, i = 0;
            byte[] targetData = new byte[con.BUFFER_SIZE];
            datagramSocket = new DatagramSocket();

            while (!con.getSocket().isClosed()) {
                numBytesRead = lineIn.read(targetData, 0, targetData.length);
                datagramPacket = new DatagramPacket(targetData, targetData.length, InetAddress.getByName(con.getServerIp()), con.getServerPort());

                if (numBytesRead == -1) {
                    break;
                }

                if (con.VOICE_ENABLE) {
                    datagramSocket.send(datagramPacket);
                }
                //System.out.println("num bytes = " + numBytesRead + " (" + (double) (numBytesRead / con.BUFFER_SIZE) + "KiB) ");
            }
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Audioline not supported");
            Logger.getLogger(VoiceCaptureThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(VoiceCaptureThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Server not found");
            Logger.getLogger(VoiceCaptureThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Error while sending packet");
            Logger.getLogger(VoiceCaptureThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public double volumeRMS(double[] raw) {
        double sum = 0d;
        if (raw.length == 0) {
            return sum;
        } else {
            for (int ii = 0; ii < raw.length; ii++) {
                sum += raw[ii];
            }
        }
        double average = sum / raw.length;

        double sumMeanSquare = 0d;
        for (int ii = 0; ii < raw.length; ii++) {
            sumMeanSquare += Math.pow(raw[ii] - average, 2d);
        }
        double averageMeanSquare = sumMeanSquare / raw.length;
        double rootMeanSquare = Math.sqrt(averageMeanSquare);

        return rootMeanSquare;
    }

}
