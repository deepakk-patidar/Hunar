package com.ics.hunar.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.adpter.VideoListAdapter;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.model.UnlockStatusResponse;
import com.ics.hunar.model.VideoNew;
import com.ics.hunar.model.VideoNewResponse;
import com.ics.hunar.model.VideoStatus;
import com.ics.hunar.model.VideoStatusResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayActivity extends AppCompatActivity implements VideoListAdapter.OnChangeDataListener {
    private static int levelNo = 1;
    private List<VideoNew> videoAList;
    private RecyclerView recyclerView;
    public static String fromQue;
    private SwipeRefreshLayout srlVideoPlayer;
    private TextView tvError;
    private List<VideoStatus> videoStatusList;
    private ApiInterface apiInterface;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        SharedPreferencesUtil.init(this);
        apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        if (!Session.getUserData(Session.USER_ID, this).equals("")) {
            userId = Session.getUserData(Session.USER_ID, VideoPlayActivity.this);
        } else {
            userId = SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "");
        }
        recyclerView = findViewById(R.id.rvVideoList);
        srlVideoPlayer = findViewById(R.id.srlVideoPlayer);
        tvError = findViewById(R.id.tvError);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        fromQue = getIntent().getStringExtra("fromQue");
        //levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        srlVideoPlayer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
            }


        });
        //Set MediaController  to enable play, pause, forward, etc options.

    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoListAdapter.setOnChangeDataListener(this);
        // levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);

    }

    @Override
    public void onChangeData(boolean change) {
        if (change) {
            getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
        }
    }
    //    public void getMainCategoryFromJson(String access_key, int levelNo, int levelNo1, int subCatId) {
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
//                new com.android.volley.Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//                            String error = jsonObject.getString(Constant.ERROR);
//                            System.out.println("=====cate res " + response);
//                            if (error.equalsIgnoreCase("false")) {
//                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    VideoA videoA = new VideoA();
//                                    JSONObject object = jsonArray.getJSONObject(i);
//                                    videoA.setId(object.getString(Constant.ID));
//                                    videoA.setCategory(object.getString("category"));
//                                    videoA.setLanguageId(object.getString("language_id"));
//                                    videoA.setLevel("level");
//                                    videoA.setSubcategory("subcategory");
//                                    videoA.setVideo("video");
//                                    videoAList.add(videoA);
//                                }
//
//                                for (int i = 0; i < videoAList.size(); i++) {
//                                    videoView.setVideoURI(Uri.parse(videoAList.get(i).getVideo()));
//                                }
//                                videoView.setMediaController(mediaController);
//                                videoView.requestFocus();
//                                videoView.start();
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> stringMap = new HashMap<>();
//                stringMap.put("access_key", access_key);
//                stringMap.put("get_videos_by_level", String.valueOf(levelNo));
//                stringMap.put("level", String.valueOf(levelNo1));
//                stringMap.put("subcategory", String.valueOf(subCatId));
//
//                return stringMap;
//            }
//        };
//
//        AppController.getInstance().getRequestQueue().getCache().clear();
//        AppController.getInstance().addToRequestQueue(stringRequest);
//    }

    private void getVideoPlayer(int levelNo, int levelNo1, int subCatId) {
//        Map<String, String> stringMap = new HashMap<>();
//        stringMap.put("access_key", access_key);
//        stringMap.put("get_videos_by_level", String.valueOf(levelNo));
//        stringMap.put("level", String.valueOf(levelNo1));
//        stringMap.put("subcategory", String.valueOf(subCatId));
//        stringMap.put("language_id", String.valueOf(1));
        srlVideoPlayer.setRefreshing(true);
        tvError.setVisibility(View.GONE);
        Call<VideoNewResponse> videoResponseCall = apiInterface.getVideoByLevel("6808", String.valueOf(levelNo), String.valueOf(levelNo1), String.valueOf(subCatId), Session.getCurrentLanguage(VideoPlayActivity.this), userId);
        videoResponseCall.enqueue(new Callback<VideoNewResponse>() {
            @Override
            public void onResponse(Call<VideoNewResponse> call, Response<VideoNewResponse> response) {
                srlVideoPlayer.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            videoAList = new ArrayList<>();
                            videoAList.clear();
                            videoAList.addAll(response.body().getVideoNewList());
                            unlockVideoWithApi(videoAList.get(0).getId());
                            videoAList.get(0).setIsUnlocked("1");
                            VideoListAdapter videoListAdapter = new VideoListAdapter(VideoPlayActivity.this, videoAList, fromQue);
                            recyclerView.setAdapter(videoListAdapter);
                        } else {
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText("not found");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoNewResponse> call, Throwable t) {
                srlVideoPlayer.setRefreshing(false);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(t.getLocalizedMessage());

            }
        });
    }


    private void getVideoStatus(String user_id) {
        srlVideoPlayer.setRefreshing(true);
        Call<VideoStatusResponse> videoStatusResponseCall = apiInterface.getVideoStatus("6808", "1", user_id);
        videoStatusResponseCall.enqueue(new Callback<VideoStatusResponse>() {
            @Override
            public void onResponse(Call<VideoStatusResponse> call, Response<VideoStatusResponse> response) {
                srlVideoPlayer.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            videoStatusList = new ArrayList<>();
                            videoStatusList.addAll(response.body().getVideoStatusList());
                            getVideoPlayer(levelNo, levelNo, Constant.SUB_CAT_ID);
                        } else {
                            Toast.makeText(VideoPlayActivity.this, "not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoStatusResponse> call, Throwable t) {
                srlVideoPlayer.setRefreshing(false);
                Toast.makeText(VideoPlayActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unlockVideoWithApi(String videoId) {
        apiInterface.unlockVideoStatus("6808", "1", userId, videoId).enqueue(new Callback<UnlockStatusResponse>() {
            @Override
            public void onResponse(Call<UnlockStatusResponse> call, Response<UnlockStatusResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            // Toast.makeText(VideoPlayActivity.this, "unlock !true", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VideoPlayActivity.this, "unlock !false", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(VideoPlayActivity.this, "Info", "null", false, "Ok", "", "response");

                    }
                }
            }

            @Override
            public void onFailure(Call<UnlockStatusResponse> call, Throwable t) {
                AlertDialogUtil.showAlertDialogBox(VideoPlayActivity.this, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }
}