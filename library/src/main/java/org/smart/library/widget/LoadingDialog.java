package org.smart.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.smart.library.R;

/**
 * loading框
 *
 * @author Liangzc
 *         created on 2014-3-20
 */
public class LoadingDialog extends Dialog {

    private String message;
    private TextView tv;
//	private LinearLayout ll_loading;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogStyle);
    }

    public LoadingDialog(Context context, String text) {
        super(context, R.style.DialogStyle);
        this.message = text;
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView) this.findViewById(R.id.tv_msg);
//		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        if (!TextUtils.isEmpty(message)) {
            tv.setText(message);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }
}
