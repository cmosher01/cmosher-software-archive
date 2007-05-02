<?xml version="1.0" encoding="UTF-8"?>
<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="xml" />
    <template match="text()" />

    <template match="classpath">
        <element name="project" namespace="">
            <attribute name="name">rsrc_xslgen</attribute>
            <text>&#x0A;</text>

            <element name="target" namespace="">
                <attribute name="name">rsrc</attribute>
                <text>&#x0A;</text>

                <if test="count(classpathentry[(@kind='src') and starts-with(@path,'/') and (not(contains(@path,'-')))]) > 0">

                    <element name="fileset" namespace="">
                        <attribute name="id">rsrcfiles</attribute>
                        <attribute name="dir">..</attribute>
                        <text>&#x0A;</text>
    
                        <apply-templates />
    
                        <element name="exclude" namespace="">
                            <attribute name="name">**/*.java</attribute>
                            <text>&#x0A;</text>
                        </element>
                        <text>&#x0A;</text>
    
                        <element name="exclude" namespace="">
                            <attribute name="name">**/package.html</attribute>
                            <text>&#x0A;</text>
                        </element>
                        <text>&#x0A;</text>
    
                    </element>
                    <text>&#x0A;</text>

                </if>

            </element>
            <text>&#x0A;</text>

        </element>
        <text>&#x0A;</text>
    </template>

    <template match="classpathentry[(@kind='src') and starts-with(@path,'/') and (not(contains(@path,'-')))]">
        <element name="include" namespace="">
            <attribute name="name">
                <value-of select="substring(@path,2)" />
                <text>/src/**/*</text>
            </attribute>
        </element>
        <text>&#x0A;</text>
    </template>
</stylesheet>
