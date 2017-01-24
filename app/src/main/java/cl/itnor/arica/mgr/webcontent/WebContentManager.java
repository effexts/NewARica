package cl.itnor.arica.mgr.webcontent;

import android.content.Context;

/**
 * Created by effexts on 1/20/17.
 */

public interface WebContentManager {
    /**
     * Shows a webpage with the given url if a markerobject is selected
     * (mixlistview, mixoverlay).
     */
    void loadWebPage(String url, Context context) throws Exception;
    /**
     * Checks if the url can be opened by another intent activity, instead of
     * the webview This method searches for possible intents that can be used
     * instead. I.E. a mp3 file can be forwarded to a mediaplayer.
     */
    boolean processUrl(String url, Context context);
}
