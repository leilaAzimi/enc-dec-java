package Info;

/**
 * Created by leilaaz on 1/2/2020 AD.
 */
public class ServerInfo {
    public String clientName;
    private int a;
    private int q;
    private int publicKey;
    //Client Ya
    private int privateKey;
    private String sharedKey;

    public ServerInfo(String clientName, int a, int q, int publicKey, int privateKey) {
        this.clientName = clientName;
        this.a = a;
        this.q = q;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.sharedKey = "null";
    }

    public String getServerName() {
        return this.clientName;
    }

    public void setServerName(String clientName) {
        this.clientName = clientName;
    }

    public int getA() {
        return this.a;
    }

    public String getSharedKey() {
        return this.sharedKey;
    }
    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public int getQ() {
        return this.q;
    }

    public int getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey( int publicKey){
        this.publicKey = publicKey;
    }

    public int getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey (int privateKey) {
        this.privateKey = privateKey;
    }

}
