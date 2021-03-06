/* @flavorc
 *
 * PriseEnCharge.java
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

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import flavor.Bitstream;
import flavor.IBitstream;
import flavor.MapResult;

public class PriseEnCharge extends Message {
    CString _tel;
    
	public static final int MESSAGE_TYPE = 4;

	public static final String MESSAGE_NAME = "prise-en-charge";

	public PriseEnCharge() {
		super();
		_entete._type =  MESSAGE_TYPE;
		_entete._champs._date = 1;
		_entete._champs._position = 0;
		_entete._champs._service = 0;
		_entete._champs._anomalie = 0;
		_entete._champs._options = 0;
		_tel = new CString();
	}
	

	public String getTel() {
		return _tel.getValue();	
	}
	
	public void setTel(String value) {		
		_tel.setValue(value);		
	}
	
	public String toString() {
		return MESSAGE_NAME + " [" + super.toString() + "]"
		+ " tel: " + _tel.toString();
	}

	public static class DefaultMessageFactory extends MessageFactory {

		protected Message unmarshal(InputStream in) throws Exception {
			IBindingFactory factory = BindingDirectory
					.getFactory(PriseEnCharge.class);
			IUnmarshallingContext context = factory
					.createUnmarshallingContext();
			return (Message) context.unmarshalDocument(in, null);

		}

		protected Message get(InputStream in) throws Exception {
			Bitstream bs = new Bitstream(in);
			Message message = new PriseEnCharge();
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
        _F_ret += super.get(_F_bs);
        _tel = new CString();
        _F_ret += _tel.get(_F_bs);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_ret += super.put(_F_bs);
        _F_ret += _tel.put(_F_bs);
        return _F_ret;
    }
}
