/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Runnables;

import Controller.ConnectionController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author daan
 */
public class VoicePlayerThread implements Runnable {

    private ConnectionController con;
    private AudioFormat format;
    private Control[] playControls;

    public VoicePlayerThread(ConnectionController c, AudioFormat af) {
        this.con = c;
        this.format = af;
    }

    @Override
    public void run() {

        try {

            //Inicializa o dispositivo de reprodução
            SourceDataLine lineOut = AudioSystem.getSourceDataLine(format);
            lineOut.open(format);
            this.playControls = lineOut.getControls();
            System.out.println("---------- Line Out controls:");
            for (Control c : playControls) {
                System.out.println("\t" + c.getType() + ": " + lineOut.getControl(c.getType()).toString());
            }
            lineOut.start();

            byte[] targetData = new byte[con.BUFFER_SIZE];

            while (true) {

                lineOut.write(targetData, 0, targetData.length);
            }
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Audioline not supported");
            Logger.getLogger(VoiceCaptureThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
