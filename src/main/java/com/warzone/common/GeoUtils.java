package com.warzone.common;

import java.util.HashMap;
import java.util.Map;

public class GeoUtils {

    private static final Map<String, double[]> COUNTRY_COORDS = new HashMap<>();
    private static final Map<String, String> COUNTRY_NAMES = new HashMap<>();

    static {
        put("AF", "Afghanistan", 33.93, 67.71);
        put("UA", "Ukraine", 48.38, 31.17);
        put("RU", "Russia", 61.52, 105.32);
        put("CN", "China", 35.86, 104.20);
        put("US", "United States", 39.83, -98.58);
        put("IL", "Israel", 31.05, 34.85);
        put("PS", "Palestine", 31.95, 35.23);
        put("SY", "Syria", 34.80, 38.99);
        put("IQ", "Iraq", 33.22, 43.68);
        put("IR", "Iran", 32.43, 53.69);
        put("YE", "Yemen", 15.55, 48.52);
        put("SD", "Sudan", 12.86, 30.22);
        put("SO", "Somalia", 5.15, 46.20);
        put("MM", "Myanmar", 19.76, 96.08);
        put("ET", "Ethiopia", 9.15, 40.49);
        put("CD", "DR Congo", -4.04, 21.76);
        put("NG", "Nigeria", 9.08, 8.68);
        put("ML", "Mali", 17.57, -4.00);
        put("BF", "Burkina Faso", 12.24, -1.56);
        put("MZ", "Mozambique", -18.67, 35.53);
        put("PK", "Pakistan", 30.38, 69.35);
        put("IN", "India", 20.59, 78.96);
        put("TW", "Taiwan", 23.70, 120.96);
        put("KP", "North Korea", 40.34, 127.51);
        put("KR", "South Korea", 35.91, 127.77);
        put("JP", "Japan", 36.20, 138.25);
        put("LB", "Lebanon", 33.85, 35.86);
        put("LY", "Libya", 26.34, 17.23);
        put("CM", "Cameroon", 7.37, 12.35);
        put("HT", "Haiti", 18.97, -72.29);
        put("MX", "Mexico", 23.63, -102.55);
        put("CO", "Colombia", 4.57, -74.30);
    }

    private static void put(String code, String name, double lat, double lng) {
        COUNTRY_COORDS.put(code, new double[]{lat, lng});
        COUNTRY_NAMES.put(code, name);
    }

    public static double[] coordsFor(String countryCode) {
        return COUNTRY_COORDS.getOrDefault(
            countryCode != null ? countryCode.toUpperCase() : "XX",
            new double[]{0.0, 0.0}
        );
    }

    public static String nameFor(String countryCode) {
        return COUNTRY_NAMES.getOrDefault(
            countryCode != null ? countryCode.toUpperCase() : "XX",
            countryCode
        );
    }

    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
