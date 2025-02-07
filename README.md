# WarmShowers for Android [![Build Status](https://travis-ci.org/warmshowers/wsandroid.svg?branch=master)](https://travis-ci.org/warmshowers/wsandroid) [![Gitter chat](https://badges.gitter.im/warmshowers-wsandroid/Lobby.png)](https://gitter.im/warmshowers-wsandroid/Lobby)


[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=fi.bitrite.android.ws)

## How may I help the project?

We’re very happy that you are thinking about helping us!
A great starting point would probably be to join us in the [Gitter chat](https://gitter.im/warmshowers-wsandroid/Lobby), but you can also have a look through our [Issues](https://github.com/warmshowers/wsandroid/issues) and especially the ones labelled [Help needed](https://github.com/warmshowers/wsandroid/labels/Help%20needed)

If you find something within the code you’d like to see changed for performance, security, accessibility, data protection, or whatever reason you see fit: Please feel free to fork the project and send us a pull request once you’re done with your changes.

### Things that are always helpful

* More translations (see [#234](https://github.com/warmshowers/wsandroid/issues/234))
  * We currently support English, German, French, and Spanish
  * Currently, we do not use Transifex, POEditor, or other services but as soon as someone would like to help, we’re going to look into it

* Tests of any kind—UI tests, unit tests, integration tests, you name it

## How to build the app

The Warmshowers API now requires an API key. Ask in the
[Gitter chat](https://gitter.im/warmshowers-wsandroid/Lobby) for how you can get
access to one.

Then either edit the variables `wsApiUserId` and `wsApiKey` in `app/build.gradle`
or set the environment variables `WS_API_USER_ID` and `WS_API_KEY`.

Then you are ready to build the app:
```
export WS_API_USER_ID=<your_user_id>
export WS_API_KEY=<your_api_key>
./gradlew build
```

The app runs against Warmshower's development API by default. To switch to the
production servers set the environment variable `WS_USE_PRODUCTION`.


## OSS libraries in use (aka, Thank You!)

Without these libraries, our lives would be a whole lot more difficult. So thank you all for developing and maintaining those fine pieces of software!

* [AssertJ](https://github.com/joel-costigliola/assertj-core)
* [BubbleSeekBar](https://github.com/woxingxiao/BubbleSeekBar)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Dagger 2](https://github.com/google/dagger)
* [Glide](https://bumptech.github.io/glide/)
* [Gson](https://github.com/google/gson)
* [JUnit](https://github.com/junit-team/junit4)
* [Mockito](https://github.com/mockito/mockito)
* [OSMBonusPack](https://github.com/MKergall/osmbonuspack)
* [Osmdroid](https://osmdroid.github.io/osmdroid)
* [Retrofit](https://github.com/square/retrofit)
* [Robolectric](https://github.com/robolectric/robolectric)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxJava 2](https://github.com/ReactiveX/RxJava)
* [SecureKeys](https://github.com/saantiaguilera/android-api-SecureKeys)
