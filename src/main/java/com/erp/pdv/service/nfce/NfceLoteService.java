package com.erp.pdv.service.nfce;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.dto.nfce.ItemNfceDTO;
import com.erp.pdv.dto.nfce.NfceRequestDTO;
import com.erp.pdv.dto.nfce.response.NfceResponseDTO;
import com.erp.pdv.dto.nfce.response.nfceLote.DocumentoDTO;
import com.erp.pdv.dto.nfce.response.nfceLote.NfceLoteResponseDTO;
import com.erp.pdv.dto.produto.ProductMinDTO;
import com.erp.pdv.model.nfce.Nfce;
import com.erp.pdv.repository.nfce.NfceRepository;
import com.erp.pdv.service.produto.HistoricoProdutoService;
import com.erp.pdv.service.produto.ProductService;
import com.erp.pdv.utils.ApiConstants;
import com.erp.pdv.utils.Mapper;
import com.erp.pdv.utils.TipoMovimento;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NfceLoteService {

  @Value("${api.url}")
  private String authApiUrl;

  private final WebClient webClient;

  @Autowired
  private NfceRepository repository;

  @Autowired
  private ProductService productService;

  @Autowired
  private HistoricoProdutoService historicoProdutoService;

  @Autowired
  private Mapper mapper;

  public NfceLoteService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(ApiConstants.API_URL).build(); // Base URL configurada
  }

  @Transactional(readOnly = true)
  public NfceResponseDTO findByIdNfce(Long id) {
    Nfce nfce = repository.findWithItensById(id)
        .orElseThrow(() -> new RuntimeException("NFC-e não encontrada: " + id));
    return mapper.toDtoResponseMin(nfce);
  }

  @Transactional
  public void enviarLoteNfce(List<Long> ids, String token) {

    List<Nfce> nfces = repository.findAllById(ids);

    for (Nfce nfce : nfces) {

      NfceRequestDTO dto = mapper.toDtoResponse(nfce);
      Map<String, Object> nfceBody = buildNfceBody(dto);

      String referencia = "NFCE_" + nfce.getId();
      nfceBody.put("referencia", referencia);

      Map<String, Object> body = new HashMap<>();
      body.put("documentos", List.of(nfceBody));
      body.put("ambiente", "homologacao");
      body.put("id_lote", String.valueOf(nfce.getId()));

      // Envia UMA NFC-e
      NfceLoteResponseDTO response = enviarNfceLoteAPI(body, token);
      // log.info("===================Chegou até Lote a API Fiscal...");
      DocumentoDTO doc = response.getDocumentos().get(0);

      nfce.setCNF(response.getIdLote());
      nfce.setIdNfce(doc.getId());
      nfce.setDhEmi(LocalDateTime.now());
      nfce.setAmbiente(doc.getAmbiente());
      nfce.setChave(doc.getAutorizacao().getChaveAcesso());
      nfce.setProtocolo(doc.getAutorizacao().getNumeroProtocolo());
      nfce.setStatus(doc.getStatus());
      nfce.setMotivo_status(doc.getAutorizacao().getMotivoStatus());
      nfce.setMensagem(doc.getAutorizacao().getMensagem());
      nfce.setCodigoMensagem(doc.getAutorizacao().getCodigoMensagem());

      log.info("nota={}", nfce);

      repository.save(nfce);
    }
  }

  public NfceLoteResponseDTO enviarNfceLoteAPI(Map<String, Object> lote, String token) {

    try {
      return webClient.post()
          .uri(ApiConstants.API_URL_TEST + "/nfce/lotes")
          .header(HttpHeaders.CONTENT_TYPE, "application/json")
          .header(HttpHeaders.ACCEPT, "application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
          .bodyValue(lote)
          .retrieve()
          .bodyToMono(NfceLoteResponseDTO.class)

          .block();

    } catch (WebClientResponseException ex) {
      log.error("Erro HTTP ao enviar lote NFC-e. status={}, body={}",
          ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
      throw ex;

    } catch (Exception ex) {
      log.error("Erro ao enviar lote NFC-e", ex);
      throw ex;
    }
  }

  @Transactional
  public void cancelarNfce(List<Long> ids, String token) {

    for (Long id : ids) {

      Nfce nfce = repository.findById(id)
          .orElseThrow(() -> new RuntimeException("NFC-e não encontrada: " + id));

      String idNuvemFiscal = nfce.getIdNfce();

      Map<String, Object> body = Map.of(
          "justificativa",
          "Cancelamento solicitado pelo consumidor devido a erro de operação");

      // log.info("===================Chegou até o canelamento API da Fiscal...");

      try {
        NfceResponseDTO response = webClient.post()
            .uri(ApiConstants.API_URL_TEST + "/nfce/" + idNuvemFiscal + "/cancelamento")
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .header(HttpHeaders.ACCEPT, "application/json")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(NfceResponseDTO.class)
            .block();

        if (response == null) {
          throw new IllegalStateException("Resposta vazia da Nuvem Fiscal");
        }

        nfce.setCNF(response.getId());
        nfce.setStatus("cancelado");
        nfce.setChave(response.getChave());
        nfce.setProtocolo(response.getProtocolo());
        nfce.setCodigoMensagem(response.getCodigo());
        nfce.setMensagem(response.getMensagemSefaz());
        nfce.setMotivo_status(response.getMotivoStatus());

        if ("REGISTRADO".equalsIgnoreCase(response.getStatus())) {
          historicoProdutoService.registrarMovimentoEstoque(nfce,
              TipoMovimento.ENTRADA);
        }

        repository.save(nfce);

      } catch (WebClientResponseException ex) {
        log.error("Erro ao cancelar NFC-e {}: {}", id, ex.getResponseBodyAsString());
        throw ex;
      }

    }
  }

  public byte[] buscaEscPosNfceImprimir(Long id, String token) {

    Nfce nfce = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("NFC-e não encontrada: " + id));

    try {
      byte[] escpos = webClient.get()
          // .uri(ApiConstants.API_URL_TEST + "/nfce/" + nfce.getIdNfce() + "/escpos")
          // //nfc_3a1d94d8ba4546bf9c9e61ae4c26a23f
          .uri(
              ApiConstants.API_URL_TEST
                  + "/nfce/nfc_3a1d94d8ba4546bf9c9e61ae4c26a23f/escpos"
                  + "?modelo=2" // 2 = Bematech
                  + "&colunas=48" // padrão MP-4200
                  + "&qrcode_lateral=true" // <<< ESSENCIAL
          )
          .header(HttpHeaders.ACCEPT, "application/octet-stream")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
          .retrieve()
          .bodyToMono(byte[].class)
          .block();

      if (escpos != null && escpos.length > 0) {
        log.info(
            "ESC/POS obtido com sucesso para NFC-e {}. Tamanho: {} bytes",
            nfce.getIdNfce(),
            escpos.length);
        return escpos;
      }

    } catch (Exception ex) {
      log.warn(
          "Falha ao buscar ESC/POS para NFC-e {}: {}",
          nfce.getIdNfce(),
          ex.getMessage(),
          ex);
    }

    throw new IllegalStateException(
        "Não foi possível obter ESC/POS para nenhuma das NFC-e solicitadas");
  }

  private Map<String, Object> buildNfceBody(NfceRequestDTO dto) {
    Map<String, Object> body = new HashMap<>();

    // Raiz: ambiente e referencia (obrigatórios/úteis)
    body.put("ambiente", "homologacao"); // Hardcode: mude para "producao" quando pronto
    // body.put("referencia", dto.getNumeroSequencial()); // Use ID como referencia
    // para evitar duplicatas

    // infNFe (obrigatório)
    Map<String, Object> infNFe = new HashMap<>();

    infNFe.put("versao", "4.00"); // Hardcode: versão 4.00 obrigatória para NFC-e
    // ide (obrigatório)
    Map<String, Object> ide = new HashMap<>();
    ide.put("cUF", 33); // Hardcode: Código UF (ex: 33 RJ). Obrigatório, ajuste pela empresa
    ide.put("cNF", generateRandomCNF()); // Gerar random 8 dígitos. Obrigatório
    ide.put("natOp", "Venda de Mercadoria"); // Hardcode obrigatório
    ide.put("mod", 65); // Hardcode: 65 para NFC-e. Obrigatório
    ide.put("serie", 1); // Hardcode: série padrão. Obrigatório, ajuste se necessário
    ide.put("nNF", dto.getNumeroSequencial()); // Hardcode sequencial baseado em ID. Obrigatório criar
                                               // logica para
    // numeração sequencial
    ide.put("dhEmi", OffsetDateTime.now(ZoneOffset.of("-03:00"))
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    // Data atual ISO. Obrigatório
    ide.put("tpNF", 1); // Hardcode: saída. Obrigatório
    ide.put("idDest", 1); // Hardcode: operação interna. Obrigatório
    ide.put("cMunFG", 3304201);

    // --------> ide.put("cMunFG",
    // dto.getEmpresa().getEndereco().getCodigoMunicipio()); // Do empresa,
    // assumindo que tem. Obrigatório

    ide.put("tpImp", 4); // Hardcode: DANFE NFC-e. Obrigatório

    // ------------------> montar logica para tpEmis e xJust
    ide.put("tpEmis", 1); // Do DTO. Obrigatório
    if (dto.getTpEmis() == 9) { // Se contingência
      ide.put("xJust", dto.getXJust() != null ? dto.getXJust() : "Justificativa padrão"); // Do DTO ou hardcode
    }
    // ------> ide.put("cDV", 0); // Hardcode: calculado pela SEFAZ. Obrigatório não
    // necessario na api

    ide.put("tpAmb", 2); // Hardcode: 2 homologação (mude para 1 em prod). Obrigatório, mas mapeado via
                         // ambiente
    ide.put("finNFe", 1); // Hardcode: normal. Obrigatório
    ide.put("indFinal", 1); // Hardcode: consumidor final (obrigatório NFC-e)
    ide.put("indPres", 1); // Hardcode: presença (obrigatório NFC-e, 1 presencial)
    ide.put("procEmi", 0); // Hardcode: emissão própria. Obrigatório
    ide.put("verProc", "1.0.0"); // Hardcode: versão app. Obrigatório
    infNFe.put("ide", ide);

    // emit (obrigatório, mapeie da empresa)
    Map<String, Object> emit = new HashMap<>();
    emit.put("CNPJ", dto.getEmpresa().getCnpj()); // Assumindo que EmpresaDTO tem cnpj
    // emit.put("xNome", dto.getEmpresa().getX_nome());
    Map<String, Object> enderEmit = new HashMap<>();
    // Preencha enderEmit com dados da empresa (assumindo que EmpresaDTO tem
    // endereco com logradouro, etc.)
    enderEmit.put("xLgr", dto.getEmpresa().getLogradouro());
    enderEmit.put("nro", dto.getEmpresa().getNumero());
    enderEmit.put("xBairro", dto.getEmpresa().getBairro());
    /*
     * enderEmit.put("cMun", dto.getEmpresa().getcMun());
     * enderEmit.put("xMun", dto.getEmpresa().getxMun());
     * 
     */

    enderEmit.put("cMun", "3304201");
    enderEmit.put("xMun", "Resende");
    enderEmit.put("UF", dto.getEmpresa().getUf());
    enderEmit.put("CEP", dto.getEmpresa().getCep());
    emit.put("enderEmit", enderEmit);
    emit.put("IE", "87455297");
    emit.put("CRT", 1); // Assumindo que tem
    infNFe.put("emit", emit);

    // ============= DESTINATARIO (opcional para NFC-e) ====================

    Map<String, Object> dest = new HashMap<>();
    if (dto.getDestinatario().getCpf() != null) {

      dest.put("CPF", dto.getDestinatario().getCpf());
      dest.put("indIEDest", 9);
      dest.put("xNome", dto.getDestinatario().getX_nome());
    } else {
      dest.put("CPF", "49234303032"); // Hardcode: consumidor final sem CPF
      dest.put("xNome", "Consumidor Final");
      dest.put("indIEDest", 9);
    }
    // Adicione mais se necessário
    infNFe.put("dest", dest);

    // ===================== Det =====================
    List<Map<String, Object>> det = new ArrayList<>();
    double somaVProd = 0.0;
    double somaVDesc = 0.0;

    // Calcula o total bruto uma única vez (performance + precisão)
    double valorBrutoTotal = dto.getItens().stream()
        .mapToDouble(item -> {
          ProductMinDTO p = productService.findById(item.getProductId());
          return item.getQuantidade() * p.getV_un_com();
        })
        .sum();

    double descontoTotal = dto.getDesconto() != null ? dto.getDesconto() : 0.0;

    for (ItemNfceDTO item : dto.getItens()) {
      ProductMinDTO productDto = productService.findById(item.getProductId());

      Map<String, Object> itemMap = new HashMap<>();
      Map<String, Object> prod = new HashMap<>();

      double valorBrutoItem = item.getQuantidade() * productDto.getV_un_com();
      valorBrutoItem = Math.round(valorBrutoItem * 100.0) / 100.0;
      somaVProd += valorBrutoItem;

      // ===================== DESCONTO RATEADO =====================
      double vDescItem = 0.0;
      if (descontoTotal > 0 && valorBrutoTotal > 0) {
        if (dto.getItens().size() == 1) {
          vDescItem = descontoTotal; // caso mais comum no PDV
        } else {
          double proporcao = valorBrutoItem / valorBrutoTotal;
          vDescItem = Math.round((descontoTotal * proporcao) * 100.0) / 100.0;

          // Correção no último item para fechar 100% exato
          if (dto.getItens().indexOf(item) == dto.getItens().size() - 1) {
            vDescItem = descontoTotal - somaVDesc;
          }
        }
      }
      vDescItem = Math.round(vDescItem * 100.0) / 100.0;

      // ===================== PRODUTO =====================
      itemMap.put("nItem", dto.getItens().indexOf(item) + 1);

      prod.put("cProd", productDto.getC_prod());
      prod.put("cEAN", productDto.getC_EAN() != null ? productDto.getC_EAN() : "SEM GTIN");
      prod.put("xProd", productDto.getX_prod());
      prod.put("NCM", productDto.getNCM());
      prod.put("CFOP", productDto.getCFOP());
      prod.put("uCom", productDto.getU_com());
      prod.put("qCom", item.getQuantidade());
      prod.put("vUnCom", productDto.getV_un_com());
      if (vDescItem > 0) {
        prod.put("vDesc", vDescItem);
      }
      somaVDesc += vDescItem;

      double valorTotalBruto = item.getQuantidade() * productDto.getV_un_com();
      valorTotalBruto = Math.round(valorTotalBruto * 100.0) / 100.0;
      prod.put("vProd", valorTotalBruto);

      // CAMPOS FISCAIS OBRIGATÓRIOS DO PRODUTO
      prod.put("cEANTrib", productDto.getC_EAN() != null ? productDto.getC_EAN() : "SEM GTIN");
      prod.put("uTrib", productDto.getU_com());
      prod.put("qTrib", item.getQuantidade());
      prod.put("vUnTrib", productDto.getV_un_com());
      prod.put("indTot", 1);

      itemMap.put("prod", prod);

      // ===================== IMPOSTOS =====================
      Map<String, Object> imposto = new HashMap<>();
      Map<String, Object> icms = new HashMap<>();
      Map<String, Object> pis = new HashMap<>();
      Map<String, Object> cofins = new HashMap<>();

      String cst = productDto.getCst() != null ? productDto.getCst() : "102";
      Integer origem = productDto.getOrigem() != null ? productDto.getOrigem() : 0;

      // ===================== ICMS =====================
      if ("102".equals(cst)) {
        // Simples Nacional - CSOSN 102 (sem crédito de ICMS)
        Map<String, Object> icmssn102 = new HashMap<>();
        icmssn102.put("orig", origem);
        icmssn102.put("CSOSN", "102");
        icms.put("ICMSSN102", icmssn102);

      } else if ("500".equals(cst)) {
        // Simples Nacional - CSOSN 500 (Substituição Tributária)
        Map<String, Object> icmssn500 = new HashMap<>();
        icmssn500.put("orig", origem);
        icmssn500.put("CSOSN", "500");
        icmssn500.put("vBCSTRet", 0.00);
        icmssn500.put("pST", 0.00);
        icmssn500.put("vICMSSubstituto", 0.00);
        icmssn500.put("vICMSSTRet", 0.00);
        icmssn500.put("vBCFCPSTRet", 0.00);
        icmssn500.put("pFCPSTRet", 0.00);
        icmssn500.put("vFCPSTRet", 0.00);
        icmssn500.put("pRedBCEfet", 0.00);
        icmssn500.put("vBCEfet", 0.00);
        icmssn500.put("pICMSEfet", 0.00);
        icmssn500.put("vICMSEfet", 0.00);

        icms.put("ICMSSN500", icmssn500);

      } else if ("60".equals(cst)) {
        // Regime Normal - ICMS ST
        Map<String, Object> icms60 = new HashMap<>();
        icms60.put("orig", origem);
        icms60.put("CST", "60");
        icms60.put("vBCSTRet", 0.00);
        icms60.put("vICMSSTRet", 0.00);
        icms.put("ICMS60", icms60);

      } else {
        // fallback seguro
        Map<String, Object> icmssn102 = new HashMap<>();
        icmssn102.put("orig", origem);
        icmssn102.put("CSOSN", "102");
        icms.put("ICMSSN102", icmssn102);
      }

      // ===================== PIS =====================
      Map<String, Object> pisnt = new HashMap<>();
      pisnt.put("CST", "07"); // não tributado
      pis.put("PISNT", pisnt);

      // ===================== COFINS =====================
      Map<String, Object> cofinsnt = new HashMap<>();
      cofinsnt.put("CST", "07"); // não tributado
      cofins.put("COFINSNT", cofinsnt);

      imposto.put("ICMS", icms);
      imposto.put("PIS", pis);
      imposto.put("COFINS", cofins);

      itemMap.put("imposto", imposto);
      det.add(itemMap);
    }

    infNFe.put("det", det);

    // ===================== TOTAL =====================

    Map<String, Object> total = new HashMap<>();
    Map<String, Object> icmsTot = new HashMap<>();

    icmsTot.put("vBC", 0.00);
    icmsTot.put("vICMS", 0.00);
    icmsTot.put("vICMSDeson", 0.00);
    icmsTot.put("vBCST", 0.00);
    icmsTot.put("vST", 0.00);
    icmsTot.put("vFCP", 0.00);
    icmsTot.put("vFCPST", 0.00);
    icmsTot.put("vFCPSTRet", 0.00);
    icmsTot.put("vProd", Math.round(somaVProd * 100.0) / 100.0);
    icmsTot.put("vFrete", 0.00);
    icmsTot.put("vSeg", 0.00);
    if (somaVDesc > 0) {
      icmsTot.put("vDesc", Math.round(somaVDesc * 100.0) / 100.0);
    } else {
      icmsTot.put("vDesc", 0.00);
    }
    icmsTot.put("vII", 0.00);
    icmsTot.put("vIPI", 0.00);
    icmsTot.put("vIPIDevol", 0.00);
    icmsTot.put("vPIS", 0.00);
    icmsTot.put("vCOFINS", 0.00);
    icmsTot.put("vOutro", 0.00);
    if (somaVProd - somaVDesc < 0) {
      icmsTot.put("vNF", dto.getValorTotalNota());
    } else {
      icmsTot.put("vNF", Math.round((somaVProd - somaVDesc) * 100.0) / 100.0);
    }
    total.put("ICMSTot", icmsTot);
    infNFe.put("total", total);
    // ===================== TRANSPORTE =====================

    // transp (obrigatório)
    Map<String, Object> transp = new HashMap<>();
    transp.put("modFrete", 9); // Hardcode: sem frete
    infNFe.put("transp", transp);

    // ===================== PAGAMENTOS =====================
    Map<String, Object> pag = new HashMap<>();
    List<Map<String, Object>> detPagList = new ArrayList<>();
    Map<String, Object> detPag = new HashMap<>();

    PagamentoDTO pagamento = dto.getPagamentos();
    String tPag = pagamento.getId() != null
        ? String.format("%02d", pagamento.getId())
        : "01";

    detPag.put("tPag", tPag);
    detPag.put("vPag", dto.getValorTotalNota() - somaVDesc);
    detPagList.add(detPag);

    pag.put("detPag", detPagList);
    infNFe.put("pag", pag);

    // infAdic (opcional, mas hardcode se quiser)
    Map<String, Object> infAdic = new HashMap<>();
    infAdic.put("infCpl", "Informacoes complementares hardcoded");
    infNFe.put("infAdic", infAdic);

    body.put("infNFe", infNFe);

    // ===================== SUPLEMENTARES =====================

    return body;
  }

  private String generateRandomCNF() {
    Random random = new Random();
    return String.format("%08d", random.nextInt(100000000)); // 8 dígitos random
  }
}
