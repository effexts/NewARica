package cl.itnor.arica.mgr.location;
import cl.itnor.arica.MixContext;

/**
 * Created by effexts on 1/20/17.
 */

public class LocationFinderFactory {
    public static LocationFinder makeLocationFinder(MixContext mixContext) {
        return new LocationMgrImpl(mixContext);
    }
}
