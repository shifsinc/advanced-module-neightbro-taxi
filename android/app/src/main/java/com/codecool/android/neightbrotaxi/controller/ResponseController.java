package com.codecool.android.neightbrotaxi.controller;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.codecool.android.neightbrotaxi.model.AlertUser;
import com.codecool.android.neightbrotaxi.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Responsible for managing Activity's screen, based on the server json responses.
 * @see AlertUser
 */
public class ResponseController {
    private static final String TAG = ResponseController.class.getSimpleName()+"<>";

    /**
     * @param mActivity from the current context
     * @param response from the server
     */
    public ResponseController(Activity mActivity, String response) {
        Log.d(TAG, "Response from server: "+response);

//        Intent intent = new Intent(mActivity, UserActivity.class);

        try {
            if (new JSONObject(response).has("id")) {
                Log.i(TAG, "USER REGISTERED!");
                Toast.makeText(mActivity, "REGISTRATION DONE!", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
                JSONObject json = new JSONObject(response);
                User user = new User(
                        json.getInt("id"),
                        json.getString("name"),
                        json.getString("email"),
                        json.getString("username"),
                        json.getString("password"),
                        json.getString("passwordConfirm"),
                        null
//                        json.getJSONArray("roles")
                );
                StorageController session = new StorageController(mActivity);

                session.setStoredUser(user);
                Log.i(TAG, "USER OBJECT SAVED: " + user);
                User getUser1 = session.getStoredUser();
                Log.d(TAG, "GET OBJECT SAVED USER: " + getUser1);
                Log.d(TAG, "TEST USER OBJECT ADDRESS:"+getUser1.getEmail());
                return;
            }
            if (new JSONObject(response).getString("infoMessages")
                    .equals("[\"Successfully logged in!\"]")) {
                Log.i(TAG, "USER LOGGED IN!");
                Toast.makeText(mActivity, "AUTHENTICATION DONE!", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
                return;
            }
            if (new JSONObject(response).getString("errorMessages")
                    .equals("[\"Invalid username or password!\"]")) {
                Log.i(TAG, "INVALID AUTHENTICATION DATA!");
                new AlertUser(mActivity).invalidAuthenticationError();
            }
        }
        catch (JSONException error) {
            try {
                JSONArray responseJson = new JSONArray(response);
                JSONObject content = (JSONObject) responseJson.get(0);
                if (content.get("codes").toString().contains("Duplicate")) {
                    Log.i(TAG, "DUPLICATE!");
                    new AlertUser(mActivity).duplicateError();
                }
            }
            catch (JSONException e) {
                Log.e(TAG, String.valueOf(e));
            }
            Log.e(TAG, String.valueOf(error));
        }
    }
}