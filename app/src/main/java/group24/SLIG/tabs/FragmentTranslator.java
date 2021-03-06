package group24.SLIG.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group24.SLIG.R;

/**
 * Fragment used for Translator tab
 */

public class FragmentTranslator extends Fragment{

    private static View mView;
    private Button mClearButton;

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

        mClearButton = (Button) mView.findViewById(R.id.buttonClearScreen);
        mClearButton.setOnClickListener(
                new View.OnClickListener()  {
                    @Override
                    public void onClick(View v) {
                        TextView gestureList = (TextView) mView.findViewById(R.id.txtViewGestureList);
                        gestureList.setText("  ");
                    }
                }
        );
        return mView;
    }

}

