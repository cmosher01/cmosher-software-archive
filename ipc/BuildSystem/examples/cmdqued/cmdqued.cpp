#define SYSLOG_NAMES
#ifndef NULL
#define NULL 0
#endif
#include <syslog.h>
#undef NULL
#include <stdio.h>
#include <fcntl.h>
#include <errno.h>
#include <time.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <signal.h>

#include "cmdqued.h"

#include <iostream>
#include <sstream>
#include <fstream>

namespace ipc_cmdq
{

using std::string;
using std::map;
using std::cout;
using std::cerr;
using std::endl;
using std::stringstream;
using std::ends;
using std::ofstream;

const char * cmd_queue::s_version = "0.99.0";
bool cmd_queue::m_exit = false;
bool cmd_queue::m_restart = false;
bool cmd_queue::m_cmdexit = false;

cmd_queue::cmd_queue( std::map< std::string, std::string > args ) 
    : m_args( args ), m_fifo( -1 ), m_debug( false ), m_verbose( false )
{
}

cmd_queue::~cmd_queue( )
{
    close_fifo( );
}

int cmd_queue::run( )
{
    map<string,string>::iterator it = m_args.find( string("logfacility" ) );

    if (it != m_args.end( ))
        open_log( it->second );
    else
        open_log( LOG_DAEMON );

    m_verbose = (m_args.find( string("verbose") ) == m_args.end( )) ? false : true;
    
    if (m_args.find( string("debug") ) == m_args.end( ))
        return daemonize( );
    else
    {
        m_debug = true;
        return do_child( );
    }
}

exit_code cmd_queue::daemonize( )
{
    if (m_verbose)
        log( LOG_NOTICE, "deamon starting in verbose mode" );
    else
        log( LOG_NOTICE, "deamon starting" );
    
    pid_t pid = fork( );
    if (pid == -1)
    {
        log( LOG_ALERT, "error forking new session leader process" );
        return syscall;
    }
    if (pid != 0)
        exit( normal );

    if (setsid( ) == -1)
    {
        log( LOG_ALERT, "error settting session leader" );
        return syscall;
    }

    for (int i=0; i<255; i++)
        close( i );

    pid = fork( );
    if (pid == -1)
    {
        log( LOG_ALERT, "error forking monitor process" );
        return syscall;
    }
    if (pid != 0)
        exit( normal );

    exit_code ret = normal;
    try
    {
        init_monitor( );
        ret = do_monitor( );
    }
    catch( cmdq_exception &ex )
    {
        log( ex.prio( ), ex.what( ) );
        ret =  ex.code( );
    }
    cleanup_monitor( );

    return normal;
}

void cmd_queue::exit_sig( int /*sig*/ )
{
    m_exit = true;
}

void cmd_queue::restart_sig( int /*sig*/ )
{
    m_restart = true;
}

void cmd_queue::init_monitor( )
{
    create_pidfile( );
    create_fifo( );

    struct sigaction sa;
    memset( &sa, 0, sizeof( sa ) );
    sa.sa_flags = SA_NOCLDSTOP;

    sa.sa_handler = exit_sig;
    sigaction( SIGINT, &sa, 0 );
    sigaction( SIGQUIT, &sa, 0 );
    sigaction( SIGTERM, &sa, 0 );

    sa.sa_handler = restart_sig;
    sigaction( SIGHUP, &sa, 0 );

    sa.sa_handler = SIG_IGN;
    sigaction( SIGUSR1, &sa, 0 );
    sigaction( SIGUSR2, &sa, 0 );
}

void cmd_queue::create_pidfile( )
{
    map<string,string>::iterator it = m_args.find( string("pidfile" ) );
    if (it == m_args.end( ))
        return;

    ofstream of( it->second.c_str( ) );
    if (of.fail( ))
    {
        log( LOG_ERR, "unable to create pid file" );
        return;
    }
    of << getpid( );
    if (of.fail( ))
        log( LOG_ERR, "unable to write pid to pid file" );
}

void cmd_queue::unlink_pidfile( )
{
    map<string,string>::iterator it = m_args.find( string("pidfile" ) );
    if (it != m_args.end( ))
        unlink( it->second.c_str( ) );
}

void cmd_queue::create_fifo( )
{
    log( LOG_INFO, "creating fifo" );

    map<string,string>::iterator it = m_args.find( string("fifo" ) );
    if (it == m_args.end( ))
        throw cmdq_exception( LOG_ALERT, "required option -f (--fifo) missing", missing_option );

    struct stat sb;
    if (stat( it->second.c_str( ), &sb ))
    {
        if (errno != ENOENT)
            throw cmdq_exception( LOG_ALERT, "invalid fifo pathname", invalid_argument );
        if (mkfifo( it->second.c_str( ), S_IRUSR | S_IWUSR | S_IWGRP | S_IWOTH ))
            throw cmdq_exception( LOG_ALERT, "error creating command fifo", syscall );
        log( LOG_INFO, "created fifo" );
    }
    else
    {
        if (!S_ISFIFO( sb.st_mode))
            throw cmdq_exception( LOG_ALERT, "fifo pathname exists, and is not a fifo", invalid_argument );
        log( LOG_INFO, "found existing fifo" );
    }
}

void cmd_queue::unlink_fifo( )
{
    log( LOG_INFO, "unlinking fifo" );
    map<string,string>::iterator it = m_args.find( string("fifo" ) );
    if (it != m_args.end( ))
        unlink( it->second.c_str( ) );
}

void cmd_queue::cleanup_monitor( )
{
    unlink_fifo( );
    unlink_pidfile( );
}

exit_code cmd_queue::do_monitor(  )
{
    time_t      now, then;
    int         quickfail, gens;

    log( LOG_NOTICE, "monitor process started" );

    gens = 0;
    quickfail = 0;
    do
    {
        ++gens;
        time( &then );

        pid_t pid = fork( );
        if (pid == -1)
        {
            log( LOG_ALERT, "error forking worker process" );
        }
        else if (pid == 0)
            exit( do_child( ) );
        else
        {
            stringstream ss;
            ss << "forking worker process pid: " << pid << ", generation " << gens << ends;
            log( LOG_NOTICE, ss.str( ).c_str( ) );
            try
            {
                monitor_loop( pid );
            }
            catch( cmdq_exception &ex )
            {
                log( ex.prio( ), ex.what( ) );
                return ex.code( );
            }
        }

        if (m_exit)
            continue;

        time( &now );
        if (now - then < 2)
        {
            if (quickfail == 5)
                log( LOG_ERR, "worker processes spawning to quickly, starting backoff" );
            if (quickfail >= 5)
            {
                log( LOG_WARNING, "quick reqpawning detected, sleeping before restart" );
                sleep( 30 );
            }
            else
                ++quickfail;
        }
        else
        {
            quickfail = 0;
            sleep( 1 );
        }
    }
    while (!m_exit);

    log( LOG_NOTICE, "monitor process received shutdown signal, daemon normally" );
    return normal;
}

void cmd_queue::monitor_loop( pid_t pid )
{
    bool    childrunning = true;

    while (childrunning)
    {
        int status = 0;
        int ret = waitpid( pid, &status, WUNTRACED );
        if (ret == -1)
        {
            switch (errno)
            {
                case ECHILD:
                    log( LOG_DEBUG, "caught SIGCHLD in waitpid for worker process" );
                    break;
                case EINVAL:
                    throw cmdq_exception( LOG_ALERT, "invalid arguments to waitpid", badbug );
                    break;
                case EINTR:
                    log( LOG_INFO, "caught signal waiting for worker process exit" );
                    if (m_restart)
                        write_child_exit( );
                    if (m_exit)
                    {
                        log( LOG_NOTICE, "caught exit signal, daemon exiting normally" );
                        write_child_exit( );
                    }
                    break;
                default:
                    throw cmdq_exception( LOG_ALERT, "some undocumented errno from waitpid!", syscall );
                    break;
            }
        }
        else if (ret == 0)
        {
            throw cmdq_exception( LOG_ALERT, "zero return from waitpid, should not happen!", syscall );
        }
        else
        {
            if (WIFEXITED( status ))
            {
                if (WEXITSTATUS( status ))
                    log( LOG_ERR, "worker process exited on error condition" );
                else
                    log( LOG_NOTICE, "worker process exited normally" );
                childrunning = false;
            }
            else if (WIFSIGNALED( status ))
            {
                log( LOG_WARNING, "worker process exit on uncaught signal" );
                childrunning = false;
            }
            else if (WIFSTOPPED( status ))
            {
                if (m_debug)
                    log( LOG_WARNING, "worker process stopped" );
                else
                {
                    log( LOG_ERR, "worker process stopped, worker will be killed" );
                    kill( pid, SIGKILL );
                }
            }
        }
    }
}

void cmd_queue::write_child_exit( )
{
    map<string,string>::iterator it = m_args.find( string("fifo" ) );
    if (it == m_args.end( ))
        throw cmdq_exception( LOG_ALERT, "required option -f (--fifo) missing", missing_option );

    ofstream of( it->second.c_str( ) );
    if (of.fail( ))
    {
        log( LOG_ERR, "unable to open command fifo for exit command" );
        return;
    }
    log( LOG_NOTICE, "writing exit command to command fifo" );
    of << "-exit" << endl;
}

void cmd_queue::cmd_exit_sig( int /*sig*/ )
{
    m_cmdexit = true;
}

exit_code cmd_queue::do_child( )
{
    int     timeout = 0;
    
    log( LOG_NOTICE, "worker process starting" );

    struct sigaction sa;
    memset( &sa, 0, sizeof( sa ) );
    sa.sa_flags = SA_NOCLDSTOP;
    sa.sa_handler = cmd_exit_sig;
    sigaction( SIGCHLD, &sa, 0 );

    map<string,string>::iterator it = m_args.find( string("timeout" ) );
    if (it != m_args.end( ))
        timeout = atoi( it->second.c_str( ) );

    try
    {
        open_fifo( );
        child_loop( timeout );
    }
    catch( cmdq_exception &ex )
    {
        log( ex.prio( ), ex.what( ) );
        log( LOG_WARNING, "worker process abend" );
        close_fifo( );
        return ex.code( );
    }

    close_fifo( );
    log( LOG_NOTICE, "worker process exiting normally" );
    return normal;
}

void cmd_queue::child_loop( int timeout )
{
    string      cmd;

    do
    {
        getline( cmd );

        if (!cmd.compare( "-exit" ))
        {
            log( LOG_INFO, "worker process exit on exit command" );
            return;
        }

        if (m_debug || m_verbose)
        {
            stringstream ss;
            ss << "executing command: " << cmd.c_str( ) << ends;
            log( LOG_INFO, ss.str( ).c_str( ) );
        }

        int ret = (timeout > 0) ? supervise( cmd.c_str( ), timeout ) : system( cmd.c_str( ) );
        if (ret == -1)
        {
            throw cmdq_exception( LOG_ERR, "error forking for command execution", syscall );
        }

        if (!WIFEXITED( ret ))
        {
            stringstream    ss;
            ss << "abnormal command termination: " << cmd << ends;
            log( LOG_WARNING, ss.str( ).c_str( ) );
        }
    }
    while (1);
}

void cmd_queue::getline( std::string &cmd ) const
{
    cmd.erase( );

    char  in[2];
    while (1)
    {
        switch (read( m_fifo, in, 1 ))
        {
            case 1:
                if (in[0] != '\n')
                    cmd += in[0];
                else
                    return;
                break;
            case -1:
                if (errno == EINTR)
                    throw cmdq_exception( LOG_WARNING, "select on fifo interrupted by signal", signalled );
                throw cmdq_exception( LOG_ERR, "error reading from fifo", syscall );
                break;
            case 0:
                fifo_select( );
                break;
            default:
                throw cmdq_exception( LOG_ALERT, "got read > 1 reading fifo", badbug );
                break;
        }
    }
}

void cmd_queue::fifo_select( ) const
{
    fd_set  rfds, efds;

    FD_ZERO( &rfds );
    FD_ZERO( &efds );
    FD_SET( m_fifo, &rfds );
    FD_SET( m_fifo, &efds );

    struct timeval tv;
    tv.tv_sec = 3;
    tv.tv_usec = 0;

    switch (select( m_fifo+1, &rfds, 0, &efds, &tv ))
    {
        case -1:
            if (errno == EINTR)
                throw cmdq_exception( LOG_WARNING, "select on fifo interrupted by signal", signalled );
            throw cmdq_exception( LOG_ERR, "fatal error from selec on fifo", syscall );
            break;
        case 0:
            throw cmdq_exception( LOG_ERR, "timeout reading command from fifo, no newline read", badbug );
            break;
        default:
            if (FD_ISSET( m_fifo, &efds ))
                throw cmdq_exception( LOG_ERR, "select detected error on fifo", syscall );
            else if (!FD_ISSET( m_fifo, &rfds ))
                throw cmdq_exception( LOG_ALERT, "select returned an unknown fd waiting for fifo", badbug );
            break;
    }
}

int cmd_queue::supervise( const char *cmd, int timeout )
{
    pid_t pid = fork( );

    if (pid == -1)
        throw cmdq_exception( LOG_ERR, "unable to fork command process", syscall );

    if (pid == 0)
    {
        execlp( "/bin/sh", "/bin/sh", "-c", cmd, 0 );
        throw cmdq_exception( LOG_ERR, "error from exec of command shell", badbug );
    }

    m_cmdexit = false;
    
    struct timeval tv;
    tv.tv_sec = timeout;
    tv.tv_usec = 0;
    if (select( 0, 0, 0, 0, &tv ) == 0)
        kill( pid, SIGKILL );

    while (1)
    {
        int status = 0;

        int ret = waitpid( pid, &status, WUNTRACED );
        if (ret == 0)
            throw cmdq_exception( LOG_ERR, "invalid command pid after SIGCHLD", badbug );
        if (ret == -1)
        {
            if (errno == EINTR)
                continue;
            throw cmdq_exception( LOG_ERR, "error from command waitpid", badbug );
        }
        return status;
    }
}

void cmd_queue::open_fifo( )
{
    close_fifo( );

    log( LOG_INFO, "opening fifo" );
    map<string,string>::iterator it = m_args.find( string("fifo" ) );
    if (it == m_args.end( ))
        throw cmdq_exception( LOG_ALERT, "required option -f (--fifo) missing", missing_option );

    struct stat sb;
    if (stat( it->second.c_str( ), &sb ))
    {
        throw cmdq_exception( LOG_ERR, "can't open fifo, fifo not found", invalid_argument );
    }
    else
    {
        if (!S_ISFIFO( sb.st_mode))
            throw cmdq_exception( LOG_ALERT, "can't open fifo, pathname given is not a fifo", invalid_argument );
        log( LOG_INFO, "found existing fifo" );
    }
    m_fifo = open( it->second.c_str( ), 0 );
    if (m_fifo == -1)
    {
        close_fifo( );
        throw cmdq_exception( LOG_ALERT, "error opening command fifo", syscall );
    }
    log( LOG_INFO, "fifo opened" );
}

void cmd_queue::close_fifo( )
{
    if (m_fifo != -1)
    {
        log( LOG_INFO, "closing fifo" );
        close( m_fifo );
    }
    m_fifo = -1;
}

void cmd_queue::open_log( const std::string &fac ) const
{
    for (int i=0; facilitynames[i].c_name; i++)
    {
        if (!fac.compare( facilitynames[i].c_name ))
        {
            open_log( facilitynames[i].c_val );
            return;
        }
    }
    open_log( LOG_DAEMON );
}

void cmd_queue::open_log( int facility ) const
{
    openlog( "cmdqued", (m_debug) ? LOG_PERROR : LOG_PID, facility );
}

void cmd_queue::log( int prio, const char *msg ) const
{
    if ((prio > LOG_NOTICE) && !(m_debug || m_verbose))
        return;
    if (m_debug)
    {
        cerr << "cmdqued[" << getpid( ) << "] (" << get_prio( prio ) << "): " << msg << endl;
    }
    syslog( prio, msg );
}

const char * cmd_queue::get_prio( int prio ) const
{
    for (int i=0; prioritynames[i].c_name; i++)
    {
        if (prio == prioritynames[i].c_val)
            return prioritynames[i].c_name;
    }
    return "unknown";
}

} // namespace ipc_cmdq

