package group24.SLIG.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group24.SLIG.R;

/**
 * Fragment used for Translator tab
 */

public class FragmentTranslator extends Fragment{

        private static View mView;

        public static final FragmentTranslator newInstance(String sampleText) {
            FragmentTranslator f = new FragmentTranslator();

            Bundle b = new Bundle();
            b.putString("bString", sampleText);
            f.setArguments(b);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            mView = inflater.inflate(R.layout.fragment_translator, container, false);
            String sampleText = getArguments().getString("bString");

            TextView txtSampleText = (TextView) mView.findViewById(R.id.txtViewTranslator);
            txtSampleText.setText(sampleText);

            return mView;
        }

}

