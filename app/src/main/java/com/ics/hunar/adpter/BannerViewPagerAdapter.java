package com.ics.hunar.adpter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ics.hunar.Constant;
import com.ics.hunar.activity.SubcategoryActivity;
import com.ics.hunar.activity.VideoPlayActivity;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.Banner;

import java.util.List;

public class BannerViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Banner> bannerList;
    public static String fromQue = "";

    public BannerViewPagerAdapter(Context context, List<Banner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Banner banner = bannerList.get(position);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Utils.loadImage(imageView, banner.getBanner(), Utils.getCircularProgressDrawable(context, 5, 15));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (banner.getSubcategory().equals("0")) {
                    Constant.CATE_ID = Integer.parseInt(banner.getCategory());
                    context.startActivity(new Intent(context, SubcategoryActivity.class));
                } else {
                    Constant.SUB_CAT_ID = Integer.parseInt(banner.getSubcategory());
                    context.startActivity(new Intent(context, VideoPlayActivity.class).putExtra("fromQue", fromQue));
                    //Snackbar.make(v, banner.getId() + " " + banner.getCategory() + " " + banner.getSubcategory(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
