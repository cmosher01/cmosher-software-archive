# A B C --> A A B B C (18 progressive frames/second --> 30 progressive frames/second)
SelectEvery(3,0,0,1,1,2)

# If there is no audio, just call it 29.97:
AssumeFPS(29.97002997)
# Otherwise, if there is audio, drop 1 frame every 1000 (and drop audio)
SelectRangeEvery(1001,1000)
