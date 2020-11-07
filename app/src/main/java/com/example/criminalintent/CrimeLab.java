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

    public void deleteCrime (Crime crime) {
        mCrimes.remove(crime.getId());
    }

    public List<Crime> getCrimes() {
        return new ArrayList<>(mCrimes.values());
    }

    public Crime getCrime (UUID id) {
        return mCrimes.get(id);
    }

}
