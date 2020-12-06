package com.example.criminalintent;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static final String TAG = CrimeFragment.class.getSimpleName();
    private static final String ARGS_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE_PICKER_FRAGMENT = 0;
    private static final int REQUEST_TIME_PICKER_FRAGMENT = 1;
    private static final int REQUEST_CONTACT_INTENT = 2;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 0;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mRequiresPoliceCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;

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

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mTitleField = v.findViewById(R.id.crime_title);
        mDateButton = v.findViewById(R.id.crime_date);
        mTimeButton = v.findViewById(R.id.crime_time);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mRequiresPoliceCheckBox = v.findViewById(R.id.crime_requires_police);
        mReportButton = v.findViewById(R.id.crime_report);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mCallSuspectButton = v.findViewById(R.id.crime_call_suspect);

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

        mReportButton.setOnClickListener(v13 -> ShareCompat.IntentBuilder.from(Objects.requireNonNull(getActivity()))
                .setType("text/plain")
                .setText(getCrimeReport())
                .setSubject(getString(R.string.crime_report_subject))
                .setChooserTitle(R.string.send_report)
                .startChooser());



        mSuspectButton.setOnClickListener(v14 -> startActivityForResult(pickContact, REQUEST_CONTACT_INTENT));

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCallSuspectButton.setOnClickListener(v15 -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                readContacts(getActivity());
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.READ_CONTACTS }, PERMISSION_REQUEST_READ_CONTACTS);
            }
        });

        if (mCrime.getSuspect() == null) {
            mCallSuspectButton.setEnabled(false);
        }

        return v;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
           if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               readContacts(getActivity());
           } else {
               Log.d(TAG, "Permission denied!");
           }
        }

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

            if (requestCode == REQUEST_CONTACT_INTENT) {

                Uri contactUri = data.getData();

                Log.i(TAG, contactUri.toString());

                Log.i(TAG, Uri.parse(contactUri.toString()).toString());

                try (Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null)) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();

                        String suspect = cursor.getString(0);

                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(suspect);
                        mCallSuspectButton.setEnabled(true);

                    }
                }

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

            Objects.requireNonNull(getActivity()).finish();

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

    @SuppressLint("StringFormatMatches")
    private String getCrimeReport () {
        return getString(R.string.crime_report,
                mCrime.getTitle(),
                DateFormat.format("", mCrime.getDate()).toString(),
                mCrime.isSolved() ? getString(R.string.crime_report_solved) : getString(R.string.crime_report_unsolved),
                (mCrime.getSuspect() == null) ? getString(R.string.crime_report_no_suspect) : getString(R.string.crime_report_suspect, mCrime.getSuspect()));

    }

    private void readContacts (Context context) {

        try (Cursor cursor = context.getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME + " = ?",
                        new String[] { mCrime.getSuspect() },
                        null)){

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    try (Cursor phoneCursor = context.getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[] { cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)) },
                                    null)) {

                        if (BuildConfig.DEBUG && phoneCursor.getCount() <= 0) {
                            throw new AssertionError("Assertion failed");
                        }

                        if (phoneCursor.getCount() > 0) {
                            phoneCursor.moveToFirst();

                            String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));

                        }

                    }


                } else {
                    Log.d(TAG, "readContacts, suspect hasn't phone");
                }

            } else {
                Log.d(TAG, "readContacts, not suspect in database");
            }

        }

    }

}
