#!/bin/sh
filePid=/var/run/detectTrain.pid

if [ -z $filePid ]
then
  exit
fi

PID=$(head -n 1 $filePid)
PPID=$(ps -o ppid= -p $PID)

if kill -s 0 $PID
then
  if [ $PPID -ne 1 ]
  then
    echo $PID
    echo $PPID
  else
    echo $PID
  fi
else
  exit
fi
