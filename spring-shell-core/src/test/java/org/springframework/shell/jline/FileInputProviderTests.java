package org.springframework.shell.jline;

import org.jline.reader.EOFError;
import org.jline.reader.impl.DefaultParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileInputProviderTests {
	private final ExtendedDefaultParser springParser = new ExtendedDefaultParser();
	private final DefaultParser jlineParser = new DefaultParser();
	private FileInputProvider fileInputProvider;

	static Stream<Arguments> regularLinesUnclosedQuotes() {
		return Stream.of(
				Arguments.of("Regular line with unclosed 'quote"),
				Arguments.of("Regular line with unclosed \"quote")
		);
	}

	static Stream<Arguments> commentsUnclosedQuotes() {
		return Stream.of(
				Arguments.of("//Commented line with unclosed 'quote"),
				Arguments.of("//Commented line with unclosed \"quote")
		);
	}

	@ParameterizedTest
	@MethodSource("regularLinesUnclosedQuotes")
	void shouldThrowOnUnclosedQuoteDefaultParser(String line) {
		jlineParser.setEofOnUnclosedQuote(true);
		Reader reader = new StringReader(line);
		fileInputProvider = new FileInputProvider(reader, jlineParser);
		Exception exception = assertThrows(EOFError.class, () -> {
			fileInputProvider.readInput();
		});
		String expectedExceptionMessage = "Missing closing quote";
		String actualExceptionMessage = exception.getMessage();
		assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
	}

	@ParameterizedTest
	@MethodSource("regularLinesUnclosedQuotes")
	void shouldThrowOnUnclosedQuoteExtendedParser(String line) {
		springParser.setEofOnUnclosedQuote(true);
		Reader reader = new StringReader(line);
		fileInputProvider = new FileInputProvider(reader, springParser);
		Exception exception = assertThrows(EOFError.class, () -> {
			fileInputProvider.readInput();
		});
		String expectedExceptionMessage = "Missing closing quote";
		String actualExceptionMessage = exception.getMessage();
		assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
	}

	@ParameterizedTest
	@MethodSource("commentsUnclosedQuotes")
	void shoulNotThrowOnUnclosedQuoteDefaultParser(String line) {
		jlineParser.setEofOnUnclosedQuote(true);
		Reader reader = new StringReader(line);
		fileInputProvider = new FileInputProvider(reader, jlineParser);
		assertDoesNotThrow(() -> {
			fileInputProvider.readInput();
		});
	}

	@ParameterizedTest
	@MethodSource("commentsUnclosedQuotes")
	void shouldNotThrowOnUnclosedQuoteExtendedParser(String line) {
		springParser.setEofOnUnclosedQuote(true);
		Reader reader = new StringReader(line);
		fileInputProvider = new FileInputProvider(reader, springParser);
		assertDoesNotThrow(() -> {
			fileInputProvider.readInput();
		});
	}
}