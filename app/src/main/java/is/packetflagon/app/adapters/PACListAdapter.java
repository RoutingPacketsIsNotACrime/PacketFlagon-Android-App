package is.packetflagon.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import is.packetflagon.app.PAC;
import is.packetflagon.app.R;
import is.packetflagon.app.cache.LocalCache;

/**
 * Created by gareth on 01/08/15.
 */
public class PACListAdapter extends BaseAdapter {

    private List<PAC> pacList = null;
    private Context mContext;
    private LocalCache lc;

    public PACListAdapter(Context context, List<PAC> results) {
        mContext = context;
        pacList = results;
    }

    public PACListAdapter(Context context) {
        mContext = context;
        Log.e("ListView", "Creating context");
        populateCache();
    }

    private void populateCache() {
        if (null == lc)
            lc = new LocalCache(mContext);

        lc.open();
        pacList = lc.getPACs();
        lc.close();
        this.notifyDataSetChanged();
        Log.e("ListView", "Notified dataset");
    }

    public void updatePACsList(List<PAC> pacs) {
        pacList = pacs;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pacList.size();
    }

    @Override
    public Object getItem(int position) {
        return pacList.get(position);
    }

    public String getPACHash(int position)
    {
        return pacList.get(position).Hash;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PAC pac = pacList.get(position);
        Log.e("ListView", "Found Position" + Integer.toString(position));

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = inflater.inflate(R.layout.relay_grid, null);
            convertView = inflater.inflate(R.layout.pac_list_item, null);
        }

        if(null != pac)
        {
            Log.e("ListView", "Setting " + pac.Name + " and " + pac.Hash);

            ((TextView) convertView.findViewById(R.id.pacTitle)).setText(pac.Name);
            ((TextView) convertView.findViewById(R.id.pacHash)).setText(pac.Hash);
        }
        else {
            Log.e("ListView", "PAC was null!" + Integer.toString(position));
        }

        return convertView;
    }
}
