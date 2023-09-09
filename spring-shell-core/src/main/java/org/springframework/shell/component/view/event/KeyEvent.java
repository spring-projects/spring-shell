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
 *        mask         special keys                  unicode keys                    ascii keys
 *   [         ] [                     ] [                                 ] [                     ]
 *   31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00
 *
 *
 */
public record KeyEvent(int key, String data) {

	public static KeyEvent of(int key) {
		return new KeyEvent(key, null);
	}

	public static KeyEvent of(String raw) {
		return new KeyEvent(Key.Unicode, raw);
	}

	public boolean hasCtrl() {
		return ((key >> 30) & 1) == 1;
	}

	public int getPlainKey() {
		return key & ~0xFFF00000;
	}

	public boolean isKey(int match) {
		return (key & ~0xF0000000) == match;
	}

	public boolean isKey() {
		return (key & ~KeyMask.CharMask) == 0;
	}

	public static class Key {

		public static final int Space = 32;
		public static final int ExclamationMark = 33;
		public static final int DoubleQuote = 34;
		public static final int NumberSign = 35;
		public static final int Dollar = 36;
		public static final int Percent = 37;
		public static final int Ampersand = 38;
		public static final int SingleQuote = 39;
		public static final int LeftParenthesis = 40;
		public static final int RightParenthesis = 41;
		public static final int Asterisk = 42;
		public static final int Plus = 43;
		public static final int Comma = 44;
		public static final int Hyphen = 45;
		public static final int Dot = 46;
		public static final int Slash = 47;

		public static final int Zero = 48;
		public static final int One = 49;
		public static final int Two = 50;
		public static final int Three = 51;
		public static final int Four = 52;
		public static final int Five = 53;
		public static final int Six = 54;
		public static final int Seven = 55;
		public static final int Eight = 56;
		public static final int Nine = 57;

		public static final int Colon = 58;
		public static final int SemiColon = 59;
		public static final int OpenBracket = 60;
		public static final int Equals = 61;
		public static final int CloseBracket = 62;
		public static final int QuestionMark = 63;
		public static final int EuroSign = 64;

		public static final int A = 65;
		public static final int B = 66;
		public static final int C = 67;
		public static final int D = 68;
		public static final int E = 69;
		public static final int F = 70;
		public static final int G = 71;
		public static final int H = 72;
		public static final int I = 73;
		public static final int J = 74;
		public static final int K = 75;
		public static final int L = 76;
		public static final int M = 77;
		public static final int N = 78;
		public static final int O = 79;
		public static final int P = 80;
		public static final int Q = 81;
		public static final int R = 82;
		public static final int S = 83;
		public static final int T = 84;
		public static final int U = 85;
		public static final int V = 86;
		public static final int W = 87;
		public static final int X = 88;
		public static final int Y = 89;
		public static final int Z = 90;

		public static final int a = 97;
		public static final int b = 98;
		public static final int c = 99;
		public static final int d = 100;
		public static final int e = 101;
		public static final int f = 102;
		public static final int g = 103;
		public static final int h = 104;
		public static final int i = 105;
		public static final int j = 106;
		public static final int k = 107;
		public static final int l = 108;
		public static final int m = 109;
		public static final int n = 110;
		public static final int o = 111;
		public static final int p = 112;
		public static final int q = 113;
		public static final int r = 114;
		public static final int s = 115;
		public static final int t = 116;
		public static final int u = 117;
		public static final int v = 118;
		public static final int w = 119;
		public static final int x = 120;
		public static final int y = 121;
		public static final int z = 122;

		public static final int BraceLeft = 123;
		public static final int Pipe = 124;
		public static final int BraceRight = 125;
		public static final int Tilde = 126;

		public static final int CursorUp = 0x100000;
		public static final int CursorDown = 0x100001;
		public static final int CursorLeft = 0x100002;
		public static final int CursorRight = 0x100003;
		public static final int Enter = 0x100004;
		public static final int Backspace = 0x100005;
		public static final int Delete = 0x100006;
		public static final int Tab = 0x100007;
		public static final int Backtab = 0x100008;

		public static final int f1 = 0x100009;
		public static final int f2 = 0x10000a;
		public static final int f3 = 0x10000b;
		public static final int f4 = 0x10000c;
		public static final int f5 = 0x10000d;
		public static final int f6 = 0x10000e;
		public static final int f7 = 0x10000f;
		public static final int f8 = 0x100010;
		public static final int f9 = 0x100011;
		public static final int f10 = 0x100012;
		public static final int f11 = 0x100013;
		public static final int f12 = 0x100014;

		public static final int Char = 0x1000000;
		public static final int Mouse = 0x1000001;
		public static final int Unicode = 0x1000002;

	}

	public static class KeyMask {
		public static final int CharMask = 0x000fffff;
		public static final int SpecialMask = 0xfff00000;
		public static final int ShiftMask = 0x10000000;
		public static final int CtrlMask = 0x40000000;
		public static final int AltMask = 0x80000000;
	}

}
