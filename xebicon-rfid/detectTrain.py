#!/usr/bin/python

#  detectTrain.py - get UID of card and MAC address of RPi and send it to URL
#
#  from Adam Laurie <adam@algroup.co.uk>
#  http://rfidiot.org/
#
#  This code is copyright (c) Adam Laurie, 2006, All rights reserved.
#  For non-commercial use only, the following terms apply - for all other
#  uses, please contact the author:
#
#    This code is free software; you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation; either version 2 of the License, or
#    (at your option) any later version.
#
#    This code is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#  to use it (reader ACR122U) : python detectTrain.py -f 0 -R READER_LIBNFC
#	-f 0 : number ID* of reader
#	-R READER_LIBNFC : type of reader
#	* : you can find the number ID with -d args (debug)


import rfidiot
import sys
import os
import requests
import json
import uuid
import re
import datetime
import time

URL = "http://172.16.1.101/detection"
#URL = "http://private-anon-f2afd5d3fa-keynoterfid.apiary-mock.com"
MAC = ':'.join(re.findall('..', '%012x' % uuid.getnode()))
DEBUG = 1

PID = str(os.getpid())
FILEPID = open("/var/run/detectTrain.pid", "w")
FILEPID.write(PID)
FILEPID.close()

SEGID = ""
RFID = ""
POSITION = ""

with open("/root/scripts/json-id.json") as json_file :
	json_data = json.load(json_file)

for segment_num in json_data['segment']:
	if segment_num['mac'].lower() == MAC.lower() :
		SEGID = segment_num['id']

while True:
	try:
	    card= rfidiot.card
	except:
		print "Couldn't open reader!"
   		os._exit(True)

	try:
		card.select()
	except:
		print "Couldn't open reader!"
   		os._exit(True)

	UID = str(card.uid).strip()
	if UID :
		for train_num in json_data['train']:
			if train_num['rfid'].lower() == UID.lower() :
				RFID = train_num['id']
				POSITION = train_num['position']

		if DEBUG :
			FILEDBG = open("/var/log/detectTrain.log", "a")
			FILEDBG.write(str(datetime.datetime.now()) + "\n")
			FILEDBG.write("MAC : " + str(MAC) + "\t")
			FILEDBG.write("UID : " + str(UID) + "\t")
			FILEDBG.write("id : " + str(SEGID) + "\t")
			FILEDBG.write("train : " + str(RFID) + "\t")
			FILEDBG.write("position : " + str(POSITION) + "\n")
			FILEDBG.write("------END-----\n")
			FILEDBG.close()

		try:
			r = requests.post(URL, json={'id':SEGID, 'train':RFID, 'position':POSITION})
		except:
			print "Server unreachable!"

		time.sleep(1)
