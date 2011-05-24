#ifndef UUID_e4c1853235df4bef809f233965122e73
#define UUID_e4c1853235df4bef809f233965122e73

#include <stdint.h>

typedef unsigned int uint;

struct opts_t {
	int test;
	uint dos_version;
	uint8_t catalog_track;
	uint used_sectors;
	uint8_t volume;
};

struct opts_t *parse_opts(int argc, char *argv[]);

#endif
