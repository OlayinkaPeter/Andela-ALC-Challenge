package com.olayinkapeter.githubapi_alc.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.olayinkapeter.githubapi_alc.R;
import com.olayinkapeter.githubapi_alc.app.model.DeveloperModel;
import com.olayinkapeter.githubapi_alc.helper.CircleTransform;

import java.util.List;

/**
 * Created by Olayinka_Peter on 3/4/2017.
 */

public class DeveloperAdapter extends RecyclerView.Adapter<DeveloperAdapter.MyViewHolder> {

    private List<DeveloperModel> developerList;
    Context context;

    public DeveloperAdapter(List<DeveloperModel> developerList, Context context) {
        this.developerList = developerList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_developer, parent, false);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(DeveloperAdapter.MyViewHolder holder, int position) {
        DeveloperModel developerModel = developerList.get(position);
        holder.developerUserName.setText(developerModel.getDeveloperUserName());
        holder.developerHTMLURL.setText(developerModel.getDeveloperHTMLURL());
        applyDeveloperImage(holder, developerModel);
    }

    @Override
    public int getItemCount() {
        return developerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView developerUserName, developerHTMLURL;
        public ImageView developerImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            developerUserName = (TextView) itemView.findViewById(R.id.user_name);
            developerHTMLURL = (TextView) itemView.findViewById(R.id.html_url);
            developerImage = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    private void applyDeveloperImage(MyViewHolder holder, DeveloperModel developerModel) {
        if (!TextUtils.isEmpty(developerModel.getDeveloperImageURL())) {
            Glide.with(context).load(developerModel.getDeveloperImageURL())
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.developerImage);
            holder.developerImage.setColorFilter(null);
        } else {
            holder.developerImage.setImageResource(R.drawable.bg_circle);
        }
    }
}
