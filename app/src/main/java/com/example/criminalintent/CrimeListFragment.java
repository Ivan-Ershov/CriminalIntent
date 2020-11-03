package com.example.criminalintent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {

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

    private void updateUI () {

        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);

    }

    private class CrimeHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleTextView;
        private final TextView mDateTextView;
        private Crime mCrime;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this::onClick);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);

        }

        public void bind (Crime crime) {
            mCrime = crime;

            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());

        }

        public void onClick (View v) {
            Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_LONG).show();
        }

    }

    private class CrimeHolderWithPolice extends CrimeHolder {

        private final TextView mTitleTextView;
        private final TextView mDateTextView;
        private final Button mCalledPoliceButton;
        private Crime mCrime;

        public CrimeHolderWithPolice(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this::onClick);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mCalledPoliceButton = itemView.findViewById(R.id.called_police_button);

            mCalledPoliceButton.setOnClickListener(v -> Toast.makeText(getActivity(),"Call police", Toast.LENGTH_LONG).show());

        }

        public void bind (Crime crime) {
            mCrime = crime;

            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());

        }

        public void onClick (View v) {
            Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_LONG).show();
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
