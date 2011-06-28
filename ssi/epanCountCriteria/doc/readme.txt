This project uses JAXB to generate Java source code that
handles reading, writing, and modifying criteria for
an Epanel count (see epanCountLibrary project). The criteria
is in XML format, as defined by epanCount_api_criteria.xsd
(an XML Schema file).

Further, some parts of the XML Schema (specifically, enumerations)
are extracted from the database, to prevent the need to
hard-code values for them. This project contains a specialized
build.xml file that is used by the standard build process.
It also contains an eclipse_build.xml file that can be used
to build this project from within the Eclipse IDE.
