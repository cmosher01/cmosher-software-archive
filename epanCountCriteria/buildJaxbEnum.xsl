<?xml version="1.0" encoding="UTF-8"?>
<xslt:stylesheet
    version="1.0"
    xmlns:xslt="http://www.w3.org/1999/XSL/Transform"
    xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
    xmlns:schema="http://www.w3.org/2001/XMLSchema"
>
    <xslt:param name="namespace" />
    <xslt:param name="typeName" />
    <xslt:param name="baseType" />

    <xslt:output method="xml" />
    <xslt:template match="text()" />

    <xslt:template match="ROWSET">
        <xslt:element name="schema" namespace="http://www.w3.org/2001/XMLSchema">
            <xslt:attribute name="targetNamespace"><xslt:value-of select="$namespace" /></xslt:attribute>
            <xslt:attribute name="version" namespace="http://java.sun.com/xml/ns/jaxb">1.0</xslt:attribute>
            <xslt:text>&#x0A;</xslt:text>

            <xslt:element name="simpleType" namespace="http://www.w3.org/2001/XMLSchema">
                <xslt:attribute name="name"><xslt:value-of select="$typeName" /></xslt:attribute>
                <xslt:text>&#x0A;</xslt:text>
                <schema:annotation><xslt:text>&#x0A;</xslt:text>
                    <schema:appinfo><xslt:text>&#x0A;</xslt:text>
                        <jaxb:typesafeEnumClass><xslt:text>&#x0A;</xslt:text>
                            <xslt:apply-templates>
                                <xslt:with-param name="type">jaxb</xslt:with-param>
                            </xslt:apply-templates>
                        </jaxb:typesafeEnumClass><xslt:text>&#x0A;</xslt:text>
                    </schema:appinfo><xslt:text>&#x0A;</xslt:text>
                </schema:annotation><xslt:text>&#x0A;</xslt:text>
                <xslt:element name="restriction" namespace="http://www.w3.org/2001/XMLSchema">
                    <xslt:attribute name="base">
                    	<xslt:text>schema:</xslt:text><xslt:value-of select="$baseType" />
                    </xslt:attribute>
                    <xslt:text>&#x0A;</xslt:text>
                    <xslt:apply-templates />
                </xslt:element>
                <xslt:text>&#x0A;</xslt:text>
            </xslt:element>
            <xslt:text>&#x0A;</xslt:text>

        </xslt:element>
        <xslt:text>&#x0A;</xslt:text>
    </xslt:template>

    <xslt:template match="ROW">
        <xslt:param name="type">xsd</xslt:param>
        <xslt:if test="$type = 'jaxb'">
            <xslt:element name="jaxb:typesafeEnumMember">
                <xslt:attribute name="name"><xslt:value-of select="NAME" /></xslt:attribute>
                <xslt:attribute name="value"><xslt:value-of select="VALUE" /></xslt:attribute>
            </xslt:element>
        </xslt:if>
        <xslt:if test="$type = 'xsd'">
            <xslt:element name="enumeration" namespace="http://www.w3.org/2001/XMLSchema">
                <xslt:attribute name="value"><xslt:value-of select="VALUE" /></xslt:attribute>
            </xslt:element>
        </xslt:if>
        <xslt:text>&#x0A;</xslt:text>
    </xslt:template>
</xslt:stylesheet>
