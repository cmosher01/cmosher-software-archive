#define WINVER (0x0400)
#include <windows.h>

#include "avisynth.h"

class Invert : public GenericVideoFilter
{
public:
    Invert(PClip _child) : GenericVideoFilter(_child) {}
    PVideoFrame __stdcall GetFrame(int n, IScriptEnvironment* env);
};


PVideoFrame __stdcall Invert::GetFrame(int n, IScriptEnvironment* env)
{
/*
    PVideoFrame frame = child->GetFrame(n, env);

    env->MakeWritable(&frame);

    unsigned char* p = frame->GetWritePtr();
    const int pitch = frame->GetPitch();
    const int row_size = frame->GetRowSize();
    const int height = frame->GetHeight();

    for (int y = 0; y < height; y++)
	{
        for (int x = 0; x < row_size; x += 2)
		{
			const char t = p[x];
			p[x] = p[x+1];
			p[x+1] = t;
		}
        p += pitch;
    }

    return frame;
*/
    PVideoFrame frame = child->GetFrame(n, env);

    env->MakeWritable(&frame);

    unsigned int* p = (unsigned int*)frame->GetWritePtr();
    const int pitch = frame->GetPitch()/4;
    const int row_size = frame->GetRowSize();
    const int height = frame->GetHeight();

    for (int y = 0; y < height; ++y)
	{
        for (int x = 0; x < row_size/4; ++x)
		{
			// swap bytes: ABCD --> BADC
			register const unsigned int b4 = p[x];

			// start     ABCD
			// and f0f0  A0C0
			// a=shift r 0A0C
			register const unsigned int a = (b4 & 0xFF00FF00) >> 8;

			// start     ABCD
			// and 0f0f  0B0D
			// b=shift l B0D0
			register const unsigned int b = (b4 & 0x00FF00FF) << 8;

			// a|b       BADC
			p[x] = a | b;
		}
        p += pitch;
    }

    return frame;
}


AVSValue __cdecl Create_Invert(AVSValue args, void* user_data, IScriptEnvironment* env)
{
    return new Invert(args[0].AsClip());
}


extern "C" __declspec(dllexport) const char* __stdcall AvisynthPluginInit2(IScriptEnvironment* env)
{
    env->AddFunction("ConvertUYVYtoYUY2", "c", Create_Invert, 0);
    return "ConvertUYVYtoYUY2";
}
