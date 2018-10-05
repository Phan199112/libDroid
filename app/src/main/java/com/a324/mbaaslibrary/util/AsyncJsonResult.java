package com.a324.mbaaslibrary.util;

import org.json.JSONObject;

public interface AsyncJsonResult {

    void onResult(JSONObject object);

    void onFailure(int responseCode);

}
