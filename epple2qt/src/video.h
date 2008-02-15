#ifndef VIDEO_H_
#define VIDEO_H_

#include <vector>

class Video
{
private:
	std::vector<unsigned short> lutTEXT[2]; // [0] is page 1, [1] is page 2
	std::vector<unsigned short> lutHRES[2]; // [0] is page 1, [1] is page 2

public:
	Video();
	~Video();
};

#endif /*VIDEO_H_*/
