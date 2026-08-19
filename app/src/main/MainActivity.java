package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.webkit.FileChooserParams;

import org.json.JSONObject;

public class MainActivity extends Activity {

    private WebView web;
    private ValueCallback<Uri[]> chooserCallback;

    private static final int FILE_CHOOSER = 100;
    private static final int MP3 = 101;
    private static final int PFP = 102;
    private static final int BANNER = 103;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(true);

        web.setWebViewClient(new WebViewClient());

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView view,
                    ValueCallback<Uri[]> callback,
                    FileChooserParams params) {

                chooserCallback = callback;

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                startActivityForResult(i, FILE_CHOOSER);
                return true;
            }
        });

        web.addJavascriptInterface(new AndroidBridge(), "Android");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                wireButtons();
            }
        });

        web.loadUrl("file:///android_asset/index.html");

        setContentView(web);
    }

    /* ---------------- ANDROID BRIDGE ---------------- */

    private class AndroidBridge {

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
            startActivityForResult(i, PFP);
        }

        @JavascriptInterface
        public void pickBanner() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, BANNER);
        }
    }

    /* ---------------- CONNECT HTML BUTTONS ---------------- */

    private void wireButtons() {

        String js =
            "(function(){"

          + "function findButtons(){"
          + "var b=document.querySelectorAll('button');"

          + "b.forEach(function(x){"
          + "var t=(x.innerText||'').toLowerCase();"

          + "if(t.includes('change pfp'))"
          + "x.onclick=function(){Android.pickPFP()};"

          + "if(t.includes('change banner'))"
          + "x.onclick=function(){Android.pickBanner()};"

          + "if(t.includes('upload song')||t.includes('upload mp3'))"
          + "x.onclick=function(){Android.pickMp3()};"

          + "});"
          + "}"

          /* PFP */
          + "window.setPfp=function(uri){"
          + "var p=document.querySelector('.pfp');"
          + "if(p){"
          + "p.style.backgroundImage='url('+uri+')';"
          + "p.style.backgroundSize='cover';"
          + "p.style.backgroundPosition='center';"
          + "}"
          + "};"

          /* Banner */
          + "window.setBanner=function(uri){"
          + "var b=document.querySelector('.banner');"
          + "if(b){"
          + "b.style.backgroundImage='url('+uri+')';"
          + "b.style.backgroundSize='cover';"
          + "b.style.backgroundPosition='center';"
          + "}"
          + "};"

          /* MP3 */
          + "window.addImportedMp3=function(uri){"
          + "window.lostGuitarSong=uri;"
          + "var title=document.querySelector('.song h2');"
          + "var sub=document.querySelector('.song p');"
          + "if(title)title.textContent='Song Ready';"
          + "if(sub)sub.textContent='MP3 loaded successfully';"
          + "};"

          + "findButtons();"

          + "})();";

        web.evaluateJavascript(js, null);
    }

    /* ---------------- PICKER RESULTS ---------------- */

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER) {

            if (chooserCallback != null) {

                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    chooserCallback.onReceiveValue(
                            new Uri[]{data.getData()}
                    );
                } else {
                    chooserCallback.onReceiveValue(null);
                }

                chooserCallback = null;
            }

            return;
        }

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        String uri = JSONObject.quote(
                data.getData().toString()
        );

        if (requestCode == PFP) {
            web.evaluateJavascript(
                    "window.setPfp(" + uri + ");",
                    null
            );
        }

        if (requestCode == BANNER) {
            web.evaluateJavascript(
                    "window.setBanner(" + uri + ");",
                    null
            );
        }

        if (requestCode == MP3) {
            web.evaluateJavascript(
                    "window.addImportedMp3(" + uri + ");",
                    null
            );
        }
    }
        }
