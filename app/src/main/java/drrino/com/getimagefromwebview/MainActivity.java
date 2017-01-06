package drrino.com.getimagefromwebview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private ProgressBar progressBar;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mContext = this;

        initView();
        setClick();
    }


    private void setClick() {
        //加载完网页隐藏progressbar
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                addImageClickListener(mWebView);
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);

            }
        });
    }


    private void initView() {
        progressBar.setVisibility(View.VISIBLE);
        mWebView.loadUrl("http://jandan.net/");
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setBuiltInZoomControls(false);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        //载入js
        mWebView.addJavascriptInterface(new JavascriptInterface(mContext), "imageListener");
    }


    // 注入js函数监听
    public void addImageClickListener(WebView mWebView) {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebView.loadUrl("javascript:(function(){" +
            "var objs = document.getElementsByTagName(\"img\"); " +
            "for(var i=0;i<objs.length;i++)  " +
            "{"
            + "    objs[i].onclick=function()  " +
            "    {  "
            + "        window.imageListener.openImage(this.src);  " +
            "    }  " +
            "}" +
            "})()");
    }


    // js通信接口
    public class JavascriptInterface {
        private Context context;


        JavascriptInterface(Context context) {
            this.context = context;
        }


        @android.webkit.JavascriptInterface
        public void showImage(String img) {
            //获取图片url，对其进行操作
            Toast.makeText(context, img, Toast.LENGTH_SHORT).show();
            Log.e("aaaa", img);
        }
    }
}
