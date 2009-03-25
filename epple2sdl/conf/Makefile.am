etcdir = $(sysconfdir)/epple2

%.conf: %.conf.in
	sed -e 's,$$(PREFIX),$(prefix)/,g' <$< >$@

CONF_IN_FILES = $(notdir $(wildcard $(VPATH)/epple2.conf*))
CONF_FILES = $(patsubst %.in,%,$(CONF_IN_FILES))
etc_DATA = $(CONF_FILES)

EXTRA_DIST = $(etc_DATA)
