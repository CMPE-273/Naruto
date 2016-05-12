#!/bin/sh

echo Terminal - Minicom Listener 
echo $$ >  mlPid

x-terminal-emulator -e sh killer.sh &

while :
do
	if  [ -c /dev/rfcomm0 ]; then
		minicom -D /dev/rfcomm0 -C devData
	fi
done
