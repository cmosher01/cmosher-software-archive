# LOAD VIDEO HERE

orig = last

# PROCESS VIDEO HERE



# show the differences:

dif = orig.Subtract(last)
dif = dif.Levels(127, 1, 129, 0, 255)

top = StackHorizontal(orig,last)
bot = StackHorizontal(orig,dif)

StackVertical(top,bot)
