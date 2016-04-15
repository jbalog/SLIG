package group24.SLIG.tabs;

/**
 * Fragment used for Gesture Library tab
 */

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import group24.SLIG.MainActivity;
import group24.SLIG.R;

public class FragmentLearning extends Fragment{
    private static View mView;
    int[] mArray = {R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5};

    public static final FragmentLearning newInstance(String sampleText) {
        FragmentLearning f = new FragmentLearning();

        Bundle b = new Bundle();
        b.putString("bString", sampleText);
        f.setArguments(b);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_learning, container, false);
        // Initialize Learning Mode
        ImageView gestureImage = (ImageView) mView.findViewById(R.id.imgViewLearningGesture);
        gestureImage.setImageDrawable(getResources().getDrawable(R.drawable.question_mark));

        // Sample code
//        String sampleText = getArguments().getString("bString");
//        TextView txtSampleText = (TextView) mView.findViewById(R.id.txtViewLibrary);
//        txtSampleText.setText(sampleText);

        return mView;
    }
}
