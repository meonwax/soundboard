# Soundboard
[![Build Status](https://travis-ci.org/meonwax/soundboard.svg?branch=master)](https://travis-ci.org/meonwax/soundboard)

Play short sound samples on your Android device.

## Building

As no binary package currently is available, you have to build the application by yourself.
The easiest way is to use the lastest [Android Studio](http://developer.android.com/sdk/index.html) along with the Android SDK.

Alternatively you can use the gradle wrapper to build the application.

You will at least need to install the following SDK packages:

* Latest versions of Android SDK Tools and Android SDK Platform-tools
* Android SDK Build-Tools 23.0.2 or later
* Android SDK Platform 23 or later
* Android Support Library 23.1.1 or later

Clone the repository:

    git clone https://github.com/meonwax/soundboard.git

Switch to application directory and make the gradle wrapper executable:

    cd soundboard
    chmod +x gradlew

To build a debug version, run:

    ./gradlew assembleDebug

To build a release version, run :

    ./gradlew assembleRelease

After a successful build, the APKs will be located in `app/build/outputs/apk`.

## Resources

[Launcher icon](https://www.iconfinder.com/icons/916730/music_sound_voice_volume_icon) by DevDesign Gmbh licensed under [Creative Commons (Attribution 3.0 Unported)](http://creativecommons.org/licenses/by/3.0/).

## License

[GPL3](LICENSE)
