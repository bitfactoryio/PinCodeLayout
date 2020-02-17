# PinCodeLayout

A customizable AndroidX pincode library for Android API 16+.

Includes animations as well as active and inactive states.


## Installation

You can include this library, by adding following code to your build.gradle(app) file:

``` Groovy

dependencies {
	implementation "com.github.bitfactoryio:PinCodeLayout:1.0.2"
}

```

Also make sure to include the following line in your project build.gradle file:

``` Groovy

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

```

Add the PinCodeLayout in your xml file:

``` xml

<io.bitfactory.pincodelayout.PinCodeLayout
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	app:pinLength="6" />

```
>
## Preview

<p><img src="static/pin_hidden.gif" width="40%" />
<img src="screenshots/pin_visible.gif" width="40%" /></p>

## Usage

### Changing Pin Length
You can set your desired pin length in the xml via
``` xml
	app:pinLength="4"
```

### Changing colors of the bottom Bar indicator:
Set up in your xml:
``` xml
	app:activeBarColor="@color/white"
	app:inactiveBarColor="@color/black"
```
or programmatically: 

``` Kotlin
	pinCodeLayout.setActiveBarColor(android.R.color.transparent)
	pinCodeLayout.setInActiveBarColor(android.R.color.transparent)
```
If you choose, not to show the bottom bar indicator, just set its color to transparent.

### Changing Pin Icon
``` xml
	app:unfilledPinIcon="@drawable/ic_dot_empty"
	app:filledPinIcon="@drawable/ic_dot_filled"
```
``` Kotlin
	pinCodeLayout.setUnfilledPinIcon(R.drawable.ic_dot_empty)
	pinCodeLayout.setFilledPinIcon(R.drawable.ic_dot_filled)
```
### Hiding pin
You can choose, if you want your users to be able to see their input, or not.
If not, the unfilledPinIcon will be visible.

``` xml
	app:hidePin="true"
```
``` Kotlin
	pinCodeLayout.setHiddenState(true)
```

### Changing Animation duration
By setting a Long value (milliseconds)

``` xml
	app:animationDuration="1000"
```
``` Kotlin
	pinCodeLayout.setAnimationDuration(750L)
```

### Changing Pin Type
You can choose one of the following types:
``` xml
	app:pinType="digits"
	app:pinType="capLettersAndDigits"
```
The keyboard will automatically show and accept only valid characters.

### Changing Pin Text Color
``` xml
	app:pinTextColor="@color/black"
```
``` Kotlin
	pinCodeLayout.setPinTextColor(R.color.black)
```
### Changing Pin Layout Background
You can choose your own background. This can be a simple color or a drawable file

``` xml
	app:inputBackground="@color/colorPrimary"
```
``` kotlin
	pinCodeLayout.setInputBackground(android.R.color.transparent)
```

