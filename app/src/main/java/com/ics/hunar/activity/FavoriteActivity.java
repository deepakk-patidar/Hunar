package com.ics.hunar.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.adpter.VideoListAdapter;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.FavoriteResponse;
import com.ics.hunar.model.VideoNew;
import com.ics.hunar.model.VideoNewResponse;
import com.ics.hunar.model.VideoStatus;
import com.ics.hunar.model.VideoStatusResponse;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {
    private static int levelNo = 1;
    private List<VideoStatus> videoStatusList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvError;
    private List<VideoNew> videoAList;
    public static String fromQue;
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        SharedPreferencesUtil.init(this);
        recyclerView = findViewById(R.id.rvFavoriteList);
        tvError = findViewById(R.id.tvFavoriteError);
        swipeRefreshLayout = findViewById(R.id.srlFavorite);
        fromQue = getIntent().getStringExtra("fromQue");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        getVideoStatus(Session.getUserData(Session.USER_ID,FavoriteActivity.this));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getVideoStatus(Session.getUserData(Session.USER_ID,FavoriteActivity.this));
        });

    }


//    private void getVideoPlayer(int levelNo, int levelNo1, int subCatId) {
//        swipeRefreshLayout.setRefreshing(true);
//        tvError.setVisibility(View.GONE);
//        ApiInterface apiService = ApiClient.getMainClient().create(ApiInterface.class);
//        Call<VideoNewResponse> videoResponseCall = apiService.getVideoByLevel("6808", String.valueOf(levelNo), String.valueOf(levelNo1), String.valueOf(subCatId), Session.getCurrentLanguage(FavoriteActivity.this), Session.getUserData(Session.USER_ID, FavoriteActivity.this));
//        videoResponseCall.enqueue(new Callback<VideoNewResponse>() {
//            @Override
//            public void onResponse(Call<VideoNewResponse> call, Response<VideoNewResponse> response) {
//                swipeRefreshLayout.setRefreshing(false);
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        if (response.body().getError().equalsIgnoreCase("false")) {
//                            videoAList = new ArrayList<>();
//                            for (int i = 0; i < response.body().getVideoNewList().size(); i++) {
//                                if (response.body().getVideoNewList().get())
//
//                            }
//                            VideoListAdapter videoListAdapter = new VideoListAdapter(FavoriteActivity.this, videoAList, fromQue);
//                            recyclerView.setAdapter(videoListAdapter);
//                        } else {
//                            tvError.setVisibility(View.VISIBLE);
//                            tvError.setText("not found");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<VideoNewResponse> call, Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
//                tvError.setVisibility(View.VISIBLE);
//                tvError.setText(t.getLocalizedMessage());
//
//            }
//        });
//    }

    private void getVideoStatus(String user_id) {
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        Call<VideoStatusResponse> videoStatusResponseCall = apiInterface.getVideoStatus("6808", "1", user_id);
        videoStatusResponseCall.enqueue(new Callback<VideoStatusResponse>() {
            @Override
            public void onResponse(Call<VideoStatusResponse> call, Response<VideoStatusResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            videoStatusList = new ArrayList<>();
                            videoStatusList.addAll(response.body().getVideoStatusList());
                            //getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
                            favoriteAdapter = new FavoriteAdapter(FavoriteActivity.this, videoStatusList);
                            recyclerView.setAdapter(favoriteAdapter);
                        } else {
                            Toast.makeText(FavoriteActivity.this, "not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoStatusResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FavoriteActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {
        private Context context;
        private List<VideoStatus> videoStatusList;

        public FavoriteAdapter(Context context, List<VideoStatus> videoStatusList) {
            this.context = context;
            this.videoStatusList = videoStatusList;
        }

        @NonNull
        @Override
        public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.video_list_layout, parent, false);
            return new FavViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
            VideoStatus videoStatus = videoStatusList.get(position);
            holder.tvVideoName.setText(videoStatus.getVideoName());
            Utils.loadImage(holder.ivVideo, videoStatus.getThumbnail(), Utils.getCircularProgressDrawable(context));
            if (videoStatus.getIsFavourite() != null) {
                if (videoStatus.getIsFavourite().equals("1")) {
                    holder.btn_like.setLiked(true);
                } else {
                    holder.btn_like.setLiked(false);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            holder.btn_like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    //likeAndUnLike(videoStatus.getId(), "1", SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""), "Like", likeButton, position);
                    if (videoStatus.getIsFavourite() != null) {
                        if (videoStatus.getIsFavourite().equals("1")) {
                            holder.btn_like.setLiked(true);
                        } else {
                            holder.btn_like.setLiked(false);
                        }
                    }

                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    //likeAndUnLike(videoStatus.getId(), "0", SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""), "unLiked", likeButton, position);
                    if (videoStatus.getIsFavourite() != null) {
                        if (videoStatus.getIsFavourite().equals("1")) {
                            holder.btn_like.setLiked(true);
                        } else {
                            holder.btn_like.setLiked(false);
                        }
                    }

                }
            });
//            holder.itemView.setOnClickListener(v -> {
//                if (videoStatus.getVideo() != null) {
//                    context.startActivity(new Intent(context, VideoPlay1Activity.class).putExtra("URL", videoStatus.getVideo()).putExtra("POSITION", position));
//                }
//            });


        }

        @Override
        public int getItemCount() {
            return videoStatusList.size();
        }

        class FavViewHolder extends RecyclerView.ViewHolder {
            private TextView tvVideoName;
            private ImageView ivVideo, ivDownload, ivVideoIcon;
            private LikeButton btn_like;

            public FavViewHolder(@NonNull View itemView) {
                super(itemView);
                tvVideoName = itemView.findViewById(R.id.tvVideoName);
                ivVideo = itemView.findViewById(R.id.ivVideo);
                ivDownload = itemView.findViewById(R.id.ivDownload);
                ivVideoIcon = itemView.findViewById(R.id.ivVideoIcon);
                btn_like = itemView.findViewById(R.id.btn_like);

            }
        }
    }

    private void likeAndUnLike(String videoId, String favorite, String userId, String like, LikeButton likeButton, int position) {
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        Call<FavoriteResponse> favoriteResponseCall = apiInterface.postFavoriteVideo("6808", videoId, favorite, userId);
        favoriteResponseCall.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            // getVideoStatus(SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""));
                            getVideoStatus(SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""));
                            if (like.equalsIgnoreCase("like")) {
                                videoStatusList.get(position).setIsFavourite("1");
                            } else {
                                videoStatusList.get(position).setIsFavourite("0");
                                videoStatusList.remove(position);
                            }
                            if (favoriteAdapter != null) {
                                favoriteAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(FavoriteActivity.this, like, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FavoriteActivity.this, "unSuccess", Toast.LENGTH_SHORT).show();
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

}