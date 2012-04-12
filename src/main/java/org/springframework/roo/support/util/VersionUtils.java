/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.roo.support.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;


/**
 * @author Jarred Li
 *
 */
public class VersionUtils {

	public static String versionInfo() {
		// Try to determine the bundle version
		String bundleVersion = null;
		JarFile jarFile = null;
		try {
			URL classContainer = VersionUtils.class.getProtectionDomain().getCodeSource().getLocation();
			if (classContainer.toString().endsWith(".jar")) {
				// Attempt to obtain the "Bundle-Version" version from the manifest
				jarFile = new JarFile(new File(classContainer.toURI()), false);
				ZipEntry manifestEntry = jarFile.getEntry("META-INF/MANIFEST.MF");
				Manifest manifest = new Manifest(jarFile.getInputStream(manifestEntry));
				bundleVersion = manifest.getMainAttributes().getValue("version");
			}
		} catch (IOException ignoreAndMoveOn) {
		} catch (URISyntaxException ignoreAndMoveOn) {
		} finally {
			IOUtils.closeQuietly(jarFile);
		}

		StringBuilder sb = new StringBuilder();

		if (bundleVersion != null) {
			sb.append(bundleVersion);
		}


		if (sb.length() == 0) {
			sb.append("UNKNOWN VERSION");
		}

		return sb.toString();
	}

}
