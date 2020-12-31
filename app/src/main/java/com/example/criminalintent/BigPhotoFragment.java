package com.example.criminalintent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.util.Objects;

public class BigPhotoFragment extends DialogFragment {
    private static final String ARGS_PHOTO_FILE = "photoFile";

    private ImageView mBigPhotoImageView;

    public static BigPhotoFragment newInstate (@NonNull File photoFile) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_PHOTO_FILE, photoFile);

        BigPhotoFragment bigPhotoFragment = new BigPhotoFragment();
        bigPhotoFragment.setArguments(args);

        return bigPhotoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_big_photo, null);

        mBigPhotoImageView = v.findViewById(R.id.photo);

        assert getArguments() != null;
        File photoFile = (File) getArguments().getSerializable(ARGS_PHOTO_FILE);

        if (photoFile.exists()) {
            mBigPhotoImageView.setImageBitmap(PictureUtils.getScaleBitmap(photoFile.getPath(), Objects.requireNonNull(getActivity())));
        }

        return v;
    }
}
