package com.ics.hunar.adpter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.activity.SubcategoryActivity;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.features.Category;
import com.ics.hunar.model.features.Subcategory;

import java.util.List;

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.NormalViewHolder> {
    private Context context;
    private List<Category> categoryList, categoryList1;
    private List<Subcategory> subcategoryList, subcategoryList1;
    private String heading;

    public FeaturesAdapter(Context context, List<Category> categoryList, List<Subcategory> subcategoryList, String heading) {
        this.context = context;
        this.categoryList = categoryList;
        this.subcategoryList = subcategoryList;
        this.heading = heading;
    }

    @NonNull
    @Override
    public NormalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.features_normal_item_layout, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalViewHolder holder, int position) {
        if (categoryList.size() > position) {
            Category category = categoryList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Constant.CATE_ID = Integer.parseInt(category.getId());
                    Constant.cate_name = category.getCategoryName();
                    Constant.cat_img_url = category.getImage();
                    Intent intent = new Intent(context, SubcategoryActivity.class);
                    context.startActivity(intent);
                }
            });

            if (heading.equalsIgnoreCase("Features")) {
                if (category.getFeatured().equals("1")) {
                    holder.tvFeatureItemName.setText(category.getCategoryName());
                    Utils.loadImage(holder.ivFeatureItem, category.getImage(), Utils.getCircularProgressDrawable(context, 5, 15));
                } else {
                    invisibleView(holder.cvItemParent);
                }
            }
            if (heading.equalsIgnoreCase("Featured Courses")) {
                if (category.getTrending1().equals("1")) {
                    holder.tvFeatureItemName.setText(category.getCategoryName());
                    Utils.loadImage(holder.ivFeatureItem, category.getImage(), Utils.getCircularProgressDrawable(context, 5, 15));
                } else {
                    invisibleView(holder.cvItemParent);
                }
            }
            if (heading.equalsIgnoreCase("Trending")) {
                if (category.getTrending2().equals("1")) {
                    holder.tvFeatureItemName.setText(category.getCategoryName());
                    Utils.loadImage(holder.ivFeatureItem, category.getImage(), Utils.getCircularProgressDrawable(context, 5, 15));
                } else {
                    invisibleView(holder.cvItemParent);
                }
            }
        } else {
            if (position > subcategoryList.size()) {
                position = 0;
            }
            if (subcategoryList.size() > position) {
                Subcategory subcategory = subcategoryList.get(position);
                if (heading.equalsIgnoreCase("Features")) {
                    if (subcategory.getFeatured().equals("1")) {
                        holder.tvFeatureItemName.setText(subcategory.getSubcategoryName());
                        Utils.loadImage(holder.ivFeatureItem, subcategory.getImage(), Utils.getCircularProgressDrawable(context, 5, 15));
                    } else {
                        invisibleView(holder.cvItemParent);
                    }
                }
                if (heading.equalsIgnoreCase("Trending List 1")) {
                    if (subcategory.getTrending1().equals("1")) {
                        holder.tvFeatureItemName.setText(subcategory.getSubcategoryName());
                        Utils.loadImage(holder.ivFeatureItem, subcategory.getImage(), Utils.getCircularProgressDrawable(context, 5, 15));
                    } else {
                        invisibleView(holder.cvItemParent);
                    }
                }
                if (heading.equalsIgnoreCase("Trending List 2")) {
                    if (subcategory.getTrending2().equals("1")) {
                        holder.tvFeatureItemName.setText(subcategory.getSubcategoryName());
                        Utils.loadImage(holder.ivFeatureItem, subcategory.getImage(), Utils.getCircularProgressDrawable(context, 5, 15));
                    } else {
                        invisibleView(holder.cvItemParent);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size() + subcategoryList.size();
    }


    class NormalViewHolder extends RecyclerView.ViewHolder {
        //        private RecyclerView rvFeaturesNormal;
        private TextView tvFeatureItemName;
        private ImageView ivFeatureItem;
        private ConstraintLayout clItemParent;
        private CardView cvItemParent;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeatureItemName = itemView.findViewById(R.id.tvFeatureItemName);
            ivFeatureItem = itemView.findViewById(R.id.ivFeatureItem);
            clItemParent = itemView.findViewById(R.id.clItemParent);
            cvItemParent = itemView.findViewById(R.id.cvItemParent);
        }
    }

    private void invisibleView(CardView cvItemParent) {
        ViewGroup.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 0);
        cvItemParent.setLayoutParams(layoutParams);
    }
}
