/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android.samples.com.gcmquickstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;

    /* begin -- added by joji */
    private ActionBar mActionBar;
    private TextView mWelcomeText;
    private EditText mStudentIdEntry;
    private EditText mStudentNameEntry;
    private ImageButton mRegisterButton;

    /* end -- added by joji */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        /* begin -- added by joji */
        // Change title
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("AutoAttendance");

        // Setup the previously saved id & name
        mWelcomeText = (TextView) findViewById(R.id.welcome_text);
        mStudentIdEntry = (EditText) findViewById(R.id.student_id);
        mStudentNameEntry = (EditText) findViewById(R.id.student_name);

        // Read in the ID
        try {
            InputStream inputStream = openFileInput("student_id.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                String mStudentId = stringBuilder.toString();
                mStudentIdEntry.setText(mStudentId);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        // Read in the name
        try {
            InputStream inputStream = openFileInput("student_name.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                String mStudentName = stringBuilder.toString();
                mStudentNameEntry.setText(mStudentName);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        // Update Welcome message
        if (!mWelcomeText.getText().equals("")) {
            mWelcomeText.setTextColor(getResources().getColor(R.color.blue_grey_700));
            mWelcomeText.setTextSize(20);
            mWelcomeText.setText("Welcome back " + mStudentNameEntry.getText().toString());
        }


        // Get handle on the register button
        mRegisterButton = (ImageButton) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Quick id check
                if (mStudentIdEntry.getText().toString().length() != 9) {
                    Toast.makeText(getApplicationContext(), "Student ID must be 9 digit number"
                    ,Toast.LENGTH_LONG).show();
                    return;
                }

                // Save student id to file
                try {
                    OutputStreamWriter outputStreamWriter =
                            new OutputStreamWriter(openFileOutput(
                                    "student_id.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(mStudentIdEntry.getText().toString());
                    outputStreamWriter.close();
                } catch (IOException e) {
                    System.out.println("File write failed: " + e.toString());
                }

                // Save student name to file
                try {
                    OutputStreamWriter outputStreamWriter =
                            new OutputStreamWriter(openFileOutput(
                                    "student_name.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(mStudentNameEntry.getText().toString());
                    outputStreamWriter.close();
                } catch (IOException e) {
                    System.out.println("File write failed: " + e.toString());
                }

                // Notify user
                Toast.makeText(getApplicationContext(), "ID: " + mStudentIdEntry.getText().toString() + " Registered", Toast.LENGTH_SHORT).show();

                // Dismiss keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mStudentIdEntry.getWindowToken(), 0);


                // Send student id and token to the backend
                final String studentId = mStudentIdEntry.getText().toString();

                // Read in the token from saved txt as no network access from the main thread
                String token = "";
                try {
                    InputStream inputStream = openFileInput("token.txt");

                    if ( inputStream != null ) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        token = stringBuilder.toString();
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File not found: " + e.toString());
                } catch (IOException e) {
                    System.out.println("Can not read file: " + e.toString());
                }

                // Create connection to send mStudentId & token
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/"
                                    + studentId + "/" + token;
                URL url = null;
                try {
                    url = new URL(endPoint);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.setRequestProperty("Content-Type", "application/json");
                try {
                    conn.setRequestMethod("PUT");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoOutput(true);

                // Send message content.
                final HttpURLConnection finalConn = conn;
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        OutputStream outputStream = null;
                        try {
                            outputStream = finalConn.getOutputStream();
                            outputStream.write("from Android Client".getBytes());
                            outputStream.close();

                            if (finalConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                System.out.println("HTTP_OK");
                            } else {
                                System.out.println("HTTP_NOT_OK");
                            }
                        } catch (IOException e) {
                            System.out.println("Unable to send message." + e);
                        }

                        return null;
                    }

                    protected void onPostExecute() {
                        System.out.println("http call completed");
                    }

                }.execute();

                System.out.println(studentId);
                System.out.println(token);


            }
        });
        /* end -- added by joji */

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
