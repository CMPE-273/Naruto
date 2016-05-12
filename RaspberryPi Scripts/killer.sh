#!/bin/sh

echo Terminal-Killer

while :
do
	if [ -f ~/Documents/AttendenceProject/devData ]; then
			
		attMarked=$(cat ~/Documents/AttendenceProject/devData)	
		
		if [ "$attMarked" != "" ]; then
			kill $(cat ~/Documents/AttendenceProject/mlPid)	
			rfcomm release /dev/rfcomm0
			#kill $(cat ~/Documents/AttendenceProject/mPid)
		
			x-terminal-emulator -e 	go run /home/cmpe273/Documents/GO-Project/src/AutoAttendence/commitToDatabase.go $attMarked &	
				
			rm ~/Documents/AttendenceProject/devData
			rm ~/Documents/AttendenceProject/mlPid
			rm ~/Documents/AttendenceProject/mPid
				
			break	
		fi	
	fi
done

sh BLE-Listener-Script.sh 
