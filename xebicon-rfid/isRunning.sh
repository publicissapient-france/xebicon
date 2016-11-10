#!/bin/sh
fileScript=/root/scripts/$1.py
filePid=/var/run/$1.pid

if [ -z $filePid ]
then
  python $fileScript -f 0 -R READER_LIBNFC
fi

PID=$(head -n 1 $filePid)

if kill -s 0 $PID
then
  exit
else
  python $fileScript -f 0 -R READER_LIBNFC
fi
