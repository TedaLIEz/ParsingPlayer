#!/bin/bash
echo "RUN TEST"
./gradlew assembleDebug
retval=$?
if [ $retval -ne 0 ]; then
    echo "error on assembling, exit code: "$retval
    exit $retval
fi
if [ ${TEST} == "android" ]; then
    echo no | android create avd --force --name test --target $ANDROID_TARGET --abi $ANDROID_ABI --sdcard 800M
    emulator -memory 4000 -avd test -no-audio -cache-size 400 -netdelay none -netspeed full -no-window &
    android-wait-for-emulator
    adb devices
    adb shell svc power stayon true
    adb sleep 10
    adb shell input keyevent 82
    adb sleep 5
    adb shell settings put global window_animation_scale 0.0 
    adb shell settings put global transition_animation_scale 0.0
    adb shell settings put global animator_duration_scale 0.0
    travis_wait 30 ./gradlew connectedAndroidTest -PdisablePreDex --stacktrace -i
elif [${TEST} == "unit"]; then
    ./gradlew --stacktrace test
fi
retval=$?
if [ $retval -ne 0 ]; then
    echo "TEST FAILING"
    exit $retval
fi

