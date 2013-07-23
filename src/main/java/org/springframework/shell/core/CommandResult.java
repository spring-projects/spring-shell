/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.shell.core;

public class CommandResult {

	private boolean success;
	
	private Object result;
	
	private Throwable exception;

	public CommandResult(boolean success) {
		this.success = success;
	}
	public CommandResult(boolean success, Object result, Throwable exception) {
		super();
		this.success = success;
		this.result = result;
		this.exception = exception;
	}

	public boolean isSuccess() {
		return success;
	}

	public Object getResult() {
		return result;
	}

	public Throwable getException() {
		return exception;
	}
	
	@Override
	public String toString() {
		return "CommandResult [success=" + success + ", result=" + result
				+ ", exception=" + exception + "]";
	}

	
}
