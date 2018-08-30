package com.example.android.gurkha;

/**
 * Created by Shaakya on 5/9/2017.
 */

import android.graphics.Color;
import android.location.Location;
import android.util.TypedValue;

import com.example.android.gurkha.utils.PaintUtils;

public class RadarView {
    /**
     * The screen
     */
    public DataView view;
    /**
     * The radar's range
     */
    float range;
    /**
     * Radius in pixel on screen
     */
    public static float RADIUS = 100;
    /**
     * Position on screen
     */
    static float originX = 0, originY = 0;

    /**
     * You can change the radar color from here.
     */
    static int radarColor = Color.argb(100, 220, 0, 0);
    Location currentLocation = new Location("provider");
    Location destinedLocation = new Location("provider");

    /*
     * pass the same set of coordinates to plot POI's on radar
     * */
    // SF Art Commission, SF Dept. of Public Health, SF Ethics Comm, SF Conservatory of Music, All Star Cafe, Magic Curry Cart, SF SEO Marketing, SF Honda,
    // SF Mun Transport Agency, SF Parking Citation, Mayors Office of Housing, SF Redev Agency, Catario Patrice, Bank of America , SF Retirement System, Bank of America Mortage,
    // Writers Corp., Van Nes Keno Mkt.
    double[] latitudes = new double[]{27.6768330, 27.680616, 27.682427, 27.684265, 27.677712, 27.673283, 27.677305,
            27.673610, 27.669497, 27.696568, 27.693806, 27.699432, 27.694571, 27.704273, 27.702723, 27.695094, 27.686046, 27.707825};
    double[] longitudes = new double[]{85.312566, 85.313267, 85.307371, 85.295354, 85.290697, 85.310716, 85.316852,
            85.324916, 85.311213, 85.305140, 85.294724, 85.300202, 85.292905, 85.307364, 85.313252, 85.314820, 85.279219, 85.315389};


    public float[][] coordinateArray = new float[latitudes.length][2];

    float angleToShift;
    public float degreetopixel;
    public float bearing;
    public float circleOriginX;
    public float circleOriginY;
    private float mscale;


    public float x = 0;
    public float y = 0;
    public float z = 0;

    float yaw = 0;
    double[] bearings;
    ARView arView = new ARView();

    public RadarView(DataView dataView, double[] bearings) {
        this.bearings = bearings;
        calculateMetrics();
    }

    public void calculateMetrics() {
        circleOriginX = originX + RADIUS;
        circleOriginY = originY + RADIUS;

        range = (float) arView.convertToPix(10) * 50;
        mscale = range / arView.convertToPix((int) RADIUS);
    }

    public void paint(PaintUtils dw, float yaw) {

//		circleOriginX = originX + RADIUS;
//		circleOriginY = originY + RADIUS;
        this.yaw = yaw;
//		range = arView.convertToPix(10) * 1000;		/** Draw the radar */
        dw.setFill(true);
        dw.setColor(radarColor);
        dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

        //put the markers in it
        float scale = range / arView.convertToPix((int) RADIUS);

        //Your current location coordinate here.

        currentLocation.setLatitude(27.69302);
        currentLocation.setLongitude(85.3155029);


        for (int i = 0; i < latitudes.length; i++) {
            destinedLocation.setLatitude(latitudes[i]);
            destinedLocation.setLongitude(longitudes[i]);
            convLocToVec(currentLocation, destinedLocation);
            float x = this.x / mscale;
            float y = this.z / mscale;


            if (x * x + y * y < RADIUS * RADIUS) {
                dw.setFill(true);
                dw.setColor(Color.rgb(255, 255, 255));
                dw.paintRect(x + RADIUS, y + RADIUS, 2, 2);
            }
        }
    }

    public void calculateDistances(PaintUtils dw, float yaw) {
        currentLocation.setLatitude(27.68302);
        currentLocation.setLongitude(85.3055029);
        for (int i = 0; i < latitudes.length; i++) {
            if (bearings[i] < 0) {
                bearings[i] = 360 - bearings[i];
            }
            if (Math.abs(coordinateArray[i][0] - yaw) > 3) {
                angleToShift = (float) bearings[i] - this.yaw;
                coordinateArray[i][0] = this.yaw;
            } else {
                angleToShift = (float) bearings[i] - coordinateArray[i][0];

            }
            destinedLocation.setLatitude(latitudes[i]);
            destinedLocation.setLongitude(longitudes[i]);
            float[] z = new float[1];
            z[0] = 0;
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), destinedLocation.getLatitude(), destinedLocation.getLongitude(), z);
            bearing = currentLocation.bearingTo(destinedLocation);

            this.x = (float) (circleOriginX + 40 * (Math.cos(angleToShift)));
            this.y = (float) (circleOriginY + 40 * (Math.sin(angleToShift)));


            if (x * x + y * y < RADIUS * RADIUS) {
                dw.setFill(true);
                dw.setColor(Color.rgb(255, 255, 255));
                dw.paintRect(x + RADIUS - 1, y + RADIUS - 1, 2, 2);
            }
        }
    }

    /**
     * Width on screen
     */
    public float getWidth() {
        return RADIUS * 2;
    }

    /**
     * Height on screen
     */
    public float getHeight() {
        return RADIUS * 2;
    }


    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void convLocToVec(Location source, Location destination) {
        float[] z = new float[1];
        z[0] = 0;
        Location.distanceBetween(source.getLatitude(), source.getLongitude(), destination
                .getLatitude(), source.getLongitude(), z);
        float[] x = new float[1];
        Location.distanceBetween(source.getLatitude(), source.getLongitude(), source
                .getLatitude(), destination.getLongitude(), x);
        if (source.getLatitude() < destination.getLatitude())
            z[0] *= -1;
        if (source.getLongitude() > destination.getLongitude())
            x[0] *= -1;

        set(x[0], (float) 0, z[0]);
    }
}
