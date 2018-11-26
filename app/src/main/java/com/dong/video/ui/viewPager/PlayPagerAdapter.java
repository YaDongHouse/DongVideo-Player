package com.dong.video.ui.viewPager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dong.video.R;
import com.dong.video.bean.VideoBean;

import java.util.List;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/26
 * Time: 17:48
 */
public class PlayPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<VideoBean> mItems;

    public PlayPagerAdapter(Context mContext, List<VideoBean> mItems) {
        this.mContext = mContext;
        this.mItems = mItems;
    }

    @Override
    public int getCount() {
        if (mItems!=null){
            return mItems.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        VideoBean bean = mItems.get(position);
        View itemView = View.inflate(mContext, R.layout.item_pager_play, null);
        FrameLayout playerContainer = itemView.findViewById(R.id.playerContainer);
        playerContainer.setTag(bean.getPath());
        ImageView coverView = itemView.findViewById(R.id.iv_cover);
        RequestOptions options = new RequestOptions()
                .centerInside();
        Glide.with(mContext)
                .load(bean.getCover())
                .apply(options)
                .into(coverView);
        container.addView(itemView);
        return itemView;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
