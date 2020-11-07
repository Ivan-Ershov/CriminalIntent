package com.example.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.Crime;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable.Columns;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime () {

        Crime crime = new Crime(UUID.fromString(getString(getColumnIndex(Columns.UUID))));

        crime.setTitle(getString(getColumnIndex(Columns.TITLE)));
        crime.setDate(new Date(getLong(getColumnIndex(Columns.DATE))));
        crime.setSolved(getInt(getColumnIndex(Columns.SOLVED)) == 1);
        crime.setRequiresPolice(getInt(getColumnIndex(Columns.REQUIRES_POLICE)) == 1);

        return crime;
    }

}
