#ifndef UUID_eeba25b1054b4b6aa5bbb7426a9ec7e1
#define UUID_eeba25b1054b4b6aa5bbb7426a9ec7e1

#include <stdint.h>
#include "assert_that.h"

void nibblize_5_3_alt_encode(const uint8_t **original, uint8_t **encoded);
void nibblize_5_3_alt_decode(const uint8_t **original, uint8_t **decoded);

void test_nibblize_5_3_alt(ctx_assertion *ctx);

#endif
