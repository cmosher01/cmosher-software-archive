#include "avisynth_c.h"

AVS_VideoFrame * AVSC_CC ConvertUYVYtoYUY2 (AVS_FilterInfo * p, int n)
{
	AVS_VideoFrame * frame;
	int row_size, height, pitch;
	int y, x;
	
	frame = avs_get_frame(p->child, n);
	
	avs_make_writable(p->env, &frame);
	
	unsigned int* data = (unsigned int*)avs_get_write_ptr(frame);
	pitch = avs_get_pitch(frame)/(sizeof(unsigned int));
	row_size = avs_get_row_size(frame);
	height = avs_get_height(frame);
	
    for (y = 0; y < height; ++y)
	{
        for (x = 0; x < row_size/4; ++x)
		{
			// swap bytes: ABCD --> BADC
			register const unsigned int b4 = data[x];

			// start     ABCD
			// and f0f0  A0C0
			// a=shift r 0A0C
			register const unsigned int a = (b4 & 0xFF00FF00) >> 8;

			// start     ABCD
			// and 0f0f  0B0D
			// b=shift l B0D0
			register const unsigned int b = (b4 & 0x00FF00FF) << 8;

			// a|b       BADC
			data[x] = a | b;
		}
        data += pitch;
    }
	
	return frame;
}

AVS_Value AVSC_CC cvt(AVS_ScriptEnvironment* env, AVS_Value args, void* user_data)
{
	AVS_FilterInfo* fi;
	AVS_Clip* new_clip = avs_new_c_filter(env, &fi, avs_array_elt(args, 0), 1);
	fi->get_frame = ConvertUYVYtoYUY2;
	AVS_Value v = avs_new_value_clip(new_clip);
	avs_release_clip(new_clip);
	return v;
}

const char* AVSC_CC avisynth_c_plugin_init(AVS_ScriptEnvironment* env)
{
	avs_add_function(env, "ConvertUYVYtoYUY2", "c", cvt, 0);
	return "ConvertUYVYtoYUY2";
}
