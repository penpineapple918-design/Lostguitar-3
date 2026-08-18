package com.lostguitar.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.ValueCallback;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {

    WebView web;
    ValueCallback<Uri[]> uploadCallback;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        web = new WebView(this);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView v,
                    ValueCallback<Uri[]> callback,
                    FileChooserParams params) {

                uploadCallback = callback;

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("audio/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(i, 42);
                return true;
            }
        });

        web.loadDataWithBaseURL(
                "https://lostguitar.app/",
                HTML,
                "text/html",
                "UTF-8",
                null
        );

        setContentView(web);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 42 || uploadCallback == null)
            return;

        Uri[] results = null;

        if (resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                results = new Uri[count];

                for (int i = 0; i < count; i++) {
                    results[i] = data.getClipData().getItemAt(i).getUri();
                }

            } else if (data.getData() != null) {
                results = new Uri[]{data.getData()};
            }
        }

        uploadCallback.onReceiveValue(results);
        uploadCallback = null;
    }

    private static final String HTML =
        "<!DOCTYPE html>" +
        "<html><head>" +

        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +

        "<style>" +

        "*{box-sizing:border-box}" +

        "body{" +
        "margin:0;" +
        "background:#070707;" +
        "color:white;" +
        "font-family:Arial,sans-serif;" +
        "padding-bottom:90px;" +
        "}" +

        "body:before{" +
        "content:'🎸     ♫        🎸      ♫';" +
        "position:fixed;" +
        "inset:0;" +
        "z-index:-1;" +
        "color:#350407;" +
        "font-size:70px;" +
        "line-height:2.3;" +
        "opacity:.3;" +
        "transform:rotate(-12deg);" +
        "pointer-events:none;" +
        "}" +

        ".wrap{" +
        "max-width:700px;" +
        "margin:auto;" +
        "padding:20px 17px;" +
        "}" +

        ".top{" +
        "display:flex;" +
        "justify-content:space-between;" +
        "align-items:center;" +
        "}" +

        ".brand{" +
        "color:#ef0b16;" +
        "font-size:28px;" +
        "font-weight:900;" +
        "letter-spacing:1px;" +
        "}" +

        ".banner{" +
        "margin:18px 0;" +
        "padding:21px;" +
        "border-radius:19px;" +
        "background:linear-gradient(135deg,#3c0005,#120000);" +
        "border:1px solid #581116;" +
        "}" +

        ".banner b{font-size:22px}" +

        ".muted{color:#999;margin-top:5px}" +

        ".search{" +
        "width:100%;" +
        "padding:16px;" +
        "border-radius:16px;" +
        "border:1px solid #551116;" +
        "background:#180507;" +
        "color:white;" +
        "font-size:16px;" +
        "outline:none;" +
        "}" +

        ".search:focus{border-color:#ef0b16}" +

        ".title{" +
        "font-size:24px;" +
        "margin:25px 0 12px;" +
        "}" +

        ".card{" +
        "background:#131313;" +
        "border:1px solid #292929;" +
        "border-radius:18px;" +
        "padding:16px;" +
        "margin:10px 0;" +
        "}" +

        ".btn,.play{" +
        "background:#ef0b16;" +
        "color:white;" +
        "border:0;" +
        "border-radius:13px;" +
        "padding:13px 17px;" +
        "font-weight:bold;" +
        "font-size:15px;" +
        "}" +

        ".song{" +
        "display:flex;" +
        "align-items:center;" +
        "gap:12px;" +
        "}" +

        ".cover{" +
        "width:54px;" +
        "height:54px;" +
        "border-radius:14px;" +
        "background:#e50914;" +
        "display:grid;" +
        "place-items:center;" +
        "font-size:24px;" +
        "}" +

        ".songinfo{" +
        "flex:1;" +
        "min-width:0;" +
        "}" +

        ".songinfo b{" +
        "display:block;" +
        "white-space:nowrap;" +
        "overflow:hidden;" +
        "text-overflow:ellipsis;" +
        "}" +

        ".small{" +
        "font-size:13px;" +
        "color:#999;" +
        "margin-top:4px;" +
        "}" +

        ".empty{" +
        "text-align:center;" +
        "color:#888;" +
        "padding:28px 10px;" +
        "}" +

        ".page{display:none}" +
        ".page.on{display:block}" +

        ".tabs{" +
        "position:fixed;" +
        "bottom:0;" +
        "left:0;" +
        "right:0;" +
        "background:#111;" +
        "border-top:1px solid #292929;" +
        "display:flex;" +
        "justify-content:space-around;" +
        "padding:14px 5px;" +
        "z-index:10;" +
        "}" +

        ".tabs button{" +
        "background:none;" +
        "border:0;" +
        "color:#888;" +
        "font-weight:bold;" +
        "font-size:14px;" +
        "}" +

        ".tabs button.on{color:white}" +

        ".player{" +
        "position:fixed;" +
        "left:12px;" +
        "right:12px;" +
        "bottom:72px;" +
        "background:#260508;" +
        "border:1px solid #68151a;" +
        "border-radius:22px;" +
        "padding:15px;" +
        "z-index:9;" +
        "}" +

        ".playerTop{" +
        "display:flex;" +
        "gap:12px;" +
        "align-items:center;" +
        "}" +

        ".playerInfo{" +
        "flex:1;" +
        "min-width:0;" +
        "}" +

        ".range{" +
        "width:100%;" +
        "accent-color:#ef0b16;" +
        "margin-top:10px;" +
        "}" +

        ".avatar{" +
        "width:76px;" +
        "height:76px;" +
        "border-radius:50%;" +
        "background:#ef0b16;" +
        "display:grid;" +
        "place-items:center;" +
        "font-size:30px;" +
        "}" +

        ".input{" +
        "width:100%;" +
        "padding:13px;" +
        "border-radius:12px;" +
        "border:1px solid #333;" +
        "background:#0e0e0e;" +
        "color:white;" +
        "margin:6px 0 10px;" +
        "}" +

        "</style></head><body>" +

        "<div class='wrap'>" +

        "<div class='top'>" +
        "<div class='brand'>LOST GUITAR</div>" +
        "<div>⚙️</div>" +
        "</div>" +

        "<div class='banner'>" +
        "<b id='greet'>Good evening</b>" +
        "<div class='muted'>Your music. Your space. 🎸</div>" +
        "</div>" +

        "<section id='home' class='page on'>" +

        "<input class='search' " +
        "placeholder='Search your music...' " +
        "oninput='search(this.value)'>" +

        "<h2 class='title'>Creator's Picks</h2>" +

        "<div class='card'>" +
        "<b>Late Night Guitar</b>" +
        "<div class='small'>A starter playlist</div>" +
        "</div>" +

        "<h2 class='title'>Your Music</h2>" +

        "<button class='btn' onclick='pick()'>＋ Upload MP3</button>" +

        "<div id='songs'></div>" +

        "<h2 class='title'>Recently Played</h2>" +

        "<div id='recent'></div>" +

        "</section>" +

        "<section id='search' class='page'>" +

        "<h2 class='title'>Search</h2>" +

        "<input id='qs' class='search' " +
        "placeholder='Search songs...' " +
        "oninput='search(this.value)'>" +

        "<div id='results' class='empty'>" +
        "Play songs to get started :D" +
        "</div>" +

        "</section>" +

        "<section id='profile' class='page'>" +

        "<h2 class='title'>Profile</h2>" +

        "<div class='card'>" +

        "<div class='playerTop'>" +
        "<div class='avatar' id='ava'>🎸</div>" +
        "<div class='playerInfo'>" +
        "<b id='pname'>there</b>" +
        "<div class='small'>Lost Guitar listener</div>" +
        "</div>" +
        "</div>" +

        "<input id='name' class='input' placeholder='Username'>" +

        "<input id='bio' class='input' placeholder='Bio'>" +

        "<button class='btn' onclick='saveProfile()'>" +
        "Save profile" +
        "</button>" +

        "</div>" +

        "</section>" +

        "</div>" +

        "<div id='player' class='player' style='display:none'>" +

        "<div class='playerTop'>" +

        "<div class='cover'>♫</div>" +

        "<div class='playerInfo'>" +
        "<b id='now'>Nothing playing</b>" +
        "<div class='small' id='ptime'>0:00 / 0:00</div>" +
        "</div>" +

        "<button class='play' onclick='toggle()'>▶</button>" +

        "</div>" +

        "<input id='bar' class='range' type='range' " +
        "min='0' max='100' value='0' " +
        "oninput='seek(this.value)'>" +

        "</div>" +

        "<div class='tabs'>" +

        "<button class='on' onclick=\"tab('home',this)\">Home</button>" +

        "<button onclick=\"tab('search',this)\">Search</button>" +

        "<button onclick=\"tab('profile',this)\">Profile</button>" +

        "</div>" +

        "<input id='file' type='file' " +
        "accept='audio/*,.mp3' multiple hidden " +
        "onchange='files(this.files)'>" +

        "<audio id='audio'></audio>" +

        "<script>" +

        "const A=document.getElementById('audio');" +
        "const S=[];" +
        "const R=[];" +
        "let current=-1;" +

        "function greet(){" +
        "let h=new Date().getHours();" +
        "let p=h<12?'Good morning':h<18?'Good afternoon':'Good evening';" +
        "document.getElementById('greet').textContent=" +
        "p+' '+(localStorage.name||'there');" +
        "}" +

        "function pick(){" +
        "document.getElementById('file').click();" +
        "}" +

        "function files(fs){" +
        "for(let f of fs){" +
        "if(!f.name.toLowerCase().endsWith('.mp3'))continue;" +
        "S.push({name:f.name,url:URL.createObjectURL(f)});" +
        "}" +
        "render();" +
        "search('');" +
        "}" +

        "function play(i){" +
        "if(!S[i])return;" +
        "current=i;" +
        "A.src=S[i].url;" +
        "A.play();" +
        "document.getElementById('now').textContent=S[i].name;" +
        "document.getElementById('player').style.display='block';" +
        "R.unshift(S[i]);" +
        "while(R.length>5)R.pop();" +
        "recent();" +
        "}" +

        "function toggle(){" +
        "if(A.paused)A.play();else A.pause();" +
        "}" +

        "function seek(v){" +
        "if(A.duration)A.currentTime=A.duration*v/100;" +
        "}" +

        "A.ontimeupdate=function(){" +
        "let p=A.duration?A.currentTime/A.duration*100:0;" +
        "document.getElementById('bar').value=p;" +
        "document.getElementById('ptime').textContent=" +
        "tm(A.currentTime)+' / '+tm(A.duration);" +
        "};" +

        "A.onended=function(){" +
        "if(current+1<S.length)play(current+1);" +
        "};" +

        "function tm(x){" +
        "if(!isFinite(x))return'0:00';" +
        "return Math.floor(x/60)+':'+" +
        "String(Math.floor(x%60)).padStart(2,'0');" +
        "}" +

        "function card(s){" +
        "let i=S.indexOf(s);" +
        "return '<div class=\"card\"><div class=\"song\">'+" +
        "'<div class=\"cover\">♫</div>'+" +
        "'<div class=\"songinfo\"><b>'+esc(s.name)+'</b>'+" +
        "'<div class=\"small\">MP3</div></div>'+" +
        "'<button class=\"play\" onclick=\"play('+i+')\">Play</button>'+" +
        "'</div></div>';" +
        "}" +

        "function render(list){" +
        "let a=list||S;" +
        "document.getElementById('songs').innerHTML=" +
        "a.length?a.map(card).join(''):" +
        "'<div class=\"empty\">No MP3s yet. Upload one 🎸</div>';" +
        "recent();" +
        "}" +

        "function recent(){" +
        "document.getElementById('recent').innerHTML=" +
        "R.length?R.map(card).join(''):" +
        "'<div class=\"empty\">Nothing played yet.</div>';" +
        "}" +

        "function search(q){" +
        "q=q.toLowerCase().trim();" +
        "let a=S.filter(s=>s.name.toLowerCase().includes(q));" +
        "let box=document.getElementById('results');" +
        "box.innerHTML=a.length?a.map(card).join(''):" +
        "'<div class=\"empty\">'+(q?'No songs found.':'Play songs to get started :D')+'</div>';" +
        "if(!document.getElementById('search').classList.contains('on'))render(a);" +
        "}" +

        "function tab(id,b){" +
        "document.querySelectorAll('.page').forEach(x=>x.classList.remove('on'));" +
        "document.getElementById(id).classList.add('on');" +
        "document.querySelectorAll('.tabs button').forEach(x=>x.classList.remove('on'));" +
        "b.classList.add('on');" +
        "}" +

        "function saveProfile(){" +
        "let n=document.getElementById('name').value.trim()||'there';" +
        "localStorage.name=n;" +
        "localStorage.bio=document.getElementById('bio').value;" +
        "document.getElementById('pname').textContent=n;" +
        "greet();" +
        "}" +

        "function esc(x){" +
        "return x.replace(/[&<>\\\"']/g,c=>({" +
        "'&':'&amp;','<':'&lt;','>':'&gt;'," +
        "'\\\"':'&quot;',\"'\":'&#39;'}[c]));" +
        "}" +

        "document.getElementById('name').value=localStorage.name||'';" +
        "document.getElementById('bio').value=localStorage.bio||'';" +
        "document.getElementById('pname').textContent=localStorage.name||'there';" +
        "greet();" +
        "render();" +

        "</script></body></html>";
                           }
