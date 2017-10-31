package cn.hbjx.alib.observer;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import cn.hbjx.alib.util.Lg;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class ObsBind {

    private ArrayList<Mapping> mappings = new ArrayList();

    public ObsBind() {
    }

    public void bind(IObsObject obj, final TextView textView) {
        if(obj.getObject() != null) {
            textView.setText(obj.getObject().toString());
        }

        IObsListener iObsListener = new IObsListener() {
            public void update(Object o) {
                textView.setText(o.toString());
            }
        };
        obj.addObserverListener(textView, iObsListener);
        this.mappings.add(new ObsBind.Mapping(obj, textView, iObsListener));
    }

    public void bind(IObsObject obj, final EditText editText) {
        if(obj.getObject() != null) {
            editText.setText(obj.getObject().toString());
        }

        IObsListener iObsListener = new IObsListener() {
            public void update(Object o) {
                editText.setText(o.toString());
            }
        };
        obj.addObserverListener(editText, iObsListener);
        this.mappings.add(new ObsBind.Mapping(obj, editText, iObsListener));
    }

    public void bind(IObsObject obj, final ImageView imageView) {
        if(obj.getObject() != null) {
            imageView.setImageBitmap((Bitmap)obj.getObject());
        }

        IObsListener iObsListener = new IObsListener() {
            public void update(Object o) {
                if(o instanceof Bitmap) {
                    imageView.setImageBitmap((Bitmap)o);
                } else if(o instanceof String && Lg.DEBUG) {
                    Lg.println("IObsListener 图片下载");
                }

            }
        };
        obj.addObserverListener(imageView, iObsListener);
        this.mappings.add(new ObsBind.Mapping(obj, imageView, iObsListener));
    }

    public void bind(IObsObject obj, final Button btn) {
        if(obj.getObject() != null) {
            btn.setText(obj.getObject().toString());
        }

        IObsListener iObsListener = new IObsListener() {
            public void update(Object o) {
                btn.setText(o.toString());
            }
        };
        obj.addObserverListener(btn, iObsListener);
        this.mappings.add(new ObsBind.Mapping(obj, btn, iObsListener));
    }

    public void unbind(IObsObject obj, View view) {
        obj.removeObserverListener(view, (IObsListener)null);
        Iterator iterator = this.mappings.iterator();

        while(iterator.hasNext()) {
            ObsBind.Mapping next = (ObsBind.Mapping)iterator.next();
            if(next.src.equals(view)) {
                iterator.remove();
            }
        }

    }

    public void clearbinds() {
        Iterator var1 = this.mappings.iterator();

        while(var1.hasNext()) {
            ObsBind.Mapping mapping = (ObsBind.Mapping)var1.next();
            mapping.obj.removeObserverListener(mapping.src, mapping.listener);
        }

        this.mappings.clear();
    }

    class Mapping {
        IObsObject obj;
        Object src;
        IObsListener listener;

        public Mapping(IObsObject obj, Object src, IObsListener listener) {
            this.obj = obj;
            this.src = src;
            this.listener = listener;
        }
    }

}
