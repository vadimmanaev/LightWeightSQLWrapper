/*******************************************************************************
 * Copyright 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.vladimanaev.lightweight.exceptions;

import java.sql.SQLException;

public class IllegalSQLQueryException extends SQLException {
	private static final long serialVersionUID = -4558850917607742767L;

	public IllegalSQLQueryException(String msg) {
		super(msg);
	}

	public IllegalSQLQueryException(Throwable e) {
		super(e);
	}
	
	public IllegalSQLQueryException(String msg, Throwable e) {
		super(msg, e);
	}
}
