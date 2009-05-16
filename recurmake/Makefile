.SUFFIXES:

.SUFFIXES: .c .h .d .o

MODULES := ant bee

CFLAGS += $(patsubst %,-I%, $(MODULES))

LIBS :=

SRC :=
include $(patsubst %,%/module.mk,$(MODULES))

OBJ := $(patsubst %.c,%.o,$(filter %.c,$(SRC)))

prog: $(OBJ)
	$(CC) -o $@ $(OBJ) $(LIBS)

include $(OBJ:.o=.d)

.c.d:
	./depend.sh `dirname $*.c` $(CFLAGS) $*.c > $@
