GCC_WARNINGSX=\
 -Wall\
 -Wextra\
 -Wshadow\
 -Wpointer-arith\
 -Wcast-qual\
 -Wcast-align\
 -Wstrict-prototypes\
 -Wmissing-prototypes\
 -Wno-conversion\
 -Wformat-nonliteral\
 -Wformat-security

GCC_WARNINGS=\
 -fno-asm\
 -fno-builtin\
 -Werror\
 -std=c89\
 -pedantic\
 -Wall\
 -Wextra\
 -Wchar-subscripts\
 -Wcomment\
 -Wformat\
 -Wformat-y2k\
 -Wformat-contains-nul\
 -Wformat-extra-args\
 -Wformat-zero-length\
 -Wformat-nonliteral\
 -Wformat-security\
 -Wnonnull\
 -Winit-self\
 -Wimplicit-int\
 -Wimplicit-function-declaration\
 -Wimplicit\
 -Wignored-qualifiers\
 -Wmain\
 -Wmissing-braces\
 -Wmissing-include-dirs\
 -Wparentheses\
 -Wsequence-point\
 -Wreturn-type\
 -Wswitch\
 -Wswitch-default\
 -Wswitch-enum\
 -Wsync-nand\
 -Wtrigraphs\
 -Wunused-function\
 -Wunused-label\
 -Wunused-parameter\
 -Wunused-result\
 -Wunused-variable\
 -Wunused-value\
 -Wunused\
 -Wuninitialized\
 -Wunknown-pragmas\
 -Wpragmas\
 -Wstrict-aliasing=1\
 -Wstrict-overflow=4\
 -Warray-bounds\
 -Wdiv-by-zero\
 -Wno-system-headers\
 -Wfloat-equal\
 -Wno-traditional\
 -Wno-traditional-conversion\
 -Wdeclaration-after-statement\
 -Wundef\
 -Wendif-labels\
 -Wshadow\
 -Wlarger-than=32768\
 -Wunsafe-loop-optimizations\
 -Wpointer-arith\
 -Wtype-limits\
 -Wbad-function-cast\
 -Wc++-compat\
 -Wcast-qual\
 -Wcast-align\
 -Wwrite-strings\
 -Wclobbered\
 -Wno-conversion\
 -Wempty-body\
 -Wenum-compare\
 -Wjump-misses-init\
 -Wsign-compare\
 -Wsign-conversion\
 -Waddress\
 -Wlogical-op\
 -Waggregate-return\
 -Wattributes\
 -Wbuiltin-macro-redefined\
 -Wstrict-prototypes\
 -Wold-style-declaration\
 -Wold-style-definition\
 -Wmissing-parameter-type\
 -Wmissing-prototypes\
 -Wmissing-declarations\
 -Wmissing-field-initializers\
 -Wno-missing-format-attribute\
 -Wmultichar\
 -Wnormalized=nfc\
 -Wdeprecated\
 -Wdeprecated-declarations\
 -Woverflow\
 -Woverride-init\
 -Wpacked\
 -Wpacked-bitfield-compat\
 -Wredundant-decls\
 -Wnested-externs\
 -Winline\
 -Wint-to-pointer-cast\
 -Wpointer-to-int-cast\
 -Winvalid-pch\
 -Wlong-long\
 -Wvariadic-macros\
 -Wvla\
 -Wvolatile-register-var\
 -Wdisabled-optimization\
 -Wpointer-sign\
 -Wstack-protector\
 -Wmudflap\
 -Woverlength-strings\
 -Wunsuffixed-float-constants




AM_CPPFLAGS=$(all_includes) -I$(top_builddir)/lib -I$(top_srcdir)/lib $(GCC_WARNINGS)
AM_LDFLAGS=$(all_libraries)
LDADD = $(top_builddir)/lib/libgnu.a



bin_PROGRAMS=a2catalog a2nibblize

a2catalog_SOURCES=\
a2catalog.c\
a2catalog_opt.c\
a2catalog_opt.h\
a2const.h\
ctest.c\
ctest.h

a2nibblize_SOURCES=\
a2nibblize.c\
a2nibblize_opt.c\
a2nibblize_opt.h\
nibblize_4_4.c\
nibblize_4_4.h\
nibblize_5_3.c\
nibblize_5_3.h\
nibblize_5_3_alt.c\
nibblize_5_3_alt.h\
nibblize_5_3_common.c\
nibblize_5_3_common.h\
nibblize_6_2.c\
nibblize_6_2.h\
a2const.h\
ctest.c\
ctest.h



check_SCRIPTS=unit_test.sh
TESTS=$(check_SCRIPTS)

unit_test.sh:
	touch $@
	echo "./a2catalog --test" >>$@
	echo "./a2nibblize --test" >>$@
	chmod +x $@



CLEANFILES=unit_test.sh
