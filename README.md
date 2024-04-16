# Material-Button-Loading
<p align="center">
<a href="https://github.com/hall9zeha/MaterialButtonLoading/actions"><img alt="Build Status" src="https://github.com/hall9zeha/MaterialButtonLoading/workflows/MaterialButtonLoading/badge.svg"/></a> 
<a href="https://jitpack.io/#hall9zeha/MaterialButtonLoading"><img alt="License" src="https://jitpack.io/v/hall9zeha/MaterialButtonLoading.svg"/></a>
</p>

Android library of button loading for sign in

## Requirements

* Android compile SDK 34
* Android min SDK 23 

## Implementation

### How to use

* First add the jitpack source in settings.gradle. In gradle 7 or latest
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' } //here
    }
}
```

* In gradle kts
```
allprojects {
    repositories {
        google()
        mavenCentral()

        maven ( url = "https://jitpack.io" )//here

    }
}

```

* In earlier versions of gradle 7, gradle project level
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()

        maven { url "https://jitpack.io" }//here

    }
}
```

* Then add the dependency at application gradle level.

```gradle
dependencies{
    ...
    implementation 'com.github.hall9zeha:MaterialButtonLoading:1.0.0'
}
```
* in gradle kts
```gradle
dependencies{
    ...
    implementation ("com.github.hall9zeha:MaterialButtonLoading:1.0.0")
}
```
### Properties

| Property          | Type      | Example   |Default value|
|--------------------|------------|-------------------------|-------------------------------------------------|
| text               | string     | "@string/.."       | "Button"|
| textColor          | string     | "#FF0000"               |system text button color|
| textSize           | dimension  | "16sp"                  |14sp|
| strokeWidth        | dimension/reference | "2dp" or "@dimen/.."|1dp|
| allCaps            | boolean    | "true" or "false" |false|
| cornerRadius       | dimension/reference |"8dp" or "@dimen/.."| 24dp |
| colorStroke        | string/reference | "#00FF00" or "@color/.."|system color primary|
| colorBackground    | string/reference | "#0000FF" or "@color/.."|system color primary|
| colorRipple        | string/reference | "#FF00FF" or "@color/.."|system color primary|
| progressColor      | string/reference| "#FFFF00" or "@color/.."|system textColorPrimaryInverseNoDisable|
| progressType       | enum       | "circular" or "dots" |circular|
| loading            | boolean    | "true" or "false"       |false|
| enabled            | boolean    | "true" or "false"       |true|
| styleButton        | enum       | "normal" or "outline" or "text"|normal|
### Xml design
* Example implementation
```xml
...

 <com.barryzeha.materialbuttonloading.components.ButtonLoading
        android:id="@+id/btnLoading"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:text="Log In"
        app:cornerRadius="32dp"
        app:loading="false"
        app:progressType="circular"
        app:styleButton="normal"
        android:layout_marginTop="16dp"
        app:layout_constraintStart_toStartOf="@id/tilPasswd"
        app:layout_constraintEnd_toEndOf="@id/tilPasswd"
        app:layout_constraintTop_toBottomOf="@id/tilPasswd"
        />
...
```

* To show the loader, when we perform an action for example loging into a service:
```java
     val buttonLoading = findViewById<ButtonLoading>(R.id.btnLoading)

     buttonLoading.setLoading(true)

```
* And when we finish it we stop the action:

```java

     buttonLoading.setLoading(false)

```

## Screenshoots
||||
|--|--|--|
| |||
|<img src="https://github.com/hall9zeha/MaterialButtonLoading/blob/main/docs/screenshots/screen1.gif" width=80% height=80% />|<img src="https://github.com/hall9zeha/MaterialButtonLoading/blob/main/docs/screenshots/screen2.gif" width=80% height=80% />|<img src="https://github.com/hall9zeha/MaterialButtonLoading/blob/main/docs/screenshots/screen3.gif" width=80% height=80% />|

## License

