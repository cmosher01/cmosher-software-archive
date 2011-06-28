#pragma once

void gendif(const char *file_1, const char *file_2, const char *file_dif, int cMinMatch, int cMaxSearch);
void applydif(const char *file_1, const char *file_dif, const char *file_2);
