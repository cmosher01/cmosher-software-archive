#include <omp.h>
#include <cstdio>

int main()
{
	#pragma omp parallel
	{
		const int tid = omp_get_thread_num();
		printf("Hello, World, from thread = %d\n", tid);
		if (tid == 0)
		{
			const int nthreads = omp_get_num_threads();
			printf("Number of threads = %d\n", nthreads);
		}
	}

	return 0;
}
