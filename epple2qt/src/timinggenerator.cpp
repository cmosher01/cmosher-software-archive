/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "timinggenerator.h"
#include "util.h"

TimingGenerator::TimingGenerator()
{
}
const int TimingGenerator::CRYSTAL_HZ(Util::divideRoundUp(315000000,22));
const int TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE(14);
const int TimingGenerator::EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE(2);

const int TimingGenerator::HORIZ_CYCLES(65);
const int TimingGenerator::AVG_CPU_HZ((int)((double)315000000*TimingGenerator::HORIZ_CYCLES)/(22*(TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE*TimingGenerator::HORIZ_CYCLES+TimingGenerator::EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE)));
const int TimingGenerator::CPU_HZ(Util::divideRoundUp(TimingGenerator::CRYSTAL_HZ,TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE));


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
