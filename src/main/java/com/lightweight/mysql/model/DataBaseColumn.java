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

package com.lightweight.mysql.model;


import java.util.Objects;

/**
 * This class serve as column in MySQL database
 * 
 * @author Vladi - 01:59 PM 9/12/2013
 */
public class DataBaseColumn {
	
	private final String columnName;
	private final String columnValue;
	
	public DataBaseColumn(String columnName, String columnValue) {
		this.columnName = columnName;
		this.columnValue = columnValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getColumnValue() {
		return columnValue;
	}

	@Override
	public String toString() {
		return String.format("%s : %s", columnName, columnValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(columnName, columnValue);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final DataBaseColumn other = (DataBaseColumn) obj;
		return Objects.equals(columnName, other.columnName) 
			   && Objects.equals(columnValue, other.columnValue);
	}
}