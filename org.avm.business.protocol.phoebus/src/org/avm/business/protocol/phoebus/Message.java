/* @flavorc
 *
 * Message.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'phoebus.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.business.protocol.phoebus;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

import flavor.Bitstream;
import flavor.IBitstream;
import flavor.MapResult;

public class Message {
    Entete _entete;
    	 
	public static final int MESSAGE_TYPE = 0;

	public static final String MESSAGE_NAME = "message";
	
	public Message() {
		_entete = new Entete();
	}

	public Message(Entete entete) {
		_entete = entete;
	}

	public Entete getEntete() {
		return _entete;
	}

	public void setEntete(Entete entete) {
		_entete = entete;
	}

	public String toString() {
		String result = "";
		if (_entete != null)
			result += "entete: " + _entete.toString();
		return result;
	}

	public void put(OutputStream out) throws Exception {
		Bitstream bs = new Bitstream(out);
		put(bs);
		bs.close();
	}

	public void marshal(OutputStream out) throws Exception {
		IBindingFactory factory = BindingDirectory.getFactory(this.getClass());
		IMarshallingContext context = factory.createMarshallingContext();
		context.marshalDocument(this, null, null, out);
	}

	public static class DefaultMessageFactory extends MessageFactory {

		protected Message unmarshal(InputStream in) throws Exception {
			IBindingFactory factory = BindingDirectory
					.getFactory(Message.class);
			IUnmarshallingContext context = factory
					.createUnmarshallingContext();
			return (Message) context.unmarshalDocument(in, null);

		}

		protected Message get(InputStream in) throws Exception {
			Bitstream bs = new Bitstream(in);
			Message message = new Message();
			message.get(bs);
			bs.close();
			in.reset();
			return message;
		}

	}

	static {
		MessageFactory.factories.put(new Integer(MESSAGE_TYPE),
				new DefaultMessageFactory());
	}

	

    public int get(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _entete = new Entete();
        _F_ret += _entete.get(_F_bs);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_ret += _entete.put(_F_bs);
        return _F_ret;
    }
}
