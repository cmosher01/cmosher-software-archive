LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.5.4\DGDecode.dll")
MPEG2Source("VTS_01.d2v")

audio = WAVSource("VTS_01 Ta0 48K 16bit 2ch.wav")
AudioDub(audio)

DoubleWeave()
Pulldown(0,2)

AssumeFPS(24)
TimeStretch(100.1)

LanczosResize(852,480,taps=4)
