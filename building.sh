#!/bin/bash
echo "ASSEMBLE ANDROIDTEST..."
./gradlew assembleAndroidTest
retval=$?
if [ $retval -ne 0 ]; then
    echo "error on assembleTest, exit code: "$retval
    exit $retval
fi
./gradlew clean
echo "DONE WITH ASSEMBLING ANDROIDTEST"
echo "BUILDING..."
./gradlew build
retval=$?
if [ $retval -ne 0 ]; then
    echo "error on building, exit code: "$retval
    exit $retval
fi
./gradlew clean
echo "DONE WITH BUILDING..."
