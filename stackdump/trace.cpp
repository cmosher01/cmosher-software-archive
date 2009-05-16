#include <execinfo.h>
#include <stdio.h>
#include <stdlib.h>

/* Obtain a backtrace and print it to stdout. */
void print_trace()
{
	void* array[10];
	size_t size = backtrace(array,10);

	char** strings = backtrace_symbols(array,size);

	//printf("Obtained %zd stack frames.\n",size);

	for (size_t i = 0; i < size; ++i)
		printf("    %s\n",strings[i]);

	free(strings);
}

/* A dummy function to make the backtrace more interesting. */
void dummy_function()
{
	print_trace();
}

int main()
{
	dummy_function();
	return 0;
}
