AVIC_DIR = c:\\avisynth3rdpartyplugins\\avisynth_c\\avisynth_c-0.15

%.dll: %.c
	gcc -shared -o $@ -I $(AVIC_DIR) $< -L $(AVIC_DIR) -lavisynth_c -lavisynth

all: uyvy2yuy2.dll
