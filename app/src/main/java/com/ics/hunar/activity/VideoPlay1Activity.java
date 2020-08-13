package com.ics.hunar.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.PublicFunctions;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.FavoriteResponse;
import com.ics.hunar.model.UnlockStatusResponse;
import com.ics.hunar.model.VideoNew;
import com.ics.hunar.model.VideoNewResponse;
import com.ics.hunar.model.VideoStatus;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlay1Activity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static int levelNo = 1;
    //private AndExoPlayerView andExoPlayerView;
    private RecyclerView rvVideoList;
    private List<VideoNew> videoAList;
    private ConstraintLayout clVideoList;
    private TextView tvVideoPlayerListError;
    private SwipeRefreshLayout srlVideoPlayerList;
    private VideoView videoView;
    private String url;
    int position, videoId;
    private MediaController mediaController;
    private int duration;
    private ImageButton ivBtnFullScreen;
    private boolean fullScreen = false;
    private ProgressBar progressBar;
    private Handler handler;
    private TextView tvVideoTitle;
    private List<VideoStatus> videoStatusList;
    private VideoListPlayAdapter videoListPlayAdapter;
    private ApiInterface apiInterface;
    private String userId;
    private Boolean resume_time = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play1);
        url = getIntent().getStringExtra("URL");
        position = getIntent().getIntExtra("POSITION", 0);
        videoId = getIntent().getIntExtra("VIDEO_ID", 0);
//      levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        // andExoPlayerView = findViewById(R.id.andExoPlayerView);
        SharedPreferencesUtil.init(this);
        apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        if (!Session.getUserData(Session.USER_ID, this).equals("")) {
            userId = Session.getUserData(Session.USER_ID, this);
        } else {
            userId = SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "");
        }
        if (videoId > 0) {
            searchVideoPlay(levelNo, levelNo, Constant.SUB_CAT_ID, url);
        }

        videoView = findViewById(R.id.videoView);
        clVideoList = findViewById(R.id.clVideoList);
        ivBtnFullScreen = findViewById(R.id.ivBtnFullScreen);
        rvVideoList = findViewById(R.id.rvVideoList);
        progressBar = findViewById(R.id.pbVideoView);
        tvVideoTitle = findViewById(R.id.tvVideoTitle);
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        rvVideoList.setHasFixedSize(true);
        tvVideoPlayerListError = findViewById(R.id.tvVideoPlayerListError);
        srlVideoPlayerList = findViewById(R.id.srlVideoPlayerList);
//        andExoPlayerView.setSource(url);
//        andExoPlayerView.setShowController(true);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoAList = new ArrayList<>();
        srlVideoPlayerList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getVideoStatus(SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""));
                getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
            }
        });
        //Set MediaController  to enable play, pause, forward, etc options.
        // getVideoStatus(SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""));
        getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        // fullScreen(true);
        ivBtnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullScreen) {
                    fullScreen(true);
                    fullScreen = false;
                } else {
                    fullScreen(false);
                    fullScreen = true;
                }
            }
        });
        videoView.setOnTouchListener((v, event) -> {
            if (ivBtnFullScreen.getVisibility() == View.GONE && tvVideoTitle.getVisibility() == View.GONE) {
                ivBtnFullScreen.setVisibility(View.VISIBLE);
                tvVideoTitle.setVisibility(View.VISIBLE);
                removeHandler();
                hideController();
            } else if (ivBtnFullScreen.getVisibility() == View.VISIBLE && tvVideoTitle.getVisibility() == View.VISIBLE) {
                ivBtnFullScreen.setVisibility(View.GONE);
                tvVideoTitle.setVisibility(View.GONE);
                removeHandler();
            }
            return false;
        });

    }

    private void searchVideoPlay(int levelNo, int levelNo1, int subCatId, String url) {
        srlVideoPlayerList.setRefreshing(true);
        tvVideoPlayerListError.setVisibility(View.GONE);
        ApiInterface apiService = ApiClient.getMainClient().create(ApiInterface.class);
        Call<VideoNewResponse> videoResponseCall = apiService.getVideoByLevel("6808", String.valueOf(levelNo), String.valueOf(levelNo1), String.valueOf(subCatId), Session.getCurrentLanguage(VideoPlay1Activity.this), Session.getUserData(Session.USER_ID, VideoPlay1Activity.this));
        videoResponseCall.enqueue(new Callback<VideoNewResponse>() {
            @Override
            public void onResponse(Call<VideoNewResponse> call, Response<VideoNewResponse> response) {
                srlVideoPlayerList.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            if (videoAList != null) {
                                videoAList.clear();
                            }
                            videoAList = response.body().getVideoNewList();
                            for (int i = 0; i < videoAList.size(); i++) {
                                if (videoId == Integer.parseInt(videoAList.get(i).getId()) && url.equals(videoAList.get(i).getVideo())) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    tvVideoTitle.setText(videoAList.get(i).getVideoName());
                                    videoView.setVideoURI(Uri.parse(videoAList.get(i).getVideo()));
                                    videoView.requestFocus();
                                    videoView.start();
                                    videoAList.get(i).setIsPlayed("1");
                                }
                            }
                            videoListPlayAdapter = new VideoListPlayAdapter(VideoPlay1Activity.this, videoAList);
                            rvVideoList.setAdapter(videoListPlayAdapter);
//                            mediaController.setPrevNextListeners(v -> {
//                               // position++;
//                                if (position < videoAList.size()) {
//                                    if (videoAList.get(position).getIsUnlocked().equals("1")) {
//                                        progressBar.setVisibility(View.VISIBLE);
//                                        tvVideoTitle.setText(videoAList.get(position).getVideoName());
//                                        videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
//                                        videoView.requestFocus();
//                                        videoView.start();
//                                        for (int i = 0; i < videoAList.size(); i++) {
//                                            if (i == position) {
//                                                videoAList.get(i).setIsPlayed("1");
//                                            } else {
//                                                videoAList.get(i).setIsPlayed("0");
//                                            }
//                                        }
//                                        videoListPlayAdapter.notifyDataSetChanged();
//                                    } else {
//                                        AlertDialogUtil.showAlertDialogBox(VideoPlay1Activity.this, "Warning", "Please watch previous video first", false, "Ok", "", "xyz");
//                                    }
//                                } else {
//                                    Toast.makeText(VideoPlay1Activity.this, "not found", Toast.LENGTH_SHORT).show();
//                                }
//                            }, v -> {
//                                position--;
//                                if (position != -1 && position < videoAList.size()) {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    tvVideoTitle.setText(videoAList.get(position).getVideoName());
//                                    videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
//                                    videoView.requestFocus();
//                                    videoView.start();
//                                    for (int i = 0; i < videoAList.size(); i++) {
//                                        if (i == position) {
//                                            videoAList.get(i).setIsPlayed("1");
//                                        } else {
//                                            videoAList.get(i).setIsPlayed("0");
//                                        }
//                                    }
//                                    videoListPlayAdapter.notifyDataSetChanged();
//                                } else {
//                                    Toast.makeText(VideoPlay1Activity.this, "not found", Toast.LENGTH_SHORT).show();
//                                }
//                            });

                        } else {
                            tvVideoPlayerListError.setVisibility(View.VISIBLE);
                            tvVideoPlayerListError.setText("not found");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoNewResponse> call, Throwable t) {
                srlVideoPlayerList.setRefreshing(false);
                tvVideoPlayerListError.setVisibility(View.VISIBLE);
                tvVideoPlayerListError.setText(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        ivBtnFullScreen.setVisibility(View.VISIBLE);
        tvVideoTitle.setVisibility(View.VISIBLE);
        hideController();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (videoAList != null) {
            position++;
            if (position == videoAList.size()) {
                position = 0;
            }
            unlockVideoWithApi(videoAList.get(position).getId());
            for (int i = 0; i < videoAList.size(); i++) {
                if (i == position) {
                    videoAList.get(i).setIsPlayed("1");
                    videoAList.get(i).setIsUnlocked("1");
                } else {
                    videoAList.get(i).setIsPlayed("0");
                }
            }
            videoListPlayAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.VISIBLE);
            tvVideoTitle.setText(videoAList.get(position).getVideoName());
            videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
            videoView.requestFocus();
            videoView.start();
        }
    }

    private void hideController() {
        handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ivBtnFullScreen.setVisibility(View.GONE);
            tvVideoTitle.setVisibility(View.GONE);
        }
    };

    private void removeHandler() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            clVideoList.setVisibility(View.GONE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            clVideoList.setVisibility(View.VISIBLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            if (videoView.isPlaying()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ivBtnFullScreen.setImageResource(R.drawable.ic_baseline_fullscreen);
                videoView.pause();
                duration = videoView.getCurrentPosition();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            if (!videoView.isPlaying()) {
                progressBar.setVisibility(View.VISIBLE);
                videoView.seekTo(duration);
                videoView.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (videoView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ivBtnFullScreen.setImageResource(R.drawable.ic_baseline_fullscreen);
        } else {
            if (resume_time){
            super.onBackPressed();}
            else{
                send_resume_time();
            }
        }
    }

    private void getVideoPlayer(int levelNo, int levelNo1, int subCatId) {
        srlVideoPlayerList.setRefreshing(true);
        tvVideoPlayerListError.setVisibility(View.GONE);
        ApiInterface apiService = ApiClient.getMainClient().create(ApiInterface.class);
        Call<VideoNewResponse> videoResponseCall = apiService.getVideoByLevel("6808", String.valueOf(levelNo), String.valueOf(levelNo1), String.valueOf(subCatId), Session.getCurrentLanguage(VideoPlay1Activity.this), Session.getUserData(Session.USER_ID, VideoPlay1Activity.this));
        videoResponseCall.enqueue(new Callback<VideoNewResponse>() {
            @Override
            public void onResponse(Call<VideoNewResponse> call, Response<VideoNewResponse> response) {
                srlVideoPlayerList.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            if (videoAList != null) {
                                videoAList.clear();
                            }
                            videoAList = response.body().getVideoNewList();
                            mediaController.setPrevNextListeners(v -> {
                                position++;
                                if (position < videoAList.size()) {
                                    if (videoAList.get(position).getIsUnlocked().equals("1")) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        tvVideoTitle.setText(videoAList.get(position).getVideoName());
                                        videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
                                        videoView.requestFocus();
                                        videoView.start();
                                        for (int i = 0; i < videoAList.size(); i++) {
                                            if (i == position) {
                                                videoAList.get(i).setIsPlayed("1");
                                            } else {
                                                videoAList.get(i).setIsPlayed("0");
                                            }
                                        }
                                        videoListPlayAdapter.notifyDataSetChanged();
                                    } else {
                                        AlertDialogUtil.showAlertDialogBox(VideoPlay1Activity.this, "Warning", "Please watch previous video first", false, "Ok", "", "xyz");
                                    }
                                } else {
                                    Toast.makeText(VideoPlay1Activity.this, "not found", Toast.LENGTH_SHORT).show();
                                }
                            }, v -> {
                                position--;
                                if (position != -1 && position < videoAList.size()) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    tvVideoTitle.setText(videoAList.get(position).getVideoName());
                                    videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
                                    videoView.requestFocus();
                                    videoView.start();
                                    for (int i = 0; i < videoAList.size(); i++) {
                                        if (i == position) {
                                            videoAList.get(i).setIsPlayed("1");
                                        } else {
                                            videoAList.get(i).setIsPlayed("0");
                                        }
                                    }
                                    videoListPlayAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(VideoPlay1Activity.this, "not found", Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressBar.setVisibility(View.VISIBLE);
                            tvVideoTitle.setText(videoAList.get(position).getVideoName());
                            videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
                            videoId = Integer.parseInt(videoAList.get(position).getId());
                            videoView.requestFocus();
                            videoView.start();
                            videoAList.get(position).setIsPlayed("1");
                            videoListPlayAdapter = new VideoListPlayAdapter(VideoPlay1Activity.this, videoAList);
                            rvVideoList.setAdapter(videoListPlayAdapter);
                        } else {
                            tvVideoPlayerListError.setVisibility(View.VISIBLE);
                            tvVideoPlayerListError.setText("not found");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoNewResponse> call, Throwable t) {
                srlVideoPlayerList.setRefreshing(false);
                tvVideoPlayerListError.setVisibility(View.VISIBLE);
                tvVideoPlayerListError.setText(t.getLocalizedMessage());
            }
        });
    }

    class VideoListPlayAdapter extends RecyclerView.Adapter<VideoListPlayAdapter.VideoListViewHolder> {
        private Context context;
        private List<VideoNew> videoAList;

        public VideoListPlayAdapter(Context context, List<VideoNew> videoAList) {
            this.context = context;
            this.videoAList = videoAList;
        }

        @NonNull
        @Override
        public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.video_list_layout, parent, false);
            return new VideoListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {
            VideoNew videoNew = videoAList.get(position);
            holder.tvVideoName.setText(videoNew.getVideoName());
            Utils.loadImage(holder.ivVideo, videoNew.getThumbnail(), Utils.getCircularProgressDrawable(context, 5, 15));
            if (videoNew.getIsPlayed().equals("1")) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            if (videoNew.getIsUnlocked().equals("1")) {
                holder.ivVideoLock.setImageResource(R.drawable.ic_open_padlock);
            } else {
                holder.ivVideoLock.setImageResource(R.drawable.ic_padlock);
            }

            holder.itemView.setOnClickListener(v -> {
                if (videoNew.getIsUnlocked().equals("1")) {
                    VideoPlay1Activity.this.position = position;
                    VideoPlay1Activity.this.videoId = Integer.parseInt(videoAList.get(position).getId());
                    progressBar.setVisibility(View.VISIBLE);
                    tvVideoTitle.setText(videoAList.get(position).getVideoName());
                    videoView.setVideoURI(Uri.parse(videoAList.get(position).getVideo()));
                    videoView.requestFocus();
                    videoView.start();
                    for (int i = 0; i < videoAList.size(); i++) {
                        if (i == position) {
                            videoAList.get(i).setIsPlayed("1");
                        } else {
                            videoAList.get(i).setIsPlayed("0");
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    AlertDialogUtil.showAlertDialogBox(context, "Warning", "Please watch previous video first", false, "Ok", "", "xyz");
                }
            });
            if (videoNew.getIsFavourite() != null) {
                if (videoNew.getIsFavourite().equals("1")) {
                    holder.btn_like.setLiked(true);
                } else {
                    holder.btn_like.setLiked(false);
                }
            }
            holder.btn_like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    likeAndUnLike(videoNew.getId(), Session.getUserData(Session.USER_ID, context), "Like", likeButton, position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeAndUnLike(videoNew.getId(), Session.getUserData(Session.USER_ID, context), "unLiked", likeButton, position);
                }
            });


        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return videoAList.size();
        }

        class VideoListViewHolder extends RecyclerView.ViewHolder {
            private TextView tvVideoName;
            private ImageView ivVideo, ivDownload, ivVideoIcon, ivVideoLock;
            private LikeButton btn_like;

            public VideoListViewHolder(@NonNull View itemView) {
                super(itemView);
                tvVideoName = itemView.findViewById(R.id.tvVideoName);
                ivVideo = itemView.findViewById(R.id.ivVideo);
                ivDownload = itemView.findViewById(R.id.ivDownload);
                ivVideoIcon = itemView.findViewById(R.id.ivVideoIcon);
                btn_like = itemView.findViewById(R.id.btn_like);
                ivVideoLock = itemView.findViewById(R.id.ivVideoLock);
            }
        }
    }

    private void fullScreen(boolean isFullScreen) {

        if (!isFullScreen) {
//            Log.v("Full screen", "-----------is full screen------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//            int height = displaymetrics.heightPixels;
//            int width = displaymetrics.widthPixels;
            int height = PublicFunctions.getScreenWidth();
            int width = PublicFunctions.getScreenHeight();
            ivBtnFullScreen.setImageResource(R.drawable.ic_baseline_fullscreen_exit);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);

        } else {
//            Log.v("Full screen", "-----------small screen------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//            int height = displaymetrics.heightPixels;
//            int width = displaymetrics.widthPixels;
            int height = PublicFunctions.getScreenWidth();
            int width = PublicFunctions.getScreenHeight();
            ivBtnFullScreen.setImageResource(R.drawable.ic_baseline_fullscreen);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);

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
                            //getVideoStatus(SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, ""));
                            if (like.equalsIgnoreCase("like")) {
                                videoAList.get(position).setIsFavourite("1");
                            } else {
                                videoAList.get(position).setIsFavourite("0");
                            }
                            if (videoListPlayAdapter != null) {
                                videoListPlayAdapter.notifyDataSetChanged();
                            }

                            Toast.makeText(VideoPlay1Activity.this, like, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VideoPlay1Activity.this, "unSuccess", Toast.LENGTH_SHORT).show();
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

    //    private void getVideoStatus(String user_id) {
//        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
//        Call<VideoStatusResponse> videoStatusResponseCall = apiInterface.getVideoStatus("6808", "1", user_id);
//        videoStatusResponseCall.enqueue(new Callback<VideoStatusResponse>() {
//            @Override
//            public void onResponse(Call<VideoStatusResponse> call, Response<VideoStatusResponse> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        if (!response.body().getError()) {
//                            videoStatusList = new ArrayList<>();
//                            videoStatusList.addAll(response.body().getVideoStatusList());
//                            getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
//                        } else {
//                            Toast.makeText(VideoPlay1Activity.this, "not found", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<VideoStatusResponse> call, Throwable t) {
//                Toast.makeText(VideoPlay1Activity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void unlockVideoWithApi(String videoId) {
        apiInterface.unlockVideoStatus("6808", "1", userId, videoId).enqueue(new Callback<UnlockStatusResponse>() {
            @Override
            public void onResponse(Call<UnlockStatusResponse> call, Response<UnlockStatusResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            // Toast.makeText(VideoPlayActivity.this, "unlock !true", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VideoPlay1Activity.this, "unlock !false", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(VideoPlay1Activity.this, "Info", "null", false, "Ok", "", "response");

                    }
                }
            }

            @Override
            public void onFailure(Call<UnlockStatusResponse> call, Throwable t) {
                AlertDialogUtil.showAlertDialogBox(VideoPlay1Activity.this, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }

    private void send_resume_time(){

        ProgressDialog pd = new ProgressDialog(VideoPlay1Activity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Processing...");
        pd.show();

        String userr_id = "";
        if (!Session.getUserData(Session.USER_ID, this).equals("")) {
            userr_id = Session.getUserData(Session.USER_ID, this);
        } else {
            userr_id = SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "");
        }
        Log.e("Video Details ","  >>>>>>>>> \n"+" Current Position - "
                +Utils.get_ms_formatvideo(videoView.getCurrentPosition())+"\n Total Length - "
                +Utils.get_ms_formatvideo(videoView.getDuration())+ "--- User id  "+userr_id+"---  Video id "+videoId+ " --- " );

        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        Call<FavoriteResponse> send_resume_time_api = apiInterface.send_resume_timing("6808", "1",
                userr_id, ""+videoId,""+videoView.getCurrentPosition(),""+videoView.getDuration());

        send_resume_time_api.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                pd.dismiss();
                Utils.retro_call_info(""+response.raw().request().url(),new Gson().toJson(response.body()));
                if (response.isSuccessful()){
                    if (!response.body().getError()) {
                        Toast.makeText(VideoPlay1Activity.this, "Resume Time Saved...", Toast.LENGTH_SHORT).show();
                        resume_time = true;
                        onBackPressed();
                    }else{

                    }
                }else{
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(VideoPlay1Activity.this, "Resume Time, Not Saved...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}