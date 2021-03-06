package com.biu.modulebase.binfenjiari.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biu.modulebase.binfenjiari.communication.ImageDisplayUtil;
import com.biu.modulebase.binfenjiari.widget.cameraroll.ImageLoader;


public class ViewHolder {
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}

	/**
	 * 拿到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

//	public ViewHolder setImageUrl(int viewId, String url,int tag) {
//		ImageView view = getView(viewId);
//		Communications.setNetImage(url, view, tag);
//		return this;
//	}
//
//	public ViewHolder setHeaderUrl(int viewId, String url) {
//		ImageView view = getView(viewId);
//		ImageUtils.displayImageUseHeaderSmallOptions(url, view);
//		return this;
//	}
//
//	/**
//	 * 为ImageView设置图片
//	 *
//	 * @param viewId
//	 * @param drawableId
//	 * @return
//	 */
//	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
//		ImageView view = getView(viewId);
//		view.setImageBitmap(bm);
//		return this;
//	}
//

	public ViewHolder setNetImage(String imgType,int viewId, String url,int tag) {
		ImageView view = getView(viewId);
		ImageDisplayUtil.displayImage(imgType,url,view,tag);
		return this;
	}
//	/**
//	 * 加载网络图片
//	 * @param viewId
//	 * @param url
//	 * @param tag
//	 * @return
//	 */
//	public ViewHolder setNetImage(int viewId, String url,int tag) {
//		ImageView view = getView(viewId);
//		ImageDisplayUtil.displayImage(url,view,tag);
//		return this;
//	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param bm
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}


	public ViewHolder setImageByUrl(int viewId, String url) {
		ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(url,
				(ImageView) getView(viewId));
		return this;
	}

	public int getPosition() {
		return mPosition;
	}

}
