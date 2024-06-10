package pl.kriskensy.wsb_java_divingsimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class represents a client for the diving simulator.
 */

public class DiveClient {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public DiveClient(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to update the depth of a student.
     */

    public void updateDepth(int studentId, double newDepth) {
        output.println("UPDATE_DEPTH," + studentId + "," + newDepth);
    }

    public void close() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}