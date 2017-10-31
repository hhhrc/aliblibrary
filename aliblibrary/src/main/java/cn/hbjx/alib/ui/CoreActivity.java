package cn.hbjx.alib.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.hbjx.alib.network.ARequesterActivity;
import cn.hbjx.alib.util.AUtil;
import cn.hbjx.alib.util.CacheUtil;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class CoreActivity extends ARequesterActivity {

    protected Toast _coreToast;
    private BottomSheetDialog _defBottomSheetDialog;
    private BottomSheetBehavior _defBottomSheetDialogBehavior;
    private boolean __isKeyboardWithInputInit = false;
    private EditText __et;
    public CoreActivity.IKeyboardWithInput keyboardListener;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private CoreActivity.ICheckSelfPermission checkSelfPermission;

    public CoreActivity() {
    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public int dp2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    public int px2dp(float pxValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            View focusView = this.getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }

    }

    public void toast(int resId) {
        if (this._coreToast == null) {
            this._coreToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        } else {
            this._coreToast.setText(resId);
            Toast var10001 = this._coreToast;
            this._coreToast.setDuration(Toast.LENGTH_SHORT);
        }

        this._coreToast.show();
    }

    public void toast(String text) {
        if (this._coreToast == null) {
            this._coreToast = Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT);
        } else {
            this._coreToast.setText(text);
            this._coreToast.setDuration(Toast.LENGTH_SHORT);
        }

        this._coreToast.show();
    }

    public void snackbar(View view, String text) {
        final Snackbar snackbar = Snackbar.make(view, text, -1);
        snackbar.setAction("ok", new View.OnClickListener() {
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void displayBottomSheetDialog(View v) {
        View parent;
        if (this._defBottomSheetDialog == null) {
            this._defBottomSheetDialog = new BottomSheetDialog(this);
            this._defBottomSheetDialog.setContentView(v);
            parent = (View) v.getParent();
            this._defBottomSheetDialogBehavior = BottomSheetBehavior.from(parent);
        } else {
            this._defBottomSheetDialog.setContentView(v);
            parent = (View) v.getParent();
            this._defBottomSheetDialogBehavior = BottomSheetBehavior.from(parent);
            if (this._defBottomSheetDialogBehavior.getState() == 5) {
                this._defBottomSheetDialogBehavior.setState(3);
            }
        }

        this._defBottomSheetDialog.show();
    }

    public void hideBottomSheetDialog() {
        this._defBottomSheetDialog.hide();
    }

    public void __initKeyboardWithInput() {
        this.__isKeyboardWithInputInit = true;
        LinearLayout popRoot = new LinearLayout(this);
        popRoot.setBackgroundColor(-1);
        popRoot.setLayoutParams(new LinearLayout.LayoutParams(-1, AUtil.dp2px(this, 44.0F)));
        popRoot.setOrientation(LinearLayout.HORIZONTAL);
        this.__et = new EditText(this);
        this.__et.setLayoutParams(new LinearLayout.LayoutParams(0, AUtil.dp2px(this, 44.0F), 5.0F));
        this.__et.setPadding(AUtil.dp2px(this, 8.0F), AUtil.dp2px(this, 2.0F), AUtil.dp2px(this, 8.0F), AUtil.dp2px(this, 2.0F));
        this.__et.setBackgroundColor(-1);
        this.__et.setHint("请输入");
        LinearLayout.LayoutParams etBtnLp = new LinearLayout.LayoutParams(0, AUtil.dp2px(this, 44.0F), 1.0F);
        Button etBtn = new Button(this);
        etBtn.setText("发送");
        etBtn.setLayoutParams(etBtnLp);
        popRoot.addView(this.__et);
        popRoot.addView(etBtn);
        final PopupWindow pop = new PopupWindow(popRoot, AUtil.getDisplayMetrics(this).widthPixels, AUtil.dp2px(this, 44.0F), true);
        this.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                CoreActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = CoreActivity.this.getWindow().getDecorView().getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                boolean visible = heightDifference > screenHeight / 3;
                if (visible) {
                    pop.showAtLocation(CoreActivity.this.getWindow().getDecorView(), 0, 0, r.bottom - AUtil.dp2px(CoreActivity.this, 44.0F));
                } else if (pop.isShowing()) {
                    pop.dismiss();
                }

            }
        });
        etBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CoreActivity.this.keyboardListener != null) {
                    CoreActivity.this.keyboardListener.getInput(CoreActivity.this.__et.getText().toString());
                    pop.dismiss();
                    CoreActivity.this.closeSoftInput();
                    CoreActivity.this.__et.setText("");
                }

            }
        });
    }

    public void displayKeyboardWithInput(String hint, CoreActivity.IKeyboardWithInput listener) {
        if (!this.__isKeyboardWithInputInit) {
            this.__initKeyboardWithInput();
        }

        this.keyboardListener = listener;
        this.__et.setHint(hint);
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, 2);
    }

    public boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else {
            if (obj instanceof String) {
                if (((String) obj).trim().equals("")) {
                    return true;
                }
            } else if (obj instanceof TextView) {
                if (((TextView) obj).getText().toString().trim().equals("")) {
                    return true;
                }
            } else if (obj instanceof EditText && ((EditText) obj).getText().toString().trim().equals("")) {
                return true;
            }

            return false;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int radius = w > h ? h : w;
        Bitmap sbmp;
        if (bmp.getWidth() == radius && bmp.getHeight() == radius) {
            sbmp = bmp;
        } else {
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        }

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = -6187148;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle((float) (sbmp.getWidth() / 2) + 0.7F, (float) (sbmp.getHeight() / 2) + 0.7F, (float) (sbmp.getWidth() / 2) + 0.1F, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        Paint paint1 = new Paint();
        paint1.setColor(-1);
        paint1.setStrokeWidth(2.0F);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        canvas.drawCircle((float) (sbmp.getWidth() / 2), (float) (sbmp.getHeight() / 2), (float) (sbmp.getWidth() / 2 - 1), paint1);
        return output;
    }

    public void displayProgressBar() {
    }

    public void dismissProgressBar() {
    }

    public boolean isFirstAppRunning() {
        boolean b = true;
        if (CacheUtil.getInteger("app_install") < 0) {
            b = true;
        } else {
            b = false;
        }

        CacheUtil.saveInteger("app_install", 100);
        return b;
    }

    public PackageInfo getPackageInfo() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packInfo = null;

        try {
            packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
        }

        return packInfo;
    }

    public void checkSelfPermission(String permission, CoreActivity.ICheckSelfPermission resultHandler) {
        this.checkSelfPermission = resultHandler;
        if (ContextCompat.checkSelfPermission(this, permission) != 0) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        } else if (this.checkSelfPermission != null) {
            this.checkSelfPermission.onRequestPermissionsResult(permission);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == 0) {
            if (this.checkSelfPermission != null) {
                this.checkSelfPermission.onRequestPermissionsResult(permissions[0]);
            }
        } else {
            this.toast("Permissions Denied");
        }

    }

    public interface ICheckSelfPermission {
        void onRequestPermissionsResult(String var1);
    }

    public interface IKeyboardWithInput {
        void getInput(String var1);
    }

}
