<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:template match="/">
  <html><body>
     <xsl:apply-templates/>
  </body></html>
  </xsl:template>

  <xsl:template match="courtesy">
      <xsl:apply-templates select="type"/> is "<b><xsl:apply-templates select="phrase"/></b>"

  </xsl:template>

</xsl:stylesheet> 


