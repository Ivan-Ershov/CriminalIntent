package com.example.criminalintent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class CrimeListFragment extends Fragment {
    @SuppressLint("SimpleDateFormat") private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
    private static final int REQUEST_CODE_CRIME_ACTIVITY = 0;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = v.findViewById(R.id.crime_recycle_view);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((requestCode == REQUEST_CODE_CRIME_ACTIVITY) && (resultCode == Activity.RESULT_OK) && (data != null)) {
            updateOneView(CrimeActivity.getIntExtraAdapterPosition(data));
        }

    }

    private void updateUI () {

        if (mAdapter == null) {

            CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
            List<Crime> crimes = crimeLab.getCrimes();

            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);

        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    private void updateOneView (int adapterPosition) {

        if (mAdapter == null) {

            CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
            List<Crime> crimes = crimeLab.getCrimes();

            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);

        } else {
            mAdapter.notifyItemChanged(adapterPosition);
        }

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
            startActivityForResult(CrimeActivity.newIntent(getActivity(), mCrime.getId(), getAdapterPosition()), REQUEST_CODE_CRIME_ACTIVITY);
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
