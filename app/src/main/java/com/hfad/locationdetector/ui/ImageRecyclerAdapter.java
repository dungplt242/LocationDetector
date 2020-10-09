package com.hfad.locationdetector.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.locationdetector.R;
import com.hfad.locationdetector.models.ImageItem;

import java.util.List;

public class ImageRecyclerAdapter extends ListAdapter<ImageItem, ImageRecyclerAdapter.ViewHolder> {
    public interface ItemClickListener {
        void onClick(int position);
    }

    private ItemClickListener itemClickListener;

    public ImageRecyclerAdapter(ItemClickListener itemClickListener) {
        super(DIFF_CALLBACK);
        this.itemClickListener = itemClickListener;
    }

    public static final DiffUtil.ItemCallback<ImageItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ImageItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ImageItem oldImageItem, @NonNull ImageItem newImageItem) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldImageItem.getImageID() == newImageItem.getImageID();
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull ImageItem oldImageItem, @NonNull ImageItem newImageItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldImageItem.equals(newImageItem);
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageIcon(getItem(position).getThumbnail());
        holder.textView.setText(getItem(position).getName());
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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
