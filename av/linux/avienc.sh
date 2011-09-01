#!/bin/bash
AVI=$1

#RES=1280x720
#FPS=24
RES=640x480
FPS=30

# extract audio from AVI into audiodump.wav
mplayer -ao pcm -vc null -vo null $AVI
# encode audiodump.wav using FAAC to autodump.aac
faac --mpeg-vers 4 audiodump.wav

# mkfifo for YUV
mkfifo tmp.fifo.yuv
# stream AVI as YUV thru fifo
mencoder -vf format=i420 -nosound -ovc raw -of rawvideo -ofps $FPS -o tmp.fifo.yuv $AVI &
# encode from fifo into vid.x264
x264 -o vid.x264 --fps $FPS --bframes 2 --crf 26 --subme 6 --analyse p8x8,b8x8,i4x4,p4x4 tmp.fifo.yuv $RES
# rm fifo
rm tmp.fifo.yuv

# combine vid.x264 and audiodump.aac into mp4
MP4=${AVI/AVI/mp4}
MP4Box -add vid.x264 -add audiodump.aac -fps $FPS $MP4
