package sunnysoft.presentapp.Interfaz;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunnysoft.presentapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class bandejasemailActivityFragment extends Fragment {

    public bandejasemailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_bandejasemail, container, false);
    }
}
