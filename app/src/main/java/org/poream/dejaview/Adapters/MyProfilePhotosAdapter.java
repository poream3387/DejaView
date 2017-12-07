package org.poream.dejaview.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.poream.dejaview.Java.MyPhoto;

import java.util.ArrayList;

/**
 * Created by 이승호 on 2017-10-21.
 */

public class MyProfilePhotosAdapter extends RecyclerView.Adapter<MyProfilePhotosAdapter.ViewHolder>{

    private ArrayList<MyPhoto> arrayList;

    public MyProfilePhotosAdapter(ArrayList<MyPhoto> arrayList) {
        this.arrayList = arrayList;
    }


    @Override
    public MyProfilePhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyProfilePhotosAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public ViewHolder(View view){
            super(view);
        }
    }
}
