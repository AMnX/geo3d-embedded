package org.avm.device.generic.afficheur.duhamel;

import junit.framework.TestCase;

import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.duhamel.protocol.AfficheurProtocolFactory;

public class TestDuhatiers extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	private String generate(String message) {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory
				.create("Duhatiers");
		return AfficheurProtocol
				.toHexaAscii(protocol2.generateMessage(message));
	}

	private String generateStatus() {
		AfficheurProtocol protocol2 = AfficheurProtocolFactory
				.create("Duhatiers");
		return AfficheurProtocol.toHexaAscii(protocol2.generateStatus());
	}

	public void testCodeMessageBonjour() {
		String code = "BONJOUR";
		String trame = generate(code);

		String expected = "0278001D07424F4E4A4F55522303";
		debug("code=" + code + "   =>" + trame);
		assertEquals(trame.length(), expected.length());
		assertEquals(expected, trame);
	}

}
