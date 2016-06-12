package org.smart.library.listener;

/**
 * 图片选择监听
 * 
 * @author LiangZiChao 
 * 		created on 2016年6月3日
 * @param <T>
 */
public interface PhotoCallback<T> {

	public void onPhotoCallBack(T extra);
}
