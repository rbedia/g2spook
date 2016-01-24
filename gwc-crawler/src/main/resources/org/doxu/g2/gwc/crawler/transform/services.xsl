<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" version="4.0" indent="yes" encoding="utf-8" omit-xml-declaration="yes"
     doctype-system="http://www.w3.org/TR/html4/strict.dtd"
     doctype-public="-//W3C//DTD HTML 4.01//EN"/>
     
  <xsl:key name="services-by-status" match="service" use="@status" />
  <xsl:template match="services">
    <html lang="en">
      <head>
        <title><xsl:value-of select="@network" /> GWebCaches</title>
        <link rel="stylesheet" type="text/css" href="style.css"/>
      </head>
      <body>
        <ul class="menu">
          <li><a href="index.html">Gnutella2 (G2) GWebCaches</a></li>
          <!--<li><a href="gnutella.html">Gnutella GWebCaches</a></li>-->
          <li><a href="discovery.html">Gnutella2 (G2) Discovery</a></li>
          <!--<li><a href="gnutella_discovery.html">Gnutella Discovery</a></li>-->
        </ul>
        <h1><xsl:value-of select="@network" /> GWebCaches</h1>
        <table summary="List of all {@network} GWebCaches found by crawling">
          <caption>Updated <xsl:value-of select="substring(@timestamp,0,11)" /></caption>
          <thead>
            <tr>
              <th scope="col">Score</th>
              <th scope="col"><abbr title="Uniform Resource Locator">URL</abbr></th>
              <th scope="col">Version</th>
              <th scope="col"><abbr title="Internet Protocol">IP</abbr> Address</th>
              <th scope="col">Hosts</th>
              <th scope="col">&#916; Age</th>
              <th scope="col">URLs</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="service">
              <xsl:sort select="@score" order="descending" data-type="number" />
              <xsl:sort select="@status" order="descending" />
              <xsl:sort select="client" />
              <tr>
                <td><xsl:value-of select="format-number(@score,'0.0')"/></td>
                <td><xsl:value-of select="url"/></td>
                <td><xsl:value-of select="client"/></td>
                <td><xsl:value-of select="ip"/></td>
                <td><xsl:value-of select="hosts/text()[normalize-space()]"/></td>
                <td><xsl:value-of select="delta_age"/></td>
                <td><xsl:value-of select="urls"/></td>
                <td><xsl:value-of select="@status"/></td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
