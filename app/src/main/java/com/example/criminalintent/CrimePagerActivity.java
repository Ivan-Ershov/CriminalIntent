package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {
    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mFistCrimeButton;
    private Button mLastCrimeButton;

    public static Intent newIntent (Context packageContext, UUID crimeId) {

        Intent intent = new Intent(packageContext, CrimePagerActivity.class);

        intent.putExtra(EXTRA_CRIME_ID, crimeId);

        return intent;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = findViewById(R.id.crime_view_pager);
        mFistCrimeButton = findViewById(R.id.first_crime_button);
        mLastCrimeButton = findViewById(R.id.last_crime_button);

        mCrimes = CrimeLab.getCrimeLab(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return CrimeFragment.newInstance(mCrimes.get(position).getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {

            if (mCrimes.get(i).getId().equals(crimeId)) {

                mViewPager.setCurrentItem(i);

                break;

            }

        }

        if (mViewPager.getCurrentItem() == 0) {
            mFistCrimeButton.setVisibility(View.INVISIBLE);
        } else {
            mFistCrimeButton.setVisibility(View.VISIBLE);
        }

        if (mViewPager.getCurrentItem() == (mCrimes.size() - 1)) {
            mLastCrimeButton.setVisibility(View.INVISIBLE);
        } else {
            mLastCrimeButton.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mViewPager.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                if (mViewPager.getCurrentItem() == 0) {
                    mFistCrimeButton.setVisibility(View.INVISIBLE);
                } else {
                    mFistCrimeButton.setVisibility(View.VISIBLE);
                }

                if (mViewPager.getCurrentItem() == (mCrimes.size() - 1)) {
                    mLastCrimeButton.setVisibility(View.INVISIBLE);
                } else {
                    mLastCrimeButton.setVisibility(View.VISIBLE);
                }

            });
        } else {

            mFistCrimeButton.setVisibility(View.VISIBLE);
            mLastCrimeButton.setVisibility(View.VISIBLE);

        }

        mFistCrimeButton.setOnClickListener(v -> mViewPager.setCurrentItem(0));

        mLastCrimeButton.setOnClickListener(v -> mViewPager.setCurrentItem(mCrimes.size() - 1));

    }

    @Override
    public void onCrimeUpdated(Crime crime) {
    }

}
