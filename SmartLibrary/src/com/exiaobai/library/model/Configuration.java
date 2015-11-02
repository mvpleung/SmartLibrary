/*
 * @Project: xbtrip
 * @File: Configuration.java
 * @Date: 2014年8月20日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package com.exiaobai.library.model;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @description 配置相关Bean
 * @author LiangZiChao
 * @Date 2014-8-20下午5:48:33
 * @Package com.xiaobai.xbtrip.model
 */
@Table(name = "config")
public class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4445068467956841903L;

	@Id
	public int id;

	/**
	 * 条目
	 */
	@Column(column = "option")
	public String option;

	/**
	 * 条目数据
	 */
	@Column(column = "optionValue")
	public String optionValue;

}
