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


#include "playqmake.h"

#include <QtGui>

#include <math.h>

 const double DefaultCenterX = -0.637011f;
 const double DefaultCenterY = -0.0395159f;
 const double DefaultScale = 0.00403897f;

 const double ZoomInFactor = 0.8f;
 const double ZoomOutFactor = 1 / ZoomInFactor;
 const int ScrollStep = 20;

 playqmake::playqmake(QWidget *parent)
     : QWidget(parent)
 {
     centerX = DefaultCenterX;
     centerY = DefaultCenterY;
     pixmapScale = DefaultScale;
     curScale = DefaultScale;

     qRegisterMetaType<QImage>("QImage");
     connect(&thread, SIGNAL(renderedImage(const QImage &, double)),
             this, SLOT(updatePixmap(const QImage &, double)));

     setWindowTitle(tr("Mandelbrot"));
     setCursor(Qt::CrossCursor);
     resize(550, 400);
 }

 void playqmake::paintEvent(QPaintEvent * /* event */)
 {
     QPainter painter(this);
     painter.fillRect(rect(), Qt::black);

     if (pixmap.isNull()) {
         painter.setPen(Qt::white);
         painter.drawText(rect(), Qt::AlignCenter,
                          tr("Rendering initial image, please wait..."));
         return;
     }

     if (curScale == pixmapScale) {
         painter.drawPixmap(pixmapOffset, pixmap);
     } else {
         double scaleFactor = pixmapScale / curScale;
         int newWidth = int(pixmap.width() * scaleFactor);
         int newHeight = int(pixmap.height() * scaleFactor);
         int newX = pixmapOffset.x() + (pixmap.width() - newWidth) / 2;
         int newY = pixmapOffset.y() + (pixmap.height() - newHeight) / 2;

         painter.save();
         painter.translate(newX, newY);
         painter.scale(scaleFactor, scaleFactor);
         QRectF exposed = painter.matrix().inverted().mapRect(rect()).adjusted(-1, -1, 1, 1);
         painter.drawPixmap(exposed, pixmap, exposed);
         painter.restore();
     }

     QString text = tr("Use mouse wheel to zoom. "
                       "Press and hold left mouse button to scroll.");
     QFontMetrics metrics = painter.fontMetrics();
     int textWidth = metrics.width(text);

     painter.setPen(Qt::NoPen);
     painter.setBrush(QColor(0, 0, 0, 127));
     painter.drawRect((width() - textWidth) / 2 - 5, 0, textWidth + 10,
                      metrics.lineSpacing() + 5);
     painter.setPen(Qt::white);
     painter.drawText((width() - textWidth) / 2,
                      metrics.leading() + metrics.ascent(), text);
 }

 void playqmake::resizeEvent(QResizeEvent * /* event */)
 {
     thread.render(centerX, centerY, curScale, size());
 }

 void playqmake::keyPressEvent(QKeyEvent *event)
 {
     switch (event->key()) {
     case Qt::Key_Plus:
         zoom(ZoomInFactor);
         break;
     case Qt::Key_Minus:
         zoom(ZoomOutFactor);
         break;
     case Qt::Key_Left:
         scroll(-ScrollStep, 0);
         break;
     case Qt::Key_Right:
         scroll(+ScrollStep, 0);
         break;
     case Qt::Key_Down:
         scroll(0, +ScrollStep);
         break;
     case Qt::Key_Up:
         scroll(0, -ScrollStep);
         break;
     default:
         QWidget::keyPressEvent(event);
     }
 }

 void playqmake::wheelEvent(QWheelEvent *event)
 {
     int numDegrees = event->delta() / 8;
     double numSteps = numDegrees / 15.0f;
     zoom(pow(ZoomInFactor, numSteps));
 }

 void playqmake::mousePressEvent(QMouseEvent *event)
 {
     if (event->button() == Qt::LeftButton)
         lastDragPos = event->pos();
 }

 void playqmake::mouseMoveEvent(QMouseEvent *event)
 {
     if (event->buttons() & Qt::LeftButton) {
         pixmapOffset += event->pos() - lastDragPos;
         lastDragPos = event->pos();
         update();
     }
 }

 void playqmake::mouseReleaseEvent(QMouseEvent *event)
 {
     if (event->button() == Qt::LeftButton) {
         pixmapOffset += event->pos() - lastDragPos;
         lastDragPos = QPoint();

         int deltaX = (width() - pixmap.width()) / 2 - pixmapOffset.x();
         int deltaY = (height() - pixmap.height()) / 2 - pixmapOffset.y();
         scroll(deltaX, deltaY);
     }
 }

 void playqmake::updatePixmap(const QImage &image, double scaleFactor)
 {
     if (!lastDragPos.isNull())
         return;

     pixmap = QPixmap::fromImage(image);
     pixmapOffset = QPoint();
     lastDragPos = QPoint();
     pixmapScale = scaleFactor;
     update();
 }

 void playqmake::zoom(double zoomFactor)
 {
     curScale *= zoomFactor;
     update();
     thread.render(centerX, centerY, curScale, size());
 }

 void playqmake::scroll(int deltaX, int deltaY)
 {
     centerX += deltaX * curScale;
     centerY += deltaY * curScale;
     update();
     thread.render(centerX, centerY, curScale, size());
 }
