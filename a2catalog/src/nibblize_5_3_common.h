#include <stdint.h>

#define GRP 0x33
#define BUF1_SIZ 0x0100
#define BUF2_SIZ (3 * GRP + 1)

extern const uint8_t xlate[];
extern uint8_t ulate[];

void build_ulate_table();
