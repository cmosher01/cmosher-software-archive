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


/* TODO put hex/ascii stuff in separate file */
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
			fprintf(stderr,"too many hexadecimal bytes in input (expected %d)\n",oc);
			exit(1);
		}
		*p++ = x;
	}
	return c;
}

#define BUF_SIZ 3

int run_program(struct opts_t *opts) {
	uint8_t buf[BUF_SIZ];
	int c_remain;
	c_remain = scan_ascii_hex(buf,BUF_SIZ);
	if (c_remain) {
		fprintf(stderr,"not enough hexadecimal bytes in input (expected %d)\n",BUF_SIZ);
		exit(1);
	}
	print_ascii_hex(buf,BUF_SIZ);
	printf("\n");
	(void)opts;
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
