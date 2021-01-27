package com.example.criminalintent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static final String TAG = CrimeFragment.class.getSimpleName();
    private static final String ARGS_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_BIG_PHOTO = "DialogBigPhoto";
    private static final int REQUEST_DATE_PICKER_FRAGMENT = 0;
    private static final int REQUEST_TIME_PICKER_FRAGMENT = 1;
    private static final int REQUEST_BIG_PHOTO_FRAGMENT = 2;
    private static final int REQUEST_CONTACT_INTENT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 0;

    private Crime mCrime;
    private File mPhotoFile;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mRequiresPoliceCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated (Crime crime);
    }

    public static CrimeFragment newInstance (UUID crimeId) {

        Bundle args = new Bundle();

        args.putSerializable(ARGS_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mCallbacks = (Callbacks) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        assert getArguments() != null;
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime((UUID) getArguments().getSerializable(ARGS_CRIME_ID));

        mPhotoFile = CrimeLab.getCrimeLab(getActivity()).getPhotoFile(mCrime);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mTitleField = v.findViewById(R.id.crime_title);
        mDateButton = v.findViewById(R.id.crime_date);
        mTimeButton = v.findViewById(R.id.crime_time);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mRequiresPoliceCheckBox = v.findViewById(R.id.crime_requires_police);
        mReportButton = v.findViewById(R.id.crime_report);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mCallSuspectButton = v.findViewById(R.id.crime_call_suspect);
        mPhotoButton = v.findViewById(R.id.crime_camera);
        mPhotoView = v.findViewById(R.id.crime_photo);

        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mCrime.setTitle(s.toString());

                updateCrime();

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
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            mCrime.setSolved(isChecked);

            updateCrime();

        });

        mRequiresPoliceCheckBox.setChecked(mCrime.isRequiresPolice());
        mRequiresPoliceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            mCrime.setRequiresPolice(isChecked);

            updateCrime();

        });

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

        boolean canTakePhoto = ((mPhotoFile != null) && (captureImage.resolveActivity(packageManager) != null));

        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(v16 -> {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.criminalintent.fileprovider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo activity : cameraActivities) {
                getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            startActivityForResult(captureImage, REQUEST_PHOTO);

        });

        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(this::updatePhotoView);
        mPhotoView.setOnClickListener(v17 -> {

            BigPhotoFragment dialog = BigPhotoFragment.newInstate(mPhotoFile);
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_BIG_PHOTO_FRAGMENT);

            assert getFragmentManager() != null;
            dialog.show(getFragmentManager(), DIALOG_BIG_PHOTO);

        });

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
                updateCrime();

            }

            if (requestCode == REQUEST_TIME_PICKER_FRAGMENT) {

                mCrime.setDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE));

                updateTime();
                updateCrime();

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

                        updateCrime();

                    }
                }

            }

            if (requestCode == REQUEST_PHOTO) {

                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()), "com.example.criminalintent.fileprovider", mPhotoFile);

                getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                updatePhotoView();
                updateCrime();

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

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = null;

    }

    private void updateCrime () {
        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate () {
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
    }

    private void updateTime () {
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));
    }

    private void updatePhotoView () {
        if ((mPhotoFile == null) || (!mPhotoFile.exists())) {
            mPhotoView.setImageDrawable(null);
        } else {
            mPhotoView.setImageBitmap(PictureUtils.getScaleBitmap(mPhotoFile.getPath(), Objects.requireNonNull(getActivity())));
        }
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
