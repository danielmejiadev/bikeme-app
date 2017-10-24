package com.android.bikeme.bikemeutils.location;

import java.io.Serializable;

/**
 * Created by Daniel on 2 sep 2017.
 */
public interface PermissionListener extends Serializable {
    void permissionResult(boolean hasPermission, int typeRequest);
}
