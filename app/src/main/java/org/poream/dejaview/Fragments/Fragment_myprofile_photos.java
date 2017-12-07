package org.poream.dejaview.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.poream.dejaview.R;

/**
 * Created by 이승호 on 2017-10-10.
 */

public class Fragment_myprofile_photos extends Fragment {

    private RecyclerView recyclerView;

    public Fragment_myprofile_photos() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myprofile_photos, container, false);



        return rootView;
    }
}
