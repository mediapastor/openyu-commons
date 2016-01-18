package org.openyu.commons.xmpp;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.openyu.commons.service.BaseService;


public interface XmppConnectionFactory extends BaseService {

	/**
	 * 建立連線
	 * 
	 * @return
	 * @throws SmackException
	 * @throws IOException
	 * @throws XMPPException
	 */
	XMPPConnection createXMPPConnection() throws SmackException, IOException, XMPPException;

}
