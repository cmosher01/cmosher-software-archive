import math
from collections import defaultdict



BOT = False
TOP = True

dz = lambda: defaultdict(lambda: 0.0)



def gt(a, b):
  d = a - b
  return d > 0 and not math.isclose(d, 0, abs_tol=1e-6)

def ge(a, b):
  d = a - b
  return d > 0 or math.isclose(d, 0, abs_tol=1e-6)



# Represents a sequence (of video or film) timing.
# For example, NTSC is about 60 fields per second,
# which would be numerator 1, denominator 60:
#   Seq(1,60) # 60 fields per second
# However, NTSC (with the introduction of color) is
# actually a bit lower frame rate (about 59.94),
# which is, completely accurately:
#   Seq(1001,60000) # NTSC 59.9400599400... fps
# Optionally, specify an offset. An offset is given
# as a portion (factor) of the numerator. For example,
# to advance an NTSC sequence by half a field's time:
#   Seq(1001,60000,0.5)
class Seq:
  def __init__(self, num, den, offset_factor=0):
    self.num = num
    self.den = den
    self.offset = offset_factor
    self.cur = 0
    self.top = False

  def len(self):
    return (1+self.offset) * self.num / self.den

  def t(self):
    return self.cur * self.len()

  def t1(self):
    return (self.cur+1) * self.len()

  def hits_next(self, other):
    return gt(self.t1(), other.t1())

  def advance(self):
    self.cur = self.cur + 1
    self.top = not self.top



def p(film, ntsc):
  return max(0, (film.t()-ntsc.t()) / ntsc.len())

def flg(x):
  grph = '*' * round(x*10)
  flag = '>' if ge(x, 0.5) else ' '
  return grph+flag



def create_field(film, ntsc):
  field = dz()
  px = p(film, ntsc)
  if gt(px, 0):
    field[film.cur-1] = px
    field[film.cur] = 1-px
  else:
    field[film.cur] = 1.0
  return field



def pick_match(p,c,n):
  m = max(p,c,n)
  s = ''
  if (ge(p,m)):
    s += 'P'
  if (ge(c,m)):
    s += 'C'
  if (ge(n,m)):
    s += 'N'

  if not ge(m, 1.0):
    s += ' ({:.4f})'.format(m)

  return s

def show_split(fm,n2,f,n,p):
  print('{:4d} -> {:4d}-{}s-> {:4d} : {:4.1f} {}'.format(fm, n2, f, n, p*100, flg(p)))

def dump_field_pcn(top, ifram, field, p, c, n):
  f = 'T' if top else 'B'
  iii = iter(field)
  if len(field) > 1:
    film_cur = max(next(iii),next(iii))
    px = field[film_cur-1]
    cx = field[film_cur]
    show_split(film_cur-1, ifram, f, ifram*2+(1 if top else 0), px)
    # print('-'*41+' {:.3f} {:.3f} {:.3f}   {}'.format(p,c,n,pick_match(p,c,n)))
    print('-'*7+' {:4d}-{} -> {:4d} : ---------------- {:.3f} {:.3f} {:.3f}   {}'.format(ifram, f, ifram*2+(1 if top else 0),p,c,n,pick_match(p,c,n)))
    show_split(film_cur, ifram, f, ifram*2+(1 if top else 0), cx)
  else:
    film_cur = next(iii)
    px = field[film_cur]
    print('{:4d} -> {:4d}-{} -> {:4d} :                  {:.3f} {:.3f} {:.3f}   {}'.format(film_cur, ifram, f, ifram*2+(1 if top else 0), p, c, n, pick_match(p,c,n)))


def match(f, o):
  common_film = f.keys() & o.keys()
  if len(common_film) == 0:
    return 0.0
  common_film = common_film.pop()
  return f[common_film] * o[common_film]

# Super 8mm film rate: Seq(denominator,numerator)
# Trial and error adjusting, to match observed patterns
# from the VHS tape. Somewhere just over 19 fps.
film = Seq(100, 1902)
# standard ntsc field rate: 60000/1001
ntsc = Seq(1001, 60000)
PATTERN_LENGTH = 10000

ntsc_frames = []

print('film -> ntsc-f ->field : film % of field    P     C     N   match')
print('-'*67)
for _ in range(PATTERN_LENGTH):
  field = create_field(film, ntsc)

  if not ntsc.top:
    ntsc_frames.append({})
  ntsc_frames[-1][ntsc.top] = field

  ntsc.advance()
  if ntsc.hits_next(film):
    film.advance()


ntsc_frames.append({})
ntsc_frames[-1][BOT] = {-1: 0.0}
ntsc_frames[-1][TOP] = {-1: 0.0}

# match fields (prev, curr, next)
for i in range(PATTERN_LENGTH//2):
  frame = ntsc_frames[i]
  top = False
  field = frame[top]
  match_p = match(field, ntsc_frames[i-1][not top])
  match_c = match(field, ntsc_frames[i  ][not top])
  match_n = match(field, ntsc_frames[i+1][not top])
  dump_field_pcn(top,i,field,match_p,match_c,match_n)

  top = not top
  field = frame[top]
  match_p = match(field, ntsc_frames[i-1][not top])
  match_c = match(field, ntsc_frames[i  ][not top])
  match_n = match(field, ntsc_frames[i+1][not top])
  dump_field_pcn(top,i,field,match_p,match_c,match_n)
