/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8.networklab.pkg2020.project;

import java.io.*;
import java.net.Socket;

/**
 * @author fatih
 * @author samet
 *
 * Ekleme yaptığımız her kod boluğu için yorum satırı eklemeye çalışalım, anlaşılır olsun.
 */
public class Client {

    private Socket socket = null;
    private ObjectOutputStream outputStream = null;

    public Client() {

    }

    /**
     * Localhost veya diğer hostlar ile server'a bağlan.
     */
    public void connect() {
        while (!isConnected) {
            try {
                socket = new Socket("localHost", 4445);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}
