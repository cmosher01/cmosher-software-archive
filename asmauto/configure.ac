#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.61])
AC_INIT([epple2sys],[0.1])
AC_CONFIG_AUX_DIR([build-aux])
AC_CONFIG_SRCDIR([src/epple2sys.m4])
AM_INIT_AUTOMAKE([-Wall -Werror])



AC_PATH_PROGS([XA65],[xa65 xa])
if test -z "$ac_cv_path_XA65"
then
	AC_MSG_ERROR([Cannot find xa (xa65) 6502 cross assembler by Andre Fachat])
fi
AC_ARG_VAR([XA65],[path to xa (xa65) 6502 cross assembler by Andre Fachat])

AC_PATH_PROGS([LDO65],[ldo65])
if test -z "$ac_cv_path_LDO65"
then
	AC_MSG_ERROR([Cannot find ldo65, from xa (xa65) 6502 cross assembler by Andre Fachat])
fi
AC_ARG_VAR([LDO65],[path to ldo65, from xa (xa65) 6502 cross assembler by Andre Fachat])

AC_PATH_PROGS([RELOC65],[reloc65])
if test -z "$ac_cv_path_RELOC65"
then
	AC_MSG_ERROR([Cannot find reloc, from xa (xa65) 6502 cross assembler by Andre Fachat])
fi
AC_ARG_VAR([RELOC65],[path to reloc65, from xa (xa65) 6502 cross assembler by Andre Fachat])

AC_PATH_PROGS([M4],[m4])
if test -z "$ac_cv_path_M4"
then
	AC_MSG_ERROR([Cannot find m4])
fi
AC_ARG_VAR([M4],[path to m4])



AC_CONFIG_FILES([

Makefile
src/Makefile

])

AC_OUTPUT
