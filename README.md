# Driver-Sdk-Example-App-Android

The Bringg Driver SDK for Android provides a simple native Android api that lets you leverage Bringg platform capabilities into for your app with just a few lines of code.

1. Add the sdk to your project:
dependencies {
    implementation 'com.bringg:driver_sdk:1.3.2'
}

2. Initialize the sdk instance:
DriverSdkProvider.init(applicationContext, foregroundServiceNotificationProvider)

3. Now you can start using the SDK functionality:
DriverSdkProvider.driverSdk()

read our docs at:
https://developers.bringg.com/docs/bringg-driver-sdk-android
