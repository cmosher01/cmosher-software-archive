#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "nibblize_5_3_alt.h"

#include <stdint.h>
#include <string.h>
#include <stdlib.h>

#include "assert_that.h"
#include "nibblize_5_3_common.h"

static void nibblize(const uint8_t **pdata, uint8_t **encoded)
{
  const uint8_t *data = *pdata;
  uint8_t *enc = *encoded;
  int i;
  int base = 0;
  for (i = 0; i < GRP; ++i)
    {
      enc[base + i]  = (data[base    + i] & 0x07) << 2;
      enc[base + i] |= (data[3 * GRP + i] & 0x04) >> 1;
      enc[base + i] |= (data[4 * GRP + i] & 0x04) >> 2;
    }
  base += GRP;
  for (i = 0; i < GRP; ++i)
    {
      enc[base + i]  = (data[base    + i] & 0x07) << 2;
      enc[base + i] |= (data[3 * GRP + i] & 0x02);
      enc[base + i] |= (data[4 * GRP + i] & 0x02) >> 1;
    }
  base += GRP;
  for (i = 0; i < GRP; ++i)
    {
      enc[base + i]  = (data[base    + i] & 0x07) << 2;
      enc[base + i] |= (data[3 * GRP + i] & 0x01) << 1;
      enc[base + i] |= (data[4 * GRP + i] & 0x01);
    }
  base += GRP;
  enc[base] = 0;
  ++base;
  for (i = 0; i < 5 * GRP; ++i)
    {
      enc[base + i] = data[i] >> 3;
    }
  base += 5 * GRP;
  enc[base] = data[5 * GRP] & 0x1F; /* throw out high 3 bits */

  (*pdata) += 0x100;
}

static void flipBuf2(uint8_t *enc)
{
  int i;
  int sw = BUF2_SIZ;
  for (i = 0; i < BUF2_SIZ / 2; ++i)
    {
      int tmp;
      --sw;
      tmp = enc[i];
      enc[i] = enc[sw];
      enc[sw] = tmp;
    }
}

static void xorBuf(uint8_t **encoded)
{
  uint8_t *enc = *encoded;
  int i;
  enc[BUF1_SIZ+BUF2_SIZ] = 0;
  for (i = BUF1_SIZ+BUF2_SIZ; i > 0; --i)
    {
      enc[i] ^= enc[i - 1];
    }
}

static void xlateBuf(uint8_t **encoded)
{
  int i;
  for (i = 0; i < BUF1_SIZ+BUF2_SIZ+1; ++i)
    {
      **encoded = xlate[**encoded];
      (*encoded)++;
    }
}

void nibblize_5_3_alt_encode(const uint8_t **original, uint8_t **encoded)
{
  nibblize(original, encoded);
  flipBuf2(*encoded);
  xorBuf(encoded);
  xlateBuf(encoded);
}


static void ulateBuf(uint8_t *enc)
{
  int i;
  for (i = 0; i < BUF1_SIZ+BUF2_SIZ+1; ++i)
    {
      enc[i] = ulate[enc[i]];
    }
}

static void unxorBuf(uint8_t *enc)
{
  int i;
  for (i = 1; i < BUF1_SIZ+BUF2_SIZ+1; ++i)
    {
      enc[i] ^= enc[i - 1];
    }
}

static void denibblize(uint8_t *buf, uint8_t **decoded)
{
  uint8_t *data = *decoded;
  int bufbase = 3 * GRP + 1;
  int base;
  int i;

  for (i = 0; i < 5 * GRP; ++i)
    {
      data[i] = buf[bufbase + i] << 3;
    }
  data[5 * GRP] = buf[bufbase + 5 * GRP];

  base = 0;
  for (i = 0; i < GRP; ++i)
    {
      data[base + i] |= buf[base + i] >> 2;
      data[3 * GRP + i] |= (buf[base + i] & 0x02) << 1;
      data[4 * GRP + i] |= (buf[base + i] & 0x01) << 2;
    }

  base += GRP;
  for (i = 0; i < GRP; ++i)
    {
      data[base + i] |= buf[base + i] >> 2;
      data[3 * GRP + i] |= (buf[base + i] & 0x02);
      data[4 * GRP + i] |= (buf[base + i] & 0x01) << 1;
    }

  base += GRP;
  for (i = 0; i < GRP; ++i)
    {
      data[base + i] |= buf[base + i] >> 2;
      data[3 * GRP + i] |= (buf[base + i] & 0x02) >> 1;
      data[4 * GRP + i] |= (buf[base + i] & 0x01);
    }

  (*decoded) += 0x100;
}

void nibblize_5_3_alt_decode(const uint8_t **original, uint8_t **decoded)
{
  uint8_t buf[BUF1_SIZ+BUF2_SIZ+1];
  build_ulate_table();
  memcpy(buf,*original,sizeof(buf));
  (*original) += sizeof(buf);
  ulateBuf(buf);
  unxorBuf(buf);
  flipBuf2(buf);
  denibblize(buf, decoded);
}

/* Taken from an Apple ][ DOS 3.1 (13-sector) floppy disk image, track 0, sector 0 */
static const uint8_t dos31_t0s0_log[] =
{
  0x99, 0xb9, 0x00, 0x08, 0x0a, 0x0a, 0x0a, 0x99, 0x00, 0x08, 0xc8, 0xd0, 0xf4, 0xa6, 0x2b, 0xa9,
  0x09, 0x85, 0x27, 0xad, 0xcc, 0x03, 0x85, 0x41, 0x84, 0x40, 0x8a, 0x4a, 0x4a, 0x4a, 0x4a, 0x09,
  0xc0, 0x85, 0x3f, 0xa9, 0x5d, 0x85, 0x3e, 0x20, 0x43, 0x03, 0x20, 0x46, 0x03, 0xa5, 0x3d, 0x4d,
  0xff, 0x03, 0xf0, 0x06, 0xe6, 0x41, 0xe6, 0x3d, 0xd0, 0xed, 0x85, 0x3e, 0xad, 0xcc, 0x03, 0x85,
  0x3f, 0xe6, 0x3f, 0x6c, 0x3e, 0x00, 0xa2, 0x32, 0xa0, 0x00, 0xbd, 0x00, 0x08, 0x4a, 0x4a, 0x4a,
  0x85, 0x3c, 0x4a, 0x85, 0x2a, 0x4a, 0x1d, 0x00, 0x09, 0x91, 0x40, 0xc8, 0xbd, 0x33, 0x08, 0x4a,
  0x4a, 0x4a, 0x4a, 0x26, 0x3c, 0x4a, 0x26, 0x2a, 0x1d, 0x33, 0x09, 0x91, 0x40, 0xc8, 0xbd, 0x66,
  0x08, 0x4a, 0x4a, 0x4a, 0x4a, 0x26, 0x3c, 0x4a, 0x26, 0x2a, 0x1d, 0x66, 0x09, 0x91, 0x40, 0xc8,
  0xa5, 0x2a, 0x29, 0x07, 0x1d, 0x99, 0x09, 0x91, 0x40, 0xc8, 0xa5, 0x3c, 0x29, 0x07, 0x1d, 0xcc,
  0x09, 0x91, 0x40, 0xc8, 0xca, 0x10, 0xb3, 0xad, 0x99, 0x08, 0x4a, 0x4a, 0x4a, 0x0d, 0xff, 0x09,
  0x91, 0x40, 0xa6, 0x2b, 0x60, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
  0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
  0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x36, 0xff, 0xff, 0xff,
  0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
  0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
  0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x09
};
static const size_t dos31_t0s0_log_len = 256;

/* Same, but .nib "nibble" format */
static const uint8_t dos31_t0s0_phy[] =
{
  0xab, 0xba, 0xdd, 0xf5, 0xd6, 0xbb, 0xbb, 0xab, 0xb5, 0xab, 0xeb, 0xb5, 0xbb, 0xf5, 0xeb, 0xb5,
  0xeb, 0xab, 0xb5, 0xab, 0xab, 0xdd, 0xbb, 0xf5, 0xd6, 0xfb, 0xeb, 0xab, 0xb5, 0xab, 0xfb, 0xd6,
  0xfb, 0xdd, 0xdd, 0xf5, 0xbb, 0xdd, 0xab, 0xab, 0xae, 0xbe, 0xf7, 0xd6, 0xee, 0xab, 0xb5, 0xab,
  0xbe, 0xf5, 0xfb, 0xde, 0xea, 0xf5, 0xbb, 0xdd, 0xab, 0xab, 0xab, 0xbb, 0xd6, 0xf5, 0xeb, 0xab,
  0xb5, 0xab, 0xb5, 0xeb, 0xfb, 0xab, 0xfb, 0xfb, 0xf5, 0xb5, 0xfb, 0xab, 0xab, 0xbb, 0xab, 0xeb,
  0xeb, 0xab, 0xbb, 0xab, 0xbb, 0xf5, 0xbb, 0xd6, 0xb5, 0xb5, 0xbb, 0xf7, 0xfe, 0xb5, 0xda, 0xd6,
  0xab, 0xee, 0xee, 0xda, 0xfb, 0xfb, 0xae, 0xf7, 0xd6, 0xdd, 0xbb, 0xab, 0xab, 0xf5, 0xeb, 0xf5,
  0xd6, 0xab, 0xd6, 0xf5, 0xd6, 0xab, 0xdd, 0xf5, 0xbb, 0xeb, 0xb5, 0xd6, 0xab, 0xab, 0xab, 0xab,
  0xbb, 0xdd, 0xeb, 0xdd, 0xf5, 0xfb, 0xb5, 0xbb, 0xbb, 0xdd, 0xab, 0xbb, 0xeb, 0xbb, 0xdf, 0xab,
  0xae, 0xae, 0xb5, 0xd6, 0xae, 0xab, 0xbe, 0xab, 0xb5, 0xab, 0xee, 0xb5, 0xef, 0xad, 0xab, 0xab,
  0xab, 0xdf, 0xea, 0xad, 0xf5, 0xaf, 0xb5, 0xbe, 0xde, 0xdd, 0xeb, 0xde, 0xeb, 0xde, 0xd6, 0xf6,
  0xdd, 0xf5, 0xf5, 0xf5, 0xf6, 0xf5, 0xab, 0xab, 0xab, 0xbb, 0xf6, 0xbb, 0xef, 0xdf, 0xfe, 0xfa,
  0xef, 0xaf, 0xd6, 0xbb, 0xb5, 0xd6, 0xbb, 0xeb, 0xea, 0xda, 0xee, 0xff, 0xfe, 0xfe, 0xfb, 0xeb,
  0xeb, 0xfa, 0xfd, 0xba, 0xd7, 0xef, 0xdf, 0xd6, 0xf6, 0xdd, 0xef, 0xfa, 0xfa, 0xbe, 0xbe, 0xba,
  0xeb, 0xdf, 0xdf, 0xeb, 0xef, 0xef, 0xad, 0xbb, 0xab, 0xab, 0xf6, 0xef, 0xda, 0xf6, 0xed, 0xd6,
  0xbe, 0xaf, 0xad, 0xea, 0xf7, 0xde, 0xda, 0xde, 0xba, 0xbb, 0xab, 0xab, 0xab, 0xd7, 0xaf, 0xda,
  0xd7, 0xad, 0xb7, 0xb6, 0xba, 0xea, 0xf7, 0xde, 0xda, 0xfa, 0xd7, 0xbb, 0xab, 0xab, 0xab, 0xd7,
  0xaf, 0xda, 0xd7, 0xad, 0xb7, 0xdb, 0xd7, 0xea, 0xf7, 0xde, 0xd7, 0xde, 0xab, 0xb6, 0xaf, 0xdd,
  0xdf, 0xea, 0xf7, 0xde, 0xd7, 0xea, 0xae, 0xb6, 0xaf, 0xf7, 0xf5, 0xea, 0xf7, 0xde, 0xab, 0xfa,
  0xeb, 0xaf, 0xb7, 0xdf, 0xbb, 0xab, 0xab, 0xbb, 0xfe, 0xfe, 0xea, 0xf7, 0xfb, 0xde, 0xbd, 0xea,
  0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab,
  0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab,
  0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xf6, 0xf6, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab,
  0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab,
  0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab,
  0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xab, 0xee, 0xbd
};
static const size_t dos31_t0s0_phy_len = 411;

void test_nibblize_5_3_alt_encode(ctx_assertion *ctx)
{
  const uint8_t *po = dos31_t0s0_log;
  const size_t c = dos31_t0s0_phy_len*sizeof(uint8_t);
  uint8_t *penc = malloc(c);
  uint8_t *pend = penc+c;
  uint8_t *p = penc;
  uint8_t *i = penc;

  memset(p,0x75,c);
  nibblize_5_3_alt_encode(&po,&p);
  ASSERT_THAT(ctx,"nibblize_5_3_alt_encode write pointer",p==pend);
  ASSERT_THAT(ctx,"nibblize_5_3_alt_encode read pointer",po==dos31_t0s0_log+dos31_t0s0_log_len);

  po = dos31_t0s0_phy;
  while (i != pend)
    {
      ASSERT_THAT(ctx,"nibblize_5_3_alt_encode",*i++==*po++);
    }

  free(penc);
}

void test_nibblize_5_3_alt_decode(ctx_assertion *ctx)
{
  const uint8_t *po = dos31_t0s0_phy;
  const size_t c = dos31_t0s0_log_len*sizeof(uint8_t);
  uint8_t *pdec = malloc(c);
  uint8_t *pend = pdec+c;
  uint8_t *p = pdec;
  uint8_t *i = pdec;

  memset(p,0xFA,c);
  nibblize_5_3_alt_decode(&po,&p);
  ASSERT_THAT(ctx,"nibblize_5_3_alt_decode write pointer",p==pend);
  ASSERT_THAT(ctx,"nibblize_5_3_alt_decode read pointer",po==dos31_t0s0_phy+dos31_t0s0_phy_len);

  po = dos31_t0s0_log;
  while (i != pend)
    {
      ASSERT_THAT(ctx,"nibblize_5_3_alt_decode",*i++==*po++);
    }

  free(pdec);
}

void test_nibblize_5_3_alt(ctx_assertion *ctx)
{
  test_nibblize_5_3_alt_encode(ctx);
  test_nibblize_5_3_alt_decode(ctx);
}
