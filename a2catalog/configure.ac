#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.61])
AC_INIT([a2catalog],[0.1])
AC_CONFIG_AUX_DIR([build-aux])
AC_CONFIG_SRCDIR([src/aaaaaa_UUID_461ccdf9082a4497b2514fc0f448860b.c])
AC_CONFIG_HEADERS([config.h])
AM_INIT_AUTOMAKE([a2catalog],[0.1])

# Checks for programs.
AC_PROG_CC
gl_EARLY

# Checks for libraries.

# Checks for header files.
AC_HEADER_STDC
AC_HEADER_SYS_WAIT
AC_CHECK_HEADERS([OS.h fcntl.h inttypes.h libintl.h limits.h locale.h netdb.h stddef.h stdint.h stdlib.h string.h sys/param.h unistd.h wchar.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_HEADER_STDBOOL
AC_C_CONST
AC_TYPE_UID_T
AC_C_INLINE
AC_TYPE_INT32_T
AC_TYPE_OFF_T
AC_TYPE_SIZE_T
AC_TYPE_SSIZE_T
AC_TYPE_UINT16_T
AC_TYPE_UINT8_T

# Checks for library functions.
AM_GNU_GETTEXT([external])
AM_GNU_GETTEXT_VERSION([0.17])
AC_FUNC_MALLOC
AC_CHECK_FUNCS([getpagesize memset strchr])
gl_INIT

AC_CONFIG_FILES([Makefile
                 po/Makefile.in
                 lib/Makefile
                 src/Makefile])
AC_OUTPUT
