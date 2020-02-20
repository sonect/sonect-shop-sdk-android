# Sonect Shop SDK for Android [PRELIMINARY]

In this document we will go through the necessary steps to integrate
Sonect Shop SDK in your Android app. 

Contact support@sonect.ch if additional info is needed.


## Installation: 

### Add jitpack repo as a repository

e.g. in project build file

```groovy
allprojects {
    repositories {
    	...
        maven { url 'https://jitpack.io' }
    }
}
```

### Add dependency to the SDK

Latest version of SDK: [![](https://jitpack.io/v/sonect/android-shop-sdk.svg)](https://jitpack.io/#sonect/android-shop-sdk)

Add to `build.gradle` of your app

```groovy
dependencies {
	...
    implementation ('com.github.sonect:android-shop-sdk:{latestVersion}) {
        exclude group: "idenfySdk"
        exclude group: "io.anyline"
    }
    ...
}
```

## SDK Integration 

To start `SDK` you need to create `SonectSDK` with provided `Config`. `Config` is created via `Builder`.

```kotlin
    val builder: SonectSDK.Config.Builder = SonectSDK.Config.Builder()
    val configBuilder = builder
        .enviroment(SonectSDK.Config.Enviroment.DEV) // Prod by default
        .userCredentials(
            SonectSDK.Config.UserCredentials(
                "Merchant ID obtained from Sonect",
                "Token SDK obtained from Sonect"
            )
        )
        .sdkCallbacks(object : SdkActionsCallback { // Callbacks from SDK
            override fun onSdkLastFragmentClosed() {
                finish() // E.g. when SDK closed we want to close the app itself
            }
        })
    val doWeWantToUseScanditForScanning = true

    if (doWeWantToUseScanditForScanning) {
        configBuilder.customScanditKey("Your Scandit Key obtained from Sonect")
    } else {
        configBuilder.customScannerFragment(CustomScannerFragment()) // Provide scanner fragment
    }

    
    val config = configBuilder.build()
    val sonectSDK = SonectSDK(this, config)

    supportFragmentManager.beginTransaction()
        .replace(R.id.container, sonectSDK.getStartFragment()) // Start SDK fragment
        .addToBackStack(null).commit()
```

### Custom barcode scanner 

If you want to provide your own scanning experience, you could override SDK's scanner (Scandit).

You have to override `ScannerFragment` from SDK. It has a `listener` inside which need to be called when scanning performed.
 
```kotlin
class CustomScannerFragment: ScannerFragment() { // ScannerFragment from SDK
    override fun getLayoutId(): Int = R.layout.fragment_custom_scanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnScannerComplete.setOnClickListener {
            listener.onScan("123456")
        }
    }
}
```
