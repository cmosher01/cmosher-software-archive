<?xml version="1.0" encoding="UTF-8"?>
<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="xml" />
    <template match="text()" />

    <template match="classpath">
        <element name="project" namespace="">
            <attribute name="name">exports_xslgen</attribute>
            <text>&#x0A;</text>
            <element name="target" namespace="">
                <attribute name="name">exports</attribute>
                <text>&#x0A;</text>
                <element name="fileset" namespace="">
                    <attribute name="id">exports</attribute>
                    <attribute name="dir">.</attribute>
                    <text>&#x0A;</text>
                    <apply-templates />
                </element>
                <text>&#x0A;</text>
            </element>
            <text>&#x0A;</text>
        </element>
        <text>&#x0A;</text>
    </template>

    <template match="classpathentry[@exported]">
        <element name="include" namespace="">
            <attribute name="name">
                <value-of select="@path" />
            </attribute>
        </element>
        <text>&#x0A;</text>
    </template>
</stylesheet>
