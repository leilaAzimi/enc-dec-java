package Algorithm;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import AES.AES;

/**
 * Created by leilaaz on 11/14/2019 AD.
 */
public class Algorithm {


    public static int runDH ( int privateKey, int a, int q) {
        double power = Math.pow(a, privateKey);
        int v = (int) Math.round(power);
        return v % q;
    }

    public static String generateValidKey (int key) {
        String sharedKey = Integer.toString(key);
        String zero = "000000000000000000";
        zero = zero.substring(0, 17 - sharedKey.length());
        return zero + sharedKey;
    }
    public static void sendMessageWithoutENC (String message, Socket client) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(message);
        out.flush();
    }

    public static void sendMessage (String message, Socket client, String secretKey) {
        String encryptedString = AES.encrypt(message, secretKey) ;
        PrintWriter out = null;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(encryptedString);
        out.flush();
    }

    public static void sendFile (String address, Socket client, String secretKey) {
        File file = new File(address);
        String message="<File>";
        try {
            message+=file.getName();
            message+="</File>";
            message+=new String(Base64.getEncoder().encode(convertFileToByteArray(file)));
            sendMessage(message,client, secretKey);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static byte[] convertFileToByteArray(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        //System.out.println(file.exists() + "!!");
        //InputStream in = resource.openStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); //no doubt here is 0
                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                System.out.println("read " + readNum + " bytes,");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bos.toByteArray();
    }

    public static File convertBytesToFile (String fileName, byte[] bytes) throws IOException {
        File file = new File("./"+fileName);
        file.createNewFile();
        OutputStream os = new FileOutputStream(file);

        // Starts writing the bytes in it
        os.write(bytes);
        System.out.println("Successfully"
                + " byte inserted");

        // Close the file
        os.close();
        return file;
    }

}
