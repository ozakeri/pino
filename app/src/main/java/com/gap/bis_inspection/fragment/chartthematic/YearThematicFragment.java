package com.gap.bis_inspection.fragment.chartthematic;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gap.bis_inspection.R;

/**
 * Created by Mohamad Cheraghi on 07/23/2016.
 */
public class YearThematicFragment extends Fragment {

    public YearThematicFragment(){

    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thematic_year, container, false);

        return view;
    }

}
