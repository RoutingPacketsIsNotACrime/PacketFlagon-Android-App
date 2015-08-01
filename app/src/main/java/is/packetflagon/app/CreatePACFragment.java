package is.packetflagon.app;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import is.packetflagon.app.cache.LocalCache;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePACFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePACFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText friendlyName;
    private EditText password;
    private EditText description;
    private EditText urlList;
    private Switch localProxy;
    private APIReturn apiReturn;
    private View progressBar;
    private PACListFragment.ManagePACListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreatePACFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePACFragment newInstance(String param1, String param2) {
        CreatePACFragment fragment = new CreatePACFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreatePACFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_pac, container, false);
        friendlyName = (EditText) rootView.findViewById(R.id.nameText);
        password = (EditText) rootView.findViewById(R.id.passwordText);
        description = (EditText) rootView.findViewById(R.id.descText);
        urlList = (EditText) rootView.findViewById(R.id.urlListText);
        localProxy = (Switch) rootView.findViewById(R.id.localRelaySwitch);
        progressBar = rootView.findViewById(R.id.progressBar);

        rootView.findViewById(R.id.createPACButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Button", "Clicked!");
                progressBar.setVisibility(View.VISIBLE);

                new AsyncTask<Void, Void, APIReturn>() {
                    @Override
                    protected APIReturn doInBackground(Void... params) {
                        try {
                            PacketFlagonAPI api = new PacketFlagonAPI(getString(R.string.apikey));
                            try {
                                return api.CreatePAC(friendlyName.getText().toString(), description.getText().toString(), password.getText().toString(), urlList.getText().toString(), localProxy.isChecked());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(APIReturn apiReturn)
                    {
                        progressBar.setVisibility(View.GONE);

                        if(apiReturn.success)
                        {
                            Toast.makeText(getActivity(),getString(R.string.pacSuccessToast) + apiReturn.hash,Toast.LENGTH_SHORT).show();

                            try {
                                LocalCache lc = new LocalCache(getActivity());
                                lc.open();
                                lc.addPAC(apiReturn.hash, friendlyName.getText().toString());
                                lc.close();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();

                            }

                            mListener.ManagePAC(apiReturn.hash);
                        }
                        else
                        {
                            Toast.makeText(getActivity(),getString(R.string.pacFailureToast) + apiReturn.message,Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(null, null, null);
            }
        });
        return rootView;
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
