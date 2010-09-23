#include <getopt.h>

#include "cmdqued.h"

#include <iostream>

namespace ipc_cmdq
{

using std::cout;
using std::cerr;
using std::endl;
using std::map;
using std::string;

static struct option lopts[] =
{
    { "help",           no_argument,        0,  'h' },
    { "version",        no_argument,        0,  'V' },
    { "fifo",           required_argument,  0,  'f' },
    { "pidfile",        required_argument,  0,  'p' },
    { "logfacility",    required_argument,  0,  'l' },
    { "timeout",        required_argument,  0,  't' },
    { "debug",          no_argument,        0,  'd' },
    { "verbose",        no_argument,        0,  'v' },
    { "stop",           no_argument,        0,  's' },
    { 0, 0, 0, 0 }
};

static char sopts[] = "hVf:p:l:t:dvs";

static const char * help = 
"usage: cmdqued -f fifo [-p pidfile -l logfacility -t timeout -d]\n\
       cmdqued -V\n\
       cmdqued -h\n\
       cmdqued -s\n\
\n\
-f, --fifo          pathname for command fifo\n\
-p, --pidfile       pathname for pid file\n\
-l, --logfacility   syslog facility to use for logging\n\
-t, --timeout       how long to wait for a command to complete before terminating it\n\
-d, --debug         run in debugging mode, foreground, console and verbose logging\n\
-v, --verbose       turn on verbose logging\n\
-V, --version       print the version and exit\n\
-h, --help          print this help synopsis and exit\n\
-s, --stop          signal the daemon to shutdown normally\n\n";

int find_lopt( int sopt )
{
    for (int i=0; lopts[i].name; i++)
    {
        if (sopt == lopts[i].val)
            return i;
    }
    return -1;
}

map<string, string> parse_args( int argc, char **argv )
{
    int                 opt=-1, ndx=-1;
    map<string, string> args;

    opterr = 1;
    do
    {
        ndx = -1;
        opt = getopt_long( argc, argv, sopts, lopts, &ndx );

        switch (opt)
        {
            case ':':
                cerr << "missing required argument to " << lopts[ndx].name << " option" << endl;
                exit( 1 );
                break;
            case '?':
                cerr << "unknown option " << static_cast<char>(lopts[ndx].val) << " (" << lopts[ndx].name << "}" << endl;
                exit( 2 );
                break;
            case 'V':
                cout << basename( argv[0] ) << ": version " << cmd_queue::version( ) << endl;
                exit( 0 );
                break;
            case 's':
                cerr << "need to implement stop command!" << endl;
                exit( 3 );
            case 'h':
                cout << help;
                exit( 0 );
                break;
            case -1:
                break;
            default:
                if (ndx == -1)
                    ndx = find_lopt( opt );
                if (ndx == -1)
                {
                    cerr << "fatal error parsing args" << endl;
                    exit( 4 );
                }
                args.insert( make_pair( string( lopts[ndx].name ), string( (optarg)?optarg:"" ) ) );
                break;
        }
    }
    while (opt != -1);

    return args;
};

} // namespace ipc_cmdq

int main( int argc, char **argv )
{
    ipc_cmdq::cmd_queue cq( ipc_cmdq::parse_args( argc, argv ) );
    return cq.run( );
}

