package org.poream.dejaview.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.poream.dejaview.R;

/**
 * Created by 이승호 on 2017-09-01.
 */

public class Fragment_googlemap extends android.support.v4.app.Fragment implements  OnMapReadyCallback{

    private MapView mapView;
    private Spinner spinner_countries;
    private GoogleMap googleMap;
    FloatingActionButton floatingActionButton;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_googlemap, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment


        spinner_countries = (Spinner)view.findViewById(R.id.spinner_countries);
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.fab);
        floatingActionButton.setElevation(100);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }
}