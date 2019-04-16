package isware.uneg.es.chateando.Interfaz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import isware.uneg.es.chateando.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SolicitudesFragment extends Fragment {



    public SolicitudesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
        }

}
