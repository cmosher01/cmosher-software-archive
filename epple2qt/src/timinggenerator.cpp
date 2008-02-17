/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "timinggenerator.h"
#include "util.h"
#include "throttle.h"
#include "timable.h"

TimingGenerator::TimingGenerator(Timable& timable, Throttle& throttle, QObject *parent):
	QThread(parent),
	shut(false),
	timable(timable),
	throttle(throttle)
{
}

const unsigned int TimingGenerator::CRYSTAL_HZ(Util::divideRoundUp(315000000,22));
const unsigned int TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE(14);
const unsigned int TimingGenerator::EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE(2);

const unsigned int TimingGenerator::HORIZ_CYCLES(65);

const unsigned int TimingGenerator::AVG_CPU_HZ((int)(((double)315000000*TimingGenerator::HORIZ_CYCLES)/(22*(TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE*TimingGenerator::HORIZ_CYCLES+TimingGenerator::EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE))));

const unsigned int TimingGenerator::CPU_HZ(Util::divideRoundUp(TimingGenerator::CRYSTAL_HZ,TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE));




//void TimingGenerator::start()
//{
//	if (this->thread)
//	{
//		throw new IllegalStateException();
//	}
//	this->shut = false;

/*
	this->thread = new Thread(new Runnable()
	{
		@SuppressWarnings("synthetic-access")
		void run()
		{
			try
			{
			threadProcedure();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
		}
	});
	this->thread.setName("User-TimingGenerator");
	this->thread.start();
*/
//}

void TimingGenerator::run()
{
	while (!isShuttingDown())
	{
		this->timable.tick();
		this->throttle.tick();
	}
}

bool TimingGenerator::isShuttingDown()
{
	return this->shut;
}

//bool TimingGenerator::isRunning()
//{
//	return this->thread;
//}

void TimingGenerator::shutdown()
{
//	if (!this->thread)
//	{
//		throw new IllegalStateException();
//	}
	this->shut = true;

//	this->thread->join();
//	this->thread = 0;
}




// This is just an example of how to use Boost threads mutex and condition
/*
class buffer
{
private:
  int buf[BUF_SIZE];
  unsigned int p, g, size;
  boost::mutex mutex;
  boost::condition cond;

public:
  buffer() : p(0), g(0), size(0) { }

  void put(const int m)
  {
    boost::mutex::scoped_lock lock(mutex);

    while (size >= BUF_SIZE)
    {
      cond.wait(lock);
    }

    buf[p++] = m;
    p %= BUF_SIZE;

    ++size;
    cond.notify_all();
  }

  int get()
  {
    boost::mutex::scoped_lock lock(mutex);

    while (size <= 0)
    {
      cond.wait(lock);
    }

    const int i = buf[g++];
    g %= BUF_SIZE;

    --size;
    cond.notify_all();

    return i;
  }
};
*/
