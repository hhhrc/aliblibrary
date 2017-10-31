package cn.hbjx.alib.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class MediaActivity extends CoreActivity{

    private static final int RESULT_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_TAKE_VIDEO = 2;
    private static final int RESULT_CAPTURE_RECORDER_SOUND = 3;
    private static final int RESULT_CAPTURE_PICTURES = 4;
    public static final int RESULT_SCANNER = 5;
    private String strImgPath = "";
    private String strVideoPath = "";
    private String strRecorderPath = "";
    private BottomSheetDialog bottomSheetPictureDialog;
    private BottomSheetBehavior bottomSheetPictureDialogBehavior;
    private MediaActivity.IBottomSheetPicture iBottomSheetPicture;

    public MediaActivity() {
    }

    public void displayBottomSheetPicture(MediaActivity.IBottomSheetPicture listener) {
        this.iBottomSheetPicture = listener;
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if(this.bottomSheetPictureDialog == null) {
            this.bottomSheetPictureDialog = new BottomSheetDialog(this);
            LinearLayout contentView = new LinearLayout(this);
            contentView.setOrientation(LinearLayout.VERTICAL);
            contentView.setBackgroundColor(Color.parseColor("#f6f6f6"));
            Button btn1 = new Button(this);
            btn1.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            btn1.setText("拍照");
            btn1.setTextColor(-16777216);
            btn1.setBackgroundColor(-1);
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MediaActivity.this.bottomSheetPictureDialog.hide();
                    MediaActivity.this.startCamera();
                }
            });
            Button btn2 = new Button(this);
            btn2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            btn2.setText("相册");
            btn2.setTextColor(-16777216);
            btn2.setBackgroundColor(-1);
            btn2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MediaActivity.this.bottomSheetPictureDialog.hide();
                    MediaActivity.this.startPictures();
                }
            });
            Button btn3 = new Button(this);
            btn3.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            btn3.setText("取消");
            btn3.setTextColor(-16777216);
            btn3.setBackgroundColor(-1);
            btn3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MediaActivity.this.bottomSheetPictureDialog.hide();
                }
            });
            View line1 = new View(this);
            LinearLayout.LayoutParams line1LP = new LinearLayout.LayoutParams(-1, 1);
            line1.setLayoutParams(line1LP);
            line1.setBackgroundColor(Color.parseColor("#dadada"));
            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(-1, 1));
            line2.setBackgroundColor(Color.parseColor("#dadada"));
            View line3 = new View(this);
            LinearLayout.LayoutParams line3Lp = new LinearLayout.LayoutParams(-1, 1);
            line3.setLayoutParams(line3Lp);
            line3.setBackgroundColor(Color.parseColor("#dadada"));
            View line4 = new View(this);
            LinearLayout.LayoutParams line4LP = new LinearLayout.LayoutParams(-1, 1);
            line4LP.setMargins(0, this.dp2px(16.0F), 0, 0);
            line4.setLayoutParams(line4LP);
            line4.setBackgroundColor(Color.parseColor("#dadada"));
            contentView.addView(line1);
            contentView.addView(btn1);
            contentView.addView(line2);
            contentView.addView(btn2);
            contentView.addView(line3);
            contentView.addView(line4);
            contentView.addView(btn3);
            this.bottomSheetPictureDialog.setContentView(contentView);
            View parent = (View)contentView.getParent();
            this.bottomSheetPictureDialogBehavior = BottomSheetBehavior.from(parent);
        } else if(this.bottomSheetPictureDialogBehavior.getState() == 5) {
            this.bottomSheetPictureDialogBehavior.setState(3);
        }

        this.bottomSheetPictureDialog.show();
    }

    public void finish() {
        if(this.bottomSheetPictureDialog != null) {
            this.bottomSheetPictureDialog.cancel();
            this.bottomSheetPictureDialog.hide();
            this.bottomSheetPictureDialog.dismiss();
        }

        super.finish();
    }

    private void startPictures() {
        Intent getAlbum = new Intent("android.intent.action.PICK");
        getAlbum.setType("image/*");
        this.startActivityForResult(getAlbum, 4);
    }

    private void startCamera() {
        this.checkSelfPermission("android.permission.CAMERA", new ICheckSelfPermission() {
            public void onRequestPermissionsResult(String permission) {
                Intent imageCaptureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                MediaActivity.this.strImgPath = Environment.getExternalStorageDirectory().toString() + "/CONSDCGMPIC/";
                String fileName = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".jpg";
                File out = new File(MediaActivity.this.strImgPath);
                if(!out.exists()) {
                    out.mkdirs();
                }

                out = new File(MediaActivity.this.strImgPath, fileName);
                MediaActivity.this.strImgPath = MediaActivity.this.strImgPath + fileName;
                Uri uri = Uri.fromFile(out);
                imageCaptureIntent.putExtra("output", uri);
                imageCaptureIntent.putExtra("android.intent.extra.videoQuality", 1);
                MediaActivity.this.startActivityForResult(imageCaptureIntent, 1);
            }
        });
    }

    private void startVideo() {
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        intent.putExtra("android.intent.extra.videoQuality", 0);
        this.startActivityForResult(intent, 2);
    }

    private void startSoundRecorder() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("audio/amr");
        this.startActivityForResult(intent, 3);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uriRecorder;
        Cursor cursor2;
        switch(requestCode) {
            case 1:
                if(resultCode == -1 && this.iBottomSheetPicture != null) {
                    this.myImageCompress(this.strImgPath);
                    this.iBottomSheetPicture.callback(this.strImgPath);
                }
                break;
            case 2:
                if(resultCode == -1) {
                    uriRecorder = data.getData();
                    cursor2 = this.getContentResolver().query(uriRecorder, (String[])null, (String)null, (String[])null, (String)null);
                    if(cursor2.moveToNext()) {
                        this.strVideoPath = cursor2.getString(cursor2.getColumnIndex("_data"));
                        if(this.iBottomSheetPicture != null) {
                            this.iBottomSheetPicture.callback(this.strVideoPath);
                        }
                    }
                }
                break;
            case 3:
                if(resultCode == -1) {
                    uriRecorder = data.getData();
                    cursor2 = this.getContentResolver().query(uriRecorder, (String[])null, (String)null, (String[])null, (String)null);
                    if(cursor2.moveToNext()) {
                        this.strRecorderPath = cursor2.getString(cursor2.getColumnIndex("_data"));
                        if(this.iBottomSheetPicture != null) {
                            this.iBottomSheetPicture.callback(this.strRecorderPath);
                        }
                    }
                }
                break;
            case 4:
                if(resultCode == -1 && this.iBottomSheetPicture != null) {
                    uriRecorder = data.getData();
                    uriRecorder = this.geturi(data);
                    String[] cursor = new String[]{"_data"};
                    Cursor cursor1 = this.getContentResolver().query(uriRecorder, cursor, (String)null, (String[])null, (String)null);
                    cursor1.moveToFirst();
                    int columnIndex = cursor1.getColumnIndex(cursor[0]);
                    String picturePath = cursor1.getString(columnIndex);
                    cursor1.close();
                    this.iBottomSheetPicture.callback(picturePath);
                }
                break;
            case 5:
                if(resultCode == -1 && this.iBottomSheetPicture != null) {
                    this.iBottomSheetPicture.callback(data.getStringExtra("result"));
                }
        }

    }

    public void myImageCompress(String strImgPath2) {
        Bitmap bmp = this.myReduceImage(strImgPath2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);

        while(baos.toByteArray().length / 1024 > 300 && options > 10) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        try {
            FileOutputStream e = new FileOutputStream(strImgPath2);
            e.write(baos.toByteArray());
            e.flush();
            e.close();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public Bitmap myReduceImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int be = (int)((float)options.outHeight / 400.0F);
        if(be <= 0) {
            be = 1;
        }

        options.inSampleSize = be;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if(Lg.DEBUG) {
            Lg.println(w + "   " + h);
        }

        return bitmap;
    }

    public Bitmap myReduceImage(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = this.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, (Rect)null, opt);
    }

    public Uri geturi(Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if(uri.getScheme().equals("file") && type.contains("image/")) {
            String path = uri.getEncodedPath();
            if(path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append("_data").append("=").append("\'" + path + "\'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, buff.toString(), (String[])null, (String)null);
                int index = 0;
                cur.moveToFirst();

                while(!cur.isAfterLast()) {
                    index = cur.getColumnIndex("_id");
                    index = cur.getInt(index);
                    cur.moveToNext();
                }

                if(index != 0) {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if(uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }

        return uri;
    }

    public interface IBottomSheetPicture {
        void callback(String var1);
    }

}
