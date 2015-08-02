package is.packetflagon.app;

/**
 * Very simple little class to describe a URL that is returned from the
 * get_pac API request
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
