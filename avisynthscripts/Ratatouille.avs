# DVD ->
#
# DVD Decrypter: extract VTS_06
#
# -> VTS_06_[1-6].VOB ->
#
# DGIndex: video: honor pulldown; audio: demux all tracks (80 AC3 3/2 448); Fix field order transition
#
# -> VTS_06.d2v + "VTS_06 T80 3_2ch 448Kbps DELAY -89ms.ac3" ->
#
# AviSynth script Ratatouille.avs:
#
#
#

LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.5.4\DGDecode.dll")
MPEG2Source("VTS_06.d2v",upConv=1,cpu=6)

#720x480 letterbox

Trim(88,0) # to synch up with audio

LoadPlugin("C:\avisynth3rdpartyplugins\NicAudio_204\NicAudio.dll")
audio = NicAC3Source("VTS_06 T80 3_2ch 448Kbps DELAY -89ms.ac3",channels=2)
AudioDub(audio)

Trim(808,198807) # drop disney and pixar ads

Crop(0,57,0,-61) # remove letterbox
# 720x362

# fix aspect ratio
# existing ratio seems unusual
# use trial and error to judge correct ratio
# stretch horizontally from 720 to 800 pixels
# giving final ratio of 2.21:1 (approx)
LanczosResize(800,362)

# remove 2:3 pulldown telecine
AssumeTFF()
Telecide(guide=1,post=2,vthresh=100.0,back=1,hints=true)
Decimate(mode=3,quality=3)

# fix frame rate
# change from 23.97 FPS to exactly 24 FPS
AssumeFPS(24)
# and stretch audio accordingly
TimeStretch(100.1)

# x264 needs YU12 color
ConvertToYV12()

#
# Ratatouille.avs ->
#
# MeGUI: x264 (Unrestricted 2pass HQ), LAME MP3-128-ABR
#
# -> Ratatouille.mp4 + Ratatouille.mp3 ->
#
# MeGUI: MP4 muxer
#
# -> Ratatouille-muxed.mp4
#
# rm Ratatouille.mp[34]
# mv Ratatouille-muxed.mp4 Ratatouille.mp4
