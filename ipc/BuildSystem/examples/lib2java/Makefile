#
# Makefile for lib2java
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-01-16
#

#define the installation prefix
prefix := /opt/ipc/onems

# Define the JAR file are we building
TARGET = $(component).jar

# Define the classpath for the java compile step.
# We reference classes in lib1java.jar, so we need to
# compile against it:
# Note: for Java, we don't put $(BUILD_TOP) here,
# because it's in the build.xml file already.
JAVAC_CLASSPATH := lib1java/build/lib1java.jar

# define versions of tools to use
JAVA_VERSION := 1.5.0.16
ANT_VERSION := 1.7.1
JUNIT_VERSION := 4.5

# define our junit test-suite class
MAIN_TEST := AllTests


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/java_component.mk
