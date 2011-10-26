METASOURCES=AUTO
ACLOCAL_AMFLAGS = -I m4

bin_PROGRAMS=sdd

dist_man_MANS=sdd.1

AM_CPPFLAGS=$(all_includes) -I$(top_srcdir)/lib -I$(top_builddir)/lib

sdd_LDFLAGS=$(all_libraries)

LDADD = lib/libgnu.a

sdd_SOURCES = sdd.c

SUBDIRS=lib
