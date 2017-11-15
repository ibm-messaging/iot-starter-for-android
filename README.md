# IoT Starter for Android
IoT Starter is a demo application for interacting with IBM Watson IoT Platform.
The application turns your mobile device into a sensor that publishes and receives data to and from the cloud using the MQTT protocol.

For the iOS version, refer to [IoT Starter for iOS](https://github.com/ibm-messaging/iot-starter-for-ios)

## Short Description
Watson IoT Platform is a cloud-hosted service to simplify managing all of your IoT devices. The key features provided by the service include:
- Device management
- Scalable connectivity
- Secure communication
- Historical data

This application demonstrates using an Android device as one an IoT device, and provides a variety of events and commands that it can publish or receive data to and from.

Events and commands are user defined values used to differentiate the data that you publish or receive. For example, if you have a device that is publishing GPS coordinates, you may choose to publish it as a 'GPS' event. Or, if you want to send an alert command to a device, you may choose to publish it as an 'alert' or 'notification' command.

The application can publish data to the following event topics:
- Accelerometer (accel event)
- Touchmove (touchmove event)
- Text (text event)

The application can receive data on the following command topics:
- Color (color command)
- Light (light command)
- Text (text command)
- Alert (alert command)

For more information on Watson IoT Platform, refer to https://docs.internetofthings.ibmcloud.com/index.html.

## How it works
A device that is registered with Watson IoT Platform may publish and subscribe to data that is presented as either an event or command using the MQTT protocol.
The Eclipse Paho MQTT Android Service is used to publish and subscribe to Watson IoT Platform. This can be downloaded from
[Eclipse Paho MQTT Android Service](http://www.eclipse.org/paho/clients/android/).

MQTT is a lightweight messaging protocol that supports publish/subscribe messaging. With MQTT, an application publishes messages to a topic. These messages may then be received by another application that is subscribed to that topic. This allows for a detached messaging network where the subscribers and publishers do not need to be aware of each other.
The topics used by this application can be seen in the table below:

## Topics
|Topic|Sample Topic|Sample Messages|
|:---------- |:---------- |:------------|
|`iot-2/evt/<eventId>/fmt/json`|`iot-2/evt/touchmove/fmt/json`|`{"d":{"screenX":0,"screenY":0,"deltaX":0,"deltaY":0}}`|
|`iot-2/cmd/<commandId>/fmt/json`|`iot-2/cmd/light/fmt/json`|`{"d":{"light":"toggle"}}`<br/>`{"d":{"light":"on"}}`<br/>`{"d":{"light":"off"}}`|

For more information on the MQTT protocol, see http://mqtt.org/.

## Try it
The IoT Starter application can be used in two ways.

### 1. Connect to the  Quickstart service of Watson IoT Platform
In order to connect to Quickstart, all you need to do is specify 'quickstart' as the organization and enter a valid device id. The Auth Token field can be left blank. The device-id is a 12 hexadecimal character MAC address in lower case, without delimiting characters. For example, a36d7c91bf9e. For more details, refer to the [MQTT Connectivity for Devices](https://docs.internetofthings.ibmcloud.com/devices/mqtt.html).

Once the app is connected, you can see the data visualized by going to [Quickstart](https://quickstart.internetofthings.ibmcloud.com/) and entering the same device-id in the MAC address field.

### 2. Connect to an IoT organization as a registered device
In order to try the application as a registered device, you must have a Watson IoT Platform organization. This can be done by signing up for an IBM Cloud trial and creating an instance of the Internet of Things Platform service. This will create an  organization where you can register devices. Next, you must register your device with your organization. When registering your device, create a new device type called `Android` (case sensitive). More detailed instructions on registering devices can be found in the [Watson IoT Platform documentation](https://docs.internetofthings.ibmcloud.com/index.html).

On launching the application for the first time, you will need to enter your credentials to connect your device to Watson IoT Platform. The required information to connect your device includes:

- Your Organization ID, e.g. ab1cde
- Your Device ID, e.g. the MAC Address of your device. This should be the same ID as the device that you registered in your Watson IoT Platform organization.
- Your device authorization token. This is returned when registering your device with Watson IoT Platform.

Once you have entered the necessary credentials, you may activate your device as a sensor. Pressing the 'Activate Sensor' button will connect the device to Watson IoT Platform and allow it to begin publishing and receiving data.

## Prerequisites
Required:
- An [IBM Cloud](https://console.ng.bluemix.net/) account. A 30 day trial account is free.
- An Internet of Things Platform service registered in IBM Cloud.
- An Android SDK installation

## Installation
1. `git clone https://github.com/ibm-messaging/iot-starter-for-android.git`
2. Launch Android Studio and select "Open an Existing Android Studio Project".
3. Navigate to your `iot-starter-for-android` folder and open the project.
4. Run the application.

## Notes
In order to really see this demo do something, you must have an application to consume its data and publish data back
to the IoT Starter application. For examples, refer to the [IoT Starter demo](http://m2m.demos.ibm.com/iotstarter.html).

The IoT Starter application can also be used in combination with the [Context Mapping and Driver Behavior tutorial] (https://github.com/IBM-Bluemix/car-data-management).
The Accelerometer events include the GPS tracking properties required for the context mapping and driver behavior services.  

## Resources
- [IoT Starter for iOS](https://github.com/ibm-messaging/iot-starter-for-ios)
- [IBM Watson IoT Platform](https://internetofthings.ibmcloud.com/#/)
- [IBM Cloud](https://console.ng.bluemix.net/)
- [IoT Recipes](https://developer.ibm.com/iot/)
- [Quickstart](http://quickstart.internetofthings.ibmcloud.com/#/)
- [Node-RED](http://nodered.org/)
- [MQTT](http://mqtt.org/)
