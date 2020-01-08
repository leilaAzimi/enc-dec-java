package Client;

/**
 * Created by leilaaz on 11/14/2019 AD.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import AES.AES;
import Algorithm.Algorithm;
import Info.ClientInfo;

//java -cp Client.jar Client.MyClientSocket 10.100.3.128 61455


public class MyClientSocket {
    private Socket socket;
    private Scanner scanner;
    private String secretKey = "ssshhhhhhhhhhh!!!!";
    public ClientInfo info = new ClientInfo("192.168.1.4", 23, 9, 0, 0);

    private MyClientSocket(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

    private void start() throws IOException {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String input;
                while (true) {
                    while (true) {
                        System.out.print("");
                        if (info.getPrivateKey() == 0 && info.getPublicKey() != 0) {
                            System.out.println("Enter your random number : ");
                            input = scanner.nextLine();
                            // generate private key and send it !
                            int privateKey = Algorithm.runDH(Integer.parseInt(input), info.getA(), info.getQ());
                            Algorithm.sendMessageWithoutENC(Integer.toString(privateKey), socket );
                            info.setPrivateKey(Integer.parseInt(input));
                            //generate shared key :
                            int sharedKey = Algorithm.runDH(info.getPrivateKey(), info.getPublicKey(), info.getQ());
                            info.setSharedKey(Algorithm.generateValidKey(sharedKey));
                            System.out.println("shared key: " + info.getSharedKey());
                        }
                        if (info.getSharedKey() != "null"){
                            System.out.println("now we can share !!!");
                            input = scanner.nextLine();
                            if(input.contains("/")) {
                                Algorithm.sendFile(input, socket, info.getSharedKey());
                            }
                            Algorithm.sendMessage(input, socket, info.getSharedKey());
                        }
                    }
                }
            }
        }.start();

        String data= null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        while ( (data = in.readLine()) != null ) {
            if( info.getPublicKey() == 0) {
                System.out.println("this public key received: " + data);
                info.setPublicKey(Integer.parseInt(data));
                System.out.println("public key" + info.getPublicKey());
            } else {
                //for sending messages after DH
                String decryptedString = AES.decrypt(data, info.getSharedKey());
                System.out.println("decrypted string : "+ decryptedString);
                if(decryptedString.startsWith("key")){
                    info.setSharedKey(decryptedString.replace("key", ""));
                    System.out.println("key updated :"+ info.getSharedKey());
                } else if(decryptedString.startsWith("<File>")){
                    decryptedString = decryptedString.replace("<File>", "");
                    int temp = decryptedString.indexOf("</File>");
                    String fileName = decryptedString.substring(0,temp);
                    decryptedString = decryptedString.substring(temp+7,decryptedString.length());
                    System.out.println("file name :"+ fileName + ":   ds  " + decryptedString);
                    File f = Algorithm.convertBytesToFile(fileName, Base64.getDecoder().decode(decryptedString));
                    System.out.println("file = "+ f.getPath() + f.getName());
                } else {
                    System.out.println("\r\nCipher: " + data + "message:"+decryptedString);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //generate a table for client
        ArrayList<ClientInfo> infoTable = new ArrayList<>();
        ClientInfo info1 = new ClientInfo("192.168.1.4", 23, 9, 0, 0);
        ClientInfo info2 = new ClientInfo("192.168.1.43", 20, 9, 0, 0);
        infoTable.add(info1);
        infoTable.add(info2);
        //find a serverName this client conected to!
        int selectedIndex = -1;
        for(int counter = 0; counter < infoTable.size(); counter++) {
         if(InetAddress.getByName(args[0]).toString() == infoTable.get(counter).getServerName()) {
            selectedIndex = counter;
         }
        }
        //if we have any information of server we can accept it!
        if(selectedIndex != -1) {
            MyClientSocket client = new MyClientSocket(
                    InetAddress.getByName(args[0]),
                    Integer.parseInt(args[1]));
            //System.out.println("first one :"+ client.info.get(0));
            System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
            System.out.println("waiting for receiving a public key from server!");
            client.start();
        } else {
            System.out.println("We have No information about this server please try another one!!");
        }
    }
}