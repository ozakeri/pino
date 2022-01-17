package com.gap.bis_inspection.widget.DatePicker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.widget.DatePicker.components.SquareTextView;
import com.gap.bis_inspection.widget.DatePicker.interfaces.DateInterface;

/**
 * Created by Alireza Afkar on 2/11/16 AD.
 */
public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder> {
    private int[] mYears;
    private int mCurrentYear;
    private DateInterface mCallback;

    public YearAdapter(DateInterface callback, int[] years) {
        mYears = years;
        mCallback = callback;
        mCurrentYear = callback.getCurrentYear();
    }

    private boolean isSelected(int year) {
        return mYears[year] == mCallback.getYear();
    }

    private boolean isThisYear(int year) {
        return mYears[year] == mCurrentYear;
    }

    public int getSelectedYear() {
        for (int i = 0; i < getItemCount(); i++) {
            if (isSelected(i))
                return i;
        }
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_year, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextView.setText(String.valueOf(mYears[position]));
        holder.mTextView.setSelected(isSelected(position));
        holder.mTextView.setChecked(isThisYear(position));
    }

    @Override
    public int getItemCount() {
        return mYears.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SquareTextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
            mTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                mCallback.setYear(mYears[getLayoutPosition()]);
                notifyDataSetChanged();
            }
        }
    }
}
