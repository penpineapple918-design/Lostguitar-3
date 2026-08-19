package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.*;

public class MainActivity extends Activity {

    WebView web;
    ValueCallback<Uri[]> callback;

    static final int MP3 = 42;
    static final int FILE = 43;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        web = new WebView(this);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);

        web.setWebViewClient(new WebViewClient());

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView v,
                    ValueCallback<Uri[]> c,
                    FileChooserParams p) {

                callback = c;

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                startActivityForResult(i, FILE);
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "Android");
        web.loadUrl("file:///android_asset/index.html");

        setContentView(web);
    }

    class Bridge {

        @JavascriptInterface
        public void pickMp3() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("audio/*");
            startActivityForResult(i, MP3);
        }

        @JavascriptInterface
        public void pickPFP() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, FILE);
        }

        @JavascriptInterface
        public void pickBanner() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, FILE);
        }
    }

    @Override
    protected void onActivityResult(int r, int c, Intent d) {
        super.onActivityResult(r, c, d);

        if (r == FILE && callback != null) {
            callback.onReceiveValue(
                    c == RESULT_OK && d != null
                            ? new Uri[]{d.getData()}
                            : null
            );
            callback = null;
            return;
        }

        if (r == MP3 && c == RESULT_OK && d != null) {
            String u = d.getData().toString()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");

            web.evaluateJavascript(
                    "window.addImportedMp3(\"" + u + "\")",
                    null
            );
        }
    }
        }
