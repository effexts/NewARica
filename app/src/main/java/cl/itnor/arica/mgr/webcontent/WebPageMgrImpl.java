package cl.itnor.arica.mgr.webcontent;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import java.util.List;

import cl.itnor.arica.MixContext;

/**
 * Created by effexts on 1/22/17.
 */

class WebPageMgrImpl implements WebContentManager {
    protected MixContext mixContext;

    public WebPageMgrImpl(MixContext mixContext) {
        this.mixContext = mixContext;
    }

    public void loadMixViewWebPage(String url) throws Exception {
        loadWebPage(url, mixContext.getActualMixView());
    }

    @Override
    public void loadWebPage(String url, Context context) throws Exception {
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);

        final Dialog dialog = new Dialog(context) {
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    this.dismiss();
                return true;
            }
        };
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.endsWith("return")) {
                    dialog.dismiss();
                    mixContext.getActualMixView().repaint();
                }
                else
                    super.onPageFinished(view, url);
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.addContentView(webView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        if (!processUrl(url,mixContext.getActualMixView())) {
            dialog.show();
            webView.loadUrl(url);
        }
    }

    @Override
    public boolean processUrl(String url, Context context) {
        List<ResolveInfo> resolveInfos = getAvailablePackagesForUrl(url, context);
        List<ResolveInfo> webBrowsers = getAvailablePackagesForUrl("http://www.google.cl", context );
        for (ResolveInfo resolveInfo: resolveInfos) {
            for (ResolveInfo webBrowser : webBrowsers) {
                if (!resolveInfo.activityInfo.packageName.equals(webBrowser.activityInfo.packageName)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    context.startActivity(intent);
                    return true;
                }
            }
        }
        return false;
    }

    private List<ResolveInfo> getAvailablePackagesForUrl(String url, Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
    }
}
