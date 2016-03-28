package group24.SLIG.tabs;

/**
 * Fragment used for Gesture Library tab
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group24.SLIG.R;

public class FragmentLibrary extends Fragment{
    private static View mView;

    public static final FragmentLibrary newInstance(String sampleText) {
        FragmentLibrary f = new FragmentLibrary();

        Bundle b = new Bundle();
        b.putString("bString", sampleText);
        f.setArguments(b);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_library, container, false);
        String sampleText = getArguments().getString("bString");

        TextView txtSampleText = (TextView) mView.findViewById(R.id.txtViewLibrary);
        txtSampleText.setText(sampleText);

        return mView;
    }
}
