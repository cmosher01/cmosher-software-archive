#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "a2catalog_opt.h"

#include <stdlib.h>
#include <stdio.h>
#include <getopt.h>

#include "a2const.h"

static const char shortopts[] = "d:hTt:u:Vv:";

static const struct option longopts[] =
  {
    {"dos",required_argument,0,'d'},
    {"help",no_argument,0,'h'},
    {"test",no_argument,0,'T'},
    {"track",required_argument,0,'t'},
    {"used",required_argument,0,'u'},
    {"version",no_argument,0,'V'},
    {"volume",required_argument,0,'v'},
    {0,0,0,0}
  };

static void help(int argc, char *argv[])
{
  (void)argc;
  printf("Writes an Apple ][ floppy disk-format catalog track.\n");
  printf("The track is written as logical ASCII hexadecimal bytes\n");
  printf("to standard output. The format is designed to be read by\n");
  printf("xxd to convert to binary format for writing to a disk image.\n");
  printf("For example:\n");
  printf("    %s | xxd -r -ps >>a2floppy.do\n",argv[0]);
  printf("\n");
  printf("Usage: %s [OPTION...]\n",argv[0]);
  printf("Options:\n");
  printf("  -d, --dos=DOSVERS    DOS version: 310,320,321,330,331,332 (default 330)\n");
  printf("  -h, --help           shows this help\n");
  printf("  -T, --test           runs all unit tests\n");
  printf("  -t, --track=TRACK    catalog track number, default 0x11\n");
  printf("  -u, --used=SECTORS   number of sectors to allocate for DOS, default 0x25\n");
  printf("  -V, --version        shows version information\n");
  printf("  -v, --volume=VOLUME  \"DISK VOLUME\" to use, default 254\n");
}

static struct opts_t *opts_factory()
  {
    struct opts_t *opts = malloc(sizeof(struct opts_t));

    opts->test = 0;
    opts->dos_version = 330; /* DOS 3.3 (.0) */
    opts->catalog_track = TRACKS_PER_DISK/2; /* middle of the disk */
    opts->used_sectors = 0x25; /* DOS-occupied sectors only */
    opts->volume = 254; /* as in "DISK VOLUME 254" */

    return opts;
  }

static void version()
{
  printf("%s\n",PACKAGE_STRING);
  printf("\n");
  printf("%s\n","Copyright (C) 2011, by Chris Mosher.");
  printf("%s\n","License GPLv3+: GNU GPL version 3 or later <http://www.gnu.org/licenses/gpl.html>.");
  printf("%s\n","This is free software: you are free to change and redistribute it.");
  printf("%s\n","There is NO WARRANTY, to the extent permitted by law.");
}

static long get_num_optarg()
{
  return strtol(optarg,0,0);
}

struct opts_t *parse_opts(int argc, char *argv[])
  {
    int c;

    struct opts_t *opts = opts_factory();

    while ((c = getopt_long(argc,argv,shortopts,longopts,0)) >= 0)
      {
        switch (c)
          {
          case 'd':
            opts->dos_version = get_num_optarg();
            break;
          case 'u':
            opts->used_sectors = get_num_optarg();
            break;
          case 'T':
            opts->test = 1;
            break;
          case 't':
            opts->catalog_track = get_num_optarg();
            break;
          case 'V':
            version();
            exit(0);
            break;
          case 'v':
            opts->volume = get_num_optarg();
            break;
          case 0:
            break;
          case 'h':
          default:
            help(argc,argv);
            exit(0);
          }
      }

    return opts;
  }
