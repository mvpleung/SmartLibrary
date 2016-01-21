package org.smart.library.adapter;

import android.content.Context;

import org.smart.library.R;
import org.smart.library.model.AlbumModel;

import java.util.ArrayList;

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
