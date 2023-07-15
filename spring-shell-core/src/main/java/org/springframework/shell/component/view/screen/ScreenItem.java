/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.component.view.screen;

/**
 *
 *
 * @author Janne Valkealahti
 */
public interface ScreenItem {

    static final int STYLE_BOLD = 1;
    static final int STYLE_FAINT = STYLE_BOLD << 1;
    static final int STYLE_ITALIC = STYLE_BOLD << 2;
    static final int STYLE_UNDERLINE = STYLE_BOLD << 3;
    static final int STYLE_BLINK = STYLE_BOLD << 4;
    static final int STYLE_INVERSE = STYLE_BOLD << 5;
    static final int STYLE_CONCEAL = STYLE_BOLD << 6;
    static final int STYLE_CROSSEDOUT = STYLE_BOLD << 7;

	static final int BORDER_LEFT = 1;
	static final int BORDER_TOP = BORDER_LEFT << 1;
	static final int BORDER_RIGHT = BORDER_LEFT << 2;
	static final int BORDER_BOTTOM = BORDER_LEFT << 3;

	CharSequence getContent();

	int getBorder();

	int getBackground();

	int getForeground();

	int getStyle();

}
