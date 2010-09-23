#
# rules_copy.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-15
#
# Defines a pattern rule that simply copies a file
# from the source area (if the file does not
# already exists, or is out of date).
#
# input variables:
#    BUILD_DIR (directory name to build into)



# If there is no other rule, just copy src to build area,
# but first make sure the dest dir exists.
$(BUILD_DIR)/% :: %
	[[ -d $(@D) ]] || mkdir -p $(@D) && :
	cp -f $(CP_FLAGS) $< $@
