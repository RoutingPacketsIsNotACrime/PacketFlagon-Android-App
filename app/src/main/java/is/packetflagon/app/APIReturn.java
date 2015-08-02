package is.packetflagon.app;

import java.util.List;

/**
 * A very simple class for describing what the PacketFlagon API returns
 *
 * TODO: constructor that takes in the JSON object and populates everything
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
