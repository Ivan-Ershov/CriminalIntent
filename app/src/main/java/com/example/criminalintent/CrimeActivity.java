package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.crime_id";
    private static final String EXTRA_ADAPTER_POSITION = "com.example.criminalintent.adapter_position";

    @Override
    protected Fragment createFragment() {
        return CrimeFragment.newInstance((UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID), getIntent().getIntExtra(EXTRA_ADAPTER_POSITION, -1));
    }

    public static Intent newIntent (Context packageContext, UUID crimeId, int adapterPosition) {

        Intent intent = new Intent(packageContext, CrimeActivity.class);

        intent.putExtra(EXTRA_CRIME_ID, crimeId);

        intent.putExtra(EXTRA_ADAPTER_POSITION, adapterPosition);

        return intent;

    }

    public static void putExtraAdapterPosition (Intent data, int adapterPosition) {
        data.putExtra(EXTRA_ADAPTER_POSITION, adapterPosition);
    }

    public static int getIntExtraAdapterPosition (Intent data) {
        return data.getIntExtra(EXTRA_ADAPTER_POSITION, -1);
    }

}