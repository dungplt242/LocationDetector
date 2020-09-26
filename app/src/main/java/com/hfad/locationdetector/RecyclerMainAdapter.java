package com.hfad.locationdetector;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerMainAdapter extends RecyclerView.Adapter<RecyclerMainAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onClick(int position);
    }

    private ArrayList<AppOption> options;
    private ItemClickListener itemClickListener;

    public RecyclerMainAdapter(ArrayList<AppOption> options, ItemClickListener itemClickListener) {
        this.options = options;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.option_logo, parent, false);
        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerMainAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageResource(options.get(position).getID());
        holder.textView.setText(options.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView;
        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.optionLogo);
            textView = itemView.findViewById(R.id.optionName);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(getAdapterPosition());
        }
    }
}
