import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class ListenTest {
    private final EV3 ev3;
    private final TextLCD lcd;
    private final Keys keys;

    private ServerSocket serverSocket;


    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
//            System.out.println("Socket listener started on port " + port);
            this.lcd.clear();
            this.lcd.drawString("Listen " + port, 1, 1);
            this.lcd.drawString("Listen " + port, 1, 1);

            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();

            this.lcd.clear();
            this.lcd.drawString("Client connected", 1, 1);

            // Handle the client connection
            handleClient(clientSocket);

            // Close the client connection when done
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        // Do something with the client connection
        this.lcd.clear();
        this.lcd.drawString("C" + clientSocket.getInetAddress().getHostAddress(), 1, 1);

    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ListenTest listener = new ListenTest();
        listener.start(8080); // Start listening on port 8080
        listener.stop(); // Stop the listener
    }

    public ListenTest() {
        this.ev3 = (EV3) BrickFinder.getLocal();
        this.lcd = ev3.getTextLCD();
        this.keys = ev3.getKeys();

    }
}
