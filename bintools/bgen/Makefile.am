METASOURCES=AUTO
ACLOCAL_AMFLAGS = -I m4

bin_PROGRAMS=bgen

dist_man_MANS=bgen.1

AM_CPPFLAGS=$(all_includes) -I$(top_srcdir)/lib -I$(top_builddir)/lib

bgen_LDFLAGS=$(all_libraries)

LDADD = lib/libgnu.a

bgen_SOURCES = bgen.c

SUBDIRS=lib
