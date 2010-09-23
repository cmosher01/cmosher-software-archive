#
# rules_o.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-05
#
# Defines a pattern for creating .o from .cpp files.
# Also creates .d dependency files, which should be
# included in the calling makefile as, for example:
#     CXX_SOURCES := $(shell find $(srcdir) -name \*.cpp -printf "%P\n")
#     -include $(patsubst %.cpp,$(BUILD_DIR)/%.d,$(CXX_SOURCES))
#
# input variables:
#    non-standard:
#        BUILD_DIR (build directory name)
#        srcdir (source directory full path; cannot end with a slash / )
#    standard:
#        COMPILE.cpp, OUTPUT_OPTION


$(BUILD_DIR)/%.o : %.cpp
	[[ -d $(@D) ]] || mkdir -p $(@D) && :
	$(COMPILE.cpp) -MMD $(OUTPUT_OPTION) $<
	sed -e 's,$(srcdir)/,,g' -i $(@:.o=.d)
