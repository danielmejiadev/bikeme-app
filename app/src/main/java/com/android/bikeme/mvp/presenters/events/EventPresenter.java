package com.android.bikeme.mvp.presenters.events;


import com.android.bikeme.classes.Point;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 12 ago 2017.
 */
public interface EventPresenter {


    void onGoToEventDetail(HashMap<String, Object> eventToShow);

}
