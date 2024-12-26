# Android Chat App

Android Chat App is a sample Android project that demonstrates how to use the PieSocket WebSocket SDK for real-time communication. The app allows users to connect to PieSocket channels, send messages, and receive real-time updates.

### Features

Connect to PieSocket WebSocket channels

Send and receive messages in real-time

Display connection status and error handling

Built with Jetpack Compose for modern UI development

### Prerequisites

Before running this project, ensure you have the following:

Android Studio: Installed and updated to the latest version.

PieSocket Account: Create an account at PieSocket and set up your project.

API Key and Cluster ID: Obtain these from your PieSocket dashboard.

### Dependencies

Add the following dependencies to your build.gradle file:

dependencies {
    implementation("com.piesocket:channels-sdk:1.0.5")
}

Setup Instructions

Clone the repository:
```
git clone https://github.com/piehostHQ/android-chat-app.git
cd ChatAndroid
```
Open the project in Android Studio.

Add your PieSocket API Key and Cluster ID in the connectToPieSocket function inside MainActivity.kt:
```
val options = PieSocketOptions().apply {
    this.apiKey = "your_api_key"
    this.clusterId = "your_cluster_id"
}
```
Enable Internet permission in AndroidManifest.xml:
```
<uses-permission android:name="android.permission.INTERNET" />
```
Sync your project to ensure dependencies are resolved.

Run the project on an emulator or a physical device.

### Usage

Enter your API key in the input field.

Click on Connect to join a default PieSocket channel.

Use the message input field to send messages.

View real-time messages received from the channel.
