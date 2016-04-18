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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import group24.SLIG.MainActivity;
import group24.SLIG.R;

public class FragmentLearning extends Fragment{
    private static View mView;
    private Button mSkipButton;
    int[] mImageArray = {R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5, R.drawable.img_6,
            R.drawable.img_7, R.drawable.img_8, R.drawable.img_9, R.drawable.img_10, R.drawable.img_11, R.drawable.img_12, R.drawable.img_13, R.drawable.img_14,
            R.drawable.img_15, R.drawable.img_16, R.drawable.img_17, R.drawable.img_18, R.drawable.img_19, R.drawable.img_20, R.drawable.img_21, R.drawable.img_22,
            R.drawable.img_23, R.drawable.img_24, R.drawable.img_25};
    final Random rnd = new Random();
    private int r = rnd.nextInt(mImageArray.length);

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

        mSkipButton = (Button) mView.findViewById(R.id.buttonSkipGesture);
        mSkipButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        r = rnd.nextInt(mImageArray.length);
                        ImageView mGestureImage = (ImageView) mView.findViewById(R.id.imgViewLearningGesture);
                        mGestureImage.setImageDrawable(getResources().getDrawable(mImageArray[r]));
                    }
                }
        );

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserVisibleHint(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setUserVisibleHint(false);
    }
}
