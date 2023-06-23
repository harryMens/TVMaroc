package com.AflamTvMaroc.moviesdarija.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.interfaces.OnContentClickListener;
import com.AflamTvMaroc.moviesdarija.model.Content;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {
    private List<Content> content;
    private List<Content> contentFiltered;
    private Context context;
    private OnContentClickListener onContentClickListener;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        View item_content_wrapper;
        ImageView item_content_logo;
        TextView item_content_title;
        TextView item_content_info;
        ImageView item_content_type_image;

        MyViewHolder(RelativeLayout v) {
            super(v);
            item_content_wrapper = v.findViewById(R.id.item_content_wrapper);
            item_content_logo = v.findViewById(R.id.item_content_logo);
            item_content_title = v.findViewById(R.id.item_content_title);
            item_content_info = v.findViewById(R.id.item_content_info);
            item_content_type_image = v.findViewById(R.id.item_content_type_image);
        }


    }

    public ContentAdapter(Context context, List<Content> content, OnContentClickListener onContentClickListener) {
        this.context = context;
        this.content = content;
        this.contentFiltered = content;
        this.onContentClickListener = onContentClickListener;
    }

    @Override
    public ContentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Content item = contentFiltered.get(position);
        if (item.getTitle() != null) {
            holder.item_content_title.setText(item.getTitle());
        } else {
            holder.item_content_title.setText("");
        }

        if (item.getInfo()!= null) {
            holder.item_content_info.setText(item.getInfo());
        } else {
            holder.item_content_info.setText("");
        }
        if (item.getLogo() != null && item.getLogo().startsWith("http")) {
            Picasso.get().load(Uri.parse(item.getLogo())).into(holder.item_content_logo);
        } else {
            holder.item_content_logo.setImageResource(R.mipmap.ic_launcher);
        }

        if (item.getExternal_link() != null && !item.getExternal_link().isEmpty() && !item.getExternal_link().get(0).isEmpty()) {
            holder.item_content_type_image.setImageResource(R.drawable.ic_link);
        } else if (item.getStream_url() != null && !item.getStream_url().isEmpty() && !item.getStream_url().get(0).isEmpty()) {
            holder.item_content_type_image.setImageResource(R.drawable.ic_video);
        }

        holder.item_content_wrapper.setOnClickListener(view -> onContentClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return contentFiltered.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contentFiltered = content;
                } else {
                    List<Content> filteredList = new ArrayList<>();
                    for (Content row : content) {
                        //uncomment if you want to filter by info as well
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) /*|| row.getInfo().contains(charSequence)*/) {
                            filteredList.add(row);
                        }
                    }

                    contentFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contentFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contentFiltered = (ArrayList<Content>) filterResults.values;
                ContentAdapter.this.notifyDataSetChanged();
            }
        };
    }

}