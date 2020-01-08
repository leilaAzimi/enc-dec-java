package Client;

import java.util.ArrayList;

/**
 * Created by leilaaz on 1/2/2020 AD.
 */
public class InfoTable {
    private ArrayList<Info> table = new ArrayList<Info>();

    public InfoTable() {
        // a = 23 // q = 9 // we don't have any valid public key now and we set it 0
        this.addInfo("server!", 23, 9, 0, 4);
    }

    public void addInfo(String serverName, int a, int q, int publicKey, int privateKey) {
        Info info = new Info(serverName, a , q, publicKey, privateKey);
        table.add(info);
    }


}
