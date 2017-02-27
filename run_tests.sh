#!/bin/bash
echo "TESTING..."
if [ ${TEST} == "android" ]; then
    ./gradlew --stacktrace :sample:connectedAndroidTest
elif [${TEST} == "unit"]; then
    ./gradlew --stacktrace test
fi
retval=$?
if [ $retval -ne 0 ]; then
   echo "TEST FAILED: " $retval
   exit $retval
fi
echo "TEST DONE"

