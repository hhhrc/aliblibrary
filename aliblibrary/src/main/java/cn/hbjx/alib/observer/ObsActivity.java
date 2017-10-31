package cn.hbjx.alib.observer;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ObsActivity extends Activity {

    private ObsBind _b = new ObsBind();

    public ObsActivity() {
    }

    public void bind(IObsObject obj, View view) {
        if(view instanceof TextView) {
            this._b.bind(obj, (TextView)view);
        } else if(view instanceof EditText) {
            this._b.bind(obj, (EditText)view);
        } else if(view instanceof Button) {
            this._b.bind(obj, (Button)view);
        } else if(view instanceof ImageView) {
            this._b.bind(obj, (ImageView)view);
        }

    }

    public void unlink(IObsObject obj, View view) {
        this._b.unbind(obj, view);
    }

    public void finish() {
        this._b.clearbinds();
        super.finish();
    }

}
