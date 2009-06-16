/*
 * eID Applet Project.
 * Copyright (C) 2009 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package be.fedict.eid.applet.service.signer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JSR105 URI dereferencer implementation using an ODF file as resource
 * repository.
 * 
 * @author fcorneli
 * 
 */
public class ODFURIDereferencer implements URIDereferencer {

	private static final Log LOG = LogFactory.getLog(ODFURIDereferencer.class);

	private final URL odfUrl;

	public ODFURIDereferencer(URL odfUrl) {
		this.odfUrl = odfUrl;
	}

	public Data dereference(URIReference uriReference, XMLCryptoContext context)
			throws URIReferenceException {
		String uri = uriReference.getURI();
		LOG.debug("dereference: " + uri);
		try {
			InputStream dataInputStream = findDataInputStream(uri);
			if (null == dataInputStream) {
				LOG.debug("return null");
				return null;
			}
			return new OctetStreamData(dataInputStream, uri, null);
		} catch (IOException e) {
			throw new URIReferenceException("I/O error: " + e.getMessage(), e);
		}
	}

	private InputStream findDataInputStream(String uri) throws IOException {
		InputStream odfInputStream = this.odfUrl.openStream();
		ZipInputStream odfZipInputStream = new ZipInputStream(odfInputStream);
		ZipEntry zipEntry;
		while (null != (zipEntry = odfZipInputStream.getNextEntry())) {
			if (zipEntry.getName().equals(uri)) {
				return odfZipInputStream;
			}
		}
		return null;
	}
}
