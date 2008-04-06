#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_INIT([bgen],[0.1],[chris@mosher.mine.nu])
AC_PREREQ(2.61)
AC_CONFIG_SRCDIR([bgen.c])
AM_CONFIG_HEADER(config.h)
AM_INIT_AUTOMAKE(bgen,0.1)

# Checks for programs.
AC_PROG_CPP
AC_PROG_CC
gl_EARLY
AC_PROG_RANLIB

# Checks for libraries.

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([libintl.h locale.h stdlib.h string.h unistd.h wchar.h])
gl_INIT

# Checks for typedefs, structures, and compiler characteristics.
AC_C_CONST
AC_C_INLINE
AC_TYPE_SIZE_T
AC_FUNC_MALLOC

# Checks for library functions.
AC_FUNC_MALLOC
AC_CHECK_FUNCS([setmode])
AC_CHECK_FUNCS([memset])
AC_CHECK_FUNCS([strchr])

AC_CONFIG_FILES([Makefile lib/Makefile])
AC_OUTPUT
