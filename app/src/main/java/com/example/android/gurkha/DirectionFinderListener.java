package com.example.android.gurkha;

/**
 * Created by Shaakya on 7/12/2017.
 */

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
