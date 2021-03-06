package org.avm.device.generic.afficheur.matis;

import junit.framework.TestCase;

import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.matis.protocol.AfficheurProtocolFactory;

public class TestMATIS extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String message) {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory.create("MATIS");
		return AfficheurProtocol.toHexaAscii(protocol2.generateMessage(message));
	}

	private String generateStatus() {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory.create("MATIS");
		return AfficheurProtocol.toHexaAscii(protocol2.generateStatus());
	}

	public void testCode3() {
		String code = "3";
		String trame = generate(code);

		String expected = "02052331310D43330D020355";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}



}
