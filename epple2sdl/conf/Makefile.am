etcdir = $(sysconfdir)/epple2

if HAVE_WINDOWS
CONF_PREFIX =
else
CONF_PREFIX = $(prefix)/
endif

%.conf: %.conf.in
	sed -e 's,$$(PREFIX),$(CONF_PREFIX),g' <$< >$@

CONF_IN_FILES = $(notdir $(wildcard $(VPATH)/epple2*.conf.in))
CONF_FILES = $(patsubst %.in,%,$(CONF_IN_FILES))
etc_DATA = $(CONF_FILES)

EXTRA_DIST = $(etc_DATA)
