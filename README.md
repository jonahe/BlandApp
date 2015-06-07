# BlandApp

My first small app for Android. Tested on emulator and Samsung Galaxy S4mini. 

* Mix up to three beverages and see what alcohol content the mix ends up with.
* Get suggestions for how to reach common levels of alcohol content, based on your existing mix of beverages.
* See animated "graphs", ie visual representation of the amounts of the different beverages. Comes in two versions.
* Change the max volume for bevarage nr 1, from 1 liter to 3 litre. (Useful when using large volumes of wine for a punch.)
* App works in both regular and landcape mode, with slighly different layout to make use of screen-width in landscape mode.

The "spinners" and the dialogs makes use of another the **MaterialDesignLibrary** library, available at https://github.com/navasmdc/MaterialDesignLibrary

When turned off, the dialog may flicker on your device if you have Android 4.2.2. This is a known bug, as described here https://github.com/navasmdc/MaterialDesignLibrary/issues/110
