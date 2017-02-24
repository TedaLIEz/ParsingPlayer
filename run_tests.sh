#!/bin/bash
echo "RUN TEST"
./gradlew assembleDebug
retval=$?
if [ $retval -ne 0 ]; then
    echo "error on assembling, exit code: "$retval
    exit $retval
fi
if [ ${TEST} == "android" ]; then
    echo no | android create avd --force --name test --target $ANDROID_TARGET --abi $ANDROID_ABI --sdcard 400M
    emulator -memory 768 -avd test -no-audio -cache-size 100 -netdelay none -netspeed full -no-window &
    android-wait-for-emulator
    adb devices
    adb shell svc power stayon true
    adb shell input keyevent 82
    adb shell settings put global window_animation_scale 0.0 
    adb shell settings put global transition_animation_scale 0.0
    adb shell settings put global animator_duration_scale 0.0
    ./gradlew connectedAndroidTest -PdisablePreDex --stacktrace -i
elif [${TEST} == "unit"]; then
    ./gradlew --stacktrace test
fi
retval=$?
if [ $retval -ne 0 ]; then
    echo "TEST FAILING"
    exit $retval
fi

