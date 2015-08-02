package is.packetflagon.app;

import java.util.List;

/**
 * An extension of the APIReturn class, a very simple class to contain / describe a PAC
 */
public class PAC {
    public String Name = "Example PAC Name";
    public String Description = "Example PAC Description";
    public String Hash = "";
    public boolean LocalProxy = false;
    public boolean PasswordProtected = false;
    public List<BlockedURL> urlList;

    public PAC(String hash, String name)
    {
        this.Name = name;
        this.Hash = hash;
    }
}
