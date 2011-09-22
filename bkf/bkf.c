/*
 * Copyright (c) 2005-2007 BitMover, Inc.
 *
 * Licensed under the GPL v2.
 *
 * Please send patches to dev@bitmover.com.
 */
#include <ctype.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>

#define	min(x, y)	((x) < (y) ? (x) : (y))
#define	unless(x)	if (!(x))
#define	streq(a, b)	!strcmp(a, b)

typedef	struct {
	int	port;
	char	host[500];
	char	repo[1024];
} url;

int	changes(int ac, char **av);
int	chomp(char *s);
int	clone(int ac, char **av);
void	bk_getline(int sock, char *buf, unsigned int len);
int	scanurl(url *u, char *p);
int	tcp_connect(char *host, int port);
int	parent(int ac, char **av);
int	pull(int ac, char **av);
int	help(int ac, char **av);
void	usage(void);

int
main(int ac, char **av)
{
	unless (av[1]) {
		exit(1);
	}
	if (streq(av[1], "changes")) return (changes(--ac, ++av));
	if (streq(av[1], "clone")) return (clone(--ac, ++av));
	if (streq(av[1], "pull")) return (pull(--ac, ++av));
	if (streq(av[1], "parent")) return (parent(--ac, ++av));
	if (streq(av[1], "help")) return (help(--ac, ++av));
	if (streq(av[1], "--help")) return (help(--ac, ++av));
	fprintf(stderr,
	    "What an excellent idea!  You should implement 'bkf %s'\n"
	    "Please send a patch to dev@bitmover.com.  Thanks!\n",
	    av[1]);
	return (1);
}

int
help(int ac, char **av)
{
	puts(
"This is bkf - the 2nd generation free BitKeeper client for BKbits.net\n"
"\n"
"It supports the following commands:\n"
"\n"
"bkf clone [-q] [-r<rev>] <url>\n"
"	-q	be quiet\n"
"	-r<rev>	specifies rev to which to clone to, nothing after that comes\n"
"	<url>	specifies the repo, i.e., bk://mysql.bkbits.net/mysql-5.2\n"
"\n"
"bkf pull [-q] [<url>]\n"
"	-q	be quiet\n"
"	<url>	specifies the repo, i.e., bk://mysql.bkbits.net/mysql-5.2\n"
"	The default url is whatever you cloned from original\n"
"\n"
"bkf parent [<url>]\n"
"	Sets or shows the parent\n"
"\n"
"bkf changes\n"
"	This command talks to your parent to get repository history.\n"
"	With no options shows the history of your repository.\n"
"	-R	show any changesets in your parent that you don't have\n"
"	-r<rev>	show history for the specified revision\n"
"	-v	include file comments in output\n"
"	-vv	include file comments and diffs in output\n");
	return (0);
}

/*
 * Get the tarball and unpack it.
 */
int
clone(int ac, char **av)
{
	int	sock, i, bytes, c;
	int	quiet = 0;
	FILE	*f;
	url	u;
	char	*repo, *dst, *rev = "+";
	char	buf[BUFSIZ];

	while ((c = getopt(ac, av, "qr:")) != -1) {
		switch (c) {
		    case 'q': quiet = 1; break;
		    case 'r': rev = optarg; break;
		    default: 
usage:			fprintf(stderr,
			    "usage: bkf clone [-r<rev>] "
			    "bk://host[:port][/path_to_repo] dest\n");
			exit(1);
		}
	}

	unless ((ac - optind) == 2) goto usage;

	repo = av[optind++];
	dst = av[optind];

	if (scanurl(&u, repo) != 0) {
		fprintf(stderr, "Bad URL\n");
		exit(1);
	}

	if (mkdir(dst, 0775)) {
		perror(dst);
		exit(1);
	}
	chdir(dst);
	mkdir("BK", 0775);
	f = fopen("BK/parent", "w");
	fprintf(f, "%s\n", repo);
	fclose(f);
	unless ((sock = tcp_connect(u.host, u.port)) >= 0) {
		perror(repo);
		exit(1);
	}
	sprintf(buf, "putenv BK_VHOST=%s\n", u.host);
	if (write(sock, buf, strlen(buf)) != strlen(buf)) exit(1);
	if (u.repo[0]) {
		unless (write(sock, "cd ", 3) == 3) exit(1);
		unless (write(sock, u.repo, strlen(u.repo)) == strlen(u.repo)) {
			exit(1);
		}
		unless (write(sock, "\n", 1) == 1) exit(1);
	}
	sprintf(buf, "bkf_tarball %s\n", rev);
	unless (write(sock, buf, strlen(buf)) == strlen(buf)) exit(1);
line:	bk_getline(sock, buf, sizeof(buf));
	switch (buf[0]) {
	    case 'I':
		unless (quiet) fprintf(stderr, "%s", buf);
		goto line;
	    case 'E':
		fprintf(stderr, "%s", buf);
		exit(1);
	    case 'O': goto line;	/* OK-root OK */
	    case '@': break;		/* @BLOCK ... */
	    default:
	    	exit(1);
	}
	if (quiet) {
		unless (f = popen("tar zxf -", "w")) exit(1);
	} else {
		unless (f = popen("tar zxvf -", "w")) exit(1);
	}
	while (sscanf(buf, "@BLOCK=%u@", &bytes) == 1) {
		if (bytes == 0) {
			pclose(f);
			unless (quiet) printf("All done!\n");
			exit(0);
		}
		do {
			switch (i = read(sock, buf, min(bytes, sizeof(buf)))) {
			    case 0: 
			    case -1: perror("read"); break;
			    default:
				fwrite(buf, i, 1, f);
				bytes -= i;
				break;
			}
		} while (bytes > 0);
		bk_getline(sock, buf, sizeof(buf));
	}
	pclose(f);
	exit(1);
}

int
parent(int ac, char **av)
{
	FILE	*f;
	char	buf[BUFSIZ];
	url	u;

	if (av[1]) {
		if (scanurl(&u, av[1])) {
			unless (f = fopen("BK/parent", "w")) {
				perror("BK/parent");
				exit(1);
			}
			fprintf(f, "%s\n", av[1]);
			return (0);
		} else {
			fprintf(stderr, "Bad URL: %s\n", av[1]);
			return (1);
		}
	}

	if (f = fopen("BK/parent", "r")) {
		fgets(buf, sizeof(buf), f);
		printf("%s", buf);
		fclose(f);
	}
	return (0);
}

/*
 * Get the patch and unpack it.
 */
int
pull(int ac, char **av)
{
	int	sock, n, left, want, i;
	int	quiet = 0;
	FILE	*f;
	char	buf[BUFSIZ];
	url	u;

	if (av[1] && streq(av[1], "-q")) {
		quiet++;
		ac--;
		av++;
	}
	if (av[1] && scanurl(&u, av[1])) {
err:		fprintf(stderr,
		    "usage: bkf pull [bk://host[:port][/path_to_repo]]\n");
		exit(1);
	}
	unless (av[1]) {
		if (f = fopen("BK/parent", "r")) {
			fgets(buf, sizeof(buf), f);
			chomp(buf);
			fclose(f);
			if (scanurl(&u, buf)) goto err;
		} else {
			goto err;
		}
	}
	unless (f = fopen("BK/keys", "r")) exit(1);
	while (fgets(buf, sizeof(buf), f)) {
		chomp(buf);
		if (putenv(strdup(buf))) perror(buf);
	}
	fclose(f);
	unless (getenv("ROOTKEY")) {
		fprintf(stderr, "No ROOTKEY in BK/keys\n");
		exit(1);
	}
	unless (getenv("TIPKEY")) {
		fprintf(stderr, "No TIPKEY BK/keys\n");
		exit(1);
	}
	unless ((sock = tcp_connect(u.host, u.port)) >= 0) {
		perror(av[1]);
		exit(1);
	}
	sprintf(buf, "putenv BK_VHOST=%s\n", u.host);
	if (write(sock, buf, strlen(buf)) != strlen(buf)) exit(1);
	if (u.repo[0]) {
		unless (write(sock, "cd ", 3) == 3) exit(1);
		unless (write(sock, u.repo, strlen(u.repo)) == strlen(u.repo)) {
			exit(1);
		}
		unless (write(sock, "\n", 1) == 1) exit(1);
	}
	sprintf(buf, "bkf_patch %s %s\n", getenv("ROOTKEY"), getenv("TIPKEY"));
	unless (write(sock, buf, strlen(buf)) == strlen(buf)) exit(1);
line:	for (i = 0; ; i++) {
		unless (read(sock, &buf[i], 1) == 1) exit(1);
		if (buf[i] == '\n') {
			buf[i+1] = 0;
			break;
		}
	}
	switch (buf[0]) {
	    case 'I':
		fprintf(stderr, "%s", buf);
		if (streq("INFO-nothing to send.\n", buf)) exit(0);
		goto line;
	    case 'E':
		fprintf(stderr, "%s", buf);
		exit(1);
	    case 'O':
		/* Might be CD command OK line */
		unless (sscanf(buf, "OK-patch coming, %u bytes", &left)== 1){
			goto line;
		}
		fprintf(stderr, "%s", buf);
		break;
	    default:
	    	exit(1);
	}
	want = min(sizeof(buf), left);
	unless (want > 0) exit(0);
	if (quiet) {
		unless (f = popen("gunzip | patch -sp1 --binary", "w")) exit(1);
	} else {
		unless (f = popen("gunzip | patch -p1 --binary", "w")) exit(1);
	}
	unlink("BK/keys");
	while ((n = read(sock, buf, want)) > 0) {
		fwrite(buf, 1, n, f);
		left -= n;
		if (left <= 0) break;
		want = min(sizeof(buf), left);
	}
	pclose(f);
	while (read(sock, buf, 1) == 1) write(2, buf, 1);
	return (0);
}

/*
 * XXX - we don't handle any quoting problems, simplistic interface.
 */
int
changes(int ac, char **av)
{
	int	i, sock;
	FILE	*f;
	url	u;
	char	buf[1024], buf2[1024];

	if (f = fopen("BK/parent", "r")) {
		fgets(buf, sizeof(buf), f);
		chomp(buf);
		fclose(f);
		if (scanurl(&u, buf)) {
			fprintf(stderr, "Bad URL %s\n", buf);
			return (1);
		}
	} else {
		fprintf(stderr, "bkf changes: set a parent first.\n");
		return (1);
	}
	unless (f = fopen("BK/keys", "r")) exit(1);
	while (fgets(buf2, sizeof(buf2), f)) {
		chomp(buf2);
		if (putenv(strdup(buf2))) perror(buf2);
	}
	fclose(f);
	unless ((sock = tcp_connect(u.host, u.port)) >= 0) {
		perror(av[1]);
		exit(1);
	}
	sprintf(buf, "putenv BK_VHOST=%s\n", u.host);
	if (write(sock, buf, strlen(buf)) != strlen(buf)) exit(1);
	if (u.repo[0]) {
		unless (write(sock, "cd ", 3) == 3) exit(1);
		unless (write(sock, u.repo, strlen(u.repo)) == strlen(u.repo)) {
			exit(1);
		}
		unless (write(sock, "\n", 1) == 1) exit(1);
	}
	strcpy(buf, "bkf_changes");
	if (av[1] && streq(av[1], "-R")) {
		sprintf(buf2, "-r%s..", getenv("TIPKEY"));
		strcat(buf, " ");
		strcat(buf, buf2);
	} else unless (av[1]) {
		sprintf(buf2, "-r..%s", getenv("TIPKEY"));
		strcat(buf, " ");
		strcat(buf, buf2);
	} else {
		for (i = 1; av[i]; ++i) {
			strcat(buf, " ");
			strcat(buf, av[i]);
		}
	}
	strcat(buf, "\n");
	if (write(sock, buf, strlen(buf)) != strlen(buf)) exit(1);
	while ((i = read(sock, buf, sizeof(buf))) > 0) {
		write(1, buf, i);
	}
	return (0);
}

/*
 * Connect to the TCP socket advertised as "port" on "host" and
 * return the connected socket.
 */
int
tcp_connect(char *host, int port)
{
	struct	hostent *h;
	struct	sockaddr_in s;
	int	sock;

	if ((sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
		perror("socket");
		return (-1);
	}
	unless (h = gethostbyname(host)) return (-2);
	memset((void *) &s, 0, sizeof(s));
	s.sin_family = AF_INET;
	memmove((void *)&s.sin_addr, (void*)h->h_addr, h->h_length);
	s.sin_port = htons(port);
	if (connect(sock, (struct sockaddr*)&s, sizeof(s)) < 0) {
		return (-3);
	}
	return (sock);
}

int
scanurl(url *u, char *p)
{
	char	*t;
	char	buf[1024];

	strcpy(buf, p);
	p = buf;
	if (strncmp("bk://", p, 5)) return (1);
	u->repo[0] = 0;
	p += 5;
	unless (*p) return (1);

	/* bk://hostname:port[/path] */
	if (t = strchr(p, ':')) {
		*t++ = 0;
		strcpy(u->host, p);
		u->port = atoi(t);
		for (p = t; isdigit(*p); p++);
		if (*p == '/') strcpy(u->repo, p+1);
		t[-1] = ':';
		return (0);
	}
	u->port = 0x3962;
	if (t = strchr(p, '/')) {
		*t++ = 0;
		strcpy(u->host, p);
		strcpy(u->repo, t);
		t[-1] = '/';
		return (0);
    	}
	strcpy(u->host, p);
	return (0);
}

void
bk_getline(int sock, char *buf, unsigned int len)
{
	unsigned int i;

	for (i = 0; i < len - 1; i++) {
		unless (read(sock, &buf[i], 1) == 1) exit(1);
		if (buf[i] == '\n') {
			buf[i+1] = 0;
			break;
		}
	}
    	buf[i+1] = 0;
}

int     
chomp(char *s)  
{       
	int     any = 0;
	char    *p;

	p = s + strlen(s); 
	while ((p > s) && ((p[-1] == '\n') || (p[-1] == '\r'))) --p, any = 1;
	*p = 0; 
	return (any);
}       
