AM_CPPFLAGS=$(all_includes) -I$(top_builddir)/lib -I$(top_srcdir)/lib -std=c89 -pedantic -fno-asm -fno-builtin -Werror -Wall -Wextra

LDADD = ../lib/libgnu.a

bin_PROGRAMS=a2catalog
a2catalog_SOURCES=a2catalog.c assert_that.c assert_that.h
a2catalog_LDFLAGS=$(all_libraries)

check_SCRIPTS=unit_test.sh
TESTS=$(check_SCRIPTS)

unit_test.sh:
	echo "./$(bin_PROGRAMS) --test" >$@
	chmod +x $@

CLEANFILES=unit_test.sh
