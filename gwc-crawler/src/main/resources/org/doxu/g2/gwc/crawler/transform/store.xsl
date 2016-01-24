<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" encoding="utf-8"/> 

	<xsl:template match="services">
					<xsl:variable name="serviceCount">
						<xsl:value-of select="count(service[@score > 0])"/>
					</xsl:variable>
					
					<xsl:variable name="serviceMiddle">
						<xsl:value-of select="ceiling($serviceCount div 2)"/>
					</xsl:variable>
					
					<xsl:variable name="serviceMedian">
						<xsl:for-each select="service">
							<xsl:sort select="@score" order="descending" data-type="number" />
							<xsl:if test="position() = $serviceMiddle">
								<xsl:value-of select="@score"/>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>

					<xsl:variable name="serviceMean">
						<xsl:value-of select="100 div $serviceCount"/>
					</xsl:variable>
					
					<xsl:variable name="serviceMin">
						<xsl:value-of select="($serviceMedian + $serviceMean) div 2"/>
					</xsl:variable>
					
						<xsl:for-each select="service">
							<xsl:sort select="@score" order="descending" data-type="number" />
							<xsl:if test="@score >= $serviceMin">
								<xsl:value-of select="url"/>|<xsl:value-of select="client"/>|<xsl:value-of select="format-dateTime(@timestamp, '[M01]/[D01]/[Y0001] [h01]:[m01]:[s01] [PN]', 'en', 'AD', 'US')"/><xsl:text>&#xa;</xsl:text>
							</xsl:if>
						</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>