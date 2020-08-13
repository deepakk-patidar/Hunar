package com.ics.hunar.adpter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.activity.VideoPlay1Activity;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.Resume_List_Data;

import java.util.List;

public class Resume_Video_Adapter extends RecyclerView.Adapter<Resume_Video_Adapter.MyViewHolder>
{
    List<Resume_List_Data> dlist;
    Context context;

    public Resume_Video_Adapter(Context context, List<Resume_List_Data> datalis) {
        this.context = context;
        this.dlist = datalis;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public Resume_Video_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_resume_video,viewGroup,false);

        return new Resume_Video_Adapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Resume_Video_Adapter.MyViewHolder myViewHolder,final int i) {
        if (myViewHolder!=null){

            Utils.loadImage(myViewHolder.thumbnail, dlist.get(i).getThumbnail(), Utils.getCircularProgressDrawable(context, 5, 15));

            myViewHolder.name.setText(""+dlist.get(i).getVideo_name());

            myViewHolder.itemView.setOnClickListener(v->{
                Constant.CATE_ID = Integer.parseInt(dlist.get(i).getCategory());
                Constant.SUB_CAT_ID = Integer.parseInt(dlist.get(i).getSubcategory());

                Intent intent = new Intent(context, VideoPlay1Activity.class);
                intent.putExtra("URL", dlist.get(i).getVideo());
                intent.putExtra("POSITION", dlist.get(i).getTotal_time());
                intent.putExtra("SEEK_TO", dlist.get(i).getPalying_time());
                context.startActivity(intent);
            });

        }
    }

    @Override
    public int getItemCount(){
        return dlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tv_name);
            thumbnail= itemView.findViewById(R.id.thumbnail);

        }
    }
}
