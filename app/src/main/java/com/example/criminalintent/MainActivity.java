package com.example.criminalintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment crime_fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if(crime_fragment == null) {

            crime_fragment = new CrimeFragment();

            fragmentManager.beginTransaction().add(R.id.fragment_container, crime_fragment).commit();

        }

    }
}