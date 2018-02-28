package kuririnz.xyz.bookdiscovery;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressDialogFragment extends DialogFragment {


    public ProgressDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false);
    }
}
