# Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
#
# See also the LICENSE file in this distribution.
#
# Makefile for the example demonstrating shared dispatchers with JNI.
#

#
# Change this to reflect your setting.  Or override it at the make
# command line:
#	% make JDK=/home/you/jdk
#
JDK     = /opt/java

#
# Use native threads
#
THREADS_FLAG = native

CFLAGS       += -g -I$(JDK)/include -I$(JDK)/include/linux
CPPFLAGS     += -c -g -I$(JDK)/include -I$(JDK)/include/linux
.SUFFIXES: .java .class .cpp .o

#
# Targets.
#
run: build FORCE
	@echo LD_LIBRARY_PATH=.:$$LD_LIBRARY_PATH; \
	LD_LIBRARY_PATH=.:$$LD_LIBRARY_PATH; \
	echo export LD_LIBRARY_PATH; \
	export LD_LIBRARY_PATH; \
	echo $(JDK)/bin/java -$(THREADS_FLAG) $(MAIN_CLASS); \
	$(JDK)/bin/java -$(THREADS_FLAG) $(MAIN_CLASS)

build: checkjdk $(CLASSES) $(NATIVE_LIB) FORCE

#
# In JDK 1.2 and 1.2.2 libjvm.so is mistakenly linked to libhpi.so.
# Otherwise we need not set the LIBHPI_PATH variable
#
LIBHPI_PATH = $(JDK)/jre/lib/sparc/native_threads
LIBJVM_PATH = $(JDK)/jre/lib/sparc/classic

runapp: buildapp FORCE
	@echo LD_LIBRARY_PATH=$(LIBHPI_PATH):$(LIBJVM_PATH):$$LD_LIBRARY_PATH; \
	LD_LIBRARY_PATH=$(LIBHPI_PATH):$(LIBJVM_PATH):$$LD_LIBRARY_PATH; \
	echo export LD_LIBRARY_PATH; \
	export LD_LIBRARY_PATH; \
	echo $(NATIVE_APP); \
        $(NATIVE_APP)

buildapp: checkjdk $(CLASSES) $(NATIVE_APP) FORCE

#
# Build class files.
#
.java.class:
	$(JDK)/bin/javac $<

.cpp.o:
	$(CC) $(CPPFLAGS) $<

#
# Run javah.
#
.class.h:
	$(JDK)/bin/javah -jni $(<:%.class=%)

#
# Build .c files.
#
$(NATIVE_LIB): $(OBJS)
	ld -G $(OBJS) -o $@

#
# Note that you should always include -lthread as the first option to the
# linker when building multithreaded Solaris applications.
#
$(NATIVE_APP): $(OBJS)
	cc -L$(LIBHPI_PATH) -L$(LIBJVM_PATH) -lthread -ljvm $(OBJS) -o $@

#
# Remove generated stuff.
#
clean: FORCE
	rm -f *.o 
	rm -f *.so *.class $(NATIVE_APP)
	rm -f *.tst

#
# Check that the user has a valid JDK install.
#
checkjdk: FORCE
	@if [ ! -x $(JDK)/bin/java ]; then				\
	    echo "ERROR: JDK not found!";				\
	    echo "";							\
	    echo "Please install JDK version 1.1 or higher, and";	\
	    echo "invoke make like this:";				\
	    echo "        % $(MAKE) JDK=/path/to/jdk";			\
	    echo "";							\
	    exit 1;							\
	fi

#
# Handling phony targets.
#
FORCE: ;
