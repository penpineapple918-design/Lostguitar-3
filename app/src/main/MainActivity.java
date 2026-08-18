package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private static final int PICK_MP3 = 42;
    private static final int PICK_PFP = 43;
    private static final int PICK_BANNER = 44;

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        web.setWebViewClient(new WebViewClient());

        web.addJavascriptInterface(new AndroidBridge(), "Android");

        web.loadDataWithBaseURL(
                "https://lostguitar.local/",
                HTML,
                "text/html",
                "UTF-8",
                null
        );

        setContentView(web);
    }

    public class AndroidBridge {

        @JavascriptInterface
        public void pickMP3() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("audio/mpeg");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(i, PICK_MP3);
        }

        @JavascriptInterface
        public void pickPFP() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(i, PICK_PFP);
        }

        @JavascriptInterface
        public void pickBanner() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(i, PICK_BANNER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        Uri uri = data.getData();

        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (Exception ignored) {
        }

        String uriString = uri.toString()
                .replace("\\", "\\\\")
                .replace("'", "\\'");

        if (requestCode == PICK_MP3) {

            web.evaluateJavascript(
                    "addSong('" + uriString + "');",
                    null
            );

        } else if (requestCode == PICK_PFP) {

            web.evaluateJavascript(
                    "changePFP('" + uriString + "');",
                    null
            );

        } else if (requestCode == PICK_BANNER) {

            web.evaluateJavascript(
                    "changeBanner('" + uriString + "');",
                    null
            );
        }
    }

    private static final String HTML =
            "<!DOCTYPE html>" +

            "<html>" +
            "<head>" +

            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width,initial-scale=1'>" +

            "<title>Lost Guitar</title>" +

            "<style>" +

            "*{box-sizing:border-box;}" +

            "body{" +
            "margin:0;" +
            "background:#080808;" +
            "color:white;" +
            "font-family:Arial,sans-serif;" +
            "min-height:100vh;" +
            "padding-bottom:90px;" +
            "}" +

            "button{" +
            "border:0;" +
            "border-radius:13px;" +
            "padding:13px 17px;" +
            "background:#f01825;" +
            "color:white;" +
            "font-size:15px;" +
            "font-weight:bold;" +
            "}" +

            "button:active{" +
            "transform:scale(.97);" +
            "}" +

            ".page{display:none;padding:18px;}" +
            ".page.active{display:block;}" +

            ".logo{" +
            "font-size:27px;" +
            "font-weight:900;" +
            "margin-bottom:20px;" +
            "}" +

            ".logo span{color:#f01825;}" +

            ".banner{" +
            "position:relative;" +
            "height:170px;" +
            "border-radius:20px;" +
            "overflow:hidden;" +
            "background:linear-gradient(135deg,#500a10,#160708);" +
            "margin-bottom:18px;" +
            "display:flex;" +
            "align-items:center;" +
            "justify-content:center;" +
            "text-align:center;" +
            "}" +

            ".banner img{" +
            "position:absolute;" +
            "width:100%;" +
            "height:100%;" +
            "object-fit:cover;" +
            "display:none;" +
            "}" +

            ".bannerContent{" +
            "position:relative;" +
            "z-index:2;" +
            "}" +

            ".banner h2{" +
            "margin:0 0 7px 0;" +
            "font-size:26px;" +
            "}" +

            ".banner p{" +
            "margin:0;" +
            "color:#ccc;" +
            "}" +

            ".bannerChange{" +
            "position:absolute;" +
            "right:10px;" +
            "top:10px;" +
            "z-index:5;" +
            "padding:9px 12px;" +
            "font-size:12px;" +
            "background:rgba(0,0,0,.7);" +
            "}" +

            ".search{" +
            "width:100%;" +
            "padding:16px;" +
            "border-radius:15px;" +
            "border:1px solid #333;" +
            "background:#151515;" +
            "color:white;" +
            "font-size:16px;" +
            "outline:none;" +
            "margin-bottom:18px;" +
            "}" +

            ".search:focus{" +
            "border-color:#f01825;" +
            "}" +

            ".sectionTitle{" +
            "display:flex;" +
            "justify-content:space-between;" +
            "align-items:center;" +
            "margin:20px 0 10px;" +
            "font-size:20px;" +
            "font-weight:bold;" +
            "}" +

            ".sectionTitle small{" +
            "font-size:11px;" +
            "color:#888;" +
            "}" +

            ".song{" +
            "display:flex;" +
            "align-items:center;" +
            "gap:12px;" +
            "background:#151515;" +
            "border:1px solid #292929;" +
            "border-radius:16px;" +
            "padding:11px;" +
            "margin-bottom:10px;" +
            "}" +

            ".cover{" +
            "width:55px;" +
            "height:55px;" +
            "border-radius:12px;" +
            "background:#f01825;" +
            "display:flex;" +
            "align-items:center;" +
            "justify-content:center;" +
            "font-size:24px;" +
            "flex-shrink:0;" +
            "overflow:hidden;" +
            "}" +

            ".cover img{" +
            "width:100%;" +
            "height:100%;" +
            "object-fit:cover;" +
            "}" +

            ".songInfo{" +
            "flex:1;" +
            "min-width:0;" +
            "}" +

            ".songName{" +
            "font-weight:bold;" +
            "white-space:nowrap;" +
            "overflow:hidden;" +
            "text-overflow:ellipsis;" +
            "}" +

            ".songType{" +
            "font-size:12px;" +
            "color:#888;" +
            "margin-top:4px;" +
            "}" +

            ".playBtn{" +
            "background:#f01825;" +
            "width:45px;" +
            "height:45px;" +
            "padding:0;" +
            "border-radius:50%;" +
            "}" +

            ".empty{" +
            "text-align:center;" +
            "color:#777;" +
            "padding:30px 10px;" +
            "}" +

            ".profileCard{" +
            "background:#141414;" +
            "border:1px solid #292929;" +
            "border-radius:20px;" +
            "padding:20px;" +
            "text-align:center;" +
            "}" +

            ".avatar{" +
            "width:90px;" +
            "height:90px;" +
            "border-radius:50%;" +
            "background:#f01825;" +
            "margin:0 auto 12px;" +
            "display:flex;" +
            "align-items:center;" +
            "justify-content:center;" +
            "font-size:34px;" +
            "overflow:hidden;" +
            "}" +

            ".avatar img{" +
            "width:100%;" +
            "height:100%;" +
            "object-fit:cover;" +
            "}" +

            ".profileButtons{" +
            "display:flex;" +
            "gap:10px;" +
            "margin-top:15px;" +
            "}" +

            ".profileButtons button{" +
            "flex:1;" +
            "}" +

            ".upload{" +
            "margin-top:15px;" +
            "background:#202020;" +
            "border:1px solid #333;" +
            "}" +

            ".player{" +
            "position:fixed;" +
            "left:10px;" +
            "right:10px;" +
            "bottom:70px;" +
            "background:#202020;" +
            "border:1px solid #333;" +
            "border-radius:20px;" +
            "padding:14px;" +
            "display:none;" +
            "z-index:20;" +
            "box-shadow:0 10px 35px #000;" +
            "}" +

            ".playerTitle{" +
            "font-weight:bold;" +
            "margin-bottom:8px;" +
            "white-space:nowrap;" +
            "overflow:hidden;" +
            "text-overflow:ellipsis;" +
            "}" +

            "audio{" +
            "width:100%;" +
            "height:40px;" +
            "}" +

            ".nav{" +
            "position:fixed;" +
            "bottom:0;" +
            "left:0;" +
            "right:0;" +
            "height:65px;" +
            "background:#101010;" +
            "border-top:1px solid #292929;" +
            "display:flex;" +
            "z-index:30;" +
            "}" +

            ".nav button{" +
            "flex:1;" +
            "background:transparent;" +
            "border-radius:0;" +
            "color:#888;" +
            "font-size:12px;" +
            "}" +

            ".nav button.active{" +
            "color:white;" +
            "}" +

            "</style>" +

            "</head>" +

            "<body>" +

            "<section id='home' class='page active'>" +

            "<div class='logo'>LOST <span>GUITAR</span></div>" +

            "<div class='banner'>" +
            "<img id='bannerImage'>" +
            "<button class='bannerChange' onclick='Android.pickBanner()'>Change Banner</button>" +
            "<div class='bannerContent'>" +
            "<h2>Find your sound.</h2>" +
            "<p>Your music. Your space.</p>" +
            "</div>" +
            "</div>" +

            "<input id='homeSearch' class='search' placeholder='Search your music...' oninput='searchSongs(this.value)'>" +

            "<div class='sectionTitle'>Your Music <small id='count'>0 SONGS</small></div>" +

            "<button class='upload' onclick='Android.pickMP3()'>＋ Upload MP3</button>" +

            "<div id='songs'></div>" +

            "</section>" +

            "<section id='searchPage' class='page'>" +

            "<div class='logo'>SEARCH</div>" +

            "<input id='searchInput' class='search' placeholder='Search songs...' oninput='searchSongs(this.value)'>" +

            "<div id='results' class='empty'>Search for a song.</div>" +

            "</section>" +

            "<section id='profilePage' class='page'>" +

            "<div class='logo'>PROFILE</div>" +

            "<div class='profileCard'>" +

            "<div class='avatar' id='avatar'>🎸</div>" +

            "<h2 id='usernameText'>Guitar Player</h2>" +

            "<input id='username' class='search' placeholder='Username'>" +

            "<div class='profileButtons'>" +
            "<button onclick='saveUsername()'>Save Name</button>" +
            "<button onclick='Android.pickPFP()'>Change PFP</button>" +
            "</div>" +

            "</div>" +

            "</section>" +

            "<div id='player' class='player'>" +
            "<div id='playerTitle' class='playerTitle'>Nothing playing</div>" +
            "<audio id='audio' controls></audio>" +
            "</div>" +

            "<nav class='nav'>" +
            "<button id='homeBtn' class='active' onclick='showPage(\"home\")'>HOME</button>" +
            "<button id='searchBtn' onclick='showPage(\"searchPage\")'>SEARCH</button>" +
            "<button id='profileBtn' onclick='showPage(\"profilePage\")'>PROFILE</button>" +
            "</nav>" +

            "<script>" +

            "let songs = JSON.parse(localStorage.getItem('songs') || '[]');" +
            "let recent = JSON.parse(localStorage.getItem('recent') || '[]');" +

            "function saveSongs(){" +
            "localStorage.setItem('songs',JSON.stringify(songs));" +
            "}" +

            "function addSong(url){" +
            "if(!url)return;" +

            "let name='MP3 Song '+(songs.length+1);" +

            "let song={" +
            "name:name," +
            "url:url" +
            "};" +

            "songs.push(song);" +
            "saveSongs();" +
            "render();" +

            "playSong(songs.length-1);" +
            "}" +

            "function playSong(index){" +
            "if(!songs[index])return;" +

            "let song=songs[index];" +
            "let audio=document.getElementById('audio');" +
            "let player=document.getElementById('player');" +
            "let title=document.getElementById('playerTitle');" +

            "title.textContent=song.name;" +
            "audio.src=song.url;" +
            "player.style.display='block';" +

            "audio.play().catch(function(){});" +

            "recent=recent.filter(function(x){return x.url!==song.url;});" +
            "recent.unshift(song);" +
            "recent=recent.slice(0,10);" +
            "localStorage.setItem('recent',JSON.stringify(recent));" +
            "}" +

            "function render(list){" +

            "let box=document.getElementById('songs');" +
            "let arr=list || songs;" +

            "document.getElementById('count').textContent=songs.length+' SONGS';" +

            "if(!arr.length){" +
            "box.innerHTML='<div class=\"empty\">No MP3s yet. Upload one! 🎸</div>';" +
            "return;" +
            "}" +

            "box.innerHTML='';" +

            "arr.forEach(function(song){" +

            "let index=songs.indexOf(song);" +

            "box.innerHTML += " +
            "'<div class=\"song\">' +" +

            "'<div class=\"cover\">🎵</div>' +" +

            "'<div class=\"songInfo\">' +" +
            "'<div class=\"songName\">'+escapeHtml(song.name)+'</div>' +" +
            "'<div class=\"songType\">MP3</div>' +" +
            "'</div>' +" +

            "'<button class=\"playBtn\" onclick=\"playSong('+index+')\">▶</button>' +" +

            "'</div>';" +

            "});" +
            "}" +

            "function escapeHtml(x){" +
            "return String(x).replace(/[&<>\\\"']/g,function(c){" +
            "return {'&':'&amp;','<':'&lt;','>':'&gt;','\\\"':'&quot;',\"'\":'&#39;'}[c];" +
            "});" +
            "}" +

            "function searchSongs(value){" +

            "value=(value||'').toLowerCase();" +

            "let found=songs.filter(function(song){" +
            "return song.name.toLowerCase().includes(value);" +
            "});" +

            "let results=document.getElementById('results');" +

            "if(document.getElementById('searchPage').classList.contains('active')){" +

            "if(!found.length){" +
            "results.innerHTML='<div class=\"empty\">No songs found.</div>';" +
            "}else{" +

            "results.innerHTML='';" +

            "found.forEach(function(song){" +
            "let index=songs.indexOf(song);" +

            "results.innerHTML += " +
            "'<div class=\"song\">' +" +
            "'<div class=\"cover\">🎵</div>' +" +
            "'<div class=\"songInfo\"><div class=\"songName\">'+escapeHtml(song.name)+'</div><div class=\"songType\">MP3</div></div>' +" +
            "'<button class=\"playBtn\" onclick=\"playSong('+index+')\">▶</button>' +" +
            "'</div>';" +

            "});" +
            "}" +
            "}else{" +
            "render(found);" +
            "}" +
            "}" +

            "function changePFP(url){" +

            "if(!url)return;" +

            "localStorage.setItem('pfp',url);" +

            "let avatar=document.getElementById('avatar');" +

            "avatar.innerHTML='<img src=\"'+url+'\">';" +
            "}" +

            "function changeBanner(url){" +

            "if(!url)return;" +

            "localStorage.setItem('banner',url);" +

            "let image=document.getElementById('bannerImage');" +

            "image.src=url;" +
            "image.style.display='block';" +

            "}" +

            "function saveUsername(){" +

            "let name=document.getElementById('username').value.trim();" +

            "if(!name)return;" +

            "localStorage.setItem('username',name);" +
            "document.getElementById('usernameText').textContent=name;" +

            "}" +

            "function showPage(page){" +

            "document.querySelectorAll('.page').forEach(function(x){" +
            "x.classList.remove('active');" +
            "});" +

            "document.getElementById(page).classList.add('active');" +

            "document.querySelectorAll('.nav button').forEach(function(x){" +
            "x.classList.remove('active');" +
            "});" +

            "if(page==='home')document.getElementById('homeBtn').classList.add('active');" +
            "if(page==='searchPage')document.getElementById('searchBtn').classList.add('active');" +
            "if(page==='profilePage')document.getElementById('profileBtn').classList.add('active');" +

            "if(page==='searchPage'){" +
            "searchSongs(document.getElementById('searchInput').value);" +
            "}" +

            "}" +

            "window.onload=function(){" +

            "let pfp=localStorage.getItem('pfp');" +
            "if(pfp)changePFP(pfp);" +

            "let banner=localStorage.getItem('banner');" +
            "if(banner)changeBanner(banner);" +

            "let username=localStorage.getItem('username');" +
            "if(username){" +
            "document.getElementById('usernameText').textContent=username;" +
            "document.getElementById('username').value=username;" +
            "}" +

            "render();" +

            "};" +

            "</script>" +

            "</body>" +
            "</html>";
                          }
