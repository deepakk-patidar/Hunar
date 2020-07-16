package com.ics.hunar.adpter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ics.hunar.R;
import com.ics.hunar.activity.PlayActivity;
import com.ics.hunar.activity.VideoPlay1Activity;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.FavoriteResponse;
import com.ics.hunar.model.VideoNew;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int FOOTER_VIEW = 1;
    private Context context;
    private List<VideoNew> videoAList;
    private String fromQue, userId;
    private static OnChangeDataListener onChangeDataListener;

    public VideoListAdapter(Context context, List<VideoNew> videoAList, String fromQue) {
        this.context = context;
        this.videoAList = videoAList;
        this.fromQue = fromQue;
        SharedPreferencesUtil.init(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == FOOTER_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.footer_layout, parent, false);
            return new FooterViewHolder(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.video_list_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof VideoViewHolder) {
                ((VideoViewHolder) holder).bind(position);

            } else if (holder instanceof FooterViewHolder) {
                String subCatName = SharedPreferencesUtil.read(SharedPreferencesUtil.SUB_CATEGORY_NAME, "");
                ((FooterViewHolder) holder).btnPlayQuiz.setText(String.format("Test of %s", subCatName));
                if (videoAList.get(videoAList.size() - 1).getIsUnlocked().equals("1")) {
                    ((FooterViewHolder) holder).ivQuizLock.setImageResource(R.drawable.ic_open_padlock);
                } else {
                    ((FooterViewHolder) holder).ivQuizLock.setImageResource(R.drawable.ic_baseline_lock_white);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void likeAndUnLike(String videoId, String userId, String like, LikeButton likeButton, int position) {
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        Call<FavoriteResponse> favoriteResponseCall = apiInterface.postFavoriteVideo("6808", videoId, "1", userId);
        favoriteResponseCall.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            Toast.makeText(context, like, Toast.LENGTH_SHORT).show();
                            if (like.equalsIgnoreCase("like")) {
                                videoAList.get(position).setIsFavourite("1");
                            } else {
                                videoAList.get(position).setIsFavourite("0");
                            }
                            notifyDataSetChanged();
//                            if (onChangeDataListener != null) {
//                                onChangeDataListener.onChangeData(true);
//                            }
                        } else {
                            Toast.makeText(context, "unSuccess", Toast.LENGTH_SHORT).show();
                            likeButton.setLiked(false);

                        }
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (videoAList == null) {
            return 0;
        }

        if (videoAList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }
        // Add extra view to show the footer view
        return videoAList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == videoAList.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoName;
        private ImageView ivVideo, ivDownload, ivVideoIcon, ivVideoLock;
        private LikeButton btn_like;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoName = itemView.findViewById(R.id.tvVideoName);
            ivVideo = itemView.findViewById(R.id.ivVideo);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            ivVideoIcon = itemView.findViewById(R.id.ivVideoIcon);
            btn_like = itemView.findViewById(R.id.btn_like);
            ivVideoLock = itemView.findViewById(R.id.ivVideoLock);
        }

        public void bind(int position) {
            VideoNew videoNew = videoAList.get(position);
            tvVideoName.setText(videoNew.getVideoName());
            Utils.loadImage(ivVideo, videoNew.getThumbnail(), Utils.getCircularProgressDrawable(context));
            if (videoNew.getIsUnlocked().equals("1")) {
                ivVideoLock.setImageResource(R.drawable.ic_open_padlock);
            } else {
                ivVideoLock.setImageResource(R.drawable.ic_padlock);
            }
            btn_like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    likeAndUnLike(videoNew.getId(), Session.getUserData(Session.USER_ID, context), "Like", likeButton, position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeAndUnLike(videoNew.getId(), Session.getUserData(Session.USER_ID, context), "unLiked", likeButton, position);
                }
            });
            itemView.setOnClickListener(v -> {
                if (videoNew.getVideo() != null) {
                    if (videoNew.getIsUnlocked().equals("1")) {
                        context.startActivity(new Intent(context, VideoPlay1Activity.class).putExtra("URL", videoNew.getVideo()).putExtra("POSITION", position));
                    } else {
                        AlertDialogUtil.showAlertDialogBox(context, "Warning", "Please watch previous video first", false, "Ok", "", "xyz");
                    }
                }
            });
            if (videoNew.getIsFavourite() != null) {
                if (videoNew.getIsFavourite().equals("1")) {
                    btn_like.setLiked(true);
                } else {
                    btn_like.setLiked(false);
                }
            }

        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button btnPlayQuiz;
        private ImageView ivQuizLock;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPlayQuiz = itemView.findViewById(R.id.btnPlayQuiz1);
            ivQuizLock = itemView.findViewById(R.id.ivQuizLock);
            btnPlayQuiz.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!Session.getUserData(Session.USER_ID, context).equals("") || !SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "").equals("")) {
                if (videoAList.get(videoAList.size() - 1).getIsUnlocked().equals("1")) {
                    context.startActivity(new Intent(context, PlayActivity.class).putExtra("fromQue", fromQue));
                } else {
                    AlertDialogUtil.showAlertDialogBox(context, "Warning", "Please watch previous video first", false, "Ok", "", "xyz");
                }
            } else {
                AlertDialogUtil.showAlertDialogBox(context, "Warning", "Please login first", false, "Ok", "", "xyz");
            }
        }
    }

    public interface OnChangeDataListener {
        void onChangeData(boolean change);
    }

    public static void setOnChangeDataListener(OnChangeDataListener listener) {
        onChangeDataListener = listener;
    }
}
