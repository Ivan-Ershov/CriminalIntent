package com.example.criminalintent;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class CrimeListFragment extends Fragment {
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
    private static final String KEY_SUBTITLE_VISIBLE = "subtitle_visible";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private Button mAddCrime;

    private Callbacks mCallbacks;

    private boolean mSubtitleVisible;

    public interface Callbacks {
        void onCrimeSelected (Crime crime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mCallbacks = (Callbacks) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE_VISIBLE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = v.findViewById(R.id.crime_recycle_view);
        mAddCrime = v.findViewById(R.id.add_crime_button);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddCrime.setOnClickListener((v1) -> {

                addCrime();

                updateUI();

            });

        updateUI();

        return v;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_SUBTITLE_VISIBLE, mSubtitleVisible);

    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.subtitle);

        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case (R.id.new_crime) : {

                addCrime();

                updateUI();

                return true;
            }

            case (R.id.subtitle) : {

                mSubtitleVisible = !mSubtitleVisible;

                getActivity().invalidateOptionsMenu();

                updateSubtitle();

                return true;
            }

            default: return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = null;

    }

    private void addCrime() {

        Crime crime = new Crime();

        CrimeLab.getCrimeLab(getActivity()).addCrime(crime);

        mCallbacks.onCrimeSelected(crime);

    }

    public void updateUI () {

        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());

        if (mAdapter == null) {

            mAdapter = new CrimeAdapter(crimeLab.getCrimes());
            mCrimeRecyclerView.setAdapter(mAdapter);

        } else {
            mAdapter.wrapNotifyDataSetChanged(CrimeLab.getCrimeLab(getActivity()).getCrimes());
        }

        if (crimeLab.getCrimes().size() == 0) {
            mAddCrime.setVisibility(View.VISIBLE);
        } else {
            mAddCrime.setVisibility(View.INVISIBLE);
        }

        updateSubtitle();

    }

    private void updateSubtitle () {

        String subtitle;

        if (!mSubtitleVisible) {
            subtitle = null;
        } else {
            subtitle = getResources().getQuantityString(R.plurals.subtitle_plurals, CrimeLab.getCrimeLab(getActivity()).getCrimes().size(), CrimeLab.getCrimeLab(getActivity()).getCrimes().size());
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);

    }

    private class CrimeHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleTextView;
        private final TextView mDateTextView;
        private final ImageView mSolvedImageView;
        protected Crime mCrime;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this::onClick);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);

        }

        public void bind (Crime crime) {
            mCrime = crime;

            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(dateFormat.format(crime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

        }

        public void onClick (View v) {
            mCallbacks.onCrimeSelected(mCrime);
        }

    }

    private class CrimeHolderWithPolice extends CrimeHolder {

        private final TextView mTitleTextView;
        private final TextView mDateTextView;
        private final Button mCalledPoliceButton;
        private final ImageView mSolvedImageView;

        public CrimeHolderWithPolice(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this::onClick);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            mCalledPoliceButton = itemView.findViewById(R.id.called_police_button);

            mCalledPoliceButton.setOnClickListener(v -> Toast.makeText(getActivity(),"Call police", Toast.LENGTH_LONG).show());

        }

        @Override
        public void bind (Crime crime) {
            super.mCrime = crime;

            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(dateFormat.format(crime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

        }

    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter (List<Crime> crimes) {
            mCrimes = crimes;
        }

        public void wrapNotifyDataSetChanged (List<Crime> crimes) {

            mCrimes = crimes;

            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return (viewType == 0) ? new CrimeHolder(inflater.inflate(R.layout.list_item_crime, parent, false)) : new CrimeHolderWithPolice(inflater.inflate(R.layout.list_item_crime_with_police, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(mCrimes.get(position));
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mCrimes.get(position).isRequiresPolice() ? 1 : 0;
        }
    }

}
