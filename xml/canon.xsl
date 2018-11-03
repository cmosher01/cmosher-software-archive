<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" />
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="@*">
                <xsl:sort select="name()" />
                <xsl:sort select="@*" />
            </xsl:apply-templates>
            <xsl:apply-templates select="node()">
                <xsl:sort select="name()" />
                <xsl:sort select="@*" />
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
