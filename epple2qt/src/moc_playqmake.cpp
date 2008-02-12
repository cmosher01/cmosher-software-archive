/****************************************************************************
** playqmake meta object code from reading C++ file 'playqmake.h'
**
** Created: Mon Feb 11 18:12:01 2008
**      by: The Qt MOC ($Id: qt/moc_yacc.cpp   3.3.7   edited Oct 19 16:22 $)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#undef QT_NO_COMPAT
#include "playqmake.h"
#include <qmetaobject.h>
#include <qapplication.h>

#include <private/qucomextra_p.h>
#if !defined(Q_MOC_OUTPUT_REVISION) || (Q_MOC_OUTPUT_REVISION != 26)
#error "This file was generated using the moc from 3.3.7. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

const char *playqmake::className() const
{
    return "playqmake";
}

QMetaObject *playqmake::metaObj = 0;
static QMetaObjectCleanUp cleanUp_playqmake( "playqmake", &playqmake::staticMetaObject );

#ifndef QT_NO_TRANSLATION
QString playqmake::tr( const char *s, const char *c )
{
    if ( qApp )
	return qApp->translate( "playqmake", s, c, QApplication::DefaultCodec );
    else
	return QString::fromLatin1( s );
}
#ifndef QT_NO_TRANSLATION_UTF8
QString playqmake::trUtf8( const char *s, const char *c )
{
    if ( qApp )
	return qApp->translate( "playqmake", s, c, QApplication::UnicodeUTF8 );
    else
	return QString::fromUtf8( s );
}
#endif // QT_NO_TRANSLATION_UTF8

#endif // QT_NO_TRANSLATION

QMetaObject* playqmake::staticMetaObject()
{
    if ( metaObj )
	return metaObj;
    QMetaObject* parentObject = QMainWindow::staticMetaObject();
    static const QUMethod slot_0 = {"newDoc", 0, 0 };
    static const QUMethod slot_1 = {"choose", 0, 0 };
    static const QUParameter param_slot_2[] = {
	{ "fileName", &static_QUType_QString, 0, QUParameter::In }
    };
    static const QUMethod slot_2 = {"load", 1, param_slot_2 };
    static const QUMethod slot_3 = {"save", 0, 0 };
    static const QUMethod slot_4 = {"saveAs", 0, 0 };
    static const QUMethod slot_5 = {"print", 0, 0 };
    static const QUMethod slot_6 = {"about", 0, 0 };
    static const QUMethod slot_7 = {"aboutQt", 0, 0 };
    static const QMetaData slot_tbl[] = {
	{ "newDoc()", &slot_0, QMetaData::Private },
	{ "choose()", &slot_1, QMetaData::Private },
	{ "load(const QString&)", &slot_2, QMetaData::Private },
	{ "save()", &slot_3, QMetaData::Private },
	{ "saveAs()", &slot_4, QMetaData::Private },
	{ "print()", &slot_5, QMetaData::Private },
	{ "about()", &slot_6, QMetaData::Private },
	{ "aboutQt()", &slot_7, QMetaData::Private }
    };
    metaObj = QMetaObject::new_metaobject(
	"playqmake", parentObject,
	slot_tbl, 8,
	0, 0,
#ifndef QT_NO_PROPERTIES
	0, 0,
	0, 0,
#endif // QT_NO_PROPERTIES
	0, 0 );
    cleanUp_playqmake.setMetaObject( metaObj );
    return metaObj;
}

void* playqmake::qt_cast( const char* clname )
{
    if ( !qstrcmp( clname, "playqmake" ) )
	return this;
    return QMainWindow::qt_cast( clname );
}

bool playqmake::qt_invoke( int _id, QUObject* _o )
{
    switch ( _id - staticMetaObject()->slotOffset() ) {
    case 0: newDoc(); break;
    case 1: choose(); break;
    case 2: load((const QString&)static_QUType_QString.get(_o+1)); break;
    case 3: save(); break;
    case 4: saveAs(); break;
    case 5: print(); break;
    case 6: about(); break;
    case 7: aboutQt(); break;
    default:
	return QMainWindow::qt_invoke( _id, _o );
    }
    return TRUE;
}

bool playqmake::qt_emit( int _id, QUObject* _o )
{
    return QMainWindow::qt_emit(_id,_o);
}
#ifndef QT_NO_PROPERTIES

bool playqmake::qt_property( int id, int f, QVariant* v)
{
    return QMainWindow::qt_property( id, f, v);
}

bool playqmake::qt_static_property( QObject* , int , int , QVariant* ){ return FALSE; }
#endif // QT_NO_PROPERTIES
