#!/bin/dash
#BAKBAS=/adrive/family_photos
BAKBAS=/mnt/Public/family_photos
for F in *
do
	YY=`echo "$F" | cut -b 1-2`
	if [ "$YY" -eq 20 -o "$YY" -eq 19 ]
	then
		YYYY=`echo "$F" | cut -b 1-4`
		MM=`echo "$F" | cut -b 5-6`
		TP=`echo "$F" | cut -b 17-`
		NEF=
		if [ "$TP" = "NEF" ]
		then
			NEF=nef
		fi
		if [ "$YYYY" -lt 2008 ]
		then
			BAKFIL=$BAKBAS/$YYYY/$NEF/$F
		else
			BAKFIL=$BAKBAS/$YYYY/$MM/$NEF/$F
		fi
		mkdir -p `dirname $BAKFIL`
		cp -vn $F $BAKFIL
	fi
done
