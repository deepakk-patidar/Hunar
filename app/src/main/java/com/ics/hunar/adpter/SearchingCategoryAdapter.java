package com.ics.hunar.adpter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.activity.SubcategoryActivity;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.searching.Category;

import java.util.List;

public class SearchingCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<Category> categoryList;

    public SearchingCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.searching_item_layout, parent, false);
            return new SearchingItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.searching_header_layout, parent, false);
            return new SearchingHeaderViewHolder(view);
        } else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchingHeaderViewHolder) {
            SearchingHeaderViewHolder searchingHeaderViewHolder = (SearchingHeaderViewHolder) holder;
            if (categoryList != null && categoryList.size() > 0) {
                searchingHeaderViewHolder.tvSearchingHeading.setText("Categories");
            } else {
                ViewGroup.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 0);
                searchingHeaderViewHolder.tvSearchingHeading.setLayoutParams(layoutParams);
            }
        } else if (holder instanceof SearchingItemViewHolder) {
            SearchingItemViewHolder searchingItemViewHolder = (SearchingItemViewHolder) holder;
            Utils.loadImage(searchingItemViewHolder.ivSearching, categoryList.get(position - 1).getImage(), Utils.getCircularProgressDrawable(context,5,15));
            searchingItemViewHolder.tvSearching.setText(String.format("%s", categoryList.get(position - 1).getCategoryName()));
            searchingItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.CATE_ID = Integer.parseInt(categoryList.get(position - 1).getId());
                    Constant.cate_name = categoryList.get(position - 1).getCategoryName();
                    context.startActivity(new Intent(context, SubcategoryActivity.class));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    class SearchingItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSearching;
        private TextView tvSearching;

        public SearchingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSearching = itemView.findViewById(R.id.ivSearching);
            tvSearching = itemView.findViewById(R.id.tvSearching);

        }
    }

    class SearchingHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSearchingHeading;

        public SearchingHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchingHeading = itemView.findViewById(R.id.tvSearchingHeading);
        }
    }
}
