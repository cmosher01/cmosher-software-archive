#
# rules_mxmlc.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-16
#
# Define rules for building swf and swc files from
# .mxml or .as sources, using mxmlc compiler.
#
# input variables:
#    MXMLC_LIBRARIES
#    EXTRA_CLASSES
#    MXMLC
#    MXMLCFLAGS
#    BUILD_DIR





MXMLC_LIB := $(patsubst %,-compiler.include-libraries=%,$(MXMLC_LIBRARIES))

MXMLC_INC := $(patsubst %,-includes=%,$(EXTRA_CLASSES))



MXMLC_CMD = $(MXMLC) \
	$(MXMLCFLAGS) \
	-load-config=$(FLEX_HOME)/frameworks/flex-config.xml \
	-source-path $(srcdir)/src \
	-output $@ \
	$(MXMLC_LIB) \
	$(MXMLC_INC) \
	$<

$(BUILD_DIR)/%.swf: %.mxml
	[[ -d $(@D) ]] || mkdir -p $(@D) && :
	$(MXMLC_CMD)

$(BUILD_DIR)/%.swf: %.as
	[[ -d $(@D) ]] || mkdir -p $(@D) && :
	$(MXMLC_CMD)



COMPC_CMD = $(COMPC) \
	$(COMPCFLAGS) \
	-load-config=$(FLEX_HOME)/frameworks/flex-config.xml \
	-source-path $(srcdir)/src \
	-output $@ \
	$(MXMLC_LIB) \
	$(MXMLC_INC) \
	-include-sources $<

$(BUILD_DIR)/%.swc: %.mxml
	[[ -d $(@D) ]] || mkdir -p $(@D) && :
	$(COMPC_CMD)

$(BUILD_DIR)/%.swc: %.as
	[[ -d $(@D) ]] || mkdir -p $(@D) && :
	$(COMPC_CMD)
