package com.android.bikeme.bikemeutils.location;

import android.location.Location;

/**
 * Created by Daniel on 21 sep 2017.
 */
public interface LocationSettingsCallback {

    void locationSettingsGranted(boolean isLocationSettingsGranted);

}
