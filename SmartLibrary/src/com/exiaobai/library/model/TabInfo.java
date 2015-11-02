package com.exiaobai.library.model;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.exiaobai.library.ui.BaseFragment;
import com.lidroid.xutils.util.LogUtils;

/**
 * 单个选项卡类，每个选项卡包含名字，图标以及提示（可选，默认不显示）
 * 
 * @author
 * @param <T>
 * @Data LiangZiChao Update By 2014-7-27下午11:21:11
 */
public class TabInfo implements Parcelable {

	/**
	 * 下标
	 */
	public int id;

	/**
	 * 选择器
	 */
	public int selectorIcon;

	public Drawable selectorDrawable;

	/**
	 * 获取焦点时的图片资源ID
	 */
	public int focusIcon;

	/**
	 * 失去焦点时的图片资源ID
	 */
	public int unFocusIcon;

	/**
	 * 反转图片（获取焦点时）
	 */
	public int matrixFocusIcon;

	/**
	 * 反转图片（失去焦点时）
	 */
	public int matrixUnFocusIcon;

	/**
	 * 获取焦点时的图片
	 */
	public Drawable focus;

	/**
	 * 失去焦点时的图片
	 */
	public Drawable unFocus;

	/**
	 * 反转图片（获取焦点时）
	 */
	public Drawable matrixFocus;

	/**
	 * 反转图片（失去焦点）
	 */
	public Drawable matrixUnFocus;

	/** 页卡名称 */
	public String name = null;

	/** 是否显示角标 */
	public boolean hasTips = false;

	/** 页卡对应的Fragment */
	public BaseFragment fragment = null;

	/** 通知改变 */
	public boolean notifyChange = false;

	/** 页卡对应的Fragment字节码 */
	public Class<?> fragmentClass = null;

	private Bundle extras;

	public TabInfo() {
	}

	/**
	 * 
	 * @param id
	 *            下标
	 * @param name
	 *            名称
	 */
	public TabInfo(int id, String name) {
		this(id, name, 0, null);
	}

	/**
	 * 
	 * @param id
	 *            下标
	 * @param name
	 *            名称
	 * @param selectorIcon
	 *            选择器
	 */
	public TabInfo(int id, String name, int selectorIcon) {
		this(id, name, selectorIcon, null);
	}

	/**
	 * 
	 * @param id
	 *            下标
	 * @param name
	 *            名称
	 * @param clazz
	 *            对应Fragment字节码
	 */
	public TabInfo(int id, String name, Class<?> clazz) {
		this(id, name, 0, clazz);
	}

	/**
	 * 
	 * @param id
	 *            下标
	 * @param name
	 *            名称
	 * @param hasTips
	 *            是否显示角标
	 * @param clazz
	 *            对应Fragment字节码
	 */
	public TabInfo(int id, String name, boolean hasTips, Class<?> clazz) {
		this(id, name, 0, clazz);
		this.hasTips = hasTips;
	}

	/**
	 * 
	 * @param id
	 *            下标
	 * @param name
	 *            名称
	 * @param selectorIcon
	 *            选择器
	 * @param clazz
	 *            对应Fragment字节码
	 */
	public TabInfo(int id, String name, int selectorIcon, Class<?> clazz) {
		super();
		this.name = name;
		this.id = id;
		this.selectorIcon = selectorIcon;
		this.fragmentClass = clazz;
	}

	/**
	 * 创建对应的Fragment
	 * 
	 * @return
	 */
	public BaseFragment createFragment() {
		return createFragment(extras);
	}

	/**
	 * 创建对应的Fragment
	 * 
	 * @return
	 */
	public BaseFragment createFragment(Bundle extras) {
		this.extras = extras;
		if (fragment == null) {
			try {
				fragment = (BaseFragment) fragmentClass.getConstructor(new Class[0]).newInstance(new Object[0]);
			} catch (Exception e) {
				LogUtils.e(e.getMessage(), e);
			}
		}
		if (fragment != null)
			fragment.setArguments(extras);
		return fragment;
	}

	// 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
	// android.os.BadParcelableException:
	// Parcelable protocol requires a Parcelable.Creator object called CREATOR
	// on class com.um.demo.Person
	// 2.这个接口实现了从Percel容器读取数据，并返回对象给逻辑层使用
	// 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
	// 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
	// 5.反序列化对象
	public static final Parcelable.Creator<TabInfo> CREATOR = new Creator<TabInfo>() {

		@Override
		public TabInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			// 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
			TabInfo tabInfo = new TabInfo();
			tabInfo.id = source.readInt();
			tabInfo.selectorIcon = source.readInt();
			tabInfo.selectorDrawable = (Drawable) source.readValue(Drawable.class.getClassLoader());
			tabInfo.focusIcon = source.readInt();
			tabInfo.unFocusIcon = source.readInt();
			tabInfo.matrixFocusIcon = source.readInt();
			tabInfo.matrixUnFocusIcon = source.readInt();
			tabInfo.focus = (Drawable) source.readValue(Drawable.class.getClassLoader());
			tabInfo.unFocus = (Drawable) source.readValue(Drawable.class.getClassLoader());
			tabInfo.matrixFocus = (Drawable) source.readValue(Drawable.class.getClassLoader());
			tabInfo.matrixUnFocus = (Drawable) source.readValue(Drawable.class.getClassLoader());
			tabInfo.name = source.readString();
			tabInfo.hasTips = (Boolean) source.readValue(Boolean.class.getClassLoader());
			tabInfo.fragment = (BaseFragment) source.readValue(BaseFragment.class.getClassLoader());
			tabInfo.notifyChange = (Boolean) source.readValue(Boolean.class.getClassLoader());
			tabInfo.fragmentClass = (Class<?>) source.readValue(Class.class.getClassLoader());
			tabInfo.extras = source.readBundle();
			return tabInfo;
		}

		@Override
		public TabInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TabInfo[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeInt(selectorIcon);
		dest.writeValue(selectorDrawable);
		dest.writeInt(focusIcon);
		dest.writeInt(unFocusIcon);
		dest.writeInt(matrixFocusIcon);
		dest.writeInt(matrixUnFocusIcon);
		dest.writeValue(focus);
		dest.writeValue(unFocus);
		dest.writeValue(matrixFocus);
		dest.writeValue(matrixUnFocus);
		dest.writeString(name);
		dest.writeValue(hasTips);
		dest.writeValue(fragment);
		dest.writeValue(notifyChange);
		dest.writeValue(fragmentClass);
		dest.writeBundle(extras);
	}
}