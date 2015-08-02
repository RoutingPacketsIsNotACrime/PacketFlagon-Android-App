package is.packetflagon.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddURLDialog.AddURLFragmentListener} interface
 * to handle interaction events.
 * Use the {@link AddURLDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddURLDialog extends DialogFragment
{
    private AddURLFragmentListener mListener;
    private View dialogView;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddURLDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static AddURLDialog newInstance(String param1, String param2) {
        AddURLDialog fragment = new AddURLDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddURLDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_add_url_dialog,null);

        Spanned htmlText = Html.fromHtml(getString(R.string.addURLMessage));
        ((TextView) dialogView.findViewById(R.id.addURLMessage)).setText(htmlText);
        builder.setTitle(getString(R.string.addURLTitle));
        builder.setView(dialogView)
            .setPositiveButton(R.string.addURLOK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    mListener.AddURL(((EditText) getDialog().findViewById(R.id.newURL)).getText().toString());
                }
            })
                .setNegativeButton(R.string.addURLCancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_url_dialog, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddURLFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AddURLFragmentListener {
        public void AddURL(String url);
    }

}
