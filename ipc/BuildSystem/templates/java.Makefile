#
# Makefile for ???COMPONENT???
# Copyright (C) ???YEAR???, by ???IPC, Fairfield, CT???
#
# author: ???NAME???
# creation date: ???YYYY-MM-DD???
#

# Define the installation prefix
# This will ususally be /opt/ipc/product,
# where product is the name of the product,
# for example:
#prefix := /opt/ipc/onems
# but for 3rd party software it should be
# /usr/local
#prefix := /usr/local
#
# Never include any subdirectories like
# bin, lib, or etc in the prefix definition
#
prefix := /opt/ipc/onems

# Define the JAR file we are building.
# This will normally be $(component).jar
#TARGET = $(component).jar
#
TARGET = $(component).jar

# Define the classpath for the java compile step.
# Any jar files that this component depends on
# need to be included in this definition.
#
# Refer to other components' jar files as:
#JAVAC_CLASSPATH := 'component1/build/component1.jar component2/build/component2.jar'
# where component1 and component2 are the names
# of the other component.
#
# Multiple jar files are delimited by spaces.
#
# Note: for Java, we don't put $(BUILD_TOP) here,
# because it's in the build.xml file already.
#

# define versions of tools to use
JAVA_VERSION := 1.5.0_16
ANT_VERSION := 1.6.5
JUNIT_VERSION := 4.5

# define our junit test-suite class
MAIN_TEST := AllTests


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/java_component.mk
