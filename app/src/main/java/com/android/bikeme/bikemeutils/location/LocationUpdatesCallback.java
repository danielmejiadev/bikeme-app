package com.android.bikeme.bikemeutils.location;

import android.location.Location;

/**
 * Created by Daniel on 31 ago 2017.
 */
public interface LocationUpdatesCallback {

    void locationChange(Location newLocation);
}
