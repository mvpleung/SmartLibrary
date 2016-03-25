/*
 * @Project: xbtrip
 * @File: MyClickSpan.java
 * @Date: 2014年11月16日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package org.smart.library.listener;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * CLickSpan
 *
 * @author Liangzc
 *         created on 2016/3/21 16:13
 */
public class MyClickSpan extends ClickableSpan {

    public MyClickSpan() {
        super();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor); //设置链接的文本颜色
        ds.setUnderlineText(false); //去掉下划线
    }

    @Override
    public void onClick(View widget) {

    }
}
