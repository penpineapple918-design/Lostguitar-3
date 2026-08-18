package com.lostguitar.app;
import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;

public class MainActivity extends Activity {
    static final int PICK=42;
    WebView w;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        w=new WebView(this);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setDomStorageEnabled(true);
        w.setWebViewClient(new WebViewClient());
        w.addJavascriptInterface(new Bridge(),"Android");
        w.loadUrl("file:///android_asset/index.html");
        setContentView(w);
    }
    class Bridge {
        @JavascriptInterface public void pickMp3() {
            Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("audio/mpeg");
            startActivityForResult(i,PICK);
        }
    }
    @Override protected void onActivityResult(int r,int c,Intent d) {
        super.onActivityResult(r,c,d);
        if(r==PICK&&c==RESULT_OK&&d!=null&&d.getData()!=null) {
            Uri u=d.getData();
            try { getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored) {}
            String s=u.toString().replace("\\","\\\\").replace("'","\\'");
            w.evaluateJavascript("window.addImportedMp3('"+s+"')",null);
        }
    }
}
