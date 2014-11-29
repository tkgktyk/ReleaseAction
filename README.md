# Release Action
* * *
# Release-To-Action User Interface for Android
This is an Android Layout Library provides Release-To-Action after pulling view.
User interface is similar to [chrisbanes/Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh).
The mechanism is similar to [SwipeRefreshLayout](https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html).
And the implementation is based on [FlyingLayout](https://github.com/tkgktyk/FlyingLayout).

You just wrap a target View or Layout in **ReleaseActionLayout** and setup listeners and indicator views.
Then it'll fire actions when you release your finger after pulling the target view.

## Features
 * Supports eight directions.
 	* four orthogonal directions and their combinations (diagonal).
 * Action is cancelable unlike SwipeRefreshLayout.
 * Customizable indicator view.
 	* Minimum indicator views are prepared by **SimpleReleaseActionLayout**.
 * Only one change your layout hierarchy: wraps a target View in **ReleaseActionLayout**.
 * Works with views having correctly **View#canScrollHorizontally/Vertically**.
	* Or make custom layout overrides **ReleaseActionLayout#canChildScrollHorizontally/Vertically**.
 * Example app includes:
	* **ListView**
	* **ViewPager**
	* **WebView**
 * Strange Features:
 	* When one direction indicator appears by swipe, you can also use another direction-actions by continuous swiping like a gesture.
 	* and more...

Repository at <https://github.com/tkgktyk/ReleaseAction>.

## Introduction
See [Quick Start Guide](https://github.com/tkgktyk/ReleaseAction/wiki/Quick-Start-Guide).

Get the example application in this repository on Google Play;:
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=jp.tkgktyk.releaseactionexample2)
