package com.example.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.example.criminalintent.date";
    private static final String ARGS_DATE = "date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstate (Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        assert getArguments() != null;
        Date date = (Date) getArguments().getSerializable(ARGS_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), null);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> sentResult((new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth())).getTime()))
                .create();
    }

    private void sentResult(Date date) {

        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();

        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

    }

}
