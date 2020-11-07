package com.example.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private final UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime (UUID uuid) {

        mId = uuid;
        mDate = new Date();

    }

    public Crime () {
        this(UUID.randomUUID());
    }

    public Date getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public UUID getId() {
        return mId;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public void setRequiresPolice(boolean mRequiresPolice) {
        this.mRequiresPolice = mRequiresPolice;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }
}
