OpenDMLSource("..\..\original\vhs_cd_720_480_dvsd.avi",audio=false,fourCC="CDVC",pixel_type="YUY2")
AssumeFrameBased()
AssumeBFF() # bottom field first is unusual, but is correct here
KillAudio() # there is no audio


Trim(575,5619) # film C (NOTE: about 400 frames missing from VHS tape)



# fix problems due to converting DV 4:1:1 to 4:2:0
LoadPlugin("C:\avisynth3rdpartyplugins\reinterpolate411\reinterpolate411.dll")
Reinterpolate411()

# I went frame by frame through the whole video to find the pattern of
# interlacing, and defined it in vhs1_a_whole.ovr. Telecide will read that
# file (which covers the whole film) to generate the resulting ~3600 frames
# as they existed in the original film. This is of course not exactly true,
# because VHS tape quality and original transfer process were so bad. There
# are therefore several interlacing artifacts, as well as a few bad spots
# on the tape. But this is the best we can do.
LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\decomb\decomb-5.2.3\Decomb.dll")
Telecide(guide=0,post=0,y0=470,y1=480,hints=false,show=false,debug=false,ovr="vhs2_c.ovr")

# For multidecimate, we run first with pass=1, to generate mfile.txt.
# Then run multidecimate.exe to read mfile.txt and create cfile.txt and dfile.txt.
# Then run pass=2 to read dfile.txt and eliminate duplicate frames (introduced
# by the previous Telecide step).
LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\multidecimate-107\multidecimate.dll")
MultiDecimate(pass=2)

# Now we have the original film frames (mostly), which are supposed to be 18 fps.
AssumeFPS(18)



# Clean up any remaining interlacing artifacts
FieldDeinterlace(full=true,dthreshold=7)



# Run the first pass with DePanEstimate to make log file.
# Edit the log file (careful not to overwrite it later) to remove
# places it gets wrong (that is, places that should have motion and
# shouldn't be compensated for), or edit places that need tweaking.
# Then run the next pass(es) using DePanStabilize(inputlog)
LoadPlugin("C:\avisynth3rdpartyplugins\depan\depan-1.10\depan.dll")
#DePanEstimate(log="vhs2_c_depan.log")
DePanStabilize(data=last,inputlog="vhs2_c_depan.log",dymax=60,dxmax=90,info=false)



# This does a really nice job of removing dirt.
ConvertToYV12()
a = Trim(0,130)
b = Trim(131,223)
c = Trim(224,3207)
LoadPlugin("c:\avisynth3rdpartyplugins\despot\despot-3.6\despot.dll")
a = a.DeSpot(p1=22, pwidth=100, pheight=100, dilate=4, color=true)
c = c.DeSpot(p1=22, pwidth=100, pheight=100, dilate=4, color=true)
last = a+b+c

# slight noise cleaning
LoadPlugin("C:\avisynth3rdpartyplugins\fluxsmooth\fluxsmooth-1.1a\fluxsmooth.dll")
FluxSmoothST(7,7)

# bump up saturation slightly
Tweak(sat=1.3)



LoadPlugin("C:\avisynth3rdpartyplugins\removegrain\removegrain-1.0\RemoveGrainSSE2.dll")
LoadPlugin("C:\avisynth3rdpartyplugins\masktools\masktools-2.0a36\mt_masktools-26.dll")
Import("C:\avisynth3rdpartyplugins\masktools\masktools-2.0a36\masktools\LimitedSharpenFaster.avsi")
LimitedSharpenFaster(soft=75,strength=70,overshoot=0)
