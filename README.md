# AttachableFloatingMenu

AttachableFloatingMenu (AFM) is an [Android Library][4] written in Kotlin that provides you with a [Floating Action Button (FAB)][1] which when triggered opens up a Menu. The menu itself comprises of other FABs. The AFM attaches itself to an [Android View][3] and only appears when that View is long pressed. The AFM renders more FABs around itself in a circular fashion. The user then slides the finger near the desired FAB to select that option. It is majorly inspired from Pinterest's menu.

![Sample Image][5]

## TODOs
- [] Make AFM compatible with a recycler view. Currently, when AFM is triggered, then any attempt to move the finger across the screen triggers scrolling in the recycler view in the background.
- [] Handle boundary cases when rendering AFM. For example, the Menu should not go out of the screen when triggered near edges or corners.
- [] Think of more types of animations ([see this][2] for examples) and implement them.


<!-- Links -->
[1]: https://material.io/design/components/buttons-floating-action-button.html#

[2]: https://github.com/rjsvieira/floatingMenu?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=5697

[3]: https://developer.android.com/reference/android/view/View

[4]: https://developer.android.com/studio/projects/android-library

[5]: screens/demo.jpg
