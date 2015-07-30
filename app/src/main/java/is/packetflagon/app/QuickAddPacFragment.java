package is.packetflagon.app;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import is.packetflagon.app.cache.LocalCache;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuickAddPacFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuickAddPacFragment extends Fragment {


    private PACListFragment.ManagePACListener mListener;
    private EditText hashIDET;
    private View progressBar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuickAddPacFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuickAddPacFragment newInstance() {
        QuickAddPacFragment fragment = new QuickAddPacFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public QuickAddPacFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quick_add_pac, container, false);

        Spanned htmlText = Html.fromHtml(getString(R.string.quickAddDesc));
        ((TextView) rootView.findViewById(R.id.quickAddDesc)).setText(htmlText);
        hashIDET = (EditText) rootView.findViewById(R.id.pacIDET);
        progressBar = rootView.findViewById(R.id.progressBar);

        ((Button) rootView.findViewById(R.id.quickAddButon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                AddPACToDB(hashIDET.getText().toString());
                //mListener.ManagePAC(hashIDET.getText().toString());
            }
        });
        return rootView;
    }

    private void AddPACToDB(final String Hash)
    {
        new AsyncTask<Void, Void, APIReturn>() {
            @Override
            protected APIReturn doInBackground(Void... params) {
                try {
                    PacketFlagonAPI api = new PacketFlagonAPI(getString(R.string.apikey));
                    try {
                        return api.GetPacDetails(Hash);
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
                if(pac != null) {
                    if (pac.success)
                    {
                        Toast.makeText(getActivity(), getString(R.string.quickAddSuccess) + " " +  pac.friendlyName, Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.GONE);

                        try {
                            LocalCache lc = new LocalCache(getActivity());
                            lc.open();
                            lc.addPAC(hashIDET.getText().toString(), pac.friendlyName);
                            lc.close();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }

                        mListener.ManagePAC(hashIDET.getText().toString());

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getString(R.string.getPACFail) + " " + pac.message, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.getPACFail) + " Returned entity was null", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PACListFragment.ManagePACListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ManagePACListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
