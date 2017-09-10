/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Controller.Runnables.VoiceCaptureThread;
import Controller.Runnables.VoicePlayerThread;
import javax.sound.sampled.AudioFormat;

/**
 *
 * @author daan
 */
public class VoiceController {

    private ConnectionController con;
    private AudioFormat format;

    public VoiceController(ConnectionController c) {
        this.con = c;
        //                            Hz    Bit Ch Sign  BigEnd
        this.format = new AudioFormat(44100, 16, 2, true, false);
        this.voiceCaptureThread();
        this.voiceReceiverThread();
        System.out.println("Audio Format: " + format.toString());
    }

    /**
     * Starts a thread that captures user's voice and send it to server
     */
    public void voiceCaptureThread() {

        VoiceCaptureThread voice = new VoiceCaptureThread(con, format);
        Thread t = new Thread(voice);
        t.start();
    }

    /**
     * Starts a thread that receives data from server and convert it to audible
     * sound
     */
    public void voiceReceiverThread() {

        VoicePlayerThread voice = new VoicePlayerThread(con, format);
        Thread t = new Thread(voice);
        t.start();

    }
}
