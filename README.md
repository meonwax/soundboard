# Soundboard

[![Build Status](https://travis-ci.org/meonwax/soundboard.svg?branch=master)](https://travis-ci.org/meonwax/soundboard)

Play short sound samples on your Android device.

## Usage

Select a sound file from your storage directory and add it to the sounds list. It will be copied to the private application folder and can be played on touch.

You can delete the original file at any time. The sound sample will still be available in the application until you decide to delete it.

## Download

There are several ways of getting a compiled binary version of Soundboard:

* [![F-Droid](https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Get_it_on_F-Droid.svg/160px-Get_it_on_F-Droid.svg.png)](https://f-droid.org/repository/browse/?fdid=de.meonwax.soundboard "Soundboard on F-Droid")

* Download a bleeding edge snapshot build from my [private repository](https://meonwax.de/soundboard/snapshots). As these snapshots are always compiled from the repository HEAD, they could be unstable or filled with bugs. The snapshots packages are signed with a *debug key* which can change occasionally. Be aware of that.

* Grab an APK from [releases on GitHub](https://github.com/meonwax/soundboard/releases). These are compiled by me from the same sources as the F-Droid releases and should be stable.

## Building

If you want to build the application yourself, the easiest way is to use the latest [Android Studio](http://developer.android.com/sdk/index.html) along with the Android SDK.

Alternatively you can use the gradle wrapper to build the application.

You will at least need to install the following SDK packages:

* Latest versions of Android SDK Tools and Android SDK Platform-tools
* Android SDK Build-tools 23.0.2 or later
* Android SDK Platform 23 or later
* Android Support Library 23.1.1 or later

Clone the repository:

    git clone https://github.com/meonwax/soundboard.git

Switch to application directory and make the gradle wrapper executable:

    cd soundboard
    chmod +x gradlew

To build a *debug* version, run:

    ./gradlew assembleDebug

To build a *release* version, you first have to alter the settings for accessing your keystore in the `app/gradle.properties` file.
Then run:

    ./gradlew assembleRelease

After a successful build, the (signed) APKs will be located in `app/build/outputs/apk`.

## Resources

[Launcher icon](https://www.iconfinder.com/icons/916730/music_sound_voice_volume_icon) by Mohamed Mbarki licensed under [Creative Commons (Attribution 3.0 Unported)](http://creativecommons.org/licenses/by/3.0/).

## License

[GPL3](LICENSE)
