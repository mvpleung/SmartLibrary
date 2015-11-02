package com.exiaobai.library.adapter;

import java.util.ArrayList;

import android.content.Context;

import com.exiaobai.library.R;
import com.exiaobai.library.model.AlbumModel;

/**
 * 相册
 * 
 * @author Liangzc
 *
 */
public class AlbumAdapter extends CommonAdapter<AlbumModel> {

	public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
		super(context, models, R.layout.adapter_album_item);
	}

	@Override
	public void convert(ViewHolder helper, AlbumModel item) {
		// TODO Auto-generated method stub
		helper.setImageByUrl(R.id.iv_album_la, "file://" + item.getRecent());
		helper.setText(R.id.tv_name_la, item.getName());
		helper.setHint(R.id.tv_count_la, item.getCount() + "张");
		helper.setVisibility(R.id.iv_index_la, item.isCheck());
	}

}
