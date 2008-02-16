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
#ifndef RENDERTHREAD_H
#define RENDERTHREAD_H
 #include <QMutex>
 #include <QSize>
 #include <QThread>
 #include <QWaitCondition>

 class QImage;

 class RenderThread : public QThread
 {
     Q_OBJECT

 public:
     RenderThread(QObject *parent = 0);
     ~RenderThread();

     void render(double centerX, double centerY, double scaleFactor,
                 QSize resultSize);

 signals:
     void renderedImage(const QImage &image, double scaleFactor);

 protected:
     void run();

 private:
     uint rgbFromWaveLength(double wave);

     QMutex mutex;
     QWaitCondition condition;
     double centerX;
     double centerY;
     double scaleFactor;
     QSize resultSize;
     bool restart;
     bool abort;

     enum { ColormapSize = 512 };
     uint colormap[ColormapSize];
 };

#endif
