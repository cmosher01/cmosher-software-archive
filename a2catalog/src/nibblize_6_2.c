#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "nibblize_6_2.h"

#include <stdint.h>
#include <string.h>
#include <stdlib.h>

#include "assert_that.h"



#define GRP 0x56
#define BUF1_SIZ 0x0100
#define BUF2_SIZ GRP

static const uint8_t xlate[] =
{
                          0x96, 0x97,       0x9A, 0x9B,       0x9D, 0x9E, 0x9F,
                          0xA6, 0xA7,     /*0xAA*/0xAB, 0xAC, 0xAD, 0xAE, 0xAF,
  0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xB9, 0xBA, 0xBB, 0xBC, 0xBD, 0xBE, 0xBF,
                                                  0xCB,       0xCD, 0xCE, 0xCF,
        0xD3,     /*0xD5*/0xD6, 0xD7, 0xD9, 0xDA, 0xDB, 0xDC, 0xDD, 0xDE, 0xDF,
                    0xE5, 0xE6, 0xE7, 0xE9, 0xEA, 0xEB, 0xEC, 0xED, 0xEE, 0xEF,
  0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF9, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, 0xFF
};

static uint8_t ulate[1<<sizeof(uint8_t)];

static void build_ulate_table()
{
  uint_fast8_t i;
  memset(ulate,0xFF,sizeof(ulate));
  for (i = 0; i < sizeof(xlate)/sizeof(xlate[0]); ++i)
    {
      ulate[xlate[i]] = i;
    }
}



static void buildBuffers(const uint8_t **data, uint8_t *top, uint8_t *two)
{
  int i;
  int j = BUF2_SIZ - 1;
  int twoShift = 0;
  memset(two,0,BUF2_SIZ);
  for (i = 0; i < BUF1_SIZ; ++i)
    {
      uint_fast32_t val = *(*data)++;
      top[i] = val >> 2;
      two[j] |= ((val & 0x01) << 1 | (val & 0x02) >> 1) << twoShift;
      if (j <= 0)
        {
          j = BUF2_SIZ;
          twoShift += 2;
        }
      --j;
    }
}

/**
 * Encodes the given sector (256 bytes) using the Apple ][ "6 and 2" encoding
 * scheme. Based on code by Andy McFadden, from CiderPress.
 *
 * @param data
 *          sector to encode
 * @return encoded sector (343 bytes)
 */
void nibblize_6_2_encode(const uint8_t **original, uint8_t **encoded)
{
  uint8_t top[BUF1_SIZ];
  uint8_t two[BUF2_SIZ];
  int chksum = 0;
  int i;
  buildBuffers(original, top, two);
  for (i = BUF2_SIZ - 1; i >= 0; --i)
    {
      *(*encoded)++ = xlate[two[i] ^ chksum];
      chksum = two[i];
    }
  for (i = 0; i < BUF1_SIZ; ++i)
    {
      *(*encoded)++ = xlate[top[i] ^ chksum];
      chksum = top[i];
    }
  *(*encoded)++ = xlate[chksum];
}

/**
 * Decodes the given sector (256 logical bytes, 343 actual bytes) using the
 * Apple ][ "6 and 2" encoding scheme. Based on code by Andy McFadden, from
 * CiderPress.
 *
 * @param enc
 *          encoded sector
 * @return sector (256 bytes) (decoded)
 * @throws CorruptDataException
 *           if the checksum in the given encoded data is not correct
 */
void nibblize_6_2_decode(const uint8_t **original, uint8_t **decoded)
{
  uint8_t two[3 * GRP];
  int chksum = 0;
  uint8_t decodedVal;
  int i;

  build_ulate_table();

  /*
   * Pull the 342 bytes out, convert them from disk bytes to 6-bit values, and
   * arrange them into a DOS-like pair of buffers.
   */
  for (i = 0; i < GRP; ++i)
    {
      decodedVal = ulate[*(*original)++];
      /* TODO handle invalid nibble value
      if (decodedVal == 0xFF) {
      }
      */
      chksum ^= decodedVal;
      two[i + 0 * GRP] = ((chksum & 0x01) << 1) | ((chksum & 0x02) >> 1);
      two[i + 1 * GRP] = ((chksum & 0x04) >> 1) | ((chksum & 0x08) >> 3);
      two[i + 2 * GRP] = ((chksum & 0x10) >> 3) | ((chksum & 0x20) >> 5);
    }
  for (i = 0; i < 0x100; ++i)
    {
      decodedVal = ulate[*(*original)++];
      /* TODO handle invalid nibble value
      if (decodedVal == 0xFF) {
      }
      */
      chksum ^= decodedVal;
      *(*decoded)++ = (chksum << 2) | two[i];
    }
  /*
   * Grab the 343rd byte (the checksum byte) and see if we did this right.
   */
  decodedVal = ulate[*(*original)++];
  /* TODO handle invalid nibble value
  if (decodedVal == 0xFF) {
  }
  */
  chksum ^= decodedVal;
  /* TODO handle bad checksum value
  if (chksum != 0) {
  }
  */
}



/* Taken from an Apple ][ DOS 3.3 floppy disk image (.do "DOS order" format), track 0, sector 0 */
static const uint8_t dos33_t0s0_log[] =
{
  0x01, 0xa5, 0x27, 0xc9, 0x09, 0xd0, 0x18, 0xa5, 0x2b, 0x4a, 0x4a, 0x4a, 0x4a, 0x09, 0xc0, 0x85,
  0x3f, 0xa9, 0x5c, 0x85, 0x3e, 0x18, 0xad, 0xfe, 0x08, 0x6d, 0xff, 0x08, 0x8d, 0xfe, 0x08, 0xae,
  0xff, 0x08, 0x30, 0x15, 0xbd, 0x4d, 0x08, 0x85, 0x3d, 0xce, 0xff, 0x08, 0xad, 0xfe, 0x08, 0x85,
  0x27, 0xce, 0xfe, 0x08, 0xa6, 0x2b, 0x6c, 0x3e, 0x00, 0xee, 0xfe, 0x08, 0xee, 0xfe, 0x08, 0x20,
  0x89, 0xfe, 0x20, 0x93, 0xfe, 0x20, 0x2f, 0xfb, 0xa6, 0x2b, 0x6c, 0xfd, 0x08, 0x00, 0x0d, 0x0b,
  0x09, 0x07, 0x05, 0x03, 0x01, 0x0e, 0x0c, 0x0a, 0x08, 0x06, 0x04, 0x02, 0x0f, 0x00, 0x20, 0x64,
  0x27, 0xb0, 0x08, 0xa9, 0x00, 0xa8, 0x8d, 0x5d, 0x36, 0x91, 0x40, 0xad, 0xc5, 0x35, 0x4c, 0xd2,
  0x26, 0xad, 0x5d, 0x36, 0xf0, 0x08, 0xee, 0xbd, 0x35, 0xd0, 0x03, 0xee, 0xbe, 0x35, 0xa9, 0x00,
  0x8d, 0x5d, 0x36, 0x4c, 0xb3, 0x36, 0x8d, 0xbc, 0x35, 0x20, 0xa8, 0x26, 0x20, 0xea, 0x22, 0x4c,
  0x7d, 0x22, 0xa0, 0x13, 0xb1, 0x42, 0xd0, 0x14, 0xc8, 0xc0, 0x17, 0xd0, 0xf7, 0xa0, 0x19, 0xb1,
  0x42, 0x99, 0xa4, 0x35, 0xc8, 0xc0, 0x1d, 0xd0, 0xf6, 0x4c, 0xbb, 0x26, 0xa2, 0xff, 0x8e, 0x5d,
  0x36, 0xd0, 0xf6, 0xad, 0xbd, 0x35, 0x8d, 0xe6, 0x35, 0x8d, 0xea, 0x35, 0xad, 0xbe, 0x35, 0x8d,
  0xe7, 0x35, 0x8d, 0xeb, 0x35, 0x8d, 0xe4, 0x35, 0xba, 0x8e, 0x9b, 0x33, 0x4c, 0x7f, 0x33, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x09
};
static const size_t dos33_t0s0_log_len = 256;

/* Same, but .nib "nibble" format */
static const uint8_t dos33_t0s0_phy[] =
{
  0xb6, 0xdb, 0xdc, 0xf4, 0xf3, 0xbb, 0xbd, 0xfe, 0x97, 0x9a, 0xae, 0xfc, 0xed, 0xad, 0xfa, 0xef,
  0xab, 0xee, 0xfe, 0xb2, 0xcb, 0xbe, 0x9a, 0xb7, 0xbe, 0x9f, 0xd7, 0xec, 0xef, 0xb3, 0xdc, 0x97,
  0xf5, 0xff, 0x96, 0xfa, 0xae, 0xa7, 0x9a, 0xb2, 0x96, 0xad, 0xac, 0x9b, 0xb2, 0xa6, 0xaf, 0xac,
  0xaf, 0xac, 0xa7, 0xab, 0x97, 0x9f, 0xa6, 0x9e, 0x97, 0x9e, 0xa7, 0xaf, 0x9e, 0xae, 0x9e, 0xae,
  0x9f, 0x9b, 0x97, 0x9b, 0xb2, 0xaf, 0xb3, 0xae, 0xac, 0x9a, 0xb3, 0xb2, 0xac, 0xa7, 0xac, 0x97,
  0xab, 0xab, 0xba, 0xf4, 0xea, 0xad, 0x9e, 0xe5, 0xd6, 0xfb, 0xed, 0xf5, 0xef, 0xec, 0xda, 0xbd,
  0x96, 0x96, 0x96, 0xb4, 0xef, 0xb5, 0xeb, 0xdc, 0xfd, 0xf5, 0xeb, 0xab, 0xea, 0xb9, 0xfd, 0xbe,
  0xdb, 0xfd, 0xd7, 0xcd, 0xfd, 0xe5, 0xb9, 0xfd, 0xb2, 0xab, 0xe6, 0xfc, 0xb5, 0xda, 0xeb, 0xfc,
  0xae, 0xfd, 0xe5, 0xb9, 0xfd, 0xda, 0xdf, 0xfa, 0xae, 0xfd, 0xe7, 0xda, 0xb5, 0xb9, 0xb3, 0xfb,
  0x9d, 0xfd, 0xf9, 0x9d, 0xfd, 0xac, 0xe6, 0xce, 0xf6, 0xe9, 0xcb, 0xf6, 0x9b, 0xf4, 0xbc, 0xda,
  0xb5, 0xdb, 0xfd, 0x9a, 0x9b, 0x97, 0x96, 0x9b, 0x96, 0x97, 0x96, 0x9b, 0x96, 0x97, 0x96, 0x9b,
  0x96, 0x97, 0x9b, 0x9b, 0xa7, 0xb5, 0xb4, 0xdc, 0xeb, 0xdf, 0xe6, 0xe6, 0xab, 0xf3, 0xbf, 0xe5,
  0xf3, 0xfb, 0xbf, 0xfc, 0xcf, 0xde, 0xfd, 0xd9, 0xfc, 0xbf, 0xee, 0xfe, 0xf9, 0xb9, 0xd9, 0xf9,
  0xf3, 0xfb, 0xb9, 0xd9, 0xde, 0xe6, 0xda, 0xf3, 0xbf, 0xcf, 0xff, 0xd7, 0xeb, 0xae, 0xd9, 0x9e,
  0xd9, 0xda, 0x97, 0xef, 0xef, 0xcb, 0xae, 0xbc, 0xd6, 0xe9, 0xdf, 0xfc, 0xdb, 0xee, 0xf6, 0x9a,
  0xf4, 0xee, 0xab, 0xba, 0xeb, 0xe6, 0xfc, 0xf5, 0xb3, 0xdb, 0xff, 0x9a, 0xf6, 0xf2, 0xab, 0xeb,
  0xfd, 0xde, 0xd7, 0xbc, 0xcd, 0xf3, 0xbf, 0xf9, 0xab, 0xbb, 0x9d, 0xd9, 0xeb, 0xbf, 0xf3, 0xeb,
  0xbe, 0xf6, 0xdd, 0x9d, 0xd9, 0xeb, 0xbf, 0xf3, 0xeb, 0xbe, 0xf6, 0xeb, 0xbf, 0xf3, 0xda, 0xaf,
  0x9e, 0xe6, 0xd3, 0xae, 0xb7, 0xae, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96,
  0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96,
  0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96, 0x96,
  0x96, 0x96, 0x96, 0x96, 0xaf, 0xb3, 0x9a
};
static const size_t dos33_t0s0_phy_len = 343;

void test_nibblize_6_2_encode(ctx_assertion *ctx)
{
  const uint8_t *po = dos33_t0s0_log;
  const size_t c = dos33_t0s0_phy_len*sizeof(uint8_t);
  uint8_t *penc = malloc(c);
  uint8_t *pend = penc+c;
  uint8_t *p = penc;
  uint8_t *i = penc;

  memset(p,0x75,c);
  nibblize_6_2_encode(&po,&p);
  ASSERT_THAT(ctx,"nibblize_6_2_encode write pointer",p==pend);
  ASSERT_THAT(ctx,"nibblize_6_2_encode read pointer",po==dos33_t0s0_log+dos33_t0s0_log_len);

  po = dos33_t0s0_phy;
  while (i != pend)
    {
      ASSERT_THAT(ctx,"nibblize_6_2_encode",*i++==*po++);
    }

  free(penc);
}

void test_nibblize_6_2_decode(ctx_assertion *ctx)
{
  const uint8_t *po = dos33_t0s0_phy;
  const size_t c = dos33_t0s0_log_len*sizeof(uint8_t);
  uint8_t *pdec = malloc(c);
  uint8_t *pend = pdec+c;
  uint8_t *p = pdec;
  uint8_t *i = pdec;

  memset(p,0xFA,c);
  nibblize_6_2_decode(&po,&p);
  ASSERT_THAT(ctx,"nibblize_6_2_decode write pointer",p==pend);
  ASSERT_THAT(ctx,"nibblize_6_2_decode read pointer",po==dos33_t0s0_phy+dos33_t0s0_phy_len);

  po = dos33_t0s0_log;
  while (i != pend)
    {
      ASSERT_THAT(ctx,"nibblize_6_2_decode",*i++==*po++);
    }

  free(pdec);
}

void test_nibblize_6_2(ctx_assertion *ctx)
{
  test_nibblize_6_2_encode(ctx);
  test_nibblize_6_2_decode(ctx);
}
