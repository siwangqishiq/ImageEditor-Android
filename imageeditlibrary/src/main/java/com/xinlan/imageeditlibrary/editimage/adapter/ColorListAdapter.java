package com.xinlan.imageeditlibrary.editimage.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.fragment.StirckerFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 颜色列表Adapter
 * 
 * @author panyi
 * 
 */
public class ColorListAdapter extends RecyclerView.Adapter<ViewHolder> {
	private StirckerFragment mStirckerFragment;
	private List<String> pathList = new ArrayList<String>();// 图片路径列表

	public ColorListAdapter(StirckerFragment fragment) {
		super();
		this.mStirckerFragment = fragment;
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
				R.layout.view_sticker_item, null);
		ImageHolder holer = new ImageHolder(v);
		return holer;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
	}

	public void addStickerImages(String folderPath) {
		pathList.clear();
		try {
			String[] files = mStirckerFragment.getActivity().getAssets()
					.list(folderPath);
			for (String name : files) {
				pathList.add(folderPath + File.separator + name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.notifyDataSetChanged();
	}
}// end class
