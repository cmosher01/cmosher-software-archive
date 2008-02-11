#include <vector>
#include <istream>

class Memory
{
private:
	std::vector<unsigned char> bytes;

public:
	Memory(const size_t n);
	size_t size() const;
	unsigned char read(const unsigned short address) const;
	void write(const unsigned short address, const unsigned char data);
	void clear();
	void powerOn();
	void powerOff();
	void load(std::istream& in);
};
