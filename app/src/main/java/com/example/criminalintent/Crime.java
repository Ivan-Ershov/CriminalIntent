package com.example.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private final UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;
    private String mSuspect;

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

    public String getSuspect() {
        return mSuspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public boolean isSolved() {
        return mSolved;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
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

    public void setSuspect(String mSuspect){
        this.mSuspect = mSuspect;
    }

}
