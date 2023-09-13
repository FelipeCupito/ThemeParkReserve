#!/bin/bash

HOST=localhost:50051

function runClient() {
    client="$1"
    action="$2"
    clientShort="${client::5}"
    shift 2
    echo
    echo "###### Running action \"$action\" on client \"$client\" ($clientShort-cli) ######"
    if [ -z "$buildPath" ]; then
        bin="mvn -q compile exec:java -pl client -Dexec.mainClass=ar.edu.itba.pod.client.$client.Client"
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
    echo "2af16ea7-4af1-47f6-bf46-8515de5a500f;HALFDAY;15"
)
runClient admin rides -DinPath=<(
    echo "name;hoursFrom;hoursTo;slotGap"
    echo "SpaceMountain;09:00;22:00;30"
    echo "TronLightcycle;10:00;22:00;15"
)
runClient admin slots -DrideName="SpaceMountain" -DdayOfYear=100 -Damount=30

runClient book availability -Dday=100 -Dslot=15:30 -Dattraction="SpaceMountain"
runClient book availability -Dday=100 -Dslot=15:30 -DslotTo=16:00 -Dattraction="SpaceMountain"
runClient book availability -Dday=100 -Dslot=15:30 -DslotTo=16:00
runClient book attractions
runClient book book -Dday=100 -Dvisitor="$visitor" -Dattraction="SpaceMountain" -Dslot=15:30
runClient book confirm -Dday=100 -Dvisitor="$visitor" -Dattraction="SpaceMountain" -Dslot=15:30
runClient book cancel -Dday=100 -Dvisitor="$visitor" -Dattraction="SpaceMountain" -Dslot=15:30

runClient notification follow -Dvisitor="$visitor" -Dday=100 -Dattraction="SpaceMountain" &
subscription=$!

sleep 1
runClient notification unfollow -Dvisitor="$visitor" -Dday=100 -Dattraction="SpaceMountain"

wait $subscription

runClient query capacity -Dday=4 -DoutPath="/dev/stdout"
runClient query confirmed -Dday=4 -DoutPath="/dev/stdout"
