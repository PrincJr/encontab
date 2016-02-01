package encontab;

import encontab.crypt.RsaSigner;

public class Application {

	public static String version = "0.0-01";
	public static String id = "encontab.sistema.nfce";
	
	public static void main(String[] args) throws Exception{
		RsaSigner s = new RsaSigner();
		s.LoadPEMEncodedKey("../CA-key.pkcs8");
	}
}
