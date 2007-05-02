<?xml version="1.0" encoding="UTF-8"?>
<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="xml" />
    <template match="text()" />

    <template match="classpath">
        <element name="project" namespace="">
            <attribute name="name">srcs_xslgen</attribute>
            <text>&#x0A;</text>

            <element name="target" namespace="">
                <attribute name="name">srcs</attribute>
                <text>&#x0A;</text>

                <element name="path" namespace="">
                    <attribute name="id">srcs</attribute>
                    <text>&#x0A;</text>

                    <if test="count(classpathentry[(@kind='src') and starts-with(@path,'/') and (not(contains(@path,'-')))]) > 0">
                        <element name="dirset" namespace="">
                            <attribute name="dir">..</attribute>
                            <text>&#x0A;</text>
                            <apply-templates />
                        </element>
                        <text>&#x0A;</text>
                    </if>

                </element>
                <text>&#x0A;</text>

            </element>
            <text>&#x0A;</text>

        </element>
        <text>&#x0A;</text>
    </template>

    <template match="classpathentry[(@kind='src') and starts-with(@path,'/') and (not(contains(@path,'-')))]">
        <element name="include" namespace="">
            <attribute name="name">
                <value-of select="substring(@path,2)" />
                <text>/src</text>
            </attribute>
        </element>
        <text>&#x0A;</text>
    </template>
</stylesheet>
