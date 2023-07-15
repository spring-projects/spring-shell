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
package org.springframework.shell.component.view.event;

/**
 *
 *                           unused                        modifier       button           type
 *   [                                                   ] [      ] [               ] [            ]
 *   31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00
 *
 *
 */
public record MouseEvent(int x, int y, int mouse) {

	public static MouseEvent of(int x, int y, int mouse) {
		return new MouseEvent(x, y, mouse);
	}

	public static MouseEvent of(org.jline.terminal.MouseEvent event) {
		int type = switch (event.getType()) {
			case Released -> Type.Released;
			case Pressed -> Type.Pressed;
			case Wheel -> Type.Wheel;
			case Moved -> Type.Moved;
			case Dragged -> Type.Dragged;
			default -> 0;
		};
		int button = switch (event.getButton()) {
			case NoButton -> Button.NoButton;
			case Button1 -> Button.Button1;
			case Button2 -> Button.Button2;
			case Button3 -> Button.Button3;
			case WheelUp -> Button.WheelUp;
			case WheelDown -> Button.WheelDown;
			default -> 0;
		};
		int modifier = 0;
		if (event.getModifiers() != null) {
			if (event.getModifiers().contains(org.jline.terminal.MouseEvent.Modifier.Shift)) {
				modifier |= Modifier.Shift;
			}
			if (event.getModifiers().contains(org.jline.terminal.MouseEvent.Modifier.Alt)) {
				modifier |= Modifier.Alt;
			}
			if (event.getModifiers().contains(org.jline.terminal.MouseEvent.Modifier.Control)) {
				modifier |= Modifier.Control;
			}
		}
		return of(event.getX(), event.getY(), type | button | modifier);
	}

	public boolean hasType() {
		return (mouse & MouseMask.TypeMask) != 0;
	}

	public boolean hasButton() {
		return (mouse & MouseMask.ButtonMask) != 0;
	}

	public boolean hasModifier() {
		return (mouse & MouseMask.ModifierMask) != 0;
	}

	public boolean has(int mask) {
		return (mouse & mask) == mask;
	}

	public static class Type {
		public static final int Released = 0x00000001;
		public static final int Pressed = 0x00000002;
		public static final int Wheel = 0x00000004;
		public static final int Moved = 0x00000008;
		public static final int Dragged = 0x00000010;
	}

	public static class Button {
		public static final int NoButton = 0x00000020;
		public static final int Button1 = 0x00000040;
		public static final int Button2 = 0x00000080;
		public static final int Button3 = 0x00000100;
		public static final int WheelUp = 0x00000200;
		public static final int WheelDown = 0x00000400;
	}

	public static class Modifier {
		public static final int Shift = 0x00000800;
		public static final int Alt = 0x00001000;
		public static final int Control = 0x00002000;
	}

	public static class MouseMask {
		// bits 0-4
		public static final int TypeMask = 0x0000001f;
		// bits 5-10
		public static final int ButtonMask = 0x000007e0;
		// bits 11-13
		public static final int ModifierMask = 0x00003800;
	}

}
