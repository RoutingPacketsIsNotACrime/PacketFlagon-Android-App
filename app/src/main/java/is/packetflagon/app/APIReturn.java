package is.packetflagon.app;

import java.util.List;

/**
 * Created by gareth on 15/07/15.
 */
public class APIReturn
{
    public String message = "";
    public boolean success = false;

    //PAC Specific
    public String hash = "";
    public String friendlyName = "";
    public String description = "";
    public boolean localProxy = false;
    public boolean passwordProtected = true;
    public String s3URL = "";
    public List<BlockedURL> urls;

}
