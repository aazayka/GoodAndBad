package com.aazayka.goodandbad.app;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH =
            "com.aazayka.goodandbad.app.image_path";
    public static ImageFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    private ImageView mImageView;

    private static Point getDisplaySize(final Display display) {
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        Image image = new Image(path);

        Point point = getDisplaySize(getActivity().getWindowManager().getDefaultDisplay());
        Bitmap bitmap = image.resize(point.x, point.y);
        mImageView.setImageBitmap(bitmap);
        return mImageView;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Image.cleanImageView(mImageView);
    }
}