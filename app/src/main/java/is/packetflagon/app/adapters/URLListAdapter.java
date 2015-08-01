package is.packetflagon.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import is.packetflagon.app.BlockedURL;
import is.packetflagon.app.R;

/**
 * Created by gareth on 01/08/15.
 */
public class URLListAdapter extends BaseAdapter {
    private List<BlockedURL> urls;
    private Context mContext;

    public URLListAdapter(List<BlockedURL> urllist, Context context)
    {
        this.urls = urllist;
        this.mContext = context;
    }

    public URLListAdapter()
    {
    }

    public URLListAdapter(JSONArray urls)
    {

    }

    public void addURL(String url)
    {
        urls.add(new BlockedURL(url,false));
        this.notifyDataSetChanged();
    }


    public String removeURL(int position)
    {
        String url = urls.get(position).url;
        urls.remove(position);
        this.notifyDataSetChanged();
        return url;
    }

    public String getURL(int position)
    {
        return urls.get(position).url;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int i) {

        return urls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup)
    {
        BlockedURL url = urls.get(i);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.url_list_item, null);
        }

        if(null != url)
        {
            Log.e("ListView", "Setting " + url);

            ((TextView) convertView.findViewById(R.id.pacURL)).setText(url.url);

            if(url.isBlocked) {
                ((ImageView) convertView.findViewById(R.id.blockStatus)).setImageResource(R.drawable.ic_content_blocked);
            }
            else
            {
                ((ImageView) convertView.findViewById(R.id.blockStatus)).setImageResource(R.drawable.ic_content_allowed);
            }
        }
        else {
            Log.e("ListView", "URL was null!" + Integer.toString(i));
        }

        return convertView;
    }
}
