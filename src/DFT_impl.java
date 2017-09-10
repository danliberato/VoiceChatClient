/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author daan
 */
public class DFT_impl {

    public static void main(String[] args) {
        short[] input = generateCosineWave(1764, 440, 44100);
        double[] result = windowHamming(input);

        System.out.print(analyzeNotes(result, 440));
    }

    public static short[] generateCosineWave(int length, double frequency, int samplingRate) {
        short[] data = new short[length];

        for (int i = 0; i < data.length; i++) {
            data[i] = (short) (32000 * Math.cos(2 * Math.PI * frequency / samplingRate * i));
        }

        return data;
    }

    public static double analyzeNotes(double[] audioData, double triedFrequency) {
        double freq = 2 * Math.PI * triedFrequency / 44100;

        double real = 0;
        double imaginary = 0;

        for (int j = 0; j < audioData.length; j++) {
            real += audioData[j] * Math.cos(freq * j) / audioData.length;
            imaginary += audioData[j] * Math.sin(freq * j) / audioData.length;
        }

        return (real * real + imaginary * imaginary) / 10;
    }

    public static double[] windowHamming(short[] data) {
        double[] result = new double[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] * (0.54 - 0.46 * Math.cos(2 * Math.PI * i / (data.length - 1)));
        }

        return result;
    }

}
