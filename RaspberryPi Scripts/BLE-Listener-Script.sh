#!/bin/sh

echo $$ > mPid

x-terminal-emulator -e sh minicomListener.sh &

rfcomm listen /dev/rfcomm0

