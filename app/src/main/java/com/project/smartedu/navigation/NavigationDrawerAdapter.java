package com.project.smartedu.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.smartedu.R;

import java.util.Collections;
import java.util.List;




public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private int[] icons;
    private LayoutInflater inflater;
    private Context context;


    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data, int icons[]) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.icons= new int[data.size()];
        Log.d("fd data size",Integer.toString(data.size()));
        this.icons=icons;
    }

    public void delete(int position) {
        data.remove(position);

        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.imageView.setImageResource(icons[position]);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            imageView= (ImageView) itemView.findViewById(R.id.rowIcon);
        }
    }
}
