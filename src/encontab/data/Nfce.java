package encontab.data;
import java.lang.String;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import encontab.consts.NfceEmitente;
import encontab.consts.NfceInfo;

public class Nfce {
	String id;
	float pIcms = 0;
	float ipi = 0;
	float vIcms = 0;
	float valor_ipi = 0;
	String dados_adic;
	String inscr_estadual;
	String cnpj;
	Date data_emissao;
	Date data_saida;
	String uf;
	String codChaveAcesso;
	
	public void saveAsXML(){
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domBuilder;
		Document nfceXML = null;
		try {
			domBuilder = domFactory.newDocumentBuilder();
			nfceXML = domBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = nfceXML.createElement(NfceInfo.RootTag);
		root.setAttributeNS("http://////www.portalfiscal.inf.br//nfe","xmlns","5A88");
		nfceXML.appendChild(root);
		
		Element el = nfceXML.createElement(NfceInfo.InfNFeTag);
		
		Attr at = nfceXML.createAttribute("id");
		at.setValue("NFE8081828384858687888980");
		el.setAttributeNode(at);
		at = nfceXML.createAttribute("version");
		at.setValue("1.1.0");
		el.setAttributeNode(at);
		root.appendChild(el);
		
		Element parent = el;
		Element ide = nfceXML.createElement(NfceInfo.IdentificationTag);
		parent.appendChild(ide);
		parent = ide;
		el = nfceXML.createElement(NfceInfo.CodUnidadeFederativaEmitenteTag);
		el.appendChild(nfceXML.createTextNode("13"));
		parent.appendChild(el);

		el = nfceXML.createElement(NfceInfo.CodNumChaveAcessoTag);
		el.appendChild(nfceXML.createTextNode(this.codChaveAcesso));
		parent.appendChild(el);
		
		
		
	}
	
	
}
