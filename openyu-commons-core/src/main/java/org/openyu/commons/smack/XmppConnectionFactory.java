package org.openyu.commons.smack;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.openyu.commons.service.BaseService;

public interface XmppConnectionFactory extends BaseService {

	/**
	 * 取得連線
	 * 
	 * @return
	 * @throws SmackException
	 * @throws IOException
	 * @throws XMPPException
	 */
	XMPPConnection getXMPPConnection() throws SmackException, IOException, XMPPException;
}
