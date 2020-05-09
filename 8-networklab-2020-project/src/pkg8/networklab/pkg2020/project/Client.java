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
    private boolean isConnected = false;
    private FileEvent fileEvent = null;
    // ----- TEST -----
    // Path'i kendi dosya sisteminize göre değiştrirerek test edin!!!!!
    private String sourceFilePath = "/Users/TEST/Desktop/SouTesting/dosya.pdf";
    private String destinationPath = "/Users/TEST/Desktop/DesTesting/";

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

    /**
     * Dosya objesini yolla.
     */
    public void sendFile() {
        fileEvent = new FileEvent();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(sourceFilePath);
        File file = new File(sourceFilePath);
        if (file.isFile()) {
            try {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead = 0;
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
                    read = read + numRead;
                }
                fileEvent.setFileSize(len);
                fileEvent.setFileData(fileBytes);
                fileEvent.setStatus("Success");
            } catch (Exception e) {
                e.printStackTrace();
                fileEvent.setStatus("Error");
            }
        } else {
            System.out.println("Belirtilen konumda dosya bulunamadi.");
            fileEvent.setStatus("Error");
        }
//Dosya objesini sokete yazma.
        try {
            outputStream.writeObject(fileEvent);
            System.out.println("Oturum sonlandırıldı..");
            Thread.sleep(3000);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
        client.sendFile();
    }
}
