package com.rays.exception;

/**
 * DatabaseException is propogated by DAO classes when an unhandled Database
 * exception occurred
 * Dilip Malav 
 */
public class DatabaseException extends RuntimeException {

	/**
	 * @param msg
	 *            : Error message
	 */
	public DatabaseException(String msg) {   //Unchecked exception
		super(msg);
	}

}