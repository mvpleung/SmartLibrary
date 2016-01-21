package org.smart.library.model;

import java.io.Serializable;
import com.baoyz.pg.Parcelable;

/**
 * 版本升级Bean
 * 
 * @author LiangZiChao
 *         created on 2015年7月27日
 *         In the net.gemeite.smartcommunity.model
 */
@Parcelable
public class AppVersionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6152060076955379824L;

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
}