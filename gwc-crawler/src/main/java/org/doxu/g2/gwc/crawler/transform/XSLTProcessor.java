/**
 * Copyright 2016 Rafael Bedia
 *
 * This file is part of g2spook.
 *
 * g2spook is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * g2spook is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * g2spook. If not, see <http://www.gnu.org/licenses/>.
 */
package org.doxu.g2.gwc.crawler.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

public class XSLTProcessor {

    public static final String SERVICES_XSL = "services.xsl";
    public static final String DISCOVERY_XSL = "discovery.xsl";
    public static final String STORE_XSL = "store.xsl";

    public static void transform(Processor proc, String xsltName, File xml, File html) throws SaxonApiException, IOException {
        try (InputStream xslt = XSLTProcessor.class.getResourceAsStream(xsltName)) {
            transform(proc, xslt, xml, html);
        }
    }

    public static void transform(Processor proc, InputStream xslt, File xml, File html) throws SaxonApiException {
        XsltCompiler xsltCompiler = proc.newXsltCompiler();
        XsltExecutable exp = xsltCompiler.compile(new StreamSource(xslt));

        XdmNode source = proc.newDocumentBuilder().build(xml);
        Serializer out = proc.newSerializer(html);
        out.setOutputProperty(Serializer.Property.METHOD, "html");
        out.setOutputProperty(Serializer.Property.INDENT, "yes");

        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();
    }
}
