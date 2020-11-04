package com.example.criminalintent;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");

    private static final String ARGS_CRIME_ID = "crime_id";
    private static final String ARGS_ADAPTER_POSITION = "adapter_position";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mRequiresPoliceCheckBox;

    public static CrimeFragment newInstance (UUID crimeId, int adapterPosition) {

        Bundle args = new Bundle();

        args.putSerializable(ARGS_CRIME_ID, crimeId);

        args.putInt(ARGS_ADAPTER_POSITION, adapterPosition);

        CrimeFragment fragment = new CrimeFragment();

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime((UUID) getArguments().getSerializable(ARGS_CRIME_ID));

        returnResult();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = v.findViewById(R.id.crime_title);
        mDateButton = v.findViewById(R.id.crime_date);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mRequiresPoliceCheckBox = v.findViewById(R.id.crime_requires_police);

        mTitleField.setText(mCrime.getTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton.setText(dateFormat.format(mCrime.getDate()));
        mDateButton.setEnabled(false);

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setSolved(isChecked));

        mRequiresPoliceCheckBox.setChecked(mCrime.isRequiresPolice());
        mRequiresPoliceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setRequiresPolice(isChecked));

        return v;

    }

    private void returnResult () {

        Intent data = new Intent();

        assert getArguments() != null;
        CrimeActivity.putExtraAdapterPosition(data, getArguments().getInt(ARGS_ADAPTER_POSITION));

        Objects.requireNonNull(getActivity()).setResult(Activity.RESULT_OK, data);

    }

}
