package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final int PICK_MP3 = 42;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());

        web.addJavascriptInterface(new Bridge(), "Android");

        web.loadDataWithBaseURL(
                "https://lostguitar.local/",
                HTML,
                "text/html",
                "UTF-8",
                null
        );

        setContentView(web);
    }

    class Bridge {
        @JavascriptInterface
        public void pickMp3() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("audio/*");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(i, PICK_MP3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MP3 &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri uri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {}

            String name = "Imported MP3";

            try {
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[]{OpenableColumns.DISPLAY_NAME},
                        null,
                        null,
                        null
                );

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index >= 0) {
                            name = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            } catch (Exception ignored) {}

            String safeUri = uri.toString()
                    .replace("\\", "\\\\")
                    .replace("'", "\\'");

            String safeName = name
                    .replace("\\", "\\\\")
                    .replace("'", "\\'");

            web.evaluateJavascript(
                    "addSong('" + safeUri + "','" + safeName + "')",
                    null
            );
        }
    }

    private static final String HTML =
            "<!DOCTYPE html>" +
            "<html><head>" +
            "<meta name='viewport' content='width=device-width,initial-scale=1'>" +

            "<style>" +
            "*{box-sizing:border-box}" +
            "body{margin:0;background:#080606;color:white;font-family:Arial,sans-serif;min-height:100vh;overflow-x:hidden}" +
            "body:before{content:'🎸';position:fixed;font-size:260px;opacity:.025;left:-50px;top:80px;transform:rotate(-25deg);pointer-events:none}" +
            "header{padding:28px 20px 15px}" +
            ".logo{font-size:30px;font-weight:900;color:#ff0b18;letter-spacing:2px}" +
            ".greeting{font-size:18px;color:#ddd;margin-top:7px}" +
            "main{padding:0 18px 110px}" +
            ".banner{min-height:145px;border-radius:22px;padding:22px;margin:10px 0 20px;background:linear-gradient(135deg,#50070d,#130507);border:1px solid #7a1820;position:relative;overflow:hidden;box-shadow:0 8px 30px #0008}" +
            ".banner:after{content:'🎸';position:absolute;right:10px;bottom:-28px;font-size:135px;opacity:.08;transform:rotate(-20deg)}" +
            ".banner h2{margin:0 0 8px;font-size:25px}" +
            ".banner p{color:#bbb;margin:0;max-width:240px;line-height:1.4}" +
            ".section{font-size:24px;font-weight:800;margin:25px 0 12px}" +
            ".search{width:100%;padding:17px;border-radius:16px;border:1px solid #572024;background:#180b0c;color:white;font-size:16px;outline:none;transition:.2s}" +
            ".search:focus{border-color:#ff101b;box-shadow:0 0 15px #ff101b55}" +
            "button{border:0;border-radius:13px;padding:13px 18px;background:#ed0712;color:white;font-size:16px;font-weight:bold}" +
            "button:active{transform:scale(.97)}" +
            ".upload{margin:8px 0 15px;width:100%;font-size:17px}" +
            ".song{background:linear-gradient(145deg,#181313,#100e0e);border:1px solid #302525;border-radius:18px;padding:14px;margin:10px 0;display:flex;align-items:center;gap:12px;box-shadow:0 5px 15px #0005}" +
            ".cover{width:54px;height:54px;flex-shrink:0;border-radius:14px;background:#e50914;display:flex;align-items:center;justify-content:center;font-size:26px;box-shadow:0 4px 12px #e5091444}" +
            ".info{flex:1;min-width:0}" +
            ".title{font-weight:bold;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}" +
            ".small{color:#999;font-size:13px;margin-top:5px}" +
            ".play{padding:10px 14px}" +
            ".empty{text-align:center;color:#777;padding:30px 10px}" +
            ".profile{background:#141111;border:1px solid #302525;border-radius:20px;padding:18px;box-shadow:0 8px 25px #0005}" +
            ".avatar{width:70px;height:70px;border-radius:50%;background:#e50914;display:flex;align-items:center;justify-content:center;font-size:30px;margin-bottom:12px}" +
            ".profile input{width:100%;padding:14px;background:#0c0a0a;border:1px solid #333;border-radius:12px;color:white;font-size:16px;margin-bottom:10px;outline:none}" +
            ".player{position:fixed;left:12px;right:12px;bottom:78px;background:linear-gradient(145deg,#36090e,#1b0507);border:1px solid #68131a;border-radius:20px;padding:15px;display:none;box-shadow:0 10px 35px #000;z-index:20}" +
            ".playerTitle{font-weight:bold;margin-bottom:10px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}" +
            ".playerControls{display:flex;gap:8px;align-items:center}" +
            ".pause{background:#292525;padding:10px 14px}" +
            "audio{width:100%;height:38px;flex:1}" +
            "nav{position:fixed;bottom:0;left:0;right:0;height:65px;background:#111;border-top:1px solid #302525;display:flex;justify-content:space-around;align-items:center;z-index:30}" +
            "nav button{background:none;color:#888;font-size:14px;padding:10px}" +
            "nav button.active{color:white}" +
            ".page{display:none}.page.active{display:block}" +
            "</style></head><body>" +

            "<header>" +
            "<div class='logo'>LOST GUITAR</div>" +
            "<div class='greeting' id='greeting'></div>" +
            "</header>" +

            "<main>" +

            "<section id='home' class='page active'>" +
            "<div class='banner'>" +
            "<h2>Welcome back 🎸</h2>" +
            "<p>Your music. Your sound. Your Lost Guitar.</p>" +
            "</div>" +

            "<div class='section'>Your Music</div>" +
            "<button class='upload' onclick='Android.pickMp3()'>＋ Upload MP3</button>" +
            "<div id='songs'></div>" +

            "<div class='section'>Recently Played</div>" +
            "<div id='recent'></div>" +
            "</section>" +

            "<section id='searchPage' class='page'>" +
            "<div class='section'>Search Your Music</div>" +
            "<input class='search' id='search' placeholder='🔎 Search songs...' oninput='searchSongs()'>" +
            "<div id='results' class='empty'>Type a song name to search.</div>" +
            "</section>" +

            "<section id='profilePage' class='page'>" +
            "<div class='section'>Profile</div>" +
            "<div class='profile'>" +
            "<div class='avatar'>🎸</div>" +
            "<input id='username' placeholder='Your username'>" +
            "<input id='bio' placeholder='Your bio'>" +
            "<button onclick='saveProfile()'>Save Profile</button>" +
            "</div>" +
            "</section>" +

            "</main>" +

            "<div class='player' id='player'>" +
            "<div class='playerTitle' id='playerTitle'></div>" +
            "<div class='playerControls'>" +
            "<audio id='audio' controls></audio>" +
            "<button class='pause' onclick='togglePause()' id='pauseBtn'>Ⅱ</button>" +
            "</div>" +
            "</div>" +

            "<nav>" +
            "<button id='homeBtn' class='active' onclick='showPage(\"home\")'>🏠 Home</button>" +
            "<button id='searchBtn' onclick='showPage(\"searchPage\")'>🔎 Search</button>" +
            "<button id='profileBtn' onclick='showPage(\"profilePage\")'>👤 Profile</button>" +
            "</nav>" +

            "<script>" +

            "var songs=JSON.parse(localStorage.getItem('songs')||'[]');" +
            "var recent=JSON.parse(localStorage.getItem('recent')||'[]');" +

            "function greeting(){" +
            "var h=new Date().getHours();" +
            "var word=h<12?'Good morning':h<18?'Good afternoon':'Good night';" +
            "var name=localStorage.getItem('username')||'there';" +
            "document.getElementById('greeting').textContent=word+' '+name+' 👋';" +
            "}" +

            "function saveProfile(){" +
            "var n=document.getElementById('username').value.trim();" +
            "var b=document.getElementById('bio').value.trim();" +
            "if(n)localStorage.setItem('username',n);" +
            "localStorage.setItem('bio',b);" +
            "greeting();" +
            "alert('Profile saved! 🎸');" +
            "}" +

            "function addSong(uri,name){" +
            "songs.push({name:name||'Imported MP3',url:uri});" +
            "localStorage.setItem('songs',JSON.stringify(songs));" +
            "render();" +
            "searchSongs();" +
            "}" +

            "function playSong(i){" +
            "var s=songs[i];" +
            "if(!s)return;" +
            "var a=document.getElementById('audio');" +
            "a.src=s.url;" +
            "document.getElementById('playerTitle').textContent=s.name;" +
            "document.getElementById('player').style.display='block';" +
            "a.play().catch(function(){});" +
            "document.getElementById('pauseBtn').textContent='Ⅱ';" +
            "recent=recent.filter(function(x){return x.url!==s.url;});" +
            "recent.unshift(s);" +
            "recent=recent.slice(0,8);" +
            "localStorage.setItem('recent',JSON.stringify(recent));" +
            "renderRecent();" +
            "}" +

            "function togglePause(){" +
            "var a=document.getElementById('audio');" +
            "var b=document.getElementById('pauseBtn');" +
            "if(a.paused){a.play();b.textContent='Ⅱ';}else{a.pause();b.textContent='▶';}" +
            "}" +

            "function searchSongs(){" +
            "var input=document.getElementById('search');" +
            "var box=document.getElementById('results');" +
            "if(!input||!box)return;" +
            "var q=input.value.toLowerCase().trim();" +
            "if(!q){" +
            "box.className='empty';" +
            "box.textContent=songs.length?'Start typing to search your music.':'Upload an MP3 first 🎸';" +
            "return;" +
            "}" +
            "var found=songs.filter(function(s){return (s.name||'').toLowerCase().includes(q);});" +
            "if(!found.length){" +
            "box.className='empty';" +
            "box.textContent='No songs found 😭';" +
            "return;" +
            "}" +
            "box.className='';" +
            "box.innerHTML='';" +
            "found.forEach(function(s){" +
            "var i=songs.indexOf(s);" +
            "box.innerHTML+=" +
            "'<div class=\"song\">'+" +
            "'<div class=\"cover\">♪</div>'+" +
            "'<div class=\"info\"><div class=\"title\">'+escapeHtml(s.name)+'</div><div class=\"small\">MP3</div></div>'+" +
            "'<button class=\"play\" onclick=\"playSong('+i+')\">Play</button>'+" +
            "'</div>';" +
            "});" +
            "}" +

            "function escapeHtml(x){" +
            "return String(x).replace(/[&<>\\\"']/g,function(c){" +
            "return {'&':'&amp;','<':'&lt;','>':'&gt;','\\\"':'&quot;',\"'\":'&#39;'}[c];" +
            "});" +
            "}" +

            "function render(){" +
            "var box=document.getElementById('songs');" +
            "if(!songs.length){" +
            "box.innerHTML='<div class=\"empty\">No MP3s yet. Upload one! 🎸</div>';" +
            "return;" +
            "}" +
            "box.innerHTML='';" +
            "songs.forEach(function(s,i){" +
            "box.innerHTML+=" +
            "'<div class=\"song\">'+" +
            "'<div class=\"cover\">♪</div>'+" +
            "'<div class=\"info\"><div class=\"title\">'+escapeHtml(s.name)+'</div><div class=\"small\">MP3</div></div>'+" +
            "'<button class=\"play\" onclick=\"playSong('+i+')\">Play</button>'+" +
            "'</div>';" +
            "});" +
            "}" +

            "function renderRecent(){" +
            "var box=document.getElementById('recent');" +
            "if(!recent.length){" +
            "box.innerHTML='<div class=\"empty\">Nothing played yet.</div>';" +
            "return;" +
            "}" +
            "box.innerHTML='';" +
            "recent.forEach(function(s){" +
            "var i=songs.findIndex(function(x){return x.url===s.url;});" +
            "if(i>=0){" +
            "box.innerHTML+=" +
            "'<div class=\"song\">'+" +
            "'<div class=\"cover\">↻</div>'+" +
            "'<div class=\"info\"><div class=\"title\">'+escapeHtml(s.name)+'</div><div class=\"small\">Recently played</div></div>'+" +
            "'<button class=\"play\" onclick=\"playSong('+i+')\">Play</button>'+" +
            "'</div>';" +
            "}" +
            "});" +
            "}" +

            "function showPage(p){" +
            "document.querySelectorAll('.page').forEach(function(x){x.classList.remove('active');});" +
            "document.getElementById(p).classList.add('active');" +
            "document.querySelectorAll('nav button').forEach(function(x){x.classList.remove('active');});" +
            "if(p==='home')document.getElementById('homeBtn').classList.add('active');" +
            "if(p==='searchPage'){" +
            "document.getElementById('searchBtn').classList.add('active');" +
            "document.getElementById('search').focus();" +
            "searchSongs();" +
            "}" +
            "if(p==='profilePage'){" +
            "document.getElementById('profileBtn').classList.add('active');" +
            "document.getElementById('username').value=localStorage.getItem('username')||'';" +
            "document.getElementById('bio').value=localStorage.getItem('bio')||'';" +
            "}" +
            "}" +

            "greeting();" +
            "render();" +
            "renderRecent();" +

            "</script></body></html>";
                }
