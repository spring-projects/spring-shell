/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.shell.command;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.messaging.handler.annotation.Header;

public abstract class AbstractCommandTests {

	protected Pojo1 pojo1;

	protected Function<CommandContext, String> function1 = ctx -> {
		String arg1 = ctx.getOptionValue("arg1");
		return "hi" + arg1;
	};

	protected Function<CommandContext, Void> function2 = ctx -> {
		return null;
	};

	@BeforeEach
	public void setupAbstractCommandTests() {
		pojo1 = new Pojo1();
	}

	protected static class Pojo1 {

		public int method1Count;
		public CommandContext method1Ctx;
		public int method1Mixed1Count;
		public String method1Mixed1Arg1;
		public CommandContext method1Mixed1Ctx;
		public String method1Mixed1Arg2;
		public int method2Count;
		public int method3Count;
		public int method4Count;
		public String method4Arg1;
		public Boolean method5ArgA;
		public Boolean method5ArgB;
		public Boolean method5ArgC;
		public int method6Count;
		public String method6Arg1;
		public String method6Arg2;
		public String method6Arg3;
		public int method7Count;
		public int method7Arg1;
		public int method7Arg2;
		public int method7Arg3;
		public int method8Count;
		public float[] method8Arg1;
		public int method9Count;
		public String[] method9Arg1;

		public void method1(CommandContext ctx) {
			method1Ctx = ctx;
			method1Count++;
		}

		public void method1Mixed1(String arg1, CommandContext ctx, String arg2) {
			method1Mixed1Arg1 = arg1;
			method1Mixed1Ctx = ctx;
			method1Mixed1Arg2 = arg2;
			method1Mixed1Count++;
		}

		public String method2() {
			method2Count++;
			return "hi";
		}

		public String method3(@Header("arg1") String arg1) {
			method3Count++;
			return "hi" + arg1;
		}

		public String method4(String arg1) {
			method4Arg1 = arg1;
			method4Count++;
			return "hi" + arg1;
		}

		public void method5(@Header("a") boolean a, @Header("b") boolean b, @Header("c") boolean c) {
			method5ArgA = a;
			method5ArgB = b;
			method5ArgC = c;
		}

		public String method6(String arg1, String arg2, String arg3) {
			method6Arg1 = arg1;
			method6Arg2 = arg2;
			method6Arg3 = arg3;
			method6Count++;
			return "hi" + arg1 + arg2 + arg3;
		}

		public void method7(int arg1, int arg2, int arg3) {
			method7Arg1 = arg1;
			method7Arg2 = arg2;
			method7Arg3 = arg3;
			method7Count++;
		}

		public void method8(float[] arg1) {
			method8Arg1 = arg1;
			method8Count++;
		}

		public void method9(String[] arg1) {
			method9Arg1 = arg1;
			method9Count++;
		}
	}
}
