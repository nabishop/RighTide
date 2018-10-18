package Utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import Models.Beach;

public class BeachLoader {
    private static class JSONParsing {
        public static final String URL = "http://api.spitcast.com/api/spot/forecast";
        public static final String DATE = "date";
        public static final String DAY = "day";
        public static final String HOUR = "hour";
        public static final String SCORE_DETAIL = "shape_detail";
        public static final String SCORE_SWELL = "swell";
        public static final String SCORE_TIDE = "tide";
        public static final String SCORE_WIND = "wind";
        public static final String WAVE_SIZE_FT = "size_ft";
        public static final String WARNINGS = "warnings";
    }

    // gets beach stats for the day, general overview
    // assumes both parameters are non-null
    public static ArrayList<Beach> getBeach(String beachName, int beachId) {
        URL request = getBeachUrl(beachId);

        if (request != null) {
            String response = Connection.urlRequest(request);

            if (response != null) {
                ArrayList<Beach> beaches = new ArrayList<>();
                beaches = parseResponse(response, beachId, beachName);
            }
        }
    }

    private static double rankScore(String category) {

    }

    private static double calculateAvgScore(String swell, String tide, String wind) {

    }

    private static ArrayList<Beach> parseResponse(String response,
                                                  int beachId, String beachName) {
        try {
            JSONArray results = new JSONArray(response);
            ArrayList<Beach> beaches = new ArrayList<>();

            for (int x = 0; x < results.length(); x++) {
                JSONObject beachObject = results.getJSONObject(x);

                String date = beachObject.getString(JSONParsing.DATE);
                String day = beachObject.getString(JSONParsing.DAY);
                String hour = beachObject.getString(JSONParsing.HOUR);
                double waveSizeFt = beachObject.getDouble(JSONParsing.WAVE_SIZE_FT);

                JSONObject scoreDetailObject = beachObject
                        .getJSONObject(JSONParsing.SCORE_DETAIL);
                String swellScore = scoreDetailObject.getString(JSONParsing.SCORE_SWELL);
                String tideScore = scoreDetailObject.getString(JSONParsing.SCORE_TIDE);
                String windScore = scoreDetailObject.getString(JSONParsing.SCORE_WIND);
                //TODO Calculate Score Here
                double score;

                JSONArray warningsObject = beachObject.getJSONArray(JSONParsing.WARNINGS);
                ArrayList<String> warnings = new ArrayList<>();
                for (int i = 0; i < warningsObject.length(); i++) {
                    warnings.add(warningsObject.getString(i));
                }
                Beach beach = new Beach(beachId, beachName, score, warnings);
                beaches.add(beach);
            }
            return beaches;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parseResponse Failure", "Failure Parsing with response: "
                    + response);
            return null;
        }
    }

    private static URL getBeachUrl(int beachId) {
        Uri requesting = Uri.parse(JSONParsing.URL).buildUpon()
                .appendPath(String.valueOf(beachId)).build();
        try {
            return new URL(requesting.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("getBeachUrl Failure", "Failed search for " + beachId
                    + ", created url: " + requesting.toString());
            return null;
        }
    }
}