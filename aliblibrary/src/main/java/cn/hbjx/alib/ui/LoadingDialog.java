package cn.hbjx.alib.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;

import cn.hbjx.alib.R;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.view_dialog_loading);
        setCanceledOnTouchOutside(false);
    }
}
