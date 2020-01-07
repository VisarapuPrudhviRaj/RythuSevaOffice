package nk.bluefrog.library.gps;


public class GpsHelper

{//changed here

    static final double pi = 3.14159265358979323846;

    public static double getDistance(double lat1, double lon1, double lat2,
                                     double lon2) {
        double theta, dist;

        theta = lon1 - lon2;

        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);

        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;

        dist = dist * 1000; // in meters

        return dist;

    }

    // 17.718602,83.215736 vskp 18.30401,83.900331 skml
    public static double getDistanceGPS(String gps1, String gps2) {

        double lat1, lon1, lat2, lon2;
        double theta, dist;
        try {
            lat1 = Double.parseDouble((gps1.substring(0, gps1.indexOf("-"))
                    .toString()));
            lon1 = Double.parseDouble(gps1.substring(gps1.indexOf("-") + 1,
                    gps1.length()));
            lat2 = Double.parseDouble((gps2.substring(0, gps2.indexOf("-"))
                    .toString()));
            lon2 = Double.parseDouble(gps2.substring(gps2.indexOf("-") + 1,
                    gps2.length()));

            theta = lon1 - lon2;

            dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                    + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);

            dist = rad2deg(dist);

            dist = dist * 60 * 1.1515;
            dist = dist * 1000; // in meters
        } catch (Exception ex) {
            dist = 0;
        }
        // System.out.println("gps1:" + gps1 + "=" + "gps2:" + gps2 + "=" +
        // "D:"+ dist);
        // System.out.println("D:" + dist);
        return (dist);

    }

    public static double deg2rad(double deg) {
        return (deg * pi / 180);
    }

    public static double rad2deg(double rad) {
        return (rad * 180 / pi);

    }

}
