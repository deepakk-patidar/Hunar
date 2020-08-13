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
import com.ics.hunar.activity.VideoPlayActivity;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.searching.Subcategory;

import java.util.List;

public class SearchingSubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<Subcategory> subcategoryList;

    public SearchingSubCategoryAdapter(Context context, List<Subcategory> subcategoryList) {
        this.context = context;
        this.subcategoryList = subcategoryList;
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
            if (subcategoryList != null && subcategoryList.size() > 0) {
                searchingHeaderViewHolder.tvSearchingHeading.setText("SubCategories");
            } else {
                ViewGroup.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 0);
                searchingHeaderViewHolder.tvSearchingHeading.setLayoutParams(layoutParams);
            }

        } else if (holder instanceof SearchingItemViewHolder) {
            SearchingItemViewHolder searchingItemViewHolder = (SearchingItemViewHolder) holder;
            Utils.loadImage(searchingItemViewHolder.ivSearching, subcategoryList.get(position - 1).getImage(), Utils.getCircularProgressDrawable(context,5,15));
            searchingItemViewHolder.tvSearching.setText(String.format("%s", subcategoryList.get(position - 1).getSubcategoryName()));
            searchingItemViewHolder.itemView.setOnClickListener(v -> {
                Constant.SUB_CAT_ID = Integer.parseInt(subcategoryList.get(position - 1).getId());
//                    if (subCate.getMaxLevel().isEmpty()) {
//                        Constant.TotalLevel = 0;
//                    } else {
//                        Constant.TotalLevel = Integer.parseInt(subcategoryList.get(position-1).getMaxLevel());
//                    }
//                    Intent intent = new Intent(mContext, LevelActivity.class);
//                    intent.putExtra("fromQue", "subCate");
//                    startActivity(intent);
                SharedPreferencesUtil.write(SharedPreferencesUtil.SUB_CATEGORY_NAME, subcategoryList.get(position - 1).getSubcategoryName());
                Session.setSubCategoryName(Session.SUB_CAT_NAME, subcategoryList.get(position - 1).getSubcategoryName(), context);

                context.startActivity(new Intent(context, VideoPlayActivity.class));
            });
        }
    }

    @Override
    public int getItemCount() {
        return subcategoryList.size() + 1;
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
