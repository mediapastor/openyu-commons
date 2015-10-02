package org.openyu.commons.fto.commons.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.openyu.commons.commons.net.ftp.CnfSession;
import org.openyu.commons.commons.net.ftp.CnfSessionFactory;
import org.openyu.commons.fto.commons.net.ftp.ex.CnfTemplateException;

public interface CnfTemplate {

	CnfSessionFactory getCnfSessionFactory();

	void setCnfSessionFactory(CnfSessionFactory cnfSessionFactory);

	CnfSession getSession();

	void closeSession();

	<T> T execute(CnfCallback<T> action) throws CnfTemplateException;

	// --------------------------------------------------
	
	String[] listNames(String pathname) throws IOException;

	String[] listNames() throws IOException;

	FTPFile[] listFiles(String pathname) throws IOException;

	FTPFile[] listFiles() throws IOException;

	FTPFile[] listFiles(String pathname, FTPFileFilter filter)
			throws IOException;

	FTPFile[] listDirectories() throws IOException;

	FTPFile[] listDirectories(String parent) throws IOException;

	// --------------------------------------------------
	boolean read(String remote, OutputStream local) throws IOException;

	boolean write(String remote, InputStream local) throws IOException;

	boolean mkdir(String pathname) throws IOException;

	boolean delete(String path) throws IOException;

	boolean rename(String from, String to) throws IOException;

	boolean exists(String path) throws IOException;
}
