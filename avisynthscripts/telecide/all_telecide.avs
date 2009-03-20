# To show all possible field combinations:
allp = last
allc = last
alln = last
mine = last
LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\decomb\decomb-5.2.2\Decomb.dll")
allp = allp.Telecide(guide=0,post=0,y0=470,y1=480,show=true,debug=false,ovr="all_p.ovr")
allc = allc.Telecide(guide=0,post=0,y0=470,y1=480,show=true,debug=false,ovr="all_c.ovr")
alln = alln.Telecide(guide=0,post=0,y0=470,y1=480,show=true,debug=false,ovr="all_n.ovr")
mine = mine.Telecide(guide=0,post=0,y0=470,y1=480,show=true,debug=false,ovr="vhs1_b_whole.ovr")
top = StackHorizontal(allc,mine)
bot = StackHorizontal(allp,alln)
StackVertical(top,bot)
