package com.jleth.android.fontcompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWebView();
        setupNativeViews();
    }

    private void setupNativeViews() {
        ViewGroup root = (ViewGroup) findViewById(R.id.dynamic_container);

        final List<TypefaceInfo> fonts = FontLister.safelyGetTypefaceInfos();
        for (TypefaceInfo info : fonts) {
            addTextView(root, info);
        }
    }

    @SuppressLint("SetTextI18n")
    private void addTextView(ViewGroup root, TypefaceInfo info) {
        TextView tv = new TextView(this);
        Typeface tf = Typeface.create(info.familyName, info.style.value);
        tv.setTypeface(tf);
        tv.setText(info.familyName + " + " + info.style + " = "+info.fontFileName);

        root.addView(tv);
    }

    private void setupWebView() {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/html/index.html");
        webView.setBackgroundColor(0x00FFFFFF);
    }

    public void openFontLink(View view) {
        String url = ((TextView) view).getText().toString();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
