package com.olayinkapeter.githubapi_alc.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.olayinkapeter.githubapi_alc.R;
import com.olayinkapeter.githubapi_alc.app.MainActivity;
import com.olayinkapeter.githubapi_alc.app.model.DeveloperModel;
import com.olayinkapeter.githubapi_alc.helper.CircleTransform;
import com.olayinkapeter.githubapi_alc.helper.FlipAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olayinka_Peter on 3/4/2017.
 */

public class DeveloperAdapter extends RecyclerView.Adapter<DeveloperAdapter.MyViewHolder> {

    private List<DeveloperModel> developerList;
    Context context;
    private DeveloperAdapterListener listener;

    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    private static int currentSelectedIndex = -1;


    public DeveloperAdapter(List<DeveloperModel> developerList, Context context, DeveloperAdapterListener listener) {
        this.developerList = developerList;
        this.context = context;
        this.listener = listener;

        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
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

        applyClickEvents(holder, position);
        applyIconAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return developerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView developerUserName, developerHTMLURL;
        public ImageView developerImage;

        public LinearLayout rowContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            developerUserName = (TextView) itemView.findViewById(R.id.user_name);
            developerHTMLURL = (TextView) itemView.findViewById(R.id.html_url);
            developerImage = (ImageView) itemView.findViewById(R.id.image);


            iconBack = (RelativeLayout) itemView.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) itemView.findViewById(R.id.icon_front);
            rowContainer = (LinearLayout) itemView.findViewById(R.id.row_container);
            iconContainer = (RelativeLayout) itemView.findViewById(R.id.icon_container);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onRowLongClicked(getAdapterPosition());
            itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
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


    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.rowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeveloperRowClicked(position);
            }
        });

        holder.rowContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(context, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(context, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    @Override
    public long getItemId(int position) {
        return developerList.get(position).getDeveloperID();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void toggleSelection(int position) {
        currentSelectedIndex = position;
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
            animationItemsIndex.delete(position);
        } else {
            selectedItems.put(position, true);
            animationItemsIndex.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public interface DeveloperAdapterListener {
        void onIconClicked(int position);

        void onDeveloperRowClicked(int position);

        void onRowLongClicked(int position);
    }
}
