/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.apache.commons.io.file.Counters;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.InfoCmp.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.PathSearch.PathSearchContext;
import org.springframework.shell.component.PathSearch.PathSearchContext.PathViewItem;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent;
import org.springframework.shell.component.support.Nameable;
import org.springframework.shell.component.support.SelectorList;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext.MessageLevel;
import org.springframework.shell.style.PartsText;
import org.springframework.shell.style.PartsText.PartText;
import org.springframework.shell.support.search.SearchMatch;
import org.springframework.shell.support.search.SearchMatchResult;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static org.jline.keymap.KeyMap.ctrl;
import static org.jline.keymap.KeyMap.key;

/**
 * Component resolving {@link Path} based on base path and optional search term.
 * User is expected to type a base path and then delimited by space and a search
 * term.
 *
 * Based on algorithms i.e. from https://github.com/junegunn/fzf and other
 * sources.
 *
 * @author Janne Valkealahti
 */
public class PathSearch extends AbstractTextComponent<Path, PathSearchContext> {

	private final static Logger log = LoggerFactory.getLogger(PathSearch.class);
	private final static String DEFAULT_TEMPLATE_LOCATION = "classpath:org/springframework/shell/component/path-search-default.stg";
	private final PathSearchConfig config;
	private PathSearchContext currentContext;
	private Function<String, Path> pathProvider = (path) -> Paths.get(path);
	private final SelectorList<PathViewItem> selectorList;

	public PathSearch(Terminal terminal) {
		this(terminal, null);
	}

	public PathSearch(Terminal terminal, String name) {
		this(terminal, name, null);
	}

	public PathSearch(Terminal terminal, String name, PathSearchConfig config) {
		this(terminal, name, config, null);
	}

	public PathSearch(Terminal terminal, String name, PathSearchConfig config,
			Function<PathSearchContext, List<AttributedString>> renderer) {
		super(terminal, name, null);
		setRenderer(renderer != null ? renderer : new DefaultRenderer());
		setTemplateLocation(DEFAULT_TEMPLATE_LOCATION);
		this.config = config != null ? config : new PathSearchConfig();
		this.selectorList = SelectorList.of(this.config.getMaxPathsShow());
	}

	@Override
	protected void bindKeyMap(KeyMap<String> keyMap) {
		super.bindKeyMap(keyMap);

		// additional binding what parent gives us
		keyMap.bind(OPERATION_DOWN, ctrl('E'), key(getTerminal(), Capability.key_down));
		keyMap.bind(OPERATION_UP, ctrl('Y'), key(getTerminal(), Capability.key_up));
	}

	@Override
	public PathSearchContext getThisContext(ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = PathSearchContext.empty();
		currentContext.setName(getName());
		currentContext.setTerminalWidth(getTerminal().getWidth());
		currentContext.setPathSearchConfig(this.config);
		currentContext.setMessage("Type '<path> <pattern>' to search", MessageLevel.INFO);
		if (context != null) {
			context.stream().forEach(e -> {
				currentContext.put(e.getKey(), e.getValue());
			});
		}
		return currentContext;
	}

	@Override
	protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, PathSearchContext context) {
		String operation = bindingReader.readBinding(keyMap);
		log.debug("Binding read result {}", operation);
		if (operation == null) {
			return true;
		}
		String input;
		switch (operation) {
			case OPERATION_CHAR:
				String lastBinding = bindingReader.getLastBinding();
				input = context.getInput();
				if (input == null) {
					input = lastBinding;
				}
				else {
					input = input + lastBinding;
				}
				context.setInput(input);
				inputUpdated(context, input);
				break;
			case OPERATION_BACKSPACE:
				input = context.getInput();
				if (StringUtils.hasLength(input)) {
					input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
				}
				context.setInput(input);
				inputUpdated(context, input);
				break;
			case OPERATION_EXIT:
				PathViewItem selected = selectorList.getSelected();
				if (selected != null) {
					context.setResultValue(selected.getPath());
				}
				return true;
			case OPERATION_UP:
				selectorList.scrollUp();
				selectorListUpdated(context);
				break;
			case OPERATION_DOWN:
				selectorList.scrollDown();
				selectorListUpdated(context);
				break;
			default:
				break;
		}
		return false;
	}

	/**
	 * Sets a path provider.
	 *
	 * @param pathProvider the path provider
	 */
	public void setPathProvider(Function<String, Path> pathProvider) {
		this.pathProvider = pathProvider;
	}

	/**
	 * Resolves a {@link Path} from a given raw {@code path}.
	 *
	 * @param path the raw path
	 * @return a resolved path
	 */
	protected Path resolvePath(String path) {
		return this.pathProvider.apply(path);
	}

	private void inputUpdated(PathSearchContext context, String input) {
		context.setMessage("Type '<path> <pattern>' to search", MessageLevel.INFO);
		updateSelectorList(input, context);
		selectorListUpdated(context);
	}

	private void selectorListUpdated(PathSearchContext context) {
		List<PathViewItem> pathViews = selectorList.getProjection().stream()
			.map(i -> {
				return new PathViewItem(i.getItem().getPath(), i.getItem().getPartsText(), i.isSelected());
			})
			.collect(Collectors.toList());
		context.setPathViewItems(pathViews);
	}

	private void updateSelectorList(String path, PathSearchContext context) {
		if (path == null) {
			// when user removes all input
			this.selectorList.reset(Collections.emptyList());
			return;
		}
		PathScannerResult result = this.config.pathScanner.get().apply(path, context);
		List<PathViewItem> items = result.getScoredPaths().stream()
			.filter(scoredPath -> {
				if (result.hasFilter()) {
					return scoredPath.result.getScore() > 0;
				}
				else {
					return true;
				}
			})
			.map(scoredPath -> {
					int[] positions = scoredPath.getResult().getPositions();
					String text = scoredPath.getPath().toString();
					if (!StringUtils.hasText(text)) {
						text = ".";
					}
					PartsText partsText = PathSearchContext.ofPositions(text, positions);
					PathViewItem item = new PathViewItem(scoredPath.getPath(), partsText, false);
					return item;
				})
			.collect(Collectors.toList());

		long total = result.getDirCount() + result.getFileCount();
		if (total > -1) {
			int found = items.size();
			String message = String.format(", %s/%s", found, total);
			context.setMessage("Type '<path> <pattern>' to search" + message, MessageLevel.INFO);
		}

		selectorList.reset(items);
	}

	/**
	 * Class defining configuration for path search.
	 */
	public static class PathSearchConfig {

		private int maxPathsShow = 5;
		private int maxPathsSearch = 20;
		private boolean searchForward = true;
		private boolean searchCaseSensitive = false;
		private boolean searchNormalize = false;
		private Supplier<BiFunction<String, PathSearchContext, PathScannerResult>> pathScanner = () -> DefaultPathScanner.of();

		public int getMaxPathsShow() {
			return this.maxPathsShow;
		}

		public void setMaxPathsShow(int maxPathsShow) {
			Assert.state(maxPathsShow > 0 || maxPathsShow < 33, "maxPathsShow has to be between 1 and 32");
			this.maxPathsShow = maxPathsShow;
		}

		public int getMaxPathsSearch() {
			return this.maxPathsSearch;
		}

		public void setMaxPathsSearch(int maxPathsSearch) {
			Assert.state(maxPathsSearch > 0, "maxPathsSearch has to be more than 0");
			this.maxPathsSearch = maxPathsSearch;
		}

		public void setPathScanner(Supplier<BiFunction<String, PathSearchContext, PathScannerResult>> pathScanner) {
			Assert.notNull(pathScanner, "pathScanner supplier cannot be null");
			this.pathScanner = pathScanner;
		}

		public boolean isSearchForward() {
			return searchForward;
		}

		public void setSearchForward(boolean searchForward) {
			this.searchForward = searchForward;
		}

		public boolean isSearchCaseSensitive() {
			return searchCaseSensitive;
		}

		public void setSearchCaseSensitive(boolean searchCaseSensitive) {
			this.searchCaseSensitive = searchCaseSensitive;
		}

		public boolean isSearchNormalize() {
			return searchNormalize;
		}

		public void setSearchNormalize(boolean searchNormalize) {
			this.searchNormalize = searchNormalize;
		}
	}

	/**
	 * Result from a path scanning.
	 */
	public static class PathScannerResult {

		private final List<ScoredPath> scoredPaths;
		private long dirCount = -1;
		private long fileCount = -1;
		private boolean hasFilter = false;

		PathScannerResult(List<ScoredPath> scoredPaths, long dirCount, long fileCount, boolean hasFilter) {
			Assert.notNull(scoredPaths, "Scored paths cannot be null");
			this.scoredPaths = scoredPaths;
			this.dirCount = dirCount;
			this.fileCount = fileCount;
			this.hasFilter = hasFilter;
		}

		public static PathScannerResult of(List<ScoredPath> scoredPaths, boolean hasFilter) {
			return new PathScannerResult(scoredPaths, -1, -1, hasFilter);
		}

		public static PathScannerResult of(List<ScoredPath> scoredPaths, long dirCount, long fileCount, boolean hasFilter) {
			return new PathScannerResult(scoredPaths, dirCount, fileCount, hasFilter);
		}

		public List<ScoredPath> getScoredPaths() {
			return scoredPaths;
		}

		public long getDirCount() {
			return dirCount;
		}

		public long getFileCount() {
			return fileCount;
		}

		public boolean hasFilter() {
			return hasFilter;
		}
	}

	/**
	 * Context for {@link PathSearch}.
	 */
	public interface PathSearchContext extends TextComponentContext<Path, PathSearchContext> {

		/**
		 * Gets a path view items.
		 *
		 * @return path view items
		 */
		List<PathViewItem> getPathViewItems();

		/**
		 * Sets a path view items.
		 *
		 * @param items the path view items
		 */
		void setPathViewItems(List<PathViewItem> items);

		/**
		 * Get path search config.
		 *
		 * @return a path search config
		 */
		PathSearchConfig getPathSearchConfig();

		/**
		 * Sets a path search config.
		 *
		 * @param config a path search config
		 */
		void setPathSearchConfig(PathSearchConfig config);

		/**
		 * Gets an empty {@link PathSearchContext}.
		 *
		 * @return empty path search context
		 */
		public static PathSearchContext empty() {
			return new DefaultPathSearchContext();
		}

		/**
		 * Domain class for path view item. Having its index, name(path), ref to cursor
		 * row index and list of name match parts.
		 */
		public static class PathViewItem implements Nameable {

			private Path path;
			private PartsText partsText;
			private boolean selected;

			public PathViewItem(Path path, PartsText partsText, boolean selected) {
				this.path = path;
				this.partsText = partsText;
				this.selected = selected;
			}

			@Override
			public String getName() {
				return path.toString();
			}

			public Path getPath() {
				return path;
			}

			public boolean isSelected() {
				return selected;
			}

			public PartsText getPartsText() {
				return partsText;
			}
		}


		/**
		 * Split given text into {@link PartText}'s by given positions.
		 *
		 * @param text the text to split
		 * @param positions the positions array, expected to be ordered and no duplicates
		 * @return parts text
		 */
		public static PartsText ofPositions(String text, int[] positions) {
			List<PartText> parts = new ArrayList<>();
			if (positions.length == 0) {
				parts.addAll(ofPosition(text, -1));
			}
			else if (positions.length == 1 && positions[0] == text.length()) {
				parts.addAll(ofPosition(text, text.length() - 1));
			}
			else {
				int sidx = 0;
				int eidx = 0;
				for (int i = 0; i < positions.length; i++) {
					eidx = positions[i];
					if (sidx < text.length()) {
						String partText = text.substring(sidx, eidx + 1);
						parts.addAll(ofPosition(partText, eidx - sidx));
					}
					else {
						parts.addAll(ofPosition(String.valueOf(text.charAt(text.length() - 1)), 0));
					}
					sidx = eidx + 1;
				}
				if (sidx < text.length()) {
					String partText = text.substring(sidx, text.length());
					parts.addAll(ofPosition(partText, -1));
				}
			}
			return PartsText.of(parts);
		}

		static List<PartText> ofPosition(String text, int position) {
			List<PartText> parts = new ArrayList<>();
			if (position < 0) {
				parts.add(PartText.of(text, false));
			}
			else {
				if (position == 0) {
					if (text.length() == 1) {
						parts.add(PartText.of(String.valueOf(text.charAt(0)), true));
					}
					else {
						parts.add(PartText.of(String.valueOf(text.charAt(0)), true));
						parts.add(PartText.of(text.substring(1, text.length()), false));
					}
				}
				else if (position == text.length() - 1) {
					parts.add(PartText.of(text.substring(0, text.length() - 1), false));
					parts.add(PartText.of(String.valueOf(text.charAt(text.length() - 1)), true));
				}
				else {
					parts.add(PartText.of(text.substring(0, position), false));
					parts.add(PartText.of(String.valueOf(text.charAt(position)), true));
					parts.add(PartText.of(text.substring(position + 1, text.length()), false));
				}
			}
			return parts;
		}
	}

	private static class DefaultPathSearchContext extends BaseTextComponentContext<Path, PathSearchContext>
			implements PathSearchContext {

		private List<PathViewItem> pathViewItems;
		private PathSearchConfig pathSearchConfig;

		@Override
		public List<PathViewItem> getPathViewItems() {
			return this.pathViewItems;
		}

		@Override
		public void setPathViewItems(List<PathViewItem> pathViewItems) {
			this.pathViewItems = pathViewItems;
		}

		@Override
		public PathSearchConfig getPathSearchConfig() {
			return this.pathSearchConfig;
		}

		@Override
		public void setPathSearchConfig(PathSearchConfig config) {
			this.pathSearchConfig = config;
		}

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			attributes.put("pathViewItems", getPathViewItems());
			Map<String, Object> model = new HashMap<>();
			model.put("model", attributes);
			return model;
		}
	}

	private class DefaultRenderer implements Function<PathSearchContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(PathSearchContext context) {
			return renderTemplateResource(context.toTemplateModel());
		}
	}

	/**
	 * Holder class keeping {@link Path} and {@link SearchMatchResult}.
	 */
	public static class ScoredPath implements Comparable<ScoredPath> {

		private final Path path;
		private final SearchMatchResult result;

		ScoredPath(Path path, SearchMatchResult result) {
			this.path = path;
			this.result = result;
		}

		public static ScoredPath of(Path path, SearchMatchResult result) {
			return new ScoredPath(path, result);
		}

		public Path getPath() {
			return this.path;
		}

		public SearchMatchResult getResult() {
			return this.result;
		}

		@Override
		public int compareTo(ScoredPath other) {
			int scoreCompare = Integer.compare(other.result.getScore(), this.result.getScore());
			if (scoreCompare == 0) {
				// secondary sort by path length
				return -Integer.compare(other.getPath().toString().length(), this.getPath().toString().length());
			}
			return scoreCompare;
		}
	}

	private static class DefaultPathScanner implements BiFunction<String, PathSearchContext, PathScannerResult> {

		static DefaultPathScanner of() {
			return new DefaultPathScanner();
		}

		@Override
		public PathScannerResult apply(String input, PathSearchContext context) {

			// input format <path> <pattern>
			String[] split = input.split(" ", 2);
			String match = split.length == 2 ? split[1] : null;

			// walk files to find candidates
			PathSearchPathVisitor visitor = new PathSearchPathVisitor(context.getPathSearchConfig().getMaxPathsSearch());
			try {
				String p = split[0];
				if (".".equals(p)) {
					p = "";
				}
				Path path = Path.of(p);
				log.debug("Walking input {} for path {}", input, path);
				Files.walkFileTree(path, visitor);
				log.debug("walked files {} dirs {}", visitor.getPathCounters().getFileCounter().get(),
						visitor.getPathCounters().getDirectoryCounter().get());
			} catch (Exception e) {
				log.debug("PathSearchPathVisitor caused exception", e);
			}

			// match and score candidates
 			Set<ScoredPath> treeSet = new HashSet<ScoredPath>();
			Stream.concat(visitor.getFileList().stream(), visitor.getDirList().stream())
				.forEach(p -> {
					SearchMatchResult result;
					if (StringUtils.hasText(match)) {
						SearchMatch searchMatch = SearchMatch.builder()
							.caseSensitive(context.getPathSearchConfig().isSearchCaseSensitive())
							.normalize(context.getPathSearchConfig().isSearchNormalize())
							.forward(context.getPathSearchConfig().searchForward)
							.build();
						result = searchMatch.match(p.toString(), match);
					}
					else {
						result = SearchMatchResult.ofMinus();
					}
					treeSet.add(ScoredPath.of(p, result));
				});

			// sort and limit
			return treeSet.stream()
				.sorted()
				.limit(context.getPathSearchConfig().getMaxPathsSearch())
				.collect(Collectors.collectingAndThen(Collectors.toList(),
						list -> PathScannerResult.of(list, visitor.getPathCounters().getDirectoryCounter().get(),
								visitor.getPathCounters().getFileCounter().get(), StringUtils.hasText(match))));
		}
	}

	/**
	 * Extension to AccumulatorPathVisitor which allows to break out from scanning
	 * when enough results are found.
	 */
	private static class PathSearchPathVisitor extends AccumulatorPathVisitor {

		private final int limitFiles;
		private final static IOFileFilter DNFILTER = new NotFileFilter(new WildcardFileFilter(".*"));

		PathSearchPathVisitor(int limitFiles) {
			super(Counters.longPathCounters(), DNFILTER, DNFILTER);
			this.limitFiles = limitFiles;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
			FileVisitResult result = super.visitFile(file, attributes);
			if (getPathCounters().getFileCounter().get() >= this.limitFiles) {
				return FileVisitResult.TERMINATE;
			}
			return result;
		}
	}
}
