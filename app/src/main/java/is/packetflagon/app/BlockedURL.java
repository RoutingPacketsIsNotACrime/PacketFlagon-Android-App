package is.packetflagon.app;

/**
 * Created by gareth on 01/08/15.
 */
public class BlockedURL {
    public String url;
    public Boolean isBlocked = false;

    public BlockedURL(String url, Boolean blocked)
    {
        this.url = url;
        this.isBlocked = blocked;
    }
}
