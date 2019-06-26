<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" />
  <!-- Either of theses work
  <xsl:variable name='newline'><xsl:text>
</xsl:text></xsl:variable>
  -->
  <!-- Prefer this one... -->
  <xsl:variable name='newline'><xsl:text>&#10;</xsl:text></xsl:variable>
  <xsl:template match="/">
     <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="courtesy">
    <xsl:apply-templates select="type"/> is <xsl:apply-templates select="phrase"/>
    <xsl:value-of select="$newline"/>

  </xsl:template>

</xsl:stylesheet> 


