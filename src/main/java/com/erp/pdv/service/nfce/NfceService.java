package com.erp.pdv.service.nfce;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.dto.nfce.ItemNfceDTO;
import com.erp.pdv.dto.nfce.NfceRequestDTO;
import com.erp.pdv.dto.nfce.response.NfceMinResponse;
import com.erp.pdv.dto.nfce.response.NfceResponseDTO;
import com.erp.pdv.dto.produto.ProductMinDTO;
import com.erp.pdv.model.nfce.Nfce;
import com.erp.pdv.projections.NfceMinProjection;
import com.erp.pdv.repository.nfce.NfceRepository;
import com.erp.pdv.service.produto.HistoricoProdutoService;
import com.erp.pdv.service.produto.ProductService;
import com.erp.pdv.utils.ApiConstants;
import com.erp.pdv.utils.Mapper;
import com.erp.pdv.utils.TipoMovimento;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NfceService {

  @Autowired
  private NfceRepository repository;

  @Autowired
  private ProductService productService;

  @Autowired
  private Mapper mapper;

  @Autowired
  private HistoricoProdutoService historicoProdutoService;

  @Value("${api.url}")
  private String authApiUrl;

  private final WebClient webClient;

  // id valido nfc_3a1d94d8ba4546bf9c9e61ae4c26a23f

  public NfceService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(ApiConstants.API_URL).build(); // Base URL configurada
  }

  public NfceResponseDTO processarNfce2(NfceRequestDTO dto) {
    log.info("Iniciando processamento da NFC-e número {}", dto.getNumeroSequencial());

    // Enviar a NFC-e para a API da Nuvem Fiscal
    /* */
    NfceResponseDTO response = enviarNfceAPI(dto);

    // Se retorno foi bem-sucedido, loga e retorna
    if (response != null) {
      log.info("NFC-e sendo salva, montando impressao ====>: {}", response.getId());
      insertNfce(dto, response);

      return response;

    } else {
      log.info("Falha no envio da NFC-e.");
      throw new RuntimeException("Falha ao enviar NFC-e.");
    }
  }

  public NfceResponseDTO processarNfce(NfceRequestDTO dto) {
    log.info("Iniciando processamento da NFC-e número {}", dto.getNumeroSequencial());

    // Enviar a NFC-e para a API da Nuvem Fiscal
    /* */
    // NfceResponseDTO response = enviarNfceAPI(dto);

    // Se retorno foi bem-sucedido, loga e retorna
    log.info("NFC-e sendo salva, montando impressao ====>: {}", dto.getId());
    insertNfce(dto, null);

    return null;

  }

  @Transactional
  public void insertNfce(NfceRequestDTO dto, NfceResponseDTO response) {

    Nfce nfce = mapper.toEntity(dto);

    historicoProdutoService.registrarMovimentoEstoque(nfce, TipoMovimento.VENDA);

    nfce.setIdNfce(response.getId());
    nfce.setCNF(response.getAutorizacao().getId());
    nfce.setDhEmi(LocalDateTime.now());
    nfce.setAmbiente(response.getAmbiente());
    nfce.setChave(response.getAutorizacao().getChaveAcesso());
    nfce.setProtocolo(response.getAutorizacao().getNumeroProtocolo());
    nfce.setStatus(response.getStatus());
    nfce.setMotivo_status(response.getAutorizacao().getMotivo_status());
    nfce.setMensagem(response.getMensagemSefaz());
    nfce.setCodigoMensagem(response.getAutorizacao().getCodigoMensagem());
    nfce = repository.save(nfce);

    log.info("NFC-e salva no banco de dados: {}", dto.getNumeroSequencial());
    /*
     * else {
     * log.warn("NFC-e não autorizada. Status: {}", response.getStatus());
     * throw new IllegalStateException(
     * "NFC-e não autorizada pela SEFAZ. Status: " + response.getStatus());
     * }
     */
  }

  @Transactional
  public NfceResponseDTO insertOrcamento(NfceRequestDTO dto) {

    Nfce nfce = mapper.toEntity(dto);

    historicoProdutoService.registrarMovimentoEstoque(nfce, TipoMovimento.VENDA);

    nfce.setStatus("orcamento");
    nfce.setDhEmi(LocalDateTime.now());

    nfce = repository.save(nfce);

    log.info("ORCAMENTO salvo no banco de dados: {}", dto.getId());

    return mapper.toDtoResponseMin(nfce);

  }

  @Transactional(readOnly = true)
  public Page<NfceMinResponse> findAllNfce(Long emitenteId, String minDate, String maxDate,
      Long destinatarioId, Pageable pageable) {

    String minDateParam = (minDate == null || minDate.isEmpty()) ? null
        : LocalDate.parse(minDate).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String maxDateParam = (maxDate == null || maxDate.isEmpty()) ? null
        : LocalDate.parse(maxDate).atTime(LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // 1. Busca só os IDs da página atual (paginação correta, sem JOIN de itens)
    Page<Long> idPage = repository.findNfceIds(minDateParam, maxDateParam,
        destinatarioId, emitenteId, pageable);

    if (idPage.isEmpty()) {
      return Page.empty(pageable);
    }

    // 2. Busca os dados completos só dos IDs dessa página
    List<NfceMinProjection> rows = repository.findNfceByIds(idPage.getContent());

    // 3. Agrupa por NFC-e
    Map<Long, NfceMinResponse> map = new LinkedHashMap<>();
    for (NfceMinProjection proj : rows) {
      Long id = proj.getId();
      NfceMinResponse dto = map.computeIfAbsent(id, k -> new NfceMinResponse(proj));

      if (proj.getItemCodigoPrincipal() != null) {
        ItemNfceDTO item = new ItemNfceDTO();
        item.setProductId(proj.getProductId());
        item.setCodigo_principal(proj.getItemCodigoPrincipal());
        item.setDescricao(proj.getItemDescricao());
        item.setQuantidade(proj.getItemQCom());
        item.setPrecoUnitario(proj.getItemPrecoVenda());
        dto.getItens().add(item);
      }
    }

    // 4. Mantém a ordem dos IDs da página
    List<NfceMinResponse> lista = idPage.getContent().stream()
        .map(id -> map.get(id))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    return new PageImpl<>(lista, pageable, idPage.getTotalElements());
  }

  public NfceResponseDTO enviarNfceAPI(NfceRequestDTO dto) {
    String acessTokenAPI = dto.getToken_fiscal();
    Map<String, Object> body = buildNfceBody(dto);
    log.info("Objeto recebido metodo ENVIAR: {}", body);

    try {
      NfceResponseDTO response = webClient.post()
          .uri(ApiConstants.API_URL_TEST + "/nfce")
          .header(HttpHeaders.CONTENT_TYPE, "application/json")
          .header(HttpHeaders.ACCEPT, "application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + acessTokenAPI)
          .bodyValue(body)
          .retrieve()
          .bodyToMono(NfceResponseDTO.class)// mape
          .block();

      if (response != null) {
        log.info("NFC-e enviada com sucesso ====>: {}", response);
        for (ItemNfceDTO item : dto.getItens()) {
          response.getItens().add(item);
        }

      } else {
        log.warn("Resposta vazia ao enviar NFC-e");
      }
      return response;
    } catch (WebClientResponseException ex) {
      log.error("Erro HTTP ao enviar NFC-e. status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
      throw ex;
    } catch (Exception ex) {
      log.error("Erro ao enviar NFC-e", ex);

      throw ex;
    }
  }

  public byte[] buscaEscPosNfce(NfceRequestDTO request, NfceResponseDTO response) {

    String acessTokenAPI = request.getToken_fiscal();

    try {
      byte[] escpos = webClient.get()
          .uri(ApiConstants.API_URL_TEST + "/nfce/" + response.getId() + "/escpos")
          .header(HttpHeaders.ACCEPT, "application/octet-stream")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + acessTokenAPI)
          .retrieve()
          .bodyToMono(byte[].class)
          .block();

      log.info("ESC/POS obtido com sucesso. Tamanho: {} bytes", escpos.length);

      return escpos;

    } catch (Exception ex) {
      log.error("Erro ao buscar ESC/POS", ex);
      throw ex;
    }
  }

  public byte[] buscaPdfNfceApi(NfceRequestDTO request, NfceResponseDTO dto) {

    String acessTokenAPI = request.getToken_fiscal();
    try {
      byte[] pdfBytes = webClient.get()
          .uri(ApiConstants.API_URL_TEST + "/nfce/" + dto.getId() + "/pdf")
          .header(HttpHeaders.ACCEPT, "application/pdf")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + acessTokenAPI)
          .retrieve()
          .bodyToMono(byte[].class)
          .block();

      if (pdfBytes != null) {
        log.info("PDF da NFC-e obtido com sucesso. Tamanho: {} bytes", pdfBytes.length);
      } else {
        log.warn("Resposta vazia ao buscar PDF da NFC-e");
      }
      return pdfBytes;
    } catch (WebClientResponseException ex) {
      log.error("Erro HTTP ao buscar PDF da NFC-e. status={}, body={}", ex.getStatusCode(),
          ex.getResponseBodyAsString(), ex);
      throw ex;
    } catch (Exception ex) {
      log.error("Erro ao buscar PDF da NFC-e", ex);
      throw ex;
    }

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
    detPag.put("tPag", "01"); // Assumindo código como 01 dinheiro
    detPag.put("vPag", pagamento.getV_pag());
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
