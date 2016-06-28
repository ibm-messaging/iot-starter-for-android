/*******************************************************************************
 * Copyright (c) 2014-2016 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Mike Robertson - initial contribution
 *    Aldo Eisma - fix occasional stale reference to drawingView
 *******************************************************************************/
package com.ibm.iot.android.iotstarter.fragments;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ibm.iot.android.iotstarter.IoTStarterApplication;
import com.ibm.iot.android.iotstarter.R;
import com.ibm.iot.android.iotstarter.iot.IoTClient;
import com.ibm.iot.android.iotstarter.utils.Constants;
import com.ibm.iot.android.iotstarter.utils.MessageFactory;
import com.ibm.iot.android.iotstarter.utils.MyIoTActionListener;
import com.ibm.iot.android.iotstarter.views.DrawingView;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * The IoT Fragment is the main fragment of the application that will be displayed while the device is connected
 * to IoT. From this fragment, users can send text event messages. Users can also see the number
 * of messages the device has published and received while connected.
 */
public class IoTPagerFragment extends IoTStarterPagerFragment {
    private final static String TAG = IoTPagerFragment.class.getName();

    /**************************************************************************
     * Fragment functions for establishing the fragment
     **************************************************************************/

    public static IoTPagerFragment newInstance() {
        IoTPagerFragment i = new IoTPagerFragment();
        return i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.iot, container, false);
    }

    /**
     * Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        app = (IoTStarterApplication) getActivity().getApplication();
        app.setCurrentRunningActivity(TAG);

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering iotBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for iotBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_IOT));

        // initialise
        initializeIoTActivity();
    }

    /**
     * Called when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        try {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException iae) {
            // Do nothing
        }
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeIoTActivity() {
        Log.d(TAG, ".initializeIoTFragment() entered");

        context = getActivity().getApplicationContext();

        updateViewStrings();

        // setup button listeners
        Button button = (Button) getActivity().findViewById(R.id.sendText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendText();
            }
        });

        DrawingView drawingView = (DrawingView) getActivity().findViewById(R.id.drawing);
        drawingView.setContext(context);
    }

    /**
     * Update strings in the fragment based on IoTStarterApplication values.
     */
    @Override
    void updateViewStrings() {
        Log.d(TAG, ".updateViewStrings() entered");
        // DeviceId should never be null at this point.
        if (app.getDeviceId() != null) {
            ((TextView) getActivity().findViewById(R.id.deviceIDIoT)).setText(app.getDeviceId());
        } else {
            ((TextView) getActivity().findViewById(R.id.deviceIDIoT)).setText("-");
        }

        // Update publish count view.
        processPublishIntent();

        // Update receive count view.
        processReceiveIntent();

        // TODO: Update badge value?
        //int unreadCount = app.getUnreadCount();
        //((MainActivity) getActivity()).updateBadge(getActivity().getActionBar().getTabAt(2), unreadCount);
    }

    /**************************************************************************
     * Functions to handle button presses
     **************************************************************************/

    /**
     * Handle pressing of the send text button. Prompt the user to enter text
     * to send.
     */
    private void handleSendText() {
        Log.d(TAG, ".handleSendText() entered");
        if (app.getConnectionType() != Constants.ConnectionType.QUICKSTART) {
            final EditText input = new EditText(context);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.send_text_title))
                    .setMessage(getResources().getString(R.string.send_text_text))
                    .setView(input)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Editable value = input.getText();
                            String messageData = MessageFactory.getTextMessage(value.toString());
                            try {
                                // create ActionListener to handle message published results
                                MyIoTActionListener listener = new MyIoTActionListener(context, Constants.ActionStateStatus.PUBLISH);
                                IoTClient iotClient = IoTClient.getInstance(context);
                                iotClient.publishEvent(Constants.TEXT_EVENT, "json", messageData, 0, false, listener);

                                int count = app.getPublishCount();
                                app.setPublishCount(++count);

                                String runningActivity = app.getCurrentRunningActivity();
                                if (runningActivity != null && runningActivity.equals(IoTPagerFragment.class.getName())) {
                                    Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                                    actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_PUBLISHED);
                                    context.sendBroadcast(actionIntent);
                                }
                            } catch (MqttException e) {
                                // Publish failed
                            }
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing.
                }
            }).show();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.send_text_title))
                    .setMessage(getResources().getString(R.string.send_text_invalid))
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
    }

    /**************************************************************************
     * Functions to process intent broadcasts from other classes
     **************************************************************************/

    /**
     * Process the incoming intent broadcast.
     * @param intent The intent which was received by the fragment.
     */
    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");

        // No matter the intent, update log button based on app.unreadCount.
        updateViewStrings();

        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.INTENT_DATA_PUBLISHED)) {
            processPublishIntent();
        } else if (data.equals(Constants.INTENT_DATA_RECEIVED)) {
            processReceiveIntent();
        } else if (data.equals(Constants.ACCEL_EVENT)) {
            processAccelEvent();
        } else if (data.equals(Constants.COLOR_EVENT)) {
            Log.d(TAG, "Updating background color");
            DrawingView drawingView = (DrawingView) getActivity().findViewById(R.id.drawing);
            drawingView.setBackgroundColor(app.getColor());
        } else if (data.equals(Constants.ALERT_EVENT)) {
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
    }

    /**
     * Intent data contained INTENT_DATA_PUBLISH
     * Update the published messages view based on app.getPublishCount()
     */
    private void processPublishIntent() {
        Log.v(TAG, ".processPublishIntent() entered");
        String publishedString = this.getString(R.string.messages_published);
        publishedString = publishedString.replace("0",Integer.toString(app.getPublishCount()));
        ((TextView) getActivity().findViewById(R.id.messagesPublishedView)).setText(publishedString);
    }

    /**
     * Intent data contained INTENT_DATA_RECEIVE
     * Update the received messages view based on app.getReceiveCount();
     */
    private void processReceiveIntent() {
        Log.v(TAG, ".processReceiveIntent() entered");
        String receivedString = this.getString(R.string.messages_received);
        receivedString = receivedString.replace("0",Integer.toString(app.getReceiveCount()));
        ((TextView) getActivity().findViewById(R.id.messagesReceivedView)).setText(receivedString);
    }

    /**
     * Update acceleration view strings
     */
    private void processAccelEvent() {
        Log.v(TAG, ".processAccelEvent()");
        float[] accelData = app.getAccelData();
        ((TextView) getActivity().findViewById(R.id.accelX)).setText("x: " + accelData[0]);
        ((TextView) getActivity().findViewById(R.id.accelY)).setText("y: " + accelData[1]);
        ((TextView) getActivity().findViewById(R.id.accelZ)).setText("z: " + accelData[2]);
    }
}
