package com.example.criminalintent;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static final String ARGS_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE_PICKER_FRAGMENT = 0;
    private static final int REQUEST_TIME_PICKER_FRAGMENT = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mRequiresPoliceCheckBox;

    public static CrimeFragment newInstance (UUID crimeId) {

        Bundle args = new Bundle();

        args.putSerializable(ARGS_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        assert getArguments() != null;
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime((UUID) getArguments().getSerializable(ARGS_CRIME_ID));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = v.findViewById(R.id.crime_title);
        mDateButton = v.findViewById(R.id.crime_date);
        mTimeButton = v.findViewById(R.id.crime_time);
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
        mDateButton.setOnClickListener(v1 -> {

            DatePickerFragment dialog = DatePickerFragment.newInstate(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE_PICKER_FRAGMENT);

            assert getFragmentManager() != null;
            dialog.show(getFragmentManager(), DIALOG_DATE);

        });

        mTimeButton.setText(timeFormat.format(mCrime.getDate()));
        mTimeButton.setOnClickListener(v12 -> {

            TimePickerFragment dialog = TimePickerFragment.newInstate(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME_PICKER_FRAGMENT);

            assert getFragmentManager() != null;
            dialog.show(getFragmentManager(), DIALOG_TIME);

        });

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setSolved(isChecked));

        mRequiresPoliceCheckBox.setChecked(mCrime.isRequiresPolice());
        mRequiresPoliceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setRequiresPolice(isChecked));

        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((resultCode == Activity.RESULT_OK) && (data != null)) {

            if (requestCode == REQUEST_DATE_PICKER_FRAGMENT) {

                mCrime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));

                updateDate();

            }

            if (requestCode == REQUEST_TIME_PICKER_FRAGMENT) {

                mCrime.setDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE));

                updateTime();

            }

        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.delete_crime) {
            CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);

            getActivity().finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);

    }

    private void updateDate () {
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
    }

    private void updateTime () {
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));
    }

}
