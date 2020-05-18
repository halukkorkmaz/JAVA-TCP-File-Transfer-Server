/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8.networklab.pkg2020.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author abdulhalik
 * @author halil
 * 
 * Ekleme yaptığımız her kod boluğu için yorum satırı eklemeye çalışalım, anlaşılır olsun.
 */
public class TCP_Server {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    
    private ObjectInputStream inputStream = null;
    private FileOutputStream fileOutputStream = null;
    
    private HashSet<ObjectOutputStream> allClients = new HashSet<>();
    
    private Thread serverThread;
    
    public static void main(String[] args) throws IOException {
        int port = 4445;
        new TCP_Server().Start(port);
    }
    
    protected void Start (int port) throws IOException {
        
        serverSocket = new ServerSocket(port);
        System.out.println("Server başlatıldı ..");
        
        
        serverThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // blocking call, yeni bir client bağlantısı bekler
                    socket = serverSocket.accept();
                    System.out.println("Yeni bir client bağlandı : " + socket);
                    

                    // bağlanan her client için bir thread oluşturup dinlemeyi başlat
                    new ListenThread(socket).start();
                } catch (IOException ex) {
                    System.out.println("Hata - new Thread() : " + ex);
                    break;
                }
            }
        });
        serverThread.start();
    }

    /**
     * Socket bağlantılarının kabul edilmesi.
     */

    protected void stop() throws IOException {
        // bütün streamleri ve soketleri kapat
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }
    
    class ListenThread extends Thread {

        // dinleyeceğimiz client'ın soket nesnesi, input ve output stream'leri
        private final Socket clientSocket;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOutput;
        private FileEvent fileEvent;
        private File dstFile = null;

        private ListenThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Bağlanan client için thread oluşturuldu : " + this.getName());

            try {
                // input  : client'dan gelen mesajları okumak için
                // output : server'a bağlı olan client'a mesaj göndermek için
                clientInput = new ObjectInputStream(clientSocket.getInputStream());
                clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                
                // FileEvent nesnesini okuma ve dosyayı diske kopyalama.
                System.out.println("Merhaba, " + socket);
                //------------------------------
                fileEvent = (FileEvent) clientInput.readObject();
                while(fileEvent != null) {
                    try {
                        fileEvent = (FileEvent) clientInput.readObject();
                        
                        if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                            System.out.println("Error occurred..");
                            //System.exit(0);
                        }
                        String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
                        if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                            new File(fileEvent.getDestinationDirectory()).mkdirs();
                        }
                        dstFile = new File(outputFile);
                        fileOutputStream = new FileOutputStream(dstFile);
                        fileOutputStream.write(fileEvent.getFileData());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        System.out.println("Output file : " + outputFile + " başarı ile kaydedildi. ");
                        Thread.sleep(3000);
                        //System.exit(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                

            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Hata - ListenThread : " + ex);
            } finally {
                try {
                    // client'ların tutulduğu listeden çıkart
                    allClients.remove(clientOutput);

                    // bütün client'lara ayrılma mesajı gönder
                    for (ObjectOutputStream out : allClients) {
                        out.writeObject(this.getName() + " server'dan ayrıldı.");
                    }

                    // bütün streamleri ve soketleri kapat
                    if (clientInput != null) {
                        clientInput.close();
                    }
                    if (clientOutput != null) {
                        clientOutput.close();
                    }
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                    System.out.println("Soket kapatıldı : " + clientSocket);
                } catch (IOException ex) {
                    System.out.println("Hata - Soket kapatılamadı : " + ex);
                }
            }
        }
    }

}

