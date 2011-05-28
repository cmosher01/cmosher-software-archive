#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "nibblize_5_3.h"

#include <stdint.h>
#include <string.h>
#include <stdlib.h>

#include "assert_that.h"
#include "nibblize_5_3_common.h"



/*
 * Encodes the given sector (256 bytes) using the normal "5 and 3" encoding
 * scheme. Based on code by Andy McFadden, from CiderPress.
 *
 * original sector to encode;  pointer updated on exit
 * encoded  encoded sector; pointer updated on exit
 */
void nibblize_5_3_encode(const uint8_t **original, uint8_t **encoded)
{
  uint8_t top[BUF1_SIZ];
  uint8_t thr[BUF2_SIZ];
  uint8_t chksum = 0;
  int i, val;

  /* Split the bytes into sections. */
  for (i = GRP - 1; i >= 0; --i)
    {
      uint8_t three1,three2,three3,three4,three5;

      three1 = *(*original)++;
      top[i + 0 * GRP] = three1 >> 3;
      three2 = *(*original)++;
      top[i + 1 * GRP] = three2 >> 3;
      three3 = *(*original)++;
      top[i + 2 * GRP] = three3 >> 3;
      three4 = *(*original)++;
      top[i + 3 * GRP] = three4 >> 3;
      three5 = *(*original)++;
      top[i + 4 * GRP] = three5 >> 3;

      thr[i + 0 * GRP] = (three1 & 0x07) << 2 | (three4 & 0x04) >> 1 | (three5 & 0x04) >> 2;
      thr[i + 1 * GRP] = (three2 & 0x07) << 2 | (three4 & 0x02)      | (three5 & 0x02) >> 1;
      thr[i + 2 * GRP] = (three3 & 0x07) << 2 | (three4 & 0x01) << 1 | (three5 & 0x01);
    }

  /* Handle the last byte. */
  val = *(*original)++;
  top[5 * GRP] = val >> 3;
  thr[3 * GRP] = val & 0x07;

  /* Write the bytes. */
  for (i = BUF2_SIZ - 1; i >= 0; --i)
    {
      *(*encoded)++ = xlate[thr[i] ^ chksum];
      chksum = thr[i];
    }
  for (i = 0; i < BUF1_SIZ; ++i)
    {
      *(*encoded)++ = xlate[top[i] ^ chksum];
      chksum = top[i];
    }
  *(*encoded)++ = xlate[chksum];
}

static uint8_t unxlate(uint8_t b)
{
  uint8_t decodedVal = ulate[b];
  if (decodedVal == 0xFF)
    {
      /* TODO handle invalid nibble value */
    }
  return decodedVal;
}


void nibblize_5_3_decode(const uint8_t **original, uint8_t **decoded)
{
  uint8_t top[BUF1_SIZ];
  uint8_t thr[BUF2_SIZ];
  uint8_t chksum = 0;
  uint8_t val;
  int i;

  build_ulate_table();

  /*
   * Pull the 410 bytes out, convert them from disk bytes to 5-bit values, and
   * arrange them into a DOS-like pair of buffers.
   */
  for (i = BUF2_SIZ - 1; i >= 0; --i)
    {
      val = unxlate(*(*original)++);
      chksum ^= val;
      thr[i] = chksum;
    }
  for (i = 0; i < BUF1_SIZ; ++i)
    {
      val = unxlate(*(*original)++);
      chksum ^= val;
      top[i] = chksum << 3;
    }

  /*
   * Grab the 411th byte (the checksum byte) and see if we did this right.
   */
  val = unxlate(*(*original)++);
  chksum ^= val;
  if (chksum)
    {
      /* TODO handle bad checksum */
    }

  /* Convert this pile of stuff into 256 data bytes. */
  for (i = GRP - 1; i >= 0; --i)
    {
      uint8_t three1,three2,three3,three4,three5;

      three1 = thr[0 * GRP + i];
      *(*decoded)++ = top[0 * GRP + i] | ((three1 >> 2) & 0x07);
      three2 = thr[1 * GRP + i];
      *(*decoded)++ = top[1 * GRP + i] | ((three2 >> 2) & 0x07);
      three3 = thr[2 * GRP + i];
      *(*decoded)++ = top[2 * GRP + i] | ((three3 >> 2) & 0x07);

      three4 = (three1 & 0x02) << 1 | (three2 & 0x02)      | (three3 & 0x02) >> 1;
      *(*decoded)++ = top[3 * GRP + i] | ((three4     ) & 0x07);
      three5 = (three1 & 0x01) << 2 | (three2 & 0x01) << 1 | (three3 & 0x01);
      *(*decoded)++ = top[4 * GRP + i] | ((three5     ) & 0x07);
    }

  /* Convert the very last byte, which is handled specially. */
  *(*decoded)++ = top[5 * GRP] | (thr[3 * GRP] & 0x07);
}

/* Taken from an Apple ][ DOS 3.1 (13-sector) floppy disk image, track 0, sector 1 */
static const uint8_t dos31_t0s1_log[] =
{
  0x8e, 0xe9, 0x37, 0x8e, 0xf7, 0x37, 0xa9, 0x01, 0x8d, 0xf8, 0x37, 0x8d, 0xea, 0x37, 0xad, 0xe0,
  0x37, 0x8d, 0xe1, 0x37, 0xa9, 0x00, 0x8d, 0xec, 0x37, 0xad, 0xe2, 0x37, 0x8d, 0xed, 0x37, 0xad,
  0xe3, 0x37, 0x8d, 0xf1, 0x37, 0xa9, 0x01, 0x8d, 0xf4, 0x37, 0x8a, 0x4a, 0x4a, 0x4a, 0x4a, 0xaa,
  0xa9, 0x00, 0x9d, 0xf8, 0x04, 0x9d, 0x78, 0x04, 0x20, 0x93, 0x37, 0xa2, 0xff, 0x9a, 0x8e, 0xeb,
  0x37, 0x20, 0x93, 0xfe, 0x20, 0x89, 0xfe, 0x4c, 0x03, 0x1b, 0xad, 0xf1, 0x37, 0x8d, 0xe3, 0x37,
  0x38, 0xad, 0xe7, 0x37, 0xed, 0xe3, 0x37, 0x8d, 0xe0, 0x37, 0xa9, 0x00, 0x8d, 0xec, 0x37, 0x8d,
  0xed, 0x37, 0x8d, 0xf0, 0x37, 0xad, 0xe7, 0x37, 0x8d, 0xf1, 0x37, 0x8d, 0xfe, 0x36, 0xa9, 0x0a,
  0x8d, 0xe1, 0x37, 0x8d, 0xe2, 0x37, 0xa9, 0x48, 0x8d, 0xff, 0x36, 0xa9, 0x02, 0x8d, 0xf4, 0x37,
  0x20, 0x93, 0x37, 0xad, 0xe3, 0x37, 0x8d, 0xf1, 0x37, 0xad, 0xe0, 0x37, 0x8d, 0xe1, 0x37, 0x20,
  0x93, 0x37, 0x60, 0xad, 0xe5, 0x37, 0xac, 0xe4, 0x37, 0x20, 0x00, 0x3d, 0xac, 0xed, 0x37, 0xc8,
  0xc0, 0x0d, 0xd0, 0x05, 0xa0, 0x00, 0xee, 0xec, 0x37, 0x8c, 0xed, 0x37, 0xee, 0xf1, 0x37, 0xce,
  0xe1, 0x37, 0xd0, 0xdf, 0x60, 0xa9, 0x0c, 0x20, 0x38, 0x22, 0xa9, 0x06, 0xcd, 0xf9, 0x34, 0xd0,
  0x03, 0x4c, 0xb2, 0x23, 0x4c, 0x78, 0x22, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x1b, 0x09, 0x0a, 0x1b, 0xe8, 0x37, 0x00, 0x36, 0x01, 0x60, 0x01, 0xfe, 0x00, 0x01, 0xfb, 0x37,
  0x00, 0x37, 0x00, 0x00, 0x02, 0xff, 0xfe, 0x60, 0x01, 0x00, 0x00, 0x00, 0x01, 0xef, 0xd8, 0x00
};
static const size_t dos31_t0s1_log_len = 256;

/* Same, but .nib "nibble" format */
static const uint8_t dos31_t0s1_phy[] =
{
  0xab, 0xfd, 0xfa, 0xd7, 0xfb, 0xae, 0xbe, 0xdd, 0xbb, 0xdb, 0xae, 0xf5, 0xfb, 0xed, 0xae, 0xee,
  0xf5, 0xbb, 0xbe, 0xab, 0xbf, 0xad, 0xbf, 0xaf, 0xbd, 0xb5, 0xba, 0xdf, 0xf7, 0xbb, 0xbe, 0xbf,
  0xbe, 0xeb, 0xdd, 0xbd, 0xb7, 0xd7, 0xbf, 0xef, 0xd6, 0xab, 0xab, 0xab, 0xab, 0xad, 0xd6, 0xbb,
  0xae, 0xba, 0xae, 0xb5, 0xad, 0xaf, 0xdf, 0xbf, 0xfb, 0xbd, 0xfe, 0xbe, 0xaf, 0xef, 0xbb, 0xaf,
  0xbb, 0xba, 0xfe, 0xd7, 0xbd, 0xbf, 0xfb, 0xed, 0xae, 0xbd, 0xee, 0xad, 0xed, 0xd6, 0xba, 0xbf,
  0xf5, 0xb5, 0xde, 0xae, 0xb7, 0xda, 0xba, 0xf5, 0xad, 0xab, 0xb6, 0xea, 0xdf, 0xab, 0xab, 0xab,
  0xad, 0xbb, 0xde, 0xfa, 0xfe, 0xb6, 0xf7, 0xf6, 0xb6, 0xad, 0xfe, 0xb7, 0xdd, 0xbb, 0xf7, 0xed,
  0xf5, 0xfe, 0xb5, 0xdb, 0xfd, 0xf6, 0xfb, 0xb7, 0xda, 0xbe, 0xde, 0xbd, 0xf5, 0xae, 0xde, 0xab,
  0xab, 0xbf, 0xf7, 0xde, 0xbf, 0xdb, 0xb7, 0xeb, 0xad, 0xee, 0xd6, 0xf6, 0xbe, 0xf5, 0xfe, 0xd6,
  0xab, 0xab, 0xab, 0xab, 0xb6, 0xb6, 0xf6, 0xf6, 0xfb, 0xfe, 0xae, 0xff, 0xff, 0xff, 0xff, 0xad,
  0xad, 0xab, 0xab, 0xab, 0xab, 0xb5, 0xae, 0xae, 0xbb, 0xed, 0xb5, 0xfd, 0xf5, 0xff, 0xdf, 0xea,
  0xef, 0xdb, 0xf5, 0xef, 0xab, 0xab, 0xb5, 0xbf, 0xf5, 0xef, 0xb5, 0xbd, 0xfa, 0xf6, 0xad, 0xfa,
  0xfa, 0xff, 0xea, 0xf7, 0xef, 0xab, 0xf5, 0xea, 0xab, 0xbd, 0xf7, 0xab, 0xef, 0xde, 0xff, 0xf6,
  0xb7, 0xb7, 0xba, 0xad, 0xab, 0xab, 0xab, 0xab, 0xbd, 0xea, 0xdb, 0xab, 0xbd, 0xf7, 0xfa, 0xfb,
  0xeb, 0xbd, 0xdd, 0xdd, 0xf7, 0xea, 0xbf, 0xad, 0xaf, 0xfd, 0xba, 0xea, 0xbb, 0xfd, 0xb7, 0xea,
  0xea, 0xdb, 0xfa, 0xad, 0xef, 0xfa, 0xee, 0xdb, 0xab, 0xea, 0xbd, 0xfb, 0xb7, 0xef, 0xb5, 0xbb,
  0xfd, 0xd6, 0xd6, 0xab, 0xab, 0xaf, 0xaf, 0xab, 0xab, 0xab, 0xab, 0xdb, 0xdb, 0xab, 0xad, 0xba,
  0xfa, 0xab, 0xba, 0xba, 0xfa, 0xea, 0xea, 0xea, 0xbd, 0xf7, 0xab, 0xab, 0xef, 0xab, 0xd7, 0xf7,
  0xef, 0xab, 0xd7, 0xd7, 0xde, 0xff, 0xda, 0xaf, 0xdf, 0xed, 0xb5, 0xb5, 0xbd, 0xf7, 0xef, 0xab,
  0xd6, 0xfd, 0xb7, 0xfa, 0xfd, 0xab, 0xff, 0xea, 0xde, 0xfd, 0xab, 0xab, 0xab, 0xab, 0xb5, 0xd7,
  0xdd, 0xfd, 0xfe, 0xb5, 0xf5, 0xb7, 0xb7, 0xae, 0xf5, 0xf5, 0xf5, 0xf7, 0xae, 0xde, 0xab, 0xbd,
  0xaf, 0xf6, 0xef, 0xd6, 0xad, 0xf7, 0xf7, 0xff, 0xba, 0xf6, 0xfa, 0xed, 0xb7, 0xfb, 0xbd, 0xb7,
  0xef, 0xd6, 0xad, 0xf7, 0xef, 0xab, 0xbe, 0xfa, 0xab, 0xb7, 0xb7, 0xb7, 0xb6, 0xaf, 0xab, 0xab,
  0xab, 0xab, 0xee, 0xbd, 0xf5, 0xfb, 0xfd, 0xef, 0xb6, 0xd7, 0xf6, 0xb7, 0xeb, 0xeb, 0xef, 0xaf,
  0xdf, 0xbd, 0xdb, 0xab, 0xef, 0xdb, 0xf5, 0xab, 0xfa, 0xfa, 0xea, 0xb5, 0xef, 0xdf, 0xfa, 0xdb,
  0xbd, 0xf5, 0xab, 0xd6, 0xfa, 0xab, 0xea, 0xbe, 0xad, 0xfe, 0xab
};
static const size_t dos31_t0s1_phy_len = 411;

void test_nibblize_5_3_encode(ctx_assertion *ctx)
{
  const uint8_t *po = dos31_t0s1_log;
  const size_t c = dos31_t0s1_phy_len*sizeof(uint8_t);
  uint8_t *penc = malloc(c);
  uint8_t *pend = penc+c;
  uint8_t *p = penc;
  uint8_t *i = penc;

  memset(p,0x75,c);
  nibblize_5_3_encode(&po,&p);
  ASSERT_THAT(ctx,"nibblize_5_3_encode write pointer",p==pend);
  ASSERT_THAT(ctx,"nibblize_5_3_encode read pointer",po==dos31_t0s1_log+dos31_t0s1_log_len);

  po = dos31_t0s1_phy;
  while (i != pend)
    {
      ASSERT_THAT(ctx,"nibblize_5_3_encode",*i++==*po++);
    }

  free(penc);
}

void test_nibblize_5_3_decode(ctx_assertion *ctx)
{
  const uint8_t *po = dos31_t0s1_phy;
  const size_t c = dos31_t0s1_log_len*sizeof(uint8_t);
  uint8_t *pdec = malloc(c);
  uint8_t *pend = pdec+c;
  uint8_t *p = pdec;
  uint8_t *i = pdec;

  memset(p,0xFA,c);
  nibblize_5_3_decode(&po,&p);
  ASSERT_THAT(ctx,"nibblize_5_3_decode write pointer",p==pend);
  ASSERT_THAT(ctx,"nibblize_5_3_decode read pointer",po==dos31_t0s1_phy+dos31_t0s1_phy_len);

  po = dos31_t0s1_log;
  while (i != pend)
    {
      ASSERT_THAT(ctx,"nibblize_5_3_decode",*i++==*po++);
    }

  free(pdec);
}

void test_nibblize_5_3(ctx_assertion *ctx)
{
  test_nibblize_5_3_encode(ctx);
  test_nibblize_5_3_decode(ctx);
}
