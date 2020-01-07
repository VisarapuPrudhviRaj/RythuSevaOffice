package nk.bluefrog.library.utils;

/**
 * Created by nagendra on 24/3/17.
 */

public interface PermissionResult {

    void permissionGranted();

    void permissionDenied();

    void permissionForeverDenied();

}
