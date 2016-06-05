package com.way.chat.activity;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;

public class AsyncViewTask extends AsyncTask<View, Void, Drawable> {

    private View mView;
    private HashMap<String, SoftReference<Drawable>> imageCache;

    public AsyncViewTask() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
    }
    
    protected Drawable doInBackground(View... views) {
        Drawable drawable = null;
        View view = views[0];
        if (view.getTag() != null) {
            if (imageCache.containsKey(view.getTag())) {
                SoftReference<Drawable> cache = imageCache.get(view.getTag().toString());
                drawable = cache.get();
                if (drawable != null) {
                    return drawable;
                }
            }

            try {
                if (URLUtil.isHttpUrl(view.getTag().toString())) {// 如果为网络地址。则连接url下载图片
                    URL url = new URL(view.getTag().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    drawable = Drawable.createFromStream(stream, "src");
                    stream.close();
                } else {// 如果为本地数据，直接解析
                    drawable = Drawable.createFromPath(view.getTag().toString());
                }

            } catch (Exception e) {
            	e.printStackTrace();
                Log.v("imgxx", e.getMessage());
                return null;
            }
        }

        this.mView = view;

        return drawable;
    }

 

    protected void onPostExecute(Drawable drawable) {
        if (drawable != null) {
            this.mView.setBackgroundDrawable(drawable);

            this.mView = null;
        }
    }
}
