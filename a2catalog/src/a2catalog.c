#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <minmax.h>
#include <binary-io.h>

#include "assert_that.h"
#include "a2const.h"
#include "a2catalog_opt.h"
#include "nibblize_4_4.h"
#include "nibblize_5_3.h"
#include "nibblize_5_3_alt.h"
#include "nibblize_6_2.h"



/*
 * Writes an 8-bit byte at the given pointer.
 *
 * b  the byte to write out
 * pp address of pointer to write b at; incremented on exit
 */
void b_out(uint8_t b, uint8_t **pp)
{
  *(*pp)++ = b;
}

void test_b_out(ctx_assertion *ctx)
{
  const uint8_t SENTINEL = 0xFD;
  uint8_t image[3];
  const uint8_t TAG = 0xa5;
  const uint8_t b = TAG;
  uint8_t *p = image+1;

  image[0] = SENTINEL;
  image[1] = SENTINEL;
  image[2] = SENTINEL;

  b_out(b,&p);

  ASSERT_THAT(ctx,"byte out nominal: value",image[1]==TAG);
  ASSERT_THAT(ctx,"byte out nominal: buffer underflow",image[0]==SENTINEL);
  ASSERT_THAT(ctx,"byte out nominal: buffer overflow",image[2]==SENTINEL);
  ASSERT_THAT(ctx,"byte out nominal: pointer updated",p==image+2);
}



/*
 * Writes a 16-bit word, low-order byte first, at the given pointer.
 *
 * w  the word to write out (upper 16 bits are ignored)
 * pp address of pointer to write w at; incremented on exit
 */
void w_out(uint16_t w, uint8_t **pp)
{
  b_out(w,pp);
  w >>= 8;
  b_out(w,pp);
}

void test_w_out(ctx_assertion *ctx)
{
  const uint8_t SENTINEL = 0xFD;
  const uint8_t TAG_HI = 0xA5;
  const uint8_t TAG_LO = 0x5A;
  const uint16_t TAG = 0xA55A;
  const uint16_t w = TAG;
  uint8_t image[4];
  uint8_t *p = image+1;

  image[0] = SENTINEL;
  image[1] = SENTINEL;
  image[2] = SENTINEL;
  image[3] = SENTINEL;

  w_out(w,&p);

  ASSERT_THAT(ctx,"word out nominal: value low",image[1]==TAG_LO);
  ASSERT_THAT(ctx,"word out nominal: value high",image[2]==TAG_HI);
  ASSERT_THAT(ctx,"word out nominal: buffer underflow",image[0]==SENTINEL);
  ASSERT_THAT(ctx,"word out nominal: buffer overflow",image[3]==SENTINEL);
  ASSERT_THAT(ctx,"word out nominal: pointer updated",p==image+3);
}



void map_out(uint16_t m, uint8_t **pp)
{
  b_out(m >> 8,pp);
  b_out(m,pp);
  w_out(0,pp);
}



/*
 * Writes n 8-bit bytes at the given pointer.
 *
 * n  the number of times to write b
 * b  the byte to write out
 * pp address of pointer to start writing b's at; incremented on exit
 */
void n_b_out(uint_fast32_t n, uint8_t b, uint8_t **pp)
{
  while (n--)
    {
      b_out(b,pp);
    }
}

void test_n_b_out(ctx_assertion *ctx)
{
  const uint8_t SENTINEL = 0xFD;
  const uint8_t TAG = 0xA5;
  const uint8_t b = TAG;
  uint8_t image[5];
  uint8_t *p = image+1;
  const uint_fast32_t N = 3;

  image[0] = SENTINEL;
  image[1] = SENTINEL;
  image[2] = SENTINEL;
  image[3] = SENTINEL;
  image[4] = SENTINEL;

  n_b_out(N,b,&p);

  ASSERT_THAT(ctx,"byte out nominal: value first",image[1]==TAG);
  ASSERT_THAT(ctx,"byte out nominal: value middle",image[2]==TAG);
  ASSERT_THAT(ctx,"byte out nominal: value last",image[3]==TAG);
  ASSERT_THAT(ctx,"byte out nominal: buffer underflow",image[0]==SENTINEL);
  ASSERT_THAT(ctx,"byte out nominal: buffer overflow",image[4]==SENTINEL);
  ASSERT_THAT(ctx,"byte out nominal: pointer updated",p==image+1+N);
}






uint_fast8_t sectors_per_track(uint_fast16_t version)
{
  return version < 330 ? 13 : 16;
}

void test_sectors_per_track(ctx_assertion *ctx)
{
  int cs;
  cs = sectors_per_track(310);
  ASSERT_THAT(ctx,"13 sectors for DOS 3.1",cs==13);
  cs = sectors_per_track(320);
  ASSERT_THAT(ctx,"13 sectors for DOS 3.2",cs==13);
  cs = sectors_per_track(321);
  ASSERT_THAT(ctx,"13 sectors for DOS 3.2.1",cs==13);
  cs = sectors_per_track(330);
  ASSERT_THAT(ctx,"16 sectors for DOS 3.3.0",cs==16);
  cs = sectors_per_track(331);
  ASSERT_THAT(ctx,"16 sectors for DOS 3.3.1",cs==16);
  cs = sectors_per_track(332);
  ASSERT_THAT(ctx,"16 sectors for DOS 3.3.2",cs==16);
}



uint16_t get_free_track_map(uint_fast16_t version)
{
  uint_fast8_t cs = sectors_per_track(version);
  uint16_t mk = 0;
  while (cs--)
    {
      mk >>= 1;
      mk |= 0x8000;
    }
  return mk;
}

void test_get_free_track_map(ctx_assertion *ctx)
{
  uint16_t fr;
  fr  = get_free_track_map(310);
  ASSERT_THAT(ctx,"13 free sector map",fr==0xFFF8);
  fr = get_free_track_map(331);
  ASSERT_THAT(ctx,"16 free sector map",fr==0xFFFF);
}



void allocate_n_sectors(uint_fast8_t c_used_sectors, uint16_t *track_map)
{
  (*track_map) <<= c_used_sectors;
}

/* TODO need to verify this on a real disk */
void test_allocate_n_sectors(ctx_assertion *ctx)
{
  uint16_t bitmap = get_free_track_map(310);
  allocate_n_sectors(3,&bitmap);
  /* 13 free sectors, minus 3 free sectors, equals 10 free sectors */
  ASSERT_THAT(ctx,"allocate sectors, DOS 3.1",bitmap==0xFFC0);

  bitmap = get_free_track_map(330);
  allocate_n_sectors(3,&bitmap);
  /* 16 free sectors, minus 3 free sectors, equals 13 free sectors */
  ASSERT_THAT(ctx,"allocate sectors, DOS 3.3",bitmap==0xFFF8);
}



/*
 * Writes a catalog-sector link at the given pointer.
 *
 * sect  sector number, or zero to mean "no link"
 * track catalog track number
 * pp address of pointer to start writing at; incremented on exit
 */
void sector_link_out(uint8_t sect, uint8_t track, uint8_t **pp)
{
  b_out(0,pp);
  b_out(sect ? track : 0,pp);
  b_out(sect,pp);
}

void test_sector_link_out(ctx_assertion *ctx)
{
  const uint8_t SENTINEL = 0xFD;
  const uint8_t SECTOR = 0x0F;
  const uint8_t TRACK = 0x11;
  const uint8_t NO_LINK = 0;
  uint8_t image[5];
  uint8_t *p = image+1;

  image[0] = SENTINEL;
  image[1] = SENTINEL;
  image[2] = SENTINEL;
  image[3] = SENTINEL;
  image[4] = SENTINEL;

  sector_link_out(SECTOR,TRACK,&p);

  ASSERT_THAT(ctx,"sector link out nominal: value zero",image[1]==0);
  ASSERT_THAT(ctx,"sector link out nominal: value track",image[2]==TRACK);
  ASSERT_THAT(ctx,"sector link out nominal: value sector",image[3]==SECTOR);
  ASSERT_THAT(ctx,"sector link out nominal: buffer underflow",image[0]==SENTINEL);
  ASSERT_THAT(ctx,"sector link out nominal: buffer overflow",image[4]==SENTINEL);
  ASSERT_THAT(ctx,"sector link out nominal: pointer updated",p==image+4);



  p = image+1;

  image[0] = SENTINEL;
  image[1] = SENTINEL;
  image[2] = SENTINEL;
  image[3] = SENTINEL;
  image[4] = SENTINEL;

  sector_link_out(NO_LINK,TRACK,&p);

  ASSERT_THAT(ctx,"sector link out nominal: value zero",image[1]==NO_LINK);
  ASSERT_THAT(ctx,"sector link out nominal: value track",image[2]==NO_LINK);
  ASSERT_THAT(ctx,"sector link out nominal: value sector",image[3]==NO_LINK);
  ASSERT_THAT(ctx,"sector link out nominal: buffer underflow",image[0]==SENTINEL);
  ASSERT_THAT(ctx,"sector link out nominal: buffer overflow",image[4]==SENTINEL);
  ASSERT_THAT(ctx,"sector link out nominal: pointer updated",p==image+4);
}

void catalog_sector_out(uint8_t sector_number, uint8_t track, uint8_t **pp)
{
  /*
   * link to previous sector, except for last sector in
   * the sequence (sector 1) which has no link.
   */
  sector_link_out(sector_number-1,track,pp);
  /* rest of sector is zeroes */
  n_b_out(BYTES_PER_SECTOR-3,0,pp);
}



void volume_sector_map_out(uint_fast16_t version, uint8_t catalog_track, uint_fast8_t used, uint8_t **pp)
{
  uint8_t tr;
  for (tr = 0; tr < TRACKS_PER_DISK; ++tr)
    {
      uint16_t bitmap = get_free_track_map(version);
      if (tr==catalog_track)
        {
          /* catalog track is always fully allocated */
          allocate_n_sectors(sectors_per_track(version),&bitmap);
        }
      else
        {
          const uint_fast8_t u = MIN(used,sectors_per_track(version));
          allocate_n_sectors(u,&bitmap);
          used -= u;
        }
      map_out(bitmap,pp);
    }
}

/* B.A.D. p. 4-9 */
#define TS_OFFSET_IN_FILEMAP 0xC

/*
 * Maximum track/sector pairs in one sector
 * of a file's sector map.
 */
uint8_t get_max_ts_in_filemap()
{
  return (BYTES_PER_SECTOR-TS_OFFSET_IN_FILEMAP)/2;
}

void catalog_VTOC_out(uint_fast16_t version, uint8_t catalog_track, uint_fast8_t used, uint8_t volume, uint8_t **pp)
{
  sector_link_out(sectors_per_track(version)-1,catalog_track,pp);

  b_out(version/10%10,pp);
  n_b_out(2,0,pp);

  b_out(volume,pp);
  n_b_out(0x20,0,pp);
  b_out(get_max_ts_in_filemap(),pp);
  n_b_out(8,0,pp);

  b_out(catalog_track,pp);
  b_out(1,pp);
  n_b_out(2,0,pp);

  b_out(TRACKS_PER_DISK,pp);
  b_out(sectors_per_track(version),pp);
  w_out(BYTES_PER_SECTOR,pp);

  volume_sector_map_out(version,catalog_track,used,pp);

  n_b_out(60,0,pp);
}


uint8_t default_vtoc[0x100] =
{
  0x00, 0x11, 0x0f, 0x03, 0x00, 0x00, 0xfe, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7a, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x11, 0x01, 0x00, 0x00, 0x23, 0x10, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0xff, 0xe0, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00, 0xff, 0xff, 0x00, 0x00,
  0xff, 0xff
};

void test_catalog_VTOC_out(ctx_assertion *ctx)
{
  uint8_t computed_vtoc[0x100];
  uint8_t *p = computed_vtoc;
  uint_fast16_t i;

  catalog_VTOC_out(330,0x11,0x25,254,&p);

  for (i = 0; i < BYTES_PER_SECTOR; ++i)
    {
      ASSERT_THAT(ctx,"default VTOC",computed_vtoc[i]==default_vtoc[i]);
    }
}

void catalog_track_out(uint_fast16_t version, uint8_t catalog_track, uint_fast8_t used, uint8_t volume, uint8_t **pp)
{
  uint8_t sc;
  catalog_VTOC_out(version,catalog_track,used,volume,pp);
  for (sc = 1; sc < sectors_per_track(version); ++sc)
    {
      catalog_sector_out(sc,catalog_track,pp);
    }
}




void put_buffer(uint8_t *p, int c)
{
  int i = 0;
  SET_BINARY(fileno(stdout));
  while (i<c)
    {
      ++i;
      putchar(*p++);
    }
}


int run_program(struct opts_t *opts)
{
  const int c = sectors_per_track(opts->dos_version)*BYTES_PER_SECTOR*sizeof(uint8_t);
  uint8_t *t;
  uint8_t *x;
  x = t = malloc(c);

  catalog_track_out(opts->dos_version,opts->catalog_track,opts->used_sectors,opts->volume,&x);

  if (x != t+c)
    {
      fprintf(stderr,"%s\n","illegal program state");
      return EXIT_FAILURE;
    }

  put_buffer(t,c);

  free(t);

  return EXIT_SUCCESS;
}

int run_tests()
{
  ctx_assertion *ctx = ctx_assertion_factory();

  printf("running unit tests...\n");
  test_b_out(ctx);
  test_w_out(ctx);
  test_n_b_out(ctx);
  test_sectors_per_track(ctx);
  test_get_free_track_map(ctx);
  test_allocate_n_sectors(ctx);
  test_sector_link_out(ctx);
  test_catalog_VTOC_out(ctx);

  return count_failed_assertions(ctx) ? EXIT_FAILURE : EXIT_SUCCESS;
}




int main(int argc, char *argv[])
{
  struct opts_t *opts = parse_opts(argc,argv);

  if (opts->test)
    {
      return run_tests();
    }

  return run_program(opts);
}
