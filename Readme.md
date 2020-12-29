# Sonect Shop SDK for Android [PRELIMINARY]

In this document we will go through the necessary steps to integrate
Sonect Shop SDK in your Android app. 

Contact support@sonect.ch if additional info is needed.


## Installation: 

### Add jitpack repo as a repository

e.g. in project build file

```Gradle
allprojects {
    repositories {
    	...
        maven { url 'https://jitpack.io' }
    }
}
```

Also if you need to have iDenfy as a KYC provider inside SDK you should provide repositories as well:

 ```Gradle
 allprojects {
     repositories {
     	...
        maven { url "https://dl.bintray.com/idenfy/idenfy" }
     }
 }
 ```

### Add dependency to the SDK

Latest version of SDK: [![](https://jitpack.io/v/sonect/android-shop-sdk.svg)](https://jitpack.io/#sonect/android-shop-sdk)

Add `SDK` to `build.gradle` of your app. SDK could work with both `okhttp3` major versions 3 and 4. They have slightly incompatible changes so you **must** define which one you want to use.

You should

- Exclude one of `okhttp3` or `okhttp4` from dependencies
- Make sure that other lib is connected - both okhttp + loggingInterceptor

Sample of using `okhttp3` major version 3.

```Gradle
dependencies {
	...
    implementation ("com.github.sonect:android-shop-sdk:{latestVersion}") {
        exclude module: "okhttp4"
    }
    // Okhttp must be provided as a separate dependency
    externalImplementation "com.squareup.okhttp3:okhttp:3.14.9"
    externalImplementation "com.squareup.okhttp3:logging-interceptor:3.14.9"
    ...
}
```

If you need iDenfy also include their dependencies:

```Gradle
dependencies {
	...
    //Idenfy
    implementation 'idenfySdk:com.idenfy.idenfySdk:2.6.0'
    implementation 'idenfySdk:com.idenfy.idenfySdk.idenfyliveness:2.6.0'
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
          
            override fun onTermsAccepted() {
              Log.e("!@#","T&C Accepted")
            }

            override fun onShopOnboardingComplete() {
              Log.e("!@#","Shop onboarding completed")
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

### Get callbacks from SDK

By providing `SdkActionsCallback` you're able to get `onTermsAccepted` and `onShopOnboardingComplete` events from SDK.

### Provide system event from outer app

#### Provide back event into SDK

Whole sdk is built on fragments and in order to handle navigation stack properly BackPress event should be provided by wrapping `Activity`.

```kotlin
override fun onBackPressed() {
   var backIsHandled = false
   for (fragment in supportFragmentManager.fragments) {
       if ((fragment as? EntryPointFragment)?.handleBack() == true) {
           // Handle back by ourselves, SDK won't handle it anymore
           backIsHandled = true
       }
   }
   if (!backIsHandled) {
       super.onBackPressed()
   }
}
```



#### Provide Activity result into SDK

Sdk have an interface `ActivityResultStorage` with two methods `getPendingResult` and `addNavigationResult`. `Activity` in which you integrate Shop SDK have to implement this interface and return peding result for the request code. It is needed to support proper navigation in case of `iDenfy` for KYC inside SDK.

Possible simple implementation of the approach:

```kotlin
class MyAwesomeActivity : AppCompatActivity(), ActivityResultStorage {
  private val pendingResults = mutableMapOf<Int, ActivityResult>()
  ...
  override fun getPendingResult(requestCode: Int) = pendingResults.remove(requestCode)
  ...
  override fun addNavigationResult(requestCode: Int, result: ActivityResult) {
    pendingResults[requestCode] = result
  }
  ...
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (couldHanldeByYourself == true) {
          
        } else {
           // Save current result into pending results with proper requestCode
					addNavigationResult(requestCode, ActivityResult(resultCode, data))

          val topFragment = supportFragmentManager.let { it.fragments[it.fragments.size - 1] }
          // Here we call system's onActivityResult method, won't harm any other fragments.
          // Inside SDK we'll call for getPendingResult and handle result by SDK.
          topFragment?.onActivityResult(requestCode, resultCode, data)
          super.onActivityResult(requestCode, resultCode, data) 
        }
    }
  ...
}
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

### Provide shop info from outer app

In order to give info about shop `SonectSDK` has method in builder

```kotlin
fun shop(shop: Shop) = apply {
    this.shop = shop
}
```

`Shop` class has a `Builder` inside which let define Shop field by field.

Provided `Shop` will prefill data during the user onboarding.

### Provide beneficiary info from outer app

In order to give info about beneficiary `SonectSDK` has method in builder

```kotlin
fun beneficiary(beneficiary: Beneficiary) = apply {
    this.beneficiary = beneficiary
}
```

`Beneficiary` class has a `Builder` inside which let define Beneficiary field by field.

Provided `Beneficiary` will prefill data during the user onboarding.

### Compile time config

In order to provide compile time config override resource variables:

```xml
<!-- Show partner reference/operational ID on confirmed receipt -->
<bool name="sonect_should_show_partner_reference">false</bool> 
```

## Proguard / R8

In case you're using proguard or R8 to obfuscate and optimize your code,
the following rules should be enough to maintain all expected functionality.
Please let us know if you find any issues.

```xml
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepnames class androidx.navigation.fragment.NavHostFragment
```

This is needed to maintain json serialization after proguard.
