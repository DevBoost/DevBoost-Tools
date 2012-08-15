/*******************************************************************************
 * Copyright (c) 2006-2012
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.onthefly_javac.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

//this code is from http://fivedots.coe.psu.ac.th/~ad/jg/javaArt1/onTheFlyArt1.pdf
public class ByteArrayJavaFileObject extends SimpleJavaFileObject {
	
	private ByteArrayOutputStream baos = null;

	public ByteArrayJavaFileObject(String className, Kind kind) throws Exception {
		super(new URI(className), kind);
	}

	public InputStream openInputStream() throws IOException {
		// the input stream to the java file object accepts bytes
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public OutputStream openOutputStream() throws IOException {
		// the output stream supplies bytes
		return baos = new ByteArrayOutputStream();
	}

	public byte[] getByteArray() {
		// access the byte output stream as an array
		return baos.toByteArray();
	}
}
