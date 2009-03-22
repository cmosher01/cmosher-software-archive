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
