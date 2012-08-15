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

import java.io.IOException;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

//this code is from http://fivedots.coe.psu.ac.th/~ad/jg/javaArt1/onTheFlyArt1.pdf
public class ByteJavaFileManager<T extends JavaFileManager> extends
		ForwardingJavaFileManager<T> {

	// global
	private Map<String, ByteArrayJavaFileObject> store;

	public ByteJavaFileManager(T fileManager, Map<String, ByteArrayJavaFileObject> store) {
		super(fileManager);
		this.store = store;
	}

	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		try {
			ByteArrayJavaFileObject jfo = new ByteArrayJavaFileObject(className, kind);
			store.put(className, jfo);
			return jfo;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
