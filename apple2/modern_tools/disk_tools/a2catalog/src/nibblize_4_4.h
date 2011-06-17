#ifndef UUID_6881609590944a84af78213e0cc16550
#define UUID_6881609590944a84af78213e0cc16550

#include <stdint.h>
#include "assert_that.h"

uint16_t nibblize_4_4_encode(uint8_t n);
uint8_t nibblize_4_4_decode(uint16_t n);

void test_nibblize_4_4(ctx_assertion *ctx);

#endif
