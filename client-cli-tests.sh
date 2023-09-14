#!/bin/bash

HOST=localhost:50051

if [ -z "$buildPath" ]; then
    mvn compile
fi

function runClient() {
    client="$1"
    action="$2"
    clientShort="${client::5}"
    shift 2
    echo
    echo "###### Running action \"$action\" on client \"$client\" ($clientShort-cli) ######"
    if [ -z "$buildPath" ]; then
        bin="mvn -q exec:java -pl client -Dexec.mainClass=ar.edu.itba.pod.client.$client.Client"
    else
        bin="$buildPath/$clientShort-cli"
    fi
    set -x
    $bin -DserverAddress="$HOST" -Daction="$action" "$@"
    set +x
}
visitor="ca286ef0-162a-42fd-b9ea-60166ff0a593" 

runClient admin tickets -DinPath=<(
    echo "visitorId;passType;dayOfYear"
    echo "$visitor;UNLIMITED;100"
    echo "$visitor;UNLIMITED;101"
    echo "2af16ea7-4af1-47f6-bf46-8515de5a500f;HALFDAY;15"
)
runClient admin rides -DinPath=<(
    echo "name;hoursFrom;hoursTo;slotGap"
    echo "SpaceMountain;09:00;22:00;30"
    echo "TronLightcycle;10:00;22:00;15"
)
runClient admin slots -Dride="SpaceMountain" -Dday=100 -Dcapacity=30

runClient book availability -Dday=100 -Dslot=15:30 -Dride="SpaceMountain"
runClient book availability -Dday=100 -Dslot=15:30 -DslotTo=16:00 -Dride="SpaceMountain"
runClient book availability -Dday=100 -Dslot=15:30 -DslotTo=16:00
runClient book attractions
runClient book book -Dday=101 -Dvisitor="$visitor" -Dride="SpaceMountain" -Dslot=15:30
runClient book cancel -Dday=101 -Dvisitor="$visitor" -Dride="SpaceMountain" -Dslot=15:30
runClient book book -Dday=100 -Dvisitor="$visitor" -Dride="SpaceMountain" -Dslot=15:30
runClient book book -Dday=100 -Dvisitor="$visitor" -Dride="TronLightcycle" -Dslot=16:00

runClient notification follow -Dvisitor="$visitor" -Dday=100 -Dride="TronLightcycle" &
subscription=$!

sleep 1
runClient notification unfollow -Dvisitor="$visitor" -Dday=100 -Dride="TronLightcycle"

wait $subscription

runClient notification follow -Dvisitor="$visitor" -Dday=100 -Dride="TronLightcycle" &
subscription=$!

sleep 1
runClient admin slots -Dride="TronLightcycle" -Dday=100 -Dcapacity=30

wait $subscription


runClient query capacity -Dday=100 -DoutPath="/dev/stdout"
runClient query confirmed -Dday=100 -DoutPath="/dev/stdout"
