package Server;

/**
 * Created by leilaaz on 12/15/2019 AD.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import AES.AES;
import java.io.*;
import Algorithm.Algorithm;
import Info.ServerInfo;

//java -cp Server.jar Server/MyServerSocket

public class MyServerSocket {
    private ServerSocket server;
    private Scanner scanner;
    private String secretKey = "ssshhhhhhhhhhh!!!!";
    private long lastKeyDate;
    public ServerInfo info = new ServerInfo("192.168.1.4", 23, 9, 0, 0);

    public MyServerSocket(String ipAddress) throws Exception {
        this.scanner = new Scanner(System.in);
        if (ipAddress != null && !ipAddress.isEmpty())
            this.server = new ServerSocket(61455, 1, InetAddress.getByName(ipAddress));
        else
            this.server = new ServerSocket(61455, 1, InetAddress.getLocalHost());

    }

    private String generate_key(int n) {
        lastKeyDate= (System.currentTimeMillis()+ 3*6*1000);
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        String key =  sb.toString();
        key = key + ":" + lastKeyDate;

        return key;
    }

    private void setTimer(Socket client){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String newSecretKey = generate_key(18);
                Algorithm.sendMessage("key" + newSecretKey, client, info.getSharedKey());
                info.setSharedKey(newSecretKey);
                System.out.println("generated key :"+ newSecretKey);
                setTimer(client);
            }
        },30*1000);
    }
    private void listen() throws Exception {
        String data = null;
        Socket client = this.server.accept();
        String clientAddress = client.getInetAddress().getHostAddress();
        System.out.println("\r\nNew connection from " + clientAddress);
        setTimer(client);
        new Thread(){
            @Override
            public void run() {
                super.run();
                String input;
                while (true) {
                    System.out.print("");
                    if( info.getPrivateKey() == 0 ) {
                        System.out.println("Enter your random number : ");
                        input = scanner.nextLine();
                        // generate private key and send it !
                        info.setPrivateKey(Integer.parseInt(input));
                        int privateKey = Algorithm.runDH(Integer.parseInt(input), info.getA(), info.getQ());
                        Algorithm.sendMessageWithoutENC(Integer.toString(privateKey), client );
                        System.out.println("waiting for receiving a public key from server!");

                    } else if( info.getSharedKey() != "null") {
                        System.out.println("now we can share !!!");
                        input = scanner.nextLine();
                        if(input.contains("/")) {
                            Algorithm.sendFile(input, client, info.getSharedKey());
                        } else {
                            Algorithm.sendMessage(input, client, info.getSharedKey());
                        }
                    }
                }
            }
        }.start();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
        while ( (data = in.readLine()) != null ) {
            if(info.getPublicKey() == 0 ){
                System.out.println("this random number received: " + data);
                info.setPublicKey(Integer.parseInt(data));
                //generate shared key :
                int sharedKey = Algorithm.runDH(info.getPrivateKey(), info.getPublicKey(), info.getQ());
                info.setSharedKey(Algorithm.generateValidKey(sharedKey));
                System.out.println("shared key: " + info.getSharedKey());
            } else {
                //Fter runnug DH algorithm
                String decryptedString = AES.decrypt(data, info.getSharedKey());
                if (decryptedString.startsWith("<File>")) {
                    System.out.println("file!");
                    decryptedString = decryptedString.replace("<File>", "");
                    int temp = decryptedString.indexOf("</File>");
                    String fileName = decryptedString.substring(0, temp);
                    decryptedString = decryptedString.substring(temp + 7, decryptedString.length());
                    System.out.println("file name :" + fileName + ":   ds  " + decryptedString);
                    File f = Algorithm.convertBytesToFile(fileName, Base64.getDecoder().decode(decryptedString));
                    System.out.println("file = " + f.getPath() + f.getName());
                } else {
                    System.out.println("\r\nCipher: " + data);
                    System.out.println("message : " + decryptedString);
                }
            }
        }
    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    public int getPort() {
        return this.server.getLocalPort();
    }
    public static void main(String[] args) throws Exception {
        MyServerSocket app = new MyServerSocket("");
        System.out.println("\r\nRunning Server: " +
                "Host=" + app.getSocketAddress().getHostAddress() +
                " Port=" + app.getPort());
        app.listen();
    }
}