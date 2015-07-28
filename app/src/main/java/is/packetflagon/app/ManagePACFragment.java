package is.packetflagon.app;


import android.content.ClipData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagePACFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagePACFragment extends Fragment
{
    private static final String ARG_PARAM1 = "hash";
    private String pacHash;
    private View progressBar;
    private TextView pacNameTextView;
    private TextView pacDescTextView;
    private ImageView pacPasswordProtectImageView;

    /**
     * @param hash The PAC hash
     * @return A new instance of fragment ManagePACFragment.
     */
    public static ManagePACFragment newInstance(String hash) {
        ManagePACFragment fragment = new ManagePACFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, hash);
        fragment.setArguments(args);
        return fragment;
    }

    public ManagePACFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pacHash = getArguments().getString(ARG_PARAM1);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getArguments().getString(ARG_PARAM1));
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_pac, container, false);
        ((TextView) rootView.findViewById(R.id.pacNameText)).setText("Fetching " + pacHash);
        progressBar = rootView.findViewById(R.id.progressBar);
        pacNameTextView = (TextView) rootView.findViewById(R.id.pacNameText);
        pacDescTextView = (TextView) rootView.findViewById(R.id.descTV);
        pacPasswordProtectImageView = (ImageView) rootView.findViewById(R.id.lockedIV);
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        new AsyncTask<Void, Void, APIReturn>() {
            @Override
            protected APIReturn doInBackground(Void... params) {
                try {
                    PacketFlagonAPI api = new PacketFlagonAPI(getString(R.string.apikey));
                    try {
                        return api.GetPacDetails(pacHash);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(APIReturn pac)
            {
                if(pac.success) {
                    pacNameTextView.setText(pac.friendlyName);
                    pacDescTextView.setText(pac.description);

                    if (pac.passwordProtected) {
                        pacPasswordProtectImageView.setImageResource(R.drawable.ic_action_lock_outline);
                    } else {
                        pacPasswordProtectImageView.setImageResource(R.drawable.ic_action_lock_open);
                    }
                }
                else
                {
                    pacNameTextView.setText(pacHash);
                    pacDescTextView.setText("...");
                    Toast.makeText(getActivity(),getString(R.string.getPACFail) + " " + pac.message,Toast.LENGTH_LONG).show();
                }

                progressBar.setVisibility(View.GONE);
            }
        }.execute(null, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId())
        {
            case R.id.action_pushtos3: {

                progressBar.setVisibility(View.VISIBLE);

                new AsyncTask<Void, Void, APIReturn>() {
                    @Override
                    protected APIReturn doInBackground(Void... params) {
                        try {
                            PacketFlagonAPI api = new PacketFlagonAPI(getString(R.string.apikey));
                            try {
                                return api.PushToS3(pacHash);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(APIReturn pac)
                    {
                        if(pac.success) {
                            Toast.makeText(getActivity(), getString(R.string.pushToS3Success), Toast.LENGTH_SHORT).show();

                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("S3 PAC URL",pac.s3URL);
                            clipboard.setText(pac.s3URL);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), getString(R.string.pushToS3Fail) + " " + pac.message, Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                }.execute(null, null, null);

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
