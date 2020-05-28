package com.xinlan.imageeditlibrary.editimage.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.fragment.StickerFragment;


/**
 * 贴图分类列表Adapter
 *
 * @author panyi
 */
public class StickerAdapter extends RecyclerView.Adapter<ViewHolder> {
    public DisplayImageOptions imageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).showImageOnLoading(R.drawable.yd_image_tx)
            .build();// 下载图片显示

    private StickerFragment mStickerFragment;
    private ImageClick mImageClick = new ImageClick();
    private List<String> pathList = new ArrayList<String>();// 图片路径列表

    public StickerAdapter(StickerFragment fragment) {
        super();
        this.mStickerFragment = fragment;
    }

    public class ImageHolder extends ViewHolder {
        public ImageView image;

        public ImageHolder(View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.img);
        }
    }// end inner class

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_item, parent, false);
        ImageHolder holer = new ImageHolder(v);
        return holer;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageHolder imageHoler = (ImageHolder) holder;
        String path = pathList.get(position);
        ImageLoader.getInstance().displayImage("assets://" + path,
                imageHoler.image, imageOption);
        imageHoler.image.setTag(path);
        imageHoler.image.setOnClickListener(mImageClick);
    }

    public void addStickerImages(String folderPath) {
        pathList.clear();
        try {
            String[] files = mStickerFragment.getActivity().getAssets()
                    .list(folderPath);
            for (String name : files) {
                pathList.add(folderPath + File.separator + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }

    /**
     * 选择贴图
     *
     * @author panyi
     */
    private final class ImageClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            String data = (String) v.getTag();
            //System.out.println("data---->" + data);
            mStickerFragment.selectedStickerItem(data);
        }
    }// end inner class

}// end class
