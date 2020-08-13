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
import com.ics.hunar.activity.VideoPlay1Activity;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.searching.Video;

import java.util.List;

public class SearchingVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<Video> videoList;

    public SearchingVideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
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
            if (videoList != null && videoList.size() > 0) {
                searchingHeaderViewHolder.tvSearchingHeading.setText("Videos");
            } else {
                ViewGroup.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 0);
                searchingHeaderViewHolder.tvSearchingHeading.setLayoutParams(layoutParams);
            }

        } else if (holder instanceof SearchingItemViewHolder) {
            SearchingItemViewHolder searchingItemViewHolder = (SearchingItemViewHolder) holder;
            Utils.loadImage(searchingItemViewHolder.ivSearching, videoList.get(position - 1).getThumbnail(), Utils.getCircularProgressDrawable(context, 5, 15));
            searchingItemViewHolder.tvSearching.setText(String.format("%s", videoList.get(position - 1).getVideoName()));
            searchingItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Session.getUserData(Session.USER_ID, context).equals("") || !SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "").equals("")) {
                        if (videoList.get(position - 1).getVideo() != null) {
                            if (videoList.get(position - 1).getIs_unlocked().equals("1")) {
                                Constant.SUB_CAT_ID = Integer.parseInt(videoList.get(position - 1).getSubcategory());
                                context.startActivity(new Intent(context, VideoPlay1Activity.class).putExtra("URL", videoList.get(position - 1).getVideo()).putExtra("POSITION", position - 1).putExtra("VIDEO_ID", videoList.get(position - 1).getId()));
                            } else {
                                AlertDialogUtil.showAlertDialogBox(context, "Warning", "Please watch previous video first", false, "Ok", "", "xyz");
                            }
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(context, "Warning", "Please login first", false, "Ok", "", "xyz");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size() + 1;
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
