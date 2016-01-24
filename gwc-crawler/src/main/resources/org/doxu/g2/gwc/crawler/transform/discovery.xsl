<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="4.0" indent="yes" encoding="utf-8" omit-xml-declaration="yes"
		doctype-system="http://www.w3.org/TR/html4/strict.dtd"
		doctype-public="-//W3C//DTD HTML 4.01//EN"/> 

	<xsl:template match="services">
		<html lang="en">
			<head>
				<title>Working <xsl:value-of select="@network" /> GWebCaches</title>
				<link rel="stylesheet" type="text/css" href="style.css"/>
				<script type="text/javascript" src="linklist.js" />
			</head>
			<body onload="init();">
				<ul class="menu">
					<li><a href="index.html">Gnutella2 (G2) GWebCaches</a></li>
					<!--<li><a href="gnutella.html">Gnutella GWebCaches</a></li>-->
					<li><a href="discovery.html">Gnutella2 (G2) Discovery</a></li>
					<!--<li><a href="gnutella_discovery.html">Gnutella Discovery</a></li>-->
				</ul>
				<div id="services">
					  <h1>Good <xsl:value-of select="@network" /> GWebCaches</h1>
					  <h2>Updated <xsl:value-of select="substring(@timestamp,0,11)"/></h2>
	
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
					
					<ul>
						<xsl:for-each select="service">
							<xsl:sort select="@score" order="descending" data-type="number" />
							<xsl:if test="@score >= $serviceMin">
								<li><a href="shareaza:gwc:{url}"><xsl:value-of select="url"/></a></li>
							</xsl:if>
						</xsl:for-each>
					</ul>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>