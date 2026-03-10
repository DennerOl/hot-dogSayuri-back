package com.erp.pdv.service.nfe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.erp.pdv.dto.nfe.ItemNfeDTO;
import com.erp.pdv.dto.nfe.NfeRequestDTO;
import com.erp.pdv.dto.nfe.response.NfeMinResponse;
import com.erp.pdv.dto.nfe.response.NfeResponseDTO;
import com.erp.pdv.model.nfe.Nfe;
import com.erp.pdv.projections.nfe.NfeMinProjection;
import com.erp.pdv.repository.nfe.NfeRepository;
import com.erp.pdv.service.produto.HistoricoProdutoService;
import com.erp.pdv.service.produto.ProductService;
import com.erp.pdv.utils.ApiConstants;
import com.erp.pdv.utils.Mapper;
import com.erp.pdv.utils.TipoMovimento;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NfeService {

  @Autowired
  private NfeRepository repository;

  @Autowired
  private ProductService productService;

  @Autowired
  private Mapper mapper;

  @Autowired
  private HistoricoProdutoService historicoProdutoService;

  private final WebClient webClient;

  // id valido nfc_3a1d94d8ba4546bf9c9e61ae4c26a23f

  public NfeService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(ApiConstants.API_URL).build(); // Base URL configurada
  }

  public NfeResponseDTO processarNfe(NfeRequestDTO dto) {
    log.info("Iniciando processamento da NFC-e número {}", dto.getNumeroSequencial());

    // Enviar a NFC-e para a API da Nuvem Fiscal
    // NfeResponseDTO response = enviarNfeAPI(dto);

    // Se retorno foi bem-sucedido, loga e retorna

    log.info("NFC-e sendo salva, montando impressao ====>: {}");
    insertNfe(dto, null);

    return null;

  }

  @Transactional
  public void insertNfe(NfeRequestDTO dto, NfeResponseDTO response) {

    Nfe nfe = mapper.toEntity(dto);

    historicoProdutoService.registrarMovimentoEstoque(nfe, TipoMovimento.VENDA);

    nfe.setIdNfe(response.getId());
    nfe.setCNF(response.getAutorizacao().getId());
    nfe.setDhEmi(LocalDateTime.now());
    nfe.setAmbiente(response.getAmbiente());
    nfe.setChave(response.getAutorizacao().getChaveAcesso());
    nfe.setProtocolo(response.getAutorizacao().getNumeroProtocolo());
    nfe.setStatus(response.getStatus());
    nfe.setMotivo_status(response.getAutorizacao().getMotivo_status());

    nfe = repository.save(nfe);

    log.info("NF-e salva no banco de dados: {}", dto.getNumeroSequencial());

  }

  @Transactional(readOnly = true)
  public Page<NfeMinResponse> findAllNfce(String cnpj, String minDate, String maxDate, String cpf,
      Pageable pageable) {

    String minDateParam = (minDate == null || minDate.isEmpty()) ? null
        : LocalDate.parse(minDate).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    String maxDateParam = (maxDate == null || maxDate.isEmpty()) ? null
        : LocalDate.parse(maxDate).atTime(LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    String cpfParam = (cpf == null || cpf.isEmpty()) ? null : cpf;
    String cnpjParam = (cnpj == null || cnpj.isEmpty()) ? null : cnpj;

    Page<NfeMinProjection> page = repository.findAllNfe(minDateParam, maxDateParam, cpfParam, cnpjParam, pageable);

    // AGRUPAMENTO POR NFC-e (itens)
    Map<Long, NfeMinResponse> map = new LinkedHashMap<>();

    for (NfeMinProjection proj : page.getContent()) {
      Long id = proj.getId();
      NfeMinResponse dto = map.computeIfAbsent(id, k -> new NfeMinResponse(proj));

      if (proj.getItemCodigoPrincipal() != null) {
        ItemNfeDTO item = new ItemNfeDTO();
        item.setProductId(proj.getProductId());
        item.setCodigo_principal(proj.getItemCodigoPrincipal());
        item.setDescricao(proj.getItemDescricao());
        item.setQuantidade(proj.getItemQCom());
        item.setPrecoUnitario(proj.getItemPrecoVenda());
        dto.getItens().add(item);
      }
    }

    List<NfeMinResponse> dtos = new ArrayList<>(map.values());
    return new PageImpl<>(dtos, pageable, page.getTotalElements());
  }

  @Transactional
  public NfeMinResponse findNfeById(Long id) {
    Nfe nfe = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("NFC-e não encontrada com ID: " + id));
    return mapper.toDto(nfe);
  }

  @Transactional
  public NfeMinResponse update(Long id, NfeRequestDTO dto) {
    Nfe nfe = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("NFC-e não encontrada com ID: " + id));
    mapper.copyDtoToEntity(dto, nfe);
    nfe = repository.save(nfe);
    return mapper.toDto(nfe);
  }

  @Transactional
  public void insertNfe(NfeRequestDTO dto) {

    Nfe nfe = mapper.toEntity(dto);

    nfe.setStatus("Não Enviada");
    nfe.setDhEmi(LocalDateTime.now());

    nfe = repository.save(nfe);

    log.info("Nf-e salva no banco de dados: {}", dto.getId());

  }

}
