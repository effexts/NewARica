package cl.itnor.arica.mgr.webcontent;

import cl.itnor.arica.MixContext;

/**
 * Created by effexts on 1/22/17.
 */

public class WebContentManagerFactory {
    public static WebContentManager makeWebContentManager(MixContext mixContext) {
        return new WebPageMgrImpl(mixContext);
    }
}
