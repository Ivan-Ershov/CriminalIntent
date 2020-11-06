package com.example.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "com.example.criminalintent.date";
    private static final String ARGS_DATE = "date";

    private TimePicker mTimePicker;
    private Date mDate;

    public static TimePickerFragment newInstate (Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        assert getArguments() != null;
        mDate = (Date) getArguments().getSerializable(ARGS_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_picker);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            mTimePicker.setHour(calendar.get(Calendar.HOUR));
            mTimePicker.setMinute(calendar.get(Calendar.MINUTE));

        } else  {

            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        }

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        mDate.setHours(mTimePicker.getHour());
                        mDate.setMinutes(mTimePicker.getMinute());

                    } else {

                        mDate.setHours(mTimePicker.getCurrentHour());
                        mDate.setMinutes(mTimePicker.getCurrentMinute());

                    }

                    sentResult(mDate);

                })
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
