package com.a324.mbaaslibrary.util;

import android.util.Log;

import com.a324.mbaaslibrary.model.MetadataSearchObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kevinfischer on 7/16/18.
 */

public class MetadataSearchQueryUtility {

    /**
     * Creates a formatted keyword search readable by the metadata search
     *
     * @param keyword the item to be searched on
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String createKeywordSearch(String keyword) throws IllegalArgumentException {
        if (keyword != null && !keyword.isEmpty()) {
            return "\"" + keyword + "\"";
        } else {
            Log.e("MetadataSearch Utility", "keyword is either null or empty");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a formatted json property search readable by the metadata search
     *
     * @param key   json key to search within
     * @param value item to be search for within json key
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String createJSONSearch(String key, String value) throws IllegalArgumentException {
        if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
            return key + ":" + value;
        } else {
            Log.e("MetadataSearch Utility", "arguments are either null or empty");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a formatted geospatial search readable by the metadata search
     *
     * @param key       json key to search within
     * @param latitude  center of the search circle
     * @param longitude center of the search circle
     * @param radius    radius of the search circle
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String createGEOSearch(String key, double latitude, double longitude, int radius) throws IllegalArgumentException {
        if (key != null && !key.isEmpty()) {
            return "geo=" + key + ":" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "," + String.valueOf(radius);
        } else {
            Log.e("MetadataSearch Utility", "key is either null or empty");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a formatted range inequality search readable by the metadata search
     *
     * @param inequality inequality enum for comparison
     * @param key        json key to search within
     * @param value      amount for comparison
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String createRangeSearch(InequalityEnum inequality, String key, Long value) throws IllegalArgumentException {
        if (key != null && !key.isEmpty()) {
            return key + inequality.valueOf() + Long.toString(value);
        } else {
            Log.e("MetadataSearch Utility", "key is either null or empty");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a formatted date range search readable by the metadata search
     *
     * @param startDate start date of date range
     * @param endDate   end date of date range
     * @param key       json key to search within
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String createDateRangeSearch(Date startDate, Date endDate, String key) throws IllegalArgumentException {
        if (startDate != null && endDate != null && key != null && !key.isEmpty()
                && endDate.compareTo(startDate) > 0) {
            String startQuery = createRangeSearch(InequalityEnum.GE, key, startDate.getTime());
            String endQuery = createRangeSearch(InequalityEnum.LE, key, endDate.getTime());
            return startQuery + "&&" + endQuery;
        } else {
            Log.e("MetadataSearch Utility", "arguments are either null or empty");
            throw new IllegalArgumentException();
        }
    }

    public static String createNotQuery(String query){
        if (query != null && !query.isEmpty()) {
            return "~"+query;
        } else {
            Log.e("MetadataSearch Utility", "key is either null or empty");
            throw new IllegalArgumentException();
        }
    }

    public static String createQuery(MetadataSearchObject searchObject){
        ArrayList<String> queries = new ArrayList<>();
        String keyword = searchObject.getKeyword();
        if(keyword != null && !keyword.isEmpty()){
            queries.add(MetadataSearchQueryUtility.createKeywordSearch(keyword));
        }
        String jsonKey = searchObject.getJsonKey();
        String jsonValue = searchObject.getJsonValue();
        if(jsonKey != null && !jsonKey.isEmpty() && jsonValue != null && !jsonValue.isEmpty()){
            queries.add(MetadataSearchQueryUtility.createJSONSearch(jsonKey, jsonValue));
        }
        Date startDate = searchObject.getStartDate();
        Date endDate = searchObject.getEndDate();
        String dateKey = searchObject.getDateKey();
        if(dateKey != null && !dateKey.isEmpty()){
            if(startDate !=null) {
                queries.add(MetadataSearchQueryUtility.createRangeSearch(InequalityEnum.GE, dateKey, startDate.getTime()));
            }
            if(endDate != null){
                queries.add(MetadataSearchQueryUtility.createRangeSearch(InequalityEnum.LE, dateKey, endDate.getTime()));
            }
        }
        String geoKey = searchObject.getGeoKey();
        double latitude = searchObject.getLatitude();
        double longitude = searchObject.getLongitude();
        int radius = searchObject.getRadius();
        if(geoKey != null && !geoKey.isEmpty() && radius != 0){
            queries.add(MetadataSearchQueryUtility.createGEOSearch(geoKey,latitude, longitude, radius));
        }
        String[] queryArray = queries.toArray(new String[queries.size()]);
        String result = MetadataSearchQueryUtility.andQueries(queryArray);
        return result;
    }

    /**
     * Combines formatted search queries by boolean AND readable by the metadata search
     *
     * @param queries unbounded string array of formatted queries
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String andQueries(String... queries) throws IllegalArgumentException {
        String singleQuery = "";
        if (queries == null || queries.length == 0) {
            throw new IllegalArgumentException();
        }
        if (queries.length == 1) {
            //cannot and together one thing
            return queries[0];
        } else {
            singleQuery = queries[0];
            for (int i = 1; i < queries.length; i++) {
                singleQuery = singleQuery.concat("&&").concat(queries[i]);
            }
            return singleQuery;
        }
    }

    /**
     * Combines formatted search queries by boolean OR readable by the metadata search
     *
     * @param queries unbounded string array of formatted queries
     * @return search formatted string
     * @throws IllegalArgumentException
     */
    public static String orQueries(String... queries) throws IllegalArgumentException {
        String singleQuery = "";
        if (queries == null || queries.length == 0) {
            throw new IllegalArgumentException();
        }
        if (queries.length == 1) {
            //cannot and together one thing
            return queries[0];
        } else {
            singleQuery = queries[0];
            for (int i = 1; i < queries.length; i++) {
                singleQuery = singleQuery.concat("||").concat(queries[i]);
            }
            return singleQuery;
        }
    }

    /**
     * Verify that the query is readable by the metadata search
     * @param completeQuery formatted string readable by metadata search
     * @return true if correct, false if not
     */
    public static boolean verifyValidFormattedString(String completeQuery) {
        String[] splitQueries = completeQuery.split("&&|\\|\\|");
        for (String query : splitQueries) {
            boolean not = false;
            if (query == null) {
                return false;
            }
            //check if not query
            if (query.charAt(0) == '~') {
                //strip off not symbol
                query = query.substring(1);
            }
            //geo search
            if (query.contains("geo=")) {
                Log.i("verify format", "creating geo query from string: " + query);
                String strippedQuery = query.replace("geo=", "");
                String[] splitQuery = strippedQuery.split(":");
                if (splitQuery.length == 2) {
                    String jsonProperty = splitQuery[0];
                    if (jsonProperty == null || jsonProperty.isEmpty()) {
                        return false;
                    }
                    String[] splitValue = splitQuery[1].split(",");
                    if (splitValue.length == 3) {
                        double lat = Double.valueOf(splitValue[0]);
                        double lon = Double.valueOf(splitValue[1]);
                        int radius = Integer.valueOf(splitValue[2]);
                        continue;
                    }else{
                        return false;
                    }
                }
                continue;
                //keyword search
            } else if (query.contains("\"") | query.contains("\'")) {
                Log.i("verify format","creating keyword query from string: " + query);
                String strippedQuery = query.replaceAll("\"", "").replaceAll("\'", "");
                if(strippedQuery == null || strippedQuery.isEmpty()){
                    return false;
                }
                continue;
                //json property search
            } else if (query.contains(":")) {
                Log.i("verify format","creating json query from string: " + query);
                String[] splitJson = query.split(":");
                if (splitJson.length == 2) {
                    if(splitJson[0] == null || splitJson[0].isEmpty() || splitJson[1] == null || splitJson[1].isEmpty()){
                        return false;
                    }
                }
                continue;
                //range search
            } else if (query.contains("<") || query.contains(">") || query.contains("=")) {
                Log.i("verify format","creating range query from string: " + query);
                String[] splitRange = query.split(">=|<=|=|>|<");
                if (splitRange.length == 2) {
                    long value = Long.valueOf(splitRange[1]);
                    if(splitRange[0] == null || splitRange[0].isEmpty()){
                        return false;
                    }
                }
                continue;

            }
        }
        return true;
    }




}
