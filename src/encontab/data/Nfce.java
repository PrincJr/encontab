package encontab.data;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.String;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import encontab.consts.NfceEmitente;
import encontab.consts.NfceInfo;
import encontab.log.Logger;
import encontab.data.Emitente;
import encontab.locale.Messages;


public class Nfce {
	public String id;
	public String versao;
	//public long codUfEmitente;
	public String codChaveAcesso;
	public String naturezaOperacao;
	public long indicadorFormaPagamento;
	public String modeloDocumento;
	public String serieDocumento;
	public String numeroDocumento;
	float pIcms = 0;
	float ipi = 0;
	float vIcms = 0;
	float valor_ipi = 0;
	String dados_adic;
	String inscr_estadual;
	String cnpj;
	public Date dataHoraEmissao;
	public Date dataHoraSaida;;
	String uf;
	public String xmlFileName;
	public short tipoOperacao = 0;		// 0-Entrada 1-Saída
	public short idLocalDestinoOperacao = 0;	// operação: 0-Local 1-Interstadual 2-no exterior
	public short codMunicipioFG;
	public short tipoImpressaoDANFE = 0;
	public short tipoEmissao = 1;
	public short digitoVerificadorChaveAcesso = 0;
	short tipoAmbiente = 2; 	//Ambiente de homologação
	short finEmissao = 1;       //NFC-e Normal
	short indOpConsumidorFinal = 0; 	//0-Normal 1-Consumidor Final
	short indConsumidorPresente = 0;		// 0-Não se aplica a Nota Fiscal Complementar
	short procEmissao = 0;		//Emissao com aplicativo do Contribuinte
	String verProcEmissao = encontab.Application.version;
	Date dataHoraContingencia;
	String justificativaContingencia;
	
	public Emitente emitente;
	Document nfceXML = null;	//Documento XML que representa a NFC-e
	Logger logger = Logger.get();
	
	public Nfce(String lid, String lversion){
		this.id = lid; this.versao = lversion;
	}
	
	public void generateXML() throws TransformerException{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domBuilder;
		try {
			domBuilder = domFactory.newDocumentBuilder();
			nfceXML = domBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
														//Cria elemento raiz do arquivo xml <NFe>
		Element root = nfceXML.createElement(NfceInfo.RootTag);
		root.setAttributeNS("http://////www.portalfiscal.inf.br//nfe","xmlns","5A88");
		nfceXML.appendChild(root);
														//Cria elemento de informação da NFC-e como 
														// descendente do elemento raiz
		Element el = nfceXML.createElement(NfceInfo.InfNFeTag);
		
		Attr at = nfceXML.createAttribute(NfceInfo.ID_ATTRIBUTE);
		at.setValue(this.id);
		el.setAttributeNode(at);
		at = nfceXML.createAttribute("version");
		at.setValue(this.versao);
		el.setAttributeNode(at);
		Element parent = el;
		
		root.appendChild(parent);						// Cria elemento de identificação <ide>
		
		Element ide = nfceXML.createElement(NfceInfo.IdentificationTag);
		parent.appendChild(ide);
		parent = ide;
		el = nfceXML.createElement(NfceInfo.CodUnidadeFederativaEmitenteTag);
		el.appendChild(nfceXML.createTextNode(Long.toString(emitente.endereco.getUF().cod_IBGE)));
		parent.appendChild(el);

		el = nfceXML.createElement(NfceInfo.CodNumChaveAcessoTag);
		el.appendChild(nfceXML.createTextNode(this.codChaveAcesso));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.NaturezaOperacaoTag);
		el.appendChild(nfceXML.createTextNode(this.naturezaOperacao));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.IndicadorFormaPagamentoTag);
		el.appendChild(nfceXML.createTextNode(Long.toString(this.indicadorFormaPagamento)));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.ModeloDocumentoTag);
		el.appendChild(nfceXML.createTextNode(this.codChaveAcesso));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.SerieDocumentoTag);
		el.appendChild(nfceXML.createTextNode(this.serieDocumento));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.NumDocumentoFiscalTag);
		el.appendChild(nfceXML.createTextNode(this.numeroDocumento));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.DataHoraEmissaoTag);
		el.appendChild(nfceXML.createTextNode(FormatDate(this.dataHoraEmissao)));
		parent.appendChild(el);
														//Cria elemento <tpNF> tipo operacao
		el = nfceXML.createElement(NfceInfo.TipoOperacaoTag);
		el.appendChild(nfceXML.createTextNode(
				Short.toString(this.tipoOperacao)));	//0 - Entrada 1- Saída
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceInfo.CodNunicipioFatoGeradorTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.codMunicipioFG)));
		parent.appendChild(el);
														//Tipo de  Impressão do DANFE
		el = nfceXML.createElement(NfceInfo.TipoImpressaoDanfeTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.tipoImpressaoDANFE)));
		parent.appendChild(el);
														//tipo emissão da NFC-e
		el = nfceXML.createElement(NfceInfo.TipoEmissaoTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.tipoEmissao)));
		parent.appendChild(el);
														//Dígito Verificador da Chave de Acesso da NFC-e
		el = nfceXML.createElement(NfceInfo.DigitoVerificadorChaveAcessoTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.digitoVerificadorChaveAcesso)));
		parent.appendChild(el);

		el = nfceXML.createElement(NfceInfo.TipoAmbienteTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.tipoAmbiente)));
		parent.appendChild(el);
														//Finalidade de emissão da NF-e
		el = nfceXML.createElement(NfceInfo.FinalidadeNotaFiscalTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.finEmissao)));
		parent.appendChild(el);
														//Indica operação com Consumidor final
		el = nfceXML.createElement(NfceInfo.IndOperacaoConsumidorFinalTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.indOpConsumidorFinal)));
		parent.appendChild(el);
														/*Indicador de presença do comprador no
														  estabelecimento comercial no momento da
														  operação*/
		el = nfceXML.createElement(NfceInfo.IND_CCONSUMIDOR_PRESENTE_TAG);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.indConsumidorPresente)));
		parent.appendChild(el);
														//Processo de emissão da NF-e
		el = nfceXML.createElement(NfceInfo.ProcessoEmissaoTag);
		el.appendChild(nfceXML.createTextNode(Short.toString(this.procEmissao)));
		parent.appendChild(el);
														//Versão do processo de emissao
		el = nfceXML.createElement(NfceInfo.VersaoProcessoEmissaoTag);
		el.appendChild(nfceXML.createTextNode(this.verProcEmissao));
		parent.appendChild(el);
		
		Element elementoEmitente = this.CreateTagEmitente(this.emitente);
		parent.appendChild(elementoEmitente);
	}
	
	public byte[] getXMLBytes(Document docXML) throws TransformerException {
		TransformerFactory fac = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = fac.newTransformer();
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		DOMSource source = new DOMSource(docXML);
		ByteArrayOutputStream xmlEncoded = new ByteArrayOutputStream();
		StreamResult streamSink = new StreamResult(xmlEncoded); 
		try {
			transformer.transform(source, streamSink);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return xmlEncoded.toByteArray();
	}
	
	public void saveXML(Document docXML) throws TransformerException {
		TransformerFactory fac = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = fac.newTransformer();
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		DOMSource source = new DOMSource(docXML);
		
		File xmlFile = new File(this.xmlFileName);
		StreamResult streamSink = new StreamResult(xmlFile); 
		try {
			transformer.transform(source, streamSink);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
	}
	
	protected Element CreateTagEmitente(Emitente emitente){
		if (nfceXML==null) {
			logger.error(Messages.INVALID_XML_DOC);
			return null;
		}
															//Cria elemento de dentificação do emitente da NF-e
		Element parent = nfceXML.createElement(NfceEmitente.ID_EMITENTE_TAG);
															
		Element el = nfceXML.createElement(NfceEmitente.CNPJ_TAG);
		el.appendChild(nfceXML.createTextNode(emitente.cnpj));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceEmitente.RAZAO_SOCIAL_EMITENTE_TAG);
		el.appendChild(nfceXML.createTextNode(emitente.razaoSocial));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceEmitente.RAZAO_SOCIAL_EMITENTE_TAG);
		el.appendChild(nfceXML.createTextNode(emitente.razaoSocial));
		parent.appendChild(el);
		
		Element elementoEnder = this.CreateTagEnderecoEmitente(emitente.endereco);
		parent.appendChild(elementoEnder);
		
		return parent;
		
	}
	
	protected Element CreateTagEnderecoEmitente(Endereco endereco){
		Element parent = nfceXML.createElement(NfceEmitente.ENDERECO_EMITENTE_TAG);
		Element el;
		
		el = nfceXML.createElement(NfceEmitente.LOGRADOURO_TAG);
		el.appendChild(nfceXML.createTextNode(endereco.logradouro));
		parent.appendChild(el);

		el = nfceXML.createElement(NfceEmitente.NUMERO_TAG);
		el.appendChild(nfceXML.createTextNode(endereco.numero));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceEmitente.COMPLEMENTO_TAG);
		el.appendChild(nfceXML.createTextNode(endereco.complemento));
		parent.appendChild(el);
		
		el = nfceXML.createElement(NfceEmitente.BAIRRO_TAG);
		el.appendChild(nfceXML.createTextNode(endereco.bairro));
		parent.appendChild(el);

		el = nfceXML.createElement(NfceEmitente.COD_MUNICIPIO_TAG);
		el.appendChild(nfceXML.createTextNode(endereco.getMunicipio().codigo));
		parent.appendChild(el);

		el = nfceXML.createElement(NfceEmitente.BAIRRO_TAG);
		el.appendChild(nfceXML.createTextNode(endereco.bairro));
		parent.appendChild(el);

		return parent;
}
	
	public String FormatDate(Date ldata){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.format(ldata);
	}
	
}
