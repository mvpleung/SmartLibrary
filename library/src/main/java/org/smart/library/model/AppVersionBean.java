package org.smart.library.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 版本升级Bean
 * 
 * @author LiangZiChao
 *         created on 2015年7月27日
 */
public class AppVersionBean implements Parcelable {

	/**
	 * 版本ID
	 */
	public Long versionId;

	/**
	 * 版本号
	 */
	public String versionCode;

	/**
	 * 版本发布日期
	 */
	public String versionDate;

	/**
	 * 强制升级标志：Y 强制升级 ;N 不需要强制升级
	 */
	public String forceUpdateFlag;

	/**
	 * 版本更新内容
	 */
	public String versionUpdateContent;

	/**
	 * 版本下载地址
	 */
	public String versionDownloadUrl;

	/**
	 * 版本描述
	 */
	public String versionDesc;

	/**
	 * 备注
	 */
	public String memo;

	/**
	 * 版本名
	 * 
	 */
	public String versionName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.versionId);
		dest.writeString(this.versionCode);
		dest.writeString(this.versionDate);
		dest.writeString(this.forceUpdateFlag);
		dest.writeString(this.versionUpdateContent);
		dest.writeString(this.versionDownloadUrl);
		dest.writeString(this.versionDesc);
		dest.writeString(this.memo);
		dest.writeString(this.versionName);
	}

	public AppVersionBean() {
	}

	protected AppVersionBean(Parcel in) {
		this.versionId = (Long) in.readValue(Long.class.getClassLoader());
		this.versionCode = in.readString();
		this.versionDate = in.readString();
		this.forceUpdateFlag = in.readString();
		this.versionUpdateContent = in.readString();
		this.versionDownloadUrl = in.readString();
		this.versionDesc = in.readString();
		this.memo = in.readString();
		this.versionName = in.readString();
	}

	public static final Parcelable.Creator<AppVersionBean> CREATOR = new Parcelable.Creator<AppVersionBean>() {
		public AppVersionBean createFromParcel(Parcel source) {
			return new AppVersionBean(source);
		}

		public AppVersionBean[] newArray(int size) {
			return new AppVersionBean[size];
		}
	};
}