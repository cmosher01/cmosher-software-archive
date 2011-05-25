AM_CPPFLAGS=$(all_includes) -I$(top_builddir)/lib -I$(top_srcdir)/lib -std=c89 -pedantic -fno-asm -fno-builtin -Werror -Wall -Wextra

bin_PROGRAMS=a2catalog
a2catalog_SOURCES=\
    a2catalog.c\
    a2catalog_opt.c\
    a2catalog_opt.h\
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
    assert_that.c\
    assert_that.h
a2catalog_LDFLAGS=$(all_libraries)
a2catalog_LDADD = ../lib/libgnu.a

check_SCRIPTS=unit_test.sh
TESTS=$(check_SCRIPTS)

unit_test.sh:
	echo "./$(bin_PROGRAMS) --test" >$@
	chmod +x $@

CLEANFILES=unit_test.sh
