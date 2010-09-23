#
# tools.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-15
#
# input variables:
#    version numbers of tools to use:
#    JAVA_VERSION
#    FLEX_VERSION
#    ANT_VERSION
#    JUNIT_VERSION





# JAVA_HOME setting is based on the Sun JDK RPM installation
JAVA_HOME := /usr/java/jdk$(JAVA_VERSION)

JAVAC := $(JAVA_HOME)/bin/javac
JAVA := $(JAVA_HOME)/jre/bin/java
JAVAJAR := $(JAVA) -jar



# FLEX_HOME setting is based on IPC-invented policy for installation path
FLEX_HOME := /opt/adobe/flex/flex-$(FLEX_VERSION)
FLEX_FRAME := +flexlib=$(FLEX_HOME)/frameworks
MXMLC := $(JAVAJAR) $(FLEX_HOME)/lib/mxmlc.jar $(FLEX_FRAME)
COMPC := $(JAVAJAR) $(FLEX_HOME)/lib/compc.jar $(FLEX_FRAME)



# JAVADIR setting is based on RHEL Ant and JUnit RPM installations
JAVADIR := /usr/share/java

ANT := $(JAVAJAR) $(JAVADIR)/ant-launcher-$(ANT_VERSION).jar -Dant.home=$(JAVADIR)

JUNIT := $(JAVAJAR) $(JAVADIR)/junit-$(JUNIT_VERSION).jar



PHPUNIT := phpunit
