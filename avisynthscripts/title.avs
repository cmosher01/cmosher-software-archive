function ttl(string "text")
{
	x = BlankClip(length=90,width=720,height=486,pixel_type="YUY2",fps=18,audio_rate=0)
	x = x.Subtitle(text,first_frame=0,last_frame=89,align=5,font="Windsor LT",size=48,text_color=$FFFFFF,spc=17)
	x = FadeIn(x,18,$0,18)
	x = FadeOut(x,18,$0,18)
	x = x+BlankClip(length=18,width=720,height=486,pixel_type="YUY2",fps=18,audio_rate=0)
	return x
}
