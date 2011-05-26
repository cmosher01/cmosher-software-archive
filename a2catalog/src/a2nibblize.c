#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>

#include "assert_that.h"
#include "a2const.h"
#include "a2nibblize_opt.h"
#include "nibblize_4_4.h"
#include "nibblize_5_3.h"
#include "nibblize_5_3_alt.h"
#include "nibblize_6_2.h"


void print_ascii_hex(uint8_t *p, int c) {
	int i = 0;
	while (i<c) {
		++i;
		printf("%02x",*p++);
		if (!(i%0x10)) {
			printf("\n");
		}
	}
}

int scan_ascii_hex(uint8_t *p, int c) {
	int oc = c;
	unsigned int x;
	int c_match;
	while ((c_match = scanf("%2x",&x)) != EOF) {
		if (c_match < 1) {
			fprintf(stderr,"invalid hexadecimal digit in input: %c\n",getchar());
			exit(1);
		}
		if (!c--) {
			fprintf(stderr,"too many hexadecimal bytes in input (expected at most 0x%X)\n",oc);
			exit(1);
		}
		*p++ = x;
	}
	return c;
}





void b_out(uint8_t b, uint8_t **pp) {
	*(*pp)++ = b;
}

void w_out(uint16_t w, uint8_t **pp) {
	b_out(w,pp);
	w >>= 8;
	b_out(w,pp);
}

void n_b_out(uint n, uint8_t b, uint8_t **pp) {
	while (n--) {
		b_out(b,pp);
	}
}




typedef uint8_t sector_t[BYTES_PER_SECTOR];

#define SECTORS_PER_TRACK_16 0x10
typedef sector_t track16_t[SECTORS_PER_TRACK_16];
typedef track16_t disk16_t[TRACKS_PER_DISK];
#define DISK16_SIZE (BYTES_PER_SECTOR*SECTORS_PER_TRACK_16*TRACKS_PER_DISK)
#define NIB16_SIZE (TRACKS_PER_DISK*(0x30+SECTORS_PER_TRACK_16*(6+3+(4*2)+3+3+343+3+0x1B)+0x110))

#define SECTORS_PER_TRACK_13 0xD
typedef sector_t track13_t[SECTORS_PER_TRACK_13];
typedef track13_t disk13_t[TRACKS_PER_DISK];
#define DISK13_SIZE (BYTES_PER_SECTOR*SECTORS_PER_TRACK_13*TRACKS_PER_DISK)
#define NIB13_SIZE /*TODO*/

#define DIFF_SIZE (DISK16_SIZE-DISK13_SIZE)



void addr16out(uint8_t volume, uint8_t track, uint8_t sector, uint8_t **pp) {
	b_out(0xD5,pp);
	b_out(0xAA,pp);
	b_out(0x96,pp);
	w_out(nibblize_4_4_encode(volume),pp);
	w_out(nibblize_4_4_encode(track),pp);
	w_out(nibblize_4_4_encode(sector),pp);
	w_out(nibblize_4_4_encode(volume^track^sector),pp);
	b_out(0xDE,pp);
	b_out(0xAA,pp);
	b_out(0xEB,pp);
}

void data16out(const uint8_t *data, uint8_t **pp) {
	b_out(0xD5,pp);
	b_out(0xAA,pp);
	b_out(0xAD,pp);
	nibblize_6_2_encode(&data, pp);
	b_out(0xDE,pp);
	b_out(0xAA,pp);
	b_out(0xEB,pp);
}

void sect16out(uint8_t *data, uint8_t volume, uint8_t track, uint8_t sector, uint8_t **pp) {
	addr16out(volume, track, sector, pp);
	n_b_out(6, 0xFF, pp);
	data16out(data, pp);
	n_b_out(0x1B, 0xFF, pp);
}

static const uint8_t mp_sector16[] = { 0x0, 0x7, 0xE, 0x6, 0xD, 0x5, 0xC, 0x4, 0xB, 0x3, 0xA, 0x2, 0x9, 0x1, 0x8, 0xF };

void write16nib(uint8_t volume, disk16_t image16, uint8_t **pp) {
	uint8_t t;
	uint8_t s;
	for (t = 0; t < TRACKS_PER_DISK; ++t) {
		n_b_out(0x30, 0xFF, pp);
		for (s = 0; s < SECTORS_PER_TRACK_16; ++s) {
			sect16out(image16[t][mp_sector16[s]], volume, t, s, pp);
		}
		n_b_out(0x110, 0xFF, pp);
	}
}

int run_program(struct opts_t *opts) {
	int c_remain;
	uint8_t *image = malloc(DISK16_SIZE);
	c_remain = scan_ascii_hex(image,DISK16_SIZE);
	if (!c_remain) {
		uint8_t *pnib = malloc(NIB16_SIZE);
		uint8_t *onib = pnib;
		write16nib(opts->volume,*((disk16_t*)image),&pnib);
		pnib = onib;
		print_ascii_hex(pnib,NIB16_SIZE);
		free(pnib);
	} else if (c_remain==DIFF_SIZE) {
	} else {
		fprintf(stderr,"wrong number of hexadecimal bytes in input (expected 0x%X or 0x%X)\n",DISK16_SIZE,DISK13_SIZE);
		exit(1);
	}
	free(image);
	return EXIT_SUCCESS;
}

int run_tests() {
	ctx_assertion *ctx = ctx_assertion_factory();

	printf("running unit tests...\n");

	test_nibblize_4_4(ctx);
	test_nibblize_5_3(ctx);
	test_nibblize_5_3_alt(ctx);
	test_nibblize_6_2(ctx);

  return count_failed_assertions(ctx) ? EXIT_FAILURE : EXIT_SUCCESS;
}

int main(int argc, char *argv[]) {
	struct opts_t *opts = parse_opts(argc,argv);

	if (opts->test) {
		return run_tests();
	}

	return run_program(opts);
}
