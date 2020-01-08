package Client;

/**
 * Created by leilaaz on 1/2/2020 AD.
 */
public class Info {
    private String serverName;
    private int a;
    private int q;
    private int publicKey;
    private int privateKey;

    public Info(String serverName, int a, int q, int publicKey, int privateKey) {
        this.serverName = serverName;
        this.a = a;
        this.q = q;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getA() {
        return a;
    }

    public int getQ() {
        return q;
    }

    public int getPublicKey() {
        return publicKey;
    }

    public void setPublicKey( int publicKey){
        this.publicKey = publicKey;
    }

    public int getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey (int privateKey) {
        this.privateKey = privateKey;
    }

}
