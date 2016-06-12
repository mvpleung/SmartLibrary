package org.smart.library.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.EnhancedQuickAdapter;

import org.smart.library.R;
import org.smart.library.model.AlbumModel;
import org.smart.library.tools.ImageLoader;

import java.util.ArrayList;

/**
 * 相册
 *
 * @author Liangzc
 */
public class AlbumAdapter extends EnhancedQuickAdapter<AlbumModel> {

    public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
        super(context, R.layout.adapter_album_item, models);
    }


    @Override
    protected void convert(BaseAdapterHelper helper, AlbumModel item, boolean itemChanged) {
        ImageLoader.load("file://" + item.getRecent(), (ImageView) helper.getView(R.id.iv_album_la));
        helper.setText(R.id.tv_name_la, item.getName());
        helper.setText(R.id.tv_count_la, item.getCount() + "张");
        helper.setVisible(R.id.iv_index_la, item.isCheck());
    }
}
