package com.example.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminalintent.database.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeCursorWrapper;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable.Columns;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private final SQLiteDatabase mDatabase;

    private CrimeLab (Context context) {

        mDatabase = (new CrimeBaseHelper(context)).getWritableDatabase();
        mContext = context.getApplicationContext();

    }

    public static CrimeLab getCrimeLab (Context context) {

        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;

    }

    public void addCrime (Crime crime) {
        mDatabase.insert(CrimeTable.NAME, null, getContentValues(crime));
    }

    public void deleteCrime (Crime crime) {
        mDatabase.delete(CrimeTable.NAME, Columns.UUID + " = ?", new String[] { crime.getId().toString() });
    }

    public List<Crime> getCrimes() {

        List<Crime> crimes = new ArrayList<>();

        try (CrimeCursorWrapper cursor = queryCrimes(null, null)) {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                crimes.add(cursor.getCrime());
                cursor.moveToNext();

            }

        }

        return crimes;
    }

    public Crime getCrime (UUID id) {

        try (CrimeCursorWrapper cursor = queryCrimes(Columns.UUID + " = ?", new String[] { id.toString() })){

            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        }

    }

    public File getPhotoFile (Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void updateCrime (Crime crime) {
        mDatabase.update(CrimeTable.NAME, getContentValues(crime), Columns.UUID + "= ?", new String[] {crime.getId().toString()});
    }

    private static ContentValues getContentValues (Crime crime) {

        ContentValues values = new ContentValues();

        values.put(Columns.UUID, crime.getId().toString());
        values.put(Columns.TITLE, crime.getTitle());
        values.put(Columns.DATE, crime.getDate().getTime());
        values.put(Columns.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(Columns.REQUIRES_POLICE, crime.isRequiresPolice() ? 1 : 0);
        values.put(Columns.SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes (String whereClause, String[] whereArgs) {
        return new CrimeCursorWrapper(mDatabase.query(CrimeTable.NAME, null, whereClause, whereArgs, null, null, null));
    }

}
