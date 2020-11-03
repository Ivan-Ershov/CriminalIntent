package com.example.criminalintent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private final HashMap<UUID, Crime> mCrimes;

    private CrimeLab (Context context) {

        mCrimes = new LinkedHashMap<>();

        for (int i = 0; i < 100; i++) {

            Crime crime = new Crime();

            crime.setTitle("Crime # " + i);
            crime.setSolved((i % 2) == 0);
            crime.setRequiresPolice((i % 3) == 0);

            addCrime(crime);

        }

    }

    public static CrimeLab getCrimeLab (Context context) {

        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;

    }

    public void addCrime (Crime crime) {
        mCrimes.put(crime.getId(), crime);
    }

    public List<Crime> getCrimes() {
        return new ArrayList<>(mCrimes.values());
    }

    public Crime getCrime (UUID id) {
        return mCrimes.get(id);
    }

}
