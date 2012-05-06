/*
	UNIX Daemon Server Programming Sample Program
	Levent Karakas <levent at mektup dot at> May 2001
	Chris Mosher Nov 2010
*/

#include <sys/types.h>
#include <errno.h>
#include <sys/stat.h>
#include <stdio.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>
#include <syslog.h>
#include <stdlib.h>
#include <string.h>



#define DAEMON_NAME "mydaemon"
#define RUNNING_DIR	"/tmp"
#define PID_FILE	"/var/run/"  DAEMON_NAME ".pid"



void signal_handler(const int sig)
{
	switch (sig)
	{
		case SIGHUP:
			syslog(LOG_INFO,"hangup signal caught (and ignored)");
		break;
		case SIGTERM:
			syslog(LOG_INFO,"terminate signal caught");
			syslog(LOG_NOTICE,"exiting");
			remove(PID_FILE);
			exit(EXIT_SUCCESS);
		break;
		case SIGINT:
			syslog(LOG_INFO,"interrupt signal caught");
			syslog(LOG_NOTICE,"exiting");
			exit(EXIT_SUCCESS);
		break;
		case SIGQUIT:
			syslog(LOG_INFO,"quit signal caught");
			syslog(LOG_NOTICE,"exiting");
			exit(EXIT_SUCCESS);
		break;
	}
}

int is_error(const int return_status)
{
	return return_status < 0;
}

void do_fork()
{
	const pid_t pid_child = fork();

	if (is_error(pid_child))
	{
		fprintf(stderr,"Error forking daemon: %s.\n",strerror(errno));
		exit(EXIT_FAILURE);
	}

	if (pid_child > 0)
	{
		/* parent */
		exit(EXIT_SUCCESS);
	}

	/* child (daemon) continues */
}

void do_files()
{
	close(STDIN_FILENO);
	close(STDOUT_FILENO);
	close(STDERR_FILENO);



	/* redirect standard I/O channels to a safe /dev/null */
	const int i = open("/dev/null",O_RDWR); /* stdin */
	dup(i); /* stdout */
	dup(i); /* stderr */
}

void do_pid_file()
{
	int pfp;
	char str[32];
	pfp = open(PID_FILE,O_RDWR|O_CREAT|O_EXCL,0640);
	if (is_error(pfp))
	{
		syslog(LOG_ERR,"Cannot create pid file: " PID_FILE);
		syslog(LOG_NOTICE,"exiting");
		exit(EXIT_FAILURE);
	}
	sprintf(str,"%d\n",getpid());
	write(pfp,str,strlen(str));
}

void new_session()
{
	const pid_t p = setsid();
	if (is_error(p))
	{
		syslog(LOG_ERR,"Cannot create new process session.");
		syslog(LOG_NOTICE,"exiting");
		exit(EXIT_FAILURE);
	}
}

void do_syslog(const int option)
{
	openlog(DAEMON_NAME,option,LOG_DAEMON);
	syslog(LOG_NOTICE,"starting");
}

void do_signals()
{
	signal(SIGCHLD,SIG_IGN); /* ignore child */
	signal(SIGTSTP,SIG_IGN); /* ignore tty signals */
	signal(SIGTTOU,SIG_IGN);
	signal(SIGTTIN,SIG_IGN);
	signal(SIGHUP,signal_handler);
	signal(SIGTERM,signal_handler);
	signal(SIGINT,signal_handler);
	signal(SIGQUIT,signal_handler);
}

void daemonize()
{
	if (getppid()==1)
	{
		/* already a daemon */
		return;
	}

	do_fork();
	do_syslog(LOG_PID);
	new_session();
	do_signals();
	do_files();
	do_pid_file();

	/*
		set newly created file permissions umask:

		rwxrwxrwx rw-rw-rw-
		000010111 000010111
		========= =========
		rwxr-x--- rw-r-----
	*/
	umask(0027);

	chdir(RUNNING_DIR); /* change running directory */
}

static const int range_min = 10;
static const int range_max = 20;

int main(int argc, char* argv[])
{
	errno = 0;

	int opt_daemon = 0;

	int c;
	while ((c = getopt(argc,argv,"d")) != -1)
	{
		switch (c)
		{
			case 'd':
				++opt_daemon;
			break;
			default:
				fprintf(stderr,"Invalid argument.");
				exit(EXIT_FAILURE);
			break;
		}
	}

	if (opt_daemon)
	{
		daemonize();
	}
	else
	{
		do_syslog(LOG_PERROR);
		do_signals();
	}

	while (1)
	{
		const int u = rand() / ((double)RAND_MAX + 1) * (range_max - range_min) + range_min;
		syslog(LOG_INFO,"sleeping %d seconds...",u);
		sleep(u);
	}
}
