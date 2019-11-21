# VolleyBall
This library wraps the HTTP library 'Volley' from Google. This is not finished yet, I do not recommend using it in this state.


## Advantages
- Includes automatic parsing for classes and arrays of classes
- Provides simple logic for caching
- Automatically uses the cache if no connection is available instead of waiting for a timout
- Can also fall back to a local resource file within the APK if no cache is available

## Disadvantages
- Expects you to use JSON for requests and responses

## Requirements
minSdkVersion 8 (same as Volley)

## Dependencies
- https://github.com/google/volley
- https://github.com/google/gson
- https://developer.android.com/studio/write/annotations.html
