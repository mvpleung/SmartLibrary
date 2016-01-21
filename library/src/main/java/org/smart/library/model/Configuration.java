/*
 * @Project: xbtrip
 * @File: Configuration.java
 *         created on: 2014年8月20日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package org.smart.library.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 配置相关Bean
 *
 * @author LiangZiChao
 *         created on 2014-8-20下午5:48:33
 */
@Table(name = "config")
public class Configuration implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4445068467956841903L;

    @Column(name = "id", isId = true)
    public int id;

    /**
     * 条目
     */
    @Column(name = "option")
    public String option;

    /**
     * 条目数据
     */
    @Column(name = "optionValue")
    public String optionValue;

}
