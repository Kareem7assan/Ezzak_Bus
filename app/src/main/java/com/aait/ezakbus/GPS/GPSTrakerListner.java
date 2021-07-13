package com.aait.ezakbus.GPS;

/**
 * Created by mahmoud on 12/03/2017.
 */

public interface GPSTrakerListner {
    void onTrakerSuccess(Double lat, Double log);
    void onStartTraker();
    void onStopTracker(double lat, double lng);
}
