package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.view.PhotoViewAttacher;

/**
 * 
 * @ClassName: ImageDetailFragment
 * @Description: 单张图片显示
 * @author huxin
 * @date 2015-12-21 下午7:38:38
 * 
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private PhotoViewAttacher mAttacher;
	private DisplayImageOptions mCardOptions;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url")
				: null;
		mCardOptions = DisplayImageOptionsUtils.getCardDisplayImageOptions();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment,
				container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + mImageUrl,
				mImageView, mCardOptions);
	}
}
