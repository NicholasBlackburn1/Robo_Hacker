/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loader.game;

import net.fabricmc.loader.util.UrlConversionException;
import net.fabricmc.loader.util.UrlUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

final class GameProviderHelper {
	public static class EntrypointResult {
		public final String entrypointName;
		public final Path entrypointPath;

		EntrypointResult(String entrypointName, Path entrypointPath) {
			this.entrypointName = entrypointName;
			this.entrypointPath = entrypointPath;
		}
	}

	private GameProviderHelper() {

	}

	static Optional<Path> getSource(ClassLoader loader, String filename) {
		URL url;
		if ((url = loader.getResource(filename)) != null) {
			try {
				URL urlSource = UrlUtil.getSource(filename, url);
				Path classSourceFile = UrlUtil.asPath(urlSource);

				return Optional.of(classSourceFile);
			} catch (UrlConversionException e) {
				// TODO: Point to a logger
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	static List<Path> getSources(ClassLoader loader, String filename) {
		try {
			Enumeration<URL> urls = loader.getResources(filename);
			List<Path> paths = new ArrayList<>();

			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();

				try {
					URL urlSource = UrlUtil.getSource(filename, url);
					paths.add(UrlUtil.asPath(urlSource));
				} catch (UrlConversionException e) {
					// TODO: Point to a logger
					e.printStackTrace();
				}
			}

			return paths;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	static Optional<EntrypointResult> findFirstClass(ClassLoader loader, List<String> classNames) {
		List<String> entrypointFilenames = classNames.stream()
			.map((ep) -> ep.replace('.', '/') + ".class")
			.collect(Collectors.toList());

		for (int i = 0; i < entrypointFilenames.size(); i++) {
			String className = classNames.get(i);
			String classFilename = entrypointFilenames.get(i);
			Optional<Path> classSourcePath = getSource(loader, classFilename);
			if (classSourcePath.isPresent()) {
				return Optional.of(new EntrypointResult(className, classSourcePath.get()));
			}
		}

		return Optional.empty();
	}
}
