package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {

    WebView web;

    static final int PICK_MP3 = 1;
    static final int PICK_PFP = 2;
    static final int PICK_BANNER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setAllowContentAccess(true);

        web.setWebViewClient(new WebViewClient());

        web.addJavascriptInterface(new Android(), "Android");

        web.loadUrl("file:///android_asset/index.html");

        setContentView(web);
    }

    public class Android {

        @JavascriptInterface
        public void pickPFP() {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            i.setType("image/*");
            startActivityForResult(i, PICK_PFP);
        }

        @JavascriptInterface
        public void pickBanner() {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            i.setType("image/*");
            startActivityForResult(i, PICK_BANNER);
        }

        @JavascriptInterface
        public void pickMp3() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("audio/*");

            startActivityForResult(i, PICK_MP3);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK ||
                data == null ||
                data.getData() == null) {
            return;
        }

        Uri uri = data.getData();

        if (requestCode == PICK_PFP) {
            String image = imageToBase64(uri);

            if (image != null) {
                web.evaluateJavascript(
                        "if(window.setPfp)window.setPfp(" +
                        quote(image) + ");",
                        null
                );
            }
        }

        else if (requestCode == PICK_BANNER) {
            String image = imageToBase64(uri);

            if (image != null) {
                web.evaluateJavascript(
                        "if(window.setBanner)window.setBanner(" +
                        quote(image) + ");",
                        null
                );
            }
        }

        else if (requestCode == PICK_MP3) {
            String song = uri.toString();

            web.evaluateJavascript(
                    "if(window.addImportedMp3)" +
                    "window.addImportedMp3(" +
                    quote(song) + ");",
                    null
            );
        }
    }

    private String imageToBase64(Uri uri) {

        try {
            InputStream input =
                    getContentResolver().openInputStream(uri);

            Bitmap bitmap =
                    BitmapFactory.decodeStream(input);

            input.close();

            if (bitmap == null) return null;

            int max = 1000;

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            if (w > max || h > max) {

                float scale =
                        Math.min(
                                (float) max / w,
                                (float) max / h
                        );

                w = Math.round(w * scale);
                h = Math.round(h * scale);

                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        w,
                        h,
                        true
                );
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    80,
                    out
            );

            bitmap.recycle();

            return "data:image/jpeg;base64," +
                    Base64.encodeToString(
                            out.toByteArray(),
                            Base64.NO_WRAP
                    );

        } catch (Exception e) {
            return null;
        }
    }

    private String quote(String text) {

        return "\"" +
                text
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r") +
                "\"";
    }
        }
