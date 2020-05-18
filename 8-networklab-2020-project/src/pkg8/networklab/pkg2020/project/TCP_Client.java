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
public class TCP_Client {

    private Socket socket = null;

    private boolean isConnected = false;
    private FileEvent fileEvent = null;

    private ObjectOutputStream outputStream = null;
    private ObjectInputStream clientInput;

    private Thread clientThread;
    
    public TCP_Client() {

    }
    
    protected void start(String host, int port) throws IOException {
        // client soketi oluşturma (ip + port numarası)
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        // input  : client'a gelen mesajları okumak için
        // output : client'dan bağlı olduğu server'a mesaj göndermek için
        // server'ı sürekli dinlemek için Thread oluştur
        clientThread = new ListenThread();
        clientThread.start();
    }

    /**
     * Dosya objesini yolla.
     */
    public void sendFile(String sourceFilePath, String destinationPath) throws IOException {
        
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
            System.out.println("Başarılı.. " + fileEvent.getFilename());
            Thread.sleep(3000);
            //System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    

    protected void disconnect() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (clientInput != null) {
            clientInput.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (socket != null) {
            socket.close();
        }
    }
    
        class ListenThread extends Thread {

        // server'dan gelen mesajları dinle
        @Override
        public void run() {
            System.out.println("Server'a bağlandı ..");

        }
    }
    
    
    /*public static void main(String[] args) {
        TCP_Client client = new TCP_Client();
        client.connect();
        client.sendFile();
    }*/
}
