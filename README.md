# VolleyBall
This library wraps the HTTP library 'Volley' from Google. The library is not well tested yet and subject to breaking changes. Use at own risk in this stage.


## Advantages
- Includes automatic parsing for classes and arrays of classes
- Provides easy to use logic for caching
- Automatically uses the cache if no connection is available instead of waiting for a timout
- Can also fall back to a local resource file within the APK if no cache is available

## Disadvantages
- Only parses JSON responses

## Requirements
minSdkVersion 8 (same as Volley)

## Usage
You can add the library to your project via _jitpack_ by adding the repository to your root gradle-file
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
And the dependency to your module
```
dependencies {
    ...
    implementation 'com.github.floriandootz:volleyball:0.2'
}
```

## Dependencies
- https://github.com/google/volley
- https://github.com/google/gson
- https://developer.android.com/studio/write/annotations.html
