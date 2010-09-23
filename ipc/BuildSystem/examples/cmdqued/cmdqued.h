#ifndef IPC_CMDQ_CMDQUED_H
#define IPC_CMDQ_CMDQUED_H

#include <string>
#include <map>
#include <exception>

#include <unistd.h> // for pid_t

namespace ipc_cmdq
{

typedef enum { normal, missing_option, syscall, invalid_argument, badbug, signalled } exit_code;

class cmdq_exception : public std::exception
{
    public:
        cmdq_exception( int prio, const char *what, exit_code code ) throw( ) : 
            m_prio( prio ), m_what( what ), m_code( code )
        { }
        virtual ~cmdq_exception( ) throw( ) { }
        virtual const char *what( ) const throw( ) { return m_what.c_str( ); }
        int prio( ) const throw( ) { return m_prio; }
        exit_code code( ) const throw( ) { return m_code; }

    private:
        int         m_prio;
        std::string m_what;
        exit_code   m_code;
};

class cmd_queue
{
    public:
        cmd_queue( std::map<std::string, std::string> args );
        ~cmd_queue( );

        int     run( );

        static const char * version( ) { return s_version; }
        static void         exit_sig( int sig );
        static void         restart_sig( int sig );
        static void         cmd_exit_sig( int sig );

    private:
        cmd_queue( );
        cmd_queue( const cmd_queue& o );
        cmd_queue & operator=( const cmd_queue& o );


        exit_code       daemonize( );
        void            init_monitor( );
        void            create_pidfile( );
        void            unlink_pidfile( );
        void            create_fifo( );
        void            unlink_fifo( );
        void            cleanup_monitor( );
        exit_code       do_monitor( );
        void            monitor_loop( pid_t pid );
        void            write_child_exit( );
        exit_code       do_child( );
        void            child_loop( int timeout );
        void            getline( std::string &cmd ) const;
        void            fifo_select( ) const;
        int             supervise( const char *cmd, int timeout );
        void            open_fifo( );
        void            close_fifo( );
        void            open_log( const std::string &fac ) const;
        void            open_log( int fac ) const;
        void            log( int prio, const char * msg ) const;
        const char *    get_prio( int prio ) const;

    private:
        static const char *                 s_version;
        static bool                         m_exit;
        static bool                         m_restart;
        static bool                         m_cmdexit;

        std::map<std::string, std::string>  m_args;
        int                                 m_fifo;
        bool                                m_debug;
        bool                                m_verbose;
};

} // namespace ipc_cmdq

/**
    @enum   ipc_cmdq::exit_code
    @brief  exit codes used by cmdq_exception and cmd_queue to mark reasons for
            parent or child termination.

    @var    ipc_cmdq::exit_code::normal   
    @brief  normal exit
    @var    ipc_cmdq::exit_code::missing_option
    @brief  a required option was not given on the command line
    @var    ipc_cmdq::exit_code::syscall
    @brief  a system call returned an un-exceptional error
    @var    ipc_cmdq::exit_code::invalid_argument
    @brief  an invalid argument was given for an option, or a mandatory
            argument was missing.
    @var    ipc_cmdq::exit_code::badbug
    @brief  an unexpected error was returned by a system call, indicating
            either a serious logic error in the program, or an seriously
            scrozzled environment
    @var    ipc_cmdq::exit_code::signalled
    @brief  a process exited due to a caught signal.
**/

/**
    @class  ipc_cmdq::cmdq_exception
    @brief  Class thrown for all exceptions encountered in cmdqued
    cmdq_execption adds two methods to std::exception.

    @fn ipc_cmdq::cmdq_exception::cmdq_exception( int prio, const char *what, exit_code code ) throw( );
    @brief  construct a cmdq_exception
    @param  prio the syslog(3) priority to use when logging this exception
    @param  what the string description of this exception
    @param  code the value to return from main, or to pass to exit if this
            exception will cause the program to exit

    @fn const char * ipc_cmdq::cmdq_exception::what( ) const throw( );
    @brief  return the string description of this exception
    @return the string description of this exception

    @fn int ipc_cmdq::cmdq_exception::prio( ) const throw( );
    @brief  return the syslog(3) priority to use when logging this exception
    @return the syslog(3) priority to use when logging this exception

    @fn exit_code ipc_cmdq::cmdq_exception::code( ) const throw( );
    @brief  return the value to return from main, or to pass to exit if this
            exception will cause the program to exit
    @return the exit value for return or exit
**/ 

/**
    @class  ipc_cmdq::cmd_queue
    @brief  The class that does it all :)

    The cmd_queue class embodies all logic for the cmdqued daemon, exception for main() and
    command line parsing.  cmd_queue normally operates as a daemon, but can be run as a
    foreground process for debugging.  The daemon acts as a monitor, and forks a monitored
    child, called the worker process, to do the actual work of the daemon.  The child reads
    a fifo one line at a time, and interprets each line as a command to be passed to system(3)
    or exec( "/bin/sh"...)(2) as determined by command line options.

    The basic theory of operation is that any error results in an exception being thrown.
    In the case of the monitor process, this will only happen on an invalid startup, or
    on an unrecoverable system error during runtime.  In the case of the worker proces, any
    error will result in an exception, which will in turn cause the termination of the worker
    process.  As the monitor will promptly restart the the worker, the only impact is the
    loss of the particular external command then in progress.  As this would have been lost
    anyway, this is the cleanest and simplest way to go.  Any exceptions are written as
    diagnostics to the system log.

    The daemon can be run in a debuging mode.  In this case, the run( ) method does not
    call the daemonize( ) method, as in the normal case.  Instead, it just calls the do_child( )
    method to directly create a worker process in the foreground.  The semantics of the 
    worker process remain the same.  When run in this mode, verbose logging is implied,
    and all diagnostics are written to standard error, as well as to the system log.

    @fn     ipc_cmdq::cmd_queue::cmd_queue( std::map< std::string, std::string > args );
    @brief  construct a cmd_queue object passing in a map of command line options
    @param  args an std::map of command line options.  The keys are the long form of the
            options from the command line.  If the option had no argument, then the value
            is an empty string, else the value is the argument to the option.

    @fn     int ipc_cmdq::cmd_queue::run( );
    @brief  the top level function, and only public function of cmd_queue.

    @fn     ipc_cmdq::cmd_queue::~cmd_queue
    @brief  the destructor - has the side effect of unlinking the fifo

    @fn     static const char * ipc_cmdq::cmd_queue::version( );
    @brief  returns the version number

    @fn     static void ipc_cmdq::cmd_queue::exit_sig( int sig );
    @brief  signal handler for signals causing a normal exit of the daemon

    @fn     static void ipc_cmdq::cmd_queue::restart_sig( int sig );
    @brief  signal handler for signals causing a restart of the worker process

    @fn     static void ipc_cmdq::cmd_queue::cmd_exit_sig( int sig );
    @brief  signal handler for SIGCHLD from the execed cmd process used by the
            worker when the worker is using exec(2) for executing commands.

    @fn     ipc_cmdq::cmd_queue::cmd_queue( );
    @brief  private to prevent usage
    @fn     ipc_cmdq::cmd_queue::cmd_queue( const ipc_cmdq::cmd_queue &o );
    @brief  private to prevent usage
    @param  o reference to cmd_queue object
    @fn     ipc_cmdq::cmd_queue & operator = ( const ipc_cmdq::cmd_queue &o );
    @brief  private to prevent usage
    @param  o reference to cmd_queue object
    @return reference to cmd_queue object

    @fn     ipc_cmdq::exit_code ipc_cmdq::cmd_queue::daemonize( );
    @brief  turns the process into a daemon, and calls init_monitor( ), do_monitor( )
    @return return will cause the daemon to exit, with the return code of daemonize( )

    @fn     void ipc_cmdq::cmd_queue::init_monitor( );
    @brief  initializes the monitor process.
    
            Calls create_pidfile( ) and create_fifo( ), and sets signal handlers.

    @fn     void ipc_cmdq::cmd_queue::create_pidfile( );
    @brief  creates a file and writes the monitor process pid to it.
    
            If the pidfile cannot be created, the error is logged, but the daemon
            continues with normal operation.  The pidfile pathname is supplied
            as an argument to a command line option.  If none is specified, then
            no pidfile will be created.

    @fn     void ipc_cmdq::cmd_queue::unlink_pidfile( );
    @brief  unlink(2) the pidfile

    @fn     void ipc_cmdq::cmd_queue::create_fifo( );
    @brief  create the command fifo.
    
            The pathname of the command fifo is specified as a mandatory argument
            to a mandatory command line option.  A missing or invalid option is a
            fatal error.  If a file with the specified pathname already exists but
            is not a fifo, it is a fatal error.  If it is a fifo, it is used.  If
            the existing fifo has wrong permissions, it is a fatal error.

    @fn     void ipc_cmdq::cmd_queue::unlink_fifo( );
    @brief  unlink(2) the command fifo

    @fn     void ipc_cmdq::cmd_queue::cleanup_monitor( );
    @brief  clean up the monitor process on daemon shut down.
    
            Calls unlink_fifo( ) and unlink_pidfile( )

    @fn     ipc_cmdq::exit_code ipc_cmdq::cmd_queue::do_monitor( )
    @brief  the top level method for the monitor process.
    @return the exit code for the monitor process.  This exit code will be returned
            by daemonize( ), hence by run( ), and ultiimately will the the exit
            code for the daemon.

            This loop spawns the worker process, and watches for either a commanded
            exit or for an unexpected worker process death by calling monitor_loop( ).
            monitor_loop( ) returns on any worker child termination.  do_monitor will
            spawn a new worker unless the termination is due to a commanded exit of
            the daemon, in which case this method will return the exit code.

    @fn     void ipc_cmdq::cmd_queue::monitor_loop( pid_t pid )
    @brief  loop to monitor child death, handling signals caught
    @param  pid the pid of the worker process spawned by do_monitor( ).

            This method blocks at waitpid(2) on the worker process pid.  A loop is
            needed to handle non-child-death returns from waitpid(2) caused by
            caught signals.  If the caught signal is for a worker restart or for,
            the shutdown of the daemon, the worker is terminated by a call to 
            write_child_exit( ), and the loop returns to waitpid(2) to reap the child.
            A return from waitpid(2) for worker child death will cause this loop to
            exit and the method to return.

    @fn     void ipc_cmdq::cmd_queue::write_child_exit( );
    @brief  commands the worker child to exit by writing to the command fifo.

            This shutdown mechanism will restult in a shutdown of the child only
            after all currently queued commands have been executed.

    @fn     void ipc_cmdq::cmd_queue::cmd_exit_sig( int sig )
    @brief  handle SIGCHLD from a supervised exec(2) of a requested command
    @param  sig ignored

    @fn     ipc_cmdq::exit_code ipc_cmdq::cmd_queue::do_child( );
    @brief  The top level method for the worker child process
    @return the exit code of the worker process, only informative, not dispositive.

            This method is called by do_monitor when the monitor forks a new
            worker child process.  It sets the signal handler for SIGCHLD then
            calls open_fifo( ) to open the command fifo, followed by child_loop( ),
            the main loop of the worker process.  On worker process exit, calls
            close_fifo( ) to close the command fifo.

    @fn     void ipc_cmdq::cmd_queue::child_loop( int timeout )
    @brief  The main loop for the worker process

            This is the main loop for the worker process.  It reads a line from the
            command fifo, and interprets that line as a command to be executed by
            a shell.  The loop blocks at the read command if there is no input in
            the fifo.  Reading is done by a call to the method getline( ).

            Commands are executed in one of two ways, as determined by a command
            line argument.  In the first mode (selected when there is no command
            line argument) each command is passed verbatim to system(3).  As the
            system(3) command does not return until the completion of the command,
            this method does not protect against a dilatory or hung external
            command.  It is included in this daemon only as a 'break-alike' of the
            current system.

            The second method of executing a command does protect against problems
            with the command.  This mode is enabled by specifying a command timeout
            on the command line.  In this mode the process will call supervise( )
            to execute the command.  supervise( ) provides the timeout protection.


    @fn     void ipc_cmdq::cmd_queue::getline( std::string &cmd ) const
    @brief  Read a single line from the command fifo
    @param  cmd a reference to a std:string which will be filled with the
            command line read from the command fifo

            getline( ) reads a new-line terminated line from the command fifo, 
            returning it in the out parameter cmd, a reference to a std::string.
            The terminating new-line is not placed in cmd.  This call blocks at
            read(2) until input is available.  Blocking is detected by a return
            of 0 from read(2), indicating EOF.  In this case fifo_select is called
            to wait for input.  This only happens in the case where part of a
            command has been read, but the new-line has not been reached.

    @fn     void ipc_cmdq::cmd_queue::fifo_select( ) const;
    @brief  Wait for input on the command fifo, with a timeout.

            This method will only be called if an EOF is encountered while reading
            the command fifo, before the terminating new-line is reached.  select(3)
            is called with a timeout to protected against a partial command in the
            fifo.  If the timeout is reached, an exception is thrown, which will
            result in the termination of the worker process, hence recovery from
            the error.

            NOTE: there is no protection against a partial command followed by a
            valid command. they will be seen as a single command.  Very likey
            this will fail, and be logged to the system log.  Given the current
            state of the consumers of this daemon, this cannot be avoided.  Should
            changes to those consumers be made, then a prefix length could be added,
            thus offering the ability to avoid this corner case.

    @fn     int ipc_cmdq::cmd_queue::supervise( const char *cmd, int timeout )
    @brief  Execute an external command, with timeout
    @param  cmd pointer to the external command to be executed
    @param  timeout permissible command execution duration, in seconds

            In the command with timeout mode mode, the worker process will fork(2) for
            each command, spawning a child process call the command process.  This
            child executes the command by passing it verbatim as the argument to
            the shell -c command line option.  The shell used is the cannonical
            '/bin/sh' shell.  The method used by the command process imitates the
            operation of system(3), by calling execlp(2).  For example, if the
            command read from the fifo what "foobar -a baz -f zyzzy", then the call
            would be exec( "/bin/sh", "/bin/sh", "-c", "foobar -a baz -f zizzy", 0 );

            In this second mode, the worker enters sleep via select(3) after spawning
            the command process, with the timeout for select(3) being the timeout
            passed in as a command line argument.  If the command process exits before
            the timeout, the SIGCHLD is caught, causing select(3) to return.  If the
            command does not complete by the timeout, select(3) will return on
            timeout, and the command process will be terminated.  In this last case
            a diagnostic message is written to the system log.

    @fn     void ipc_cmdq::cmd_queue::open_fifo( );
    @brief  Open the command fifo for reading by the worker process

            The pathname of the command fifo is first checked for existance, and that
            the file is in fact a fifo.

    @fn     void ipc_cmdq::cmd_queue::close_fifo( );
    @brief  The command fifo is closed for reading by the workker process.

    @fn     void ipc_cmdq::cmd_queue::open_log( const std::string &facility ) const;
    @brief  Open syslog(3) for writing using facility
    @param  facility the name of the syslog(3) facility to use for logging

            The facility is specified by a command line argument, and defaults to
            LOG_DAEMON.  If the facility given on the command line is invalid, then
            logging will still take place, using the default facility.  Calls
            open_log( int ) to open syslog(3).

    @fn     void ipc_cmdq::cmd_queue::open_log( int facility ) const;
    @brief  Open syslog(3) for writing using facility
    @param  facility the syslog(3) facility to use for logging

    @fn     void ipc_cmdq::cmd_queue::log( int prio, const char *msg ) const;
    @brief  Write a message to syslog(3) with priority prio
    @param  prio the syslog(3) priority to use when writing this message
    @param  msg the string to write to syslog(3)

            This method checks whether to write the message to syslog(3) based
            on the priority and the command line options for verbose logging
            and debug operation.  Also, if debug operation (foreground) was
            specified, then the message will also be written to standard error.

    @fn     const char * ipc_cmdq::cmd_queue::get_prio( int prio ) const
    @brief  get the syslog(3) priority name from the priority value
    @param  prio the syslog(3) priority value
    @return the syslog(3) priority name

            A helper function for log( ).  When writing messages to standard
            error, log( ) will add the priority name to the message; this function
            maps value to name.

    @var    static const char * ipc_cmdq::cmd_queue::s_version;
    @brief  the version string reported by the command line option for version

    @var    static bool ipc_cmdq::cmd_queue::m_exit
    @brief  flag set by signal handler to indicate a request for daemon shutdown

    @var    static bool ipc_cmdq::cmd_queue::m_restart
    @brief  flag set by signal handler to indicate a request for worker process restart

    @var    static bool ipc_cmdq::cmd_queue::m_cmdexit
    @brief  flag set by signal handler to indicate a completion of an external command

    @var    std::map< std::string, std::string > ipc_cmdq::cmd_queue::m_args
    @brief  a map of command line options

            This is the map of command line options and arguments.  The keys of the
            map are the specified command line options.  If the option has an argument
            then the argument is the value for that key.  If the option does not
            have an argument, the the value is an empty string.

    @var    int ipc_cmdq::cmd_queue::m_fifo
    @brief  used only by the worker, this is the handle to the command fifo

    @var    bool ipc_cmdq::cmd_queue::m_debug
    @brief  set to true when running in debugging mode

    @var    bool ipc_cmdq::cmd_queue::m_verbose
    @brief  set to true when in debugging mode, or when verbose logging is reqested.
**/

#endif // IPC_CMDQ_CMDQUED_H
