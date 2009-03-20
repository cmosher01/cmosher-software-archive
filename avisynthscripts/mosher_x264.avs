OpenDMLSource("\\backy\public\home_movies\1967_1978\reel1.avi",fourcc="YUY2")+\
OpenDMLSource("\\backy\public\home_movies\1967_1978\reel2.avi",fourcc="YUY2")+\
OpenDMLSource("\\backy\public\home_movies\1967_1978\reel3.avi",fourcc="YUY2")


LoadPlugin("C:\avisynth3rdpartyplugins\avisynth_c\avisynth_c-0.15\avisynth_c.dll")
LoadCPlugin("C:\msys\1.0\home\Administrator\uyvy2yuy2\uyvy2yuy2.dll")
ConvertUYVYtoYUY2()

AssumeFPS(18)
r1 = Trim(0,31897)
r2 = Trim(31898,58485)
r3 = Trim(58486,87019)
r3 = r3.FlipHorizontal()

last = r1+r2+r3

#LoadPlugin("C:\avisynth3rdpartyplugins\avsmonitor\Release\avsmon25a.dll")
#MonitorFilter()


LoadPlugin("C:\avisynth3rdpartyplugins\degrainmedian\degrainmedian-0.8.2\degrainmedian.dll")
DeGrainMedian(limitY=6, limitUV=6, mode=2, interlaced=false, norow=false)

LoadPlugin("C:\avisynth3rdpartyplugins\depan\depan-1.10\depan.dll")
#######DePanEstimate(log="depan.log")
DePanStabilize(data=last,inputlog="depan.log",dymax=60,dxmax=90,info=false)


#ConvertToYV12()
#LoadPlugin("c:\avisynth3rdpartyplugins\despot\despot-3.6.0\despot.dll")
#DeSpot(p2=12, mthres=14, p1=24, pwidth=100, pheight=100, dilate=4, color=true, motpn=true, sign=1, maxpts=1000)

#after = last

#dif = Subtract(after,bef).Levels(127, 1, 129, 0, 255)
#blk = Blackness(dif,pixel_type="YUY2")
#top = StackHorizontal(bef,after)
#bot = StackHorizontal(dif,blk)
#StackVertical(top,bot)


#return last

#reel 1
f01 = Trim(    2, 3659) #3658
f02 = Trim( 3666, 7297) #3632
f03 = Trim( 7334,10455) #3122*
f04 = Trim(10481,14135) #3655
f05 = Trim(14139,17479) #3341*
f06 = Trim(17524,21116) #3593
f07 = Trim(21138,24749) #3612
f08 = Trim(24750,28285) #3536
f09 = Trim(28294,31895) #3602
#reel 2
f10 = Trim(31909,35510) #3602
f11 = Trim(35540,39141) #3602
f12 = Trim(39147,41981) #2835*
#(a few leader frames here were omitted during transfer)
f13 = Trim(41983,44521) #2539*
#(~170 black frames here were omitted during transfer)
f14 = Trim(44525,47924) #3400*
f15 = Trim(47933,51459) #3527
f16 = Trim(51489,54857) #3369*
f17 = Trim(54926,58485) #3560
#(10 or 20 frames at end here were omitted during transfer)
#reel 3
f18 = Trim(58486,62082) #3597
f19 = Trim(62086,65672) #3587
f20 = Trim(65706,69310) #3605
f21 = Trim(69313,72865) #3553
f22 = Trim(72903,76526) #3624
f23 = Trim(76557,79729) #3173*
f24 = Trim(79761,83371) #3611
f25 = Trim(83402,87019) #3618

#* = short films (less than 3500 frames out of 3600)
#(most are short because the missing frames were not on the original films)




toofast = f13.Trim(243,475)
toofast = toofast.SelectEvery(1,0,0)
toofast = toofast.AssumeFPS(18)
f13 = f13.Trim(0,242)+toofast+f13.Trim(476,2538)

toofast2 = f20.Trim(0,305)
toofast2 = toofast2.SelectEvery(1,0,0)
toofast2 = toofast2.AssumeFPS(18)
f20 = toofast2+f20.Trim(306,3604)

f01_01 = f01.Trim(0,351)
f01_01 = f01_01.ColorYUV(gain_u=-17,gain_v=7)

f01_02 = f01.Trim(1438,1882)
f01_02 = f01_02.Levels(0,1.2,255,0,255,false)
f01_02 = f01_02.Tweak(cont=1.2)

f01_03 = f01.Trim(2330,3525)
f01_03 = f01_03.ColorYUV(gain_u=-15,gain_v=4)
f01_03 = f01_03.Tweak(sat=.9)

f01_04 = f01.Trim(3526,3657)
f01_04 = f01_04.ColorYUV(gain_u=-20,gain_v=10)
f01_04 = f01_04.Tweak(sat=.9)
f01_04 = f01_04.Levels(0,1.1,255,0,255,false)

f01 = f01_01+f01.Trim(352,1437)+f01_02+f01.Trim(1883,2329)+f01_03+f01_04

f03_01 = f03.Trim(0,243)
f03_01 = f03_01.ColorYUV(gain_u=-17,gain_v=8)

f03_02 = f03.Trim(825,978)
f03_02 = f03_02.ColorYUV(gain_u=-17,gain_v=8)

f03_03 = f03.Trim(1164,2438)
f03_03 = f03_03.ColorYUV(gain_u=-20,gain_v=10)

f03_04 = f03.Trim(2439,2757)
f03_04 = f03_04.ColorYUV(gain_u=-45,gain_v=23)
f03_04 = f03_04.Tweak(bright=-15)

f03_05 = f03.Trim(2758,3121)
f03_05 = f03_05.ColorYUV(gain_u=-20,gain_v=10)

f03 = f03_01+f03.Trim(244,824)+f03_02+f03.Trim(979,1163)+f03_03+f03_04+f03_05

f04_01 = f04.Trim(0,707)
f04_01 = f04_01.ColorYUV(gain_u=-17,gain_v=8)

f04_02 = f04.Trim(919,2559)
f04_02 = f04_02.ColorYUV(gain_u=-20,gain_v=10)

f05_01 = f05.Trim(0,2003)
f05_01 = f05_01.ColorYUV(gain_u=-17,gain_v=8)
f05_01 = f05_01.Levels(0,1.2,255,0,255,false)
f05_01 = f05_01.Tweak(sat=.9)

f07 = f07.ColorYUV(gain_u=-17,gain_v=8)

#f08: very dirty

f09_01 = f09.Trim(0,1045)
f09_01 = f09_01.ColorYUV(gain_u=-20,gain_v=6)
f09_01 = f09_01.Tweak(sat=1.1)

f09_02 = f09.Trim(1046,2203)
f09_02 = f09_02.ColorYUV(gain_u=-15,gain_v=7)

f09_03 = f09.Trim(2204,2227)
f09_03 = f09_03.ColorYUV(gain_u=-10,gain_v=6)
f09_03 = f09_03.Tweak(bright=-10,cont=1,sat=1.1)
f09_03 = f09_03.Levels(0,.8,255,0,255,false)

f09_04 = f09.Trim(2228,2402)
f09_04 = f09_04.Tweak(bright=-12,cont=1.4,sat=1.1)
f09_04 = f09_04.Levels(0,.75,255,0,255,false)

f09_05 = f09.Trim(2403,2651)
f09_05 = f09_05.ColorYUV(gain_u=-10,gain_v=6)
f09_05 = f09_05.Tweak(bright=9,cont=1.6,sat=1.1)

f09_06 = f09.Trim(2652,3601)
f09_06 = f09_06.ColorYUV(gain_u=-10)
f09_06 = f09_06.Tweak(bright=9,cont=1.3,sat=1.3)

f09 = f09_01+f09_02+f09_03+f09_04+f09_05+f09_06

f10_01 = f10.Trim(2607,3276)
f10_01 = f10_01.ColorYUV(gain_u=-15,gain_v=7)

f10_02 = f10.Trim(3277,3601)
f10_02 = f10_02.Levels(0,1.2,255,0,255,false)
f10_02 = f10_02.Tweak(cont=1.4,sat=1.3)

f10 = f10.Trim(0,2605)+f10_01+f10_02

f11_01 = f11.Trim(3380,3601)
f11_01 = f11_01.Levels(0,.7,255,0,255,false)
f11_01 = f11_01.Tweak(bright=-25,cont=2.6,sat=2.0)
f11_01 = f11_01.ColorYUV(gain_v=9,gain_u=15)

f11 = f11.Trim(0,3379)+f11_01

f12_01 = f12.Trim(0,1166)
f12_01 = f12_01.ColorYUV(gain_u=-20,gain_v=4)
f12_01 = f12_01.Levels(0,1.1,255,0,210,false)
f12_01 = f12_01.Tweak(cont=1.3,sat=1.3)

f12_02 = f12.Trim(1167,1755)
f12_02 = f12_02.ColorYUV(gain_u=-20,gain_v=10)
f12_02 = f12_02.Levels(0,.9,255,0,255,false)
f12_02 = f12_02.Tweak(cont=1.2,sat=1.2)

f12_03 = f12.Trim(1770,2834)
f12_03 = f12_03.ColorYUV(gain_u=-17,gain_v=5)
f12_03 = f12_03.Tweak(sat=1.3)

f12 = f12_01+f12_02+f12_03

f13_01 = f13.Trim(0,242)
f13_01 = f13_01.ColorYUV(gain_u=0,gain_v=-4)
f13_01 = f13_01.Tweak(cont=1.2,sat=1.3)

f13_02 = f13.Trim(243,482)
f13_02 = f13_02.Tweak(cont=1.2,sat=1.3)

f13_03 = f13.Trim(483,708)
f13_03 = f13_03.Tweak(cont=1.2,sat=1.3)

f13_04 = f13.Trim(709,1716)
f13_04 = f13_04.ColorYUV(gain_u=-15,gain_v=8)
f13_04 = f13_04.Tweak(sat=1.3)

f13_05 = f13.Trim(1717,2771)
f13_05 = f13_05.ColorYUV(gain_u=-15,gain_v=7)
f13_05 = f13_05.Tweak(cont=1.2,sat=1.2)

f13 = f13_01+f13_02+f13_03+f13_04+f13_05

f14_01 = f14.Trim(0,1241)
f14_01 = f14_01.ColorYUV(gain_u=-20,gain_v=10)

f14_02 = f14.Trim(1242,2731)
f14_02 = f14_02.ColorYUV(gain_u=-24,gain_v=10)
f14_02 = f14_02.Tweak(bright=10,cont=1.2,sat=1.2)

f14_03 = f14.Trim(2732,3399)
f14_03 = f14_03.ColorYUV(gain_u=-38,gain_v=20)
f14_03 = f14_03.Tweak(cont=1.2,sat=1.3)
f14_03 = f14_03.Levels(0,.8,255,0,240,false)

f15 = f15.ColorYUV(gain_u=-15,gain_v=7)

f17_01 = f17.Trim(0,758)
f17_01 = f17_01.ColorYUV(gain_u=-6,gain_v=3)
f17_02 = f17.Trim(759,1066)
f17_02 = f17_02.ColorYUV(gain_u=-30,gain_v=25)
f17_02 = f17_02.Tweak(cont=1.4,sat=1.2)
f17_03 = f17.Trim(1067,2231)
f17_03 = f17_03.ColorYUV(gain_u=-6,gain_v=3)
f17_04 = f17.Trim(2232,2471)
f17_04 = f17_04.ColorYUV(gain_u=-30,gain_v=25)
f17_04 = f17_04.Tweak(cont=1.4,sat=1.2)
f17_05 = f17.Trim(2472,3559)
f17_05 = f17_05.ColorYUV(gain_u=-6,gain_v=3)

f17 = f17_01+f17_02+f17_03+f17_04+f17_05

f18 = f18.ColorYUV(gain_u=-10,gain_v=5)

f19 = f19.ColorYUV(gain_u=-20,gain_v=8)
f19 = f19.Tweak(sat=1.1)

f22_01 = f22.Trim(0,2125)

f22_02 = f22.Trim(2126,2828)
f22_02 = f22_02.ColorYUV(gain_u=-28,gain_v=14)
f22_02 = f22_02.Tweak(cont=1.2)

f22_03 = f22.Trim(2829,3623)
f22_03 = f22_03.ColorYUV(gain_u=-6,gain_v=3)

f22 = f22_01+f22_02+f22_03

f24_01 = f24.Trim(0,1803)
f24_01 = f24_01.ColorYUV(gain_u=-20,gain_v=10)

#unfixable over-exposure = f24.Trim(1804,1848)

f24_012 = f24.Trim(1849,3089)

f24_02 = f24.Trim(3090,3158)
f24_02 = f24_02.Tweak(bright=-1200,cont=8.2,sat=8.0)
f24_02 = f24_02.ColorYUV(gain_u=30,gain_v=-35)

f24_03 = f24.Trim(3159,3216)
f24_03 = f24_03.Tweak(bright=-550,cont=5.1,sat=5.5)
f24_03 = f24_03.ColorYUV(gain_u=30,gain_v=-35)

f24_04 = f24.Trim(3217,3610)
f24_04 = f24_04.ColorYUV(gain_u=-20,gain_v=10)

f24 = f24_01+f24_012+f24_02+f24_03+f24_04

f25_01 = f25.Trim(0,2679)
#black = f25.Trim(2680,2727)
f25_03 = f25.Trim(2728,3617)

f25 = f25_01+f25_03




#vhs (4 missing films recovered from vhs tape)
fvA = OpenDMLSource("vhs\result\a.avi")
fvA = fvA.ConvertToYUY2()
fvA = fvA.AddBorders(0,3,0,3)
fvB = OpenDMLSource("vhs\result\b.avi")
fvB = fvB.ConvertToYUY2()
fvB = fvB.AddBorders(0,3,0,3)
fvC = OpenDMLSource("vhs\result\c.avi")
fvC = fvC.ConvertToYUY2()
fvC = fvC.AddBorders(0,3,0,3)
fvD = OpenDMLSource("vhs\result\d.avi")
fvD = fvD.ConvertToYUY2()
fvD = fvD.AddBorders(0,3,0,3)


#show subtitles to debug while I'm arranging them
#f01 = f01.Subtitle("01",align=4)
#f02 = f02.Subtitle("02",align=4)
#f03 = f03.Subtitle("03",align=4)
#f04 = f04.Subtitle("04",align=4)
#f05 = f05.Subtitle("05",align=4)
#f06 = f06.Subtitle("06",align=4)
#f07 = f07.Subtitle("07",align=4)
#f08 = f08.Subtitle("08",align=4)
#f09 = f09.Subtitle("09",align=4)
#f10 = f10.Subtitle("10",align=4)
#f11 = f11.Subtitle("11",align=4)
#f12 = f12.Subtitle("12",align=4)
#f13 = f13.Subtitle("13",align=4)
#f14 = f14.Subtitle("14",align=4)
#f15 = f15.Subtitle("15",align=4)
#f16 = f16.Subtitle("16",align=4)
#f17 = f17.Subtitle("17",align=4)
#f18 = f18.Subtitle("18",align=4)
#f19 = f19.Subtitle("19",align=4)
#f20 = f20.Subtitle("20",align=4)
#f21 = f21.Subtitle("21",align=4)
#f22 = f22.Subtitle("22",align=4)
#f23 = f23.Subtitle("23",align=4)
#f24 = f24.Subtitle("24",align=4)
#f25 = f25.Subtitle("25",align=4)
#fvA = fvA.Subtitle(" A",align=4)
#fvB = fvB.Subtitle(" B",align=4)
#fvC = fvC.Subtitle(" C",align=4)
#fvD = fvD.Subtitle(" D",align=4)

function subt(clip c, int "frame", string "text")
{
	return c.Subtitle(text,first_frame=frame,last_frame=frame+54,x=640,y=428,align=3,font="Windsor LT",size=18,spc=8,text_color=$FFFFFF)
}

f01 = f01.subt(18,"July 1967")
f01 = f01.subt(90,"Cardinal Dr., Pulaski, VA")
f01 = f01.subt(2348,"Virginia Beach")
f01 = f01.subt(3544,"100 Cambridge Dr., Windsor Hills, Wilmington, DE")

fvB = fvB.subt(18,"fall 1967")
fvB = fvB.subt(284,"49 N. Main St., Earlville, NY")
fvB = fvB.subt(1337,"Windsor Hills")
fvB = fvB.subt(1733,"Pennsylvania roadside")
fvB = fvB.subt(2305,"Windsor Hills")
fvB = fvB.subt(2624,"Prices Corner park")
fvB = fvB.subt(2793,"Windsor Hills")

f02 = f02.subt(18,"Feb. 1968")
f02 = f02.subt(1056,"Apr. 1968")
f02 = f02.subt(1200,"Campbells' house, Fairfax, VA")
f02 = f02.subt(2108,"Windsor Hills")

f03 = f03.subt(18,"May 1968")
f03 = f03.subt(843,"July 1968")

f04 = f04.subt(937,"Aug. 1968")
f04 = f04.subt(1049,"Ocean City, MD?")
f04 = f04.subt(2578,"Windsor Hills")
f04 = f04.subt(3258,"Sept. 1968")

fvC = fvC.subt(452,"Oct. 1968")
fvC = fvC.subt(2553,"Nov. 1968")

fvA = fvA.subt(157,"Dec. 1968")
fvA = fvA.subt(2249,"Feb. 1969")

fvD = fvD.subt(478,"Mar. 1969")
fvD = fvD.subt(929,"Apr. 1969")
fvD = fvD.subt(2546,"May 1969")
fvD = fvD.subt(3319,"June 1969")

f17 = f17.subt(18,"July 1969")
f17 = f17.subt(2676,"Graylyn Crest pool")
f17 = f17.subt(3057,"Windsor Hills")

f10 = f10.subt(8,"Graylyn Crest pool")
f10 = f10.subt(1309,"Windsor Hills")
f10 = f10.subt(1640,"Aug. 1969")
f10 = f10.subt(2624,"Ocean City, MD?")
f10 = f10.subt(2808,"spring 1970")
f10 = f10.subt(2880,"Philadelphia Zoo")

f05 = f05.subt(18,"Apr. 1971")
f05 = f05.subt(90,"Falmouth, MA")
f05 = f05.subt(2022,"May 1971")
f05 = f05.subt(2094,"1303 Faulk Rd., Wilmington, DE")

f18 = f18.subt(18,"Windsor Hills")
f18 = f18.subt(2146,"Hagley, Brandywine River")
f18 = f18.subt(3116,"July 1971")
f18 = f18.subt(3186,"Stapler Park, Wilmington, DE")

f19 = f19.subt(18,"Windsor Hills")
f19 = f19.subt(2115,"Sept. 1971")
f19 = f19.subt(2187,"Chittenango Falls, Cazenovia, NY")

f21 = f21.subt(18,"Dec. 1971")
f21 = f21.subt(90,"Windsor Hills")

f07 = f07.subt(18,"July 1972")
f07 = f07.subt(90,"Ocean City, MD")
f07 = f07.subt(3397,"Windsor Hills")

f12 = f12.subt(18,"Oct. 1972")
f12 = f12.subt(90,"43 Shelburne Dr., Wilmington, DE")

f06 = f06.subt(18,"Nov. 1972")
f06 = f06.subt(2181,"spring 1973")

f24 = f24.subt(18,"summer 1973")
f24 = f24.subt(90,"Ocean City, MD")

f11 = f11.subt(18,"Shelburne")
f11 = f11.subt(2491,"Timber Lane pool")

f20 = f20.subt(18,"Shelburne")
f20 = f20.subt(90,"fall 1973")
f20 = f20.subt(1697,"Dec. 1973")
f20 = f20.subt(3555,"winter 1974")

f13 = f13.subt(261,"spring 1974")
f13 = f13.subt(727,"fall 1974")
f13 = f13.subt(799,"3417 Huckabay Cir., Raleigh, NC")
f13 = f13.subt(1725,"Dec. 1974")

f14 = f14.subt(1260,"Easter 1975")
f14 = f14.subt(1332,"Florida")

f25 = f25.subt(18,"winter 1976")
f25 = f25.subt(90,"1211 Brook Dr., Normandy Manor, Wilmington, DE")

f15 = f15.subt(18,"summer 1976")
f15 = f15.subt(90,"Norbee Dr., Normandy Manor")
f15 = f15.subt(3378,"Magic Age of Steam, Yorklyn, DE")

f23 = f23.subt(2115,"Normandy Manor")
f23 = f23.subt(2451,"Sterling Gardens, Middletown, NY")

f22 = f22.subt(18,"summer 1977")
f22 = f22.subt(90,"Normandy Manor")

f16 = f16.subt(18,"fall 1977")

f08 = f08.subt(18,"winter 1978")
f08 = f08.subt(705,"summer 1978")
f08 = f08.subt(777,"Valley Forge")
f08 = f08.subt(2104,"Normandy Manor")
f08 = f08.subt(3033,"fall 1978")



function bounce(clip c, int "frame")
{
	f = c.Trim(frame,frame)
	f = f.CropBottom(1)
	x = BlankClip(length=1,width=720,height=1,pixel_type="YUY2",fps=18,audio_rate=0)
	return c.Trim(0,frame-1)+StackVertical(x,f)+c.Trim(frame+1,100000000)
}

function toss(clip c, int "frame")
{
	f = c.Trim(frame,frame)
	f = f.Crop(0,1,0,0)
	x = BlankClip(length=1,width=720,height=1,pixel_type="YUY2",fps=18,audio_rate=0)
	return c.Trim(0,frame-1)+StackVertical(f,x)+c.Trim(frame+1,100000000)
}

function ttl(string "text")
{
	x = BlankClip(length=90,width=720,height=486,pixel_type="YUY2",fps=18,audio_rate=0)
	x = x.Subtitle(text,first_frame=0,last_frame=89,align=5,font="Windsor LT",size=48,text_color=$FFFFFF,spc=17)
	x = FadeIn(x,18,$0,18)
	x = FadeOut(x,18,$0,18)
	x = x+BlankClip(length=18,width=720,height=486,pixel_type="YUY2",fps=18,audio_rate=0)
#	x = bounce(x,9)
#	x = toss(x,11)
#	x = toss(x,18)
#	x = bounce(x,22)
#	x = bounce(x,29)
#	x = toss(x,34)
#	x = bounce(x,40)
#	x = toss(x,44)
#	x = toss(x,49)
#	x = bounce(x,51)
#	x = bounce(x,63)
#	x = toss(x,66)
#	x = bounce(x,68)
#	x = toss(x,75)
#	x = bounce(x,80)
#	x = toss(x,83)
	return x
}

#title = \
#ttl("Mosher Home Movies")+ \
#ttl("Starring")+ \
#ttl("Christopher Mosher")+ \
#ttl("Nancy Mosher")+ \
#ttl("Casey Rapp")



#last = title+f01+fvB+f02+f03+f04+fvC+fvA+fvD+f17+f10+f05+f18+f19+f21+f07+f12+f06+f09+f24+f11+f20+f13+f14+f25+f15+f23+f22+f16+f08
last = f01+fvB+f02+f03+f04+fvC+fvA+fvD+f17+f10+f05+f18+f19+f21+f07+f12+f06+f09+f24+f11+f20+f13+f14+f25+f15+f23+f22+f16+f08

#a sample clip: Trim(1400,1700)

#just to see it with the real aspect ratio
#LanczosResize(640,480,taps=4)


#to convert to widescreen 16:9 (assuming 1:1 PAR)
#LanczosResize(1920,1440,taps=4)
#Crop(0,180,0,-180)
#Info()

#Import("C:\avisynth3rdpartyplugins\limitedsharpen\limitedsharpenbeta.avsi")
#LimitedSharpen(soft=75,strength=70,overshoot=0)

#last = f08
#AddBorders(0,1,0,1)
#ConvertToYV12()
#bef = last
##LoadPlugin("c:\avisynth3rdpartyplugins\despot\despot.dll")
##DeSpot(p2=12, mthres=24, p1=30, pwidth=10000, pheight=10000, dilate=5, color=false, motpn=true, sign=1)
#LoadPlugin("C:\avisynth3rdpartyplugins\removedirt\removedirt.dll")
#input = last
#LoadPlugin("C:\avisynth3rdpartyplugins\deen\deen-beta-2\deen.dll")
#Deen("c3d",mode=0,thrY=50,thrUV=12,ti=1000)
#after = last
#difex = Subtract(after,bef).Levels(127, 1, 129, 0, 255)
#dif = Subtract(after,bef)
#top = StackHorizontal(bef,after)
#bot = StackHorizontal(dif,difex)
#StackVertical(top,bot)
#KillAudio()





#Info()

#ConvertToYV12()
#bef = last
#LoadPlugin("c:\avisynth3rdpartyplugins\despot\despot.dll")
#DeSpot(p1=20, pwidth=100, pheight=100, dilate=4, color=true)

#LoadPlugin("C:\avisynth3rdpartyplugins\fluxsmooth-1-1-a\fluxsmooth.dll")
#FluxSmoothST(7,7)
#Import("C:\avisynth3rdpartyplugins\limitedsharpen\limitedsharpenbeta.avsi")
#LimitedSharpen(soft=75,strength=90,overshoot=0)

#after = last

#dif = Subtract(after,bef).Levels(127, 1, 129, 0, 255)
#blk = Blackness(dif,pixel_type="YV12")
#top = StackHorizontal(bef,after)
#bot = StackHorizontal(dif,blk)
#StackVertical(top,bot)
#return last




# A B C --> A A B B C (18 progressive frames/second --> 30 progressive frames/second)
#SelectEvery(3,0,0,1,1,2)
# Assuming the original film is exactly 18 fps, then we now
# have exactly 30 fps. We can just call it 29.97 fps.
# We don't have any audio to synch up with, so there's no
# reason to drop 1 frame every 1000 to achieve 29.97
#AssumeFPS(29.97002997)

#Info()

#finally, get ready for x264 encoding (1:1 PAR, YV12)
Crop(0,3,0,-3)
LanczosResize(640,480,taps=4)
ConvertToYV12()

return last
