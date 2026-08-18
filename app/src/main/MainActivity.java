package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.*;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int FILE_PICKER = 42;
    private ValueCallback<Uri[]> fileCallback;
    private WebView w;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        w = new WebView(this);

        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);

        w.setWebViewClient(new WebViewClient());

        w.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView view,
                    ValueCallback<Uri[]> callback,
                    FileChooserParams params) {

                fileCallback = callback;

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("audio/*");

                try {
                    startActivityForResult(i, FILE_PICKER);
                } catch (Exception e) {
                    fileCallback = null;
                    Toast.makeText(
                            MainActivity.this,
                            "Couldn't open music files",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return true;
            }
        });

        w.addJavascriptInterface(new Bridge(), "Android");

        w.loadUrl("file:///android_asset/index.html");
        setContentView(w);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER && fileCallback != null) {

            Uri[] result = null;

            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                result = new Uri[]{data.getData()};
            }

            fileCallback.onReceiveValue(result);
            fileCallback = null;
        }
    }

    class Bridge {

        @JavascriptInterface
        public void pickMp3() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("audio/*");

            startActivityForResult(i, FILE_PICKER);
        }
    }
                        }
