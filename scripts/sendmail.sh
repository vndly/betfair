#!/bin/bash

from=$1    #admin@zeronest.com
to=$2      #mauricio.togneri@gmail.com
subject=$3 #This is the subject of the email
content=$4 #This is the content of the email
message=message.txt

echo "From: "$from > $message
echo "To: "$to >> $message
echo "Subject: "$subject >> $message
echo "Content-Type: text/plain; charset=UTF-8" >> $message
echo "" >> $message
echo $content >> $message

/usr/sbin/sendmail $to < $message

rm $message