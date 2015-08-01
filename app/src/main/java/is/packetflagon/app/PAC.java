package is.packetflagon.app;

/**
 * Created by gareth on 27/07/15.
 */
public class PAC {
    public String Name = "Example PAC Name";
    public String Description = "Example PAC Description";
    public String Hash = "";
    public boolean LocalProxy = false;
    public boolean PasswordProtected = false;

    public PAC(String hash, String name)
    {
        this.Name = name;
        this.Hash = hash;
    }
}
