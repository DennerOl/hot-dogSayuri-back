package com.erp.pdv.utils;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.dto.calculoTributos.TributoFiscalDTO;
import com.erp.pdv.dto.nfce.ItemNfceDTO;
import com.erp.pdv.dto.nfce.NfceRequestDTO;
import com.erp.pdv.dto.nfce.NfceSerieDTO;
import com.erp.pdv.dto.nfce.response.NfceResponseDTO;
import com.erp.pdv.dto.nfe.ItemNfeDTO;
import com.erp.pdv.dto.nfe.NfeRequestDTO;
import com.erp.pdv.dto.nfe.response.NfeMinResponse;
import com.erp.pdv.dto.produto.HistoricoProdutoDTO;
import com.erp.pdv.dto.produto.ProductMinDTO;
import com.erp.pdv.dto.usuario.UserDTO;
import com.erp.pdv.model.Destinatario;
import com.erp.pdv.model.Empresa;
import com.erp.pdv.model.Pagamento;
import com.erp.pdv.model.calculoTributos.Tributofiscal;
import com.erp.pdv.model.nfce.ItemNfce;
import com.erp.pdv.model.nfce.ItemNfcePk;
import com.erp.pdv.model.nfce.Nfce;
import com.erp.pdv.model.nfce.NfceSerieEmpresa1;
import com.erp.pdv.model.nfce.NfceSerieEmpresa2;
import com.erp.pdv.model.nfce.NfceSerieEmpresa3;
import com.erp.pdv.model.nfe.ItemNfe;
import com.erp.pdv.model.nfe.ItemNfePk;
import com.erp.pdv.model.nfe.Nfe;
import com.erp.pdv.model.produto.HistoricoProduto;
import com.erp.pdv.model.produto.Product;
import com.erp.pdv.model.usuario.User;
import com.erp.pdv.repository.EmpresaRepository;
import com.erp.pdv.repository.PagamentoRepository;
import com.erp.pdv.repository.produto.ProductRepository;
import com.erp.pdv.service.DestinatarioService;

@Service
public class Mapper {

  private static final Logger LOG = LoggerFactory.getLogger(
      Mapper.class);

  private final ModelMapper modelMapper;
  private final ProductRepository productRepository;
  private final EmpresaRepository empresaRepository;
  private final PagamentoRepository pagamentoRepository;
  private final DestinatarioService destinatarioService;

  public Mapper(ModelMapper modelMapper, ProductRepository productRepository, EmpresaRepository empresaRepository,
      DestinatarioService destinatarioService, PagamentoRepository pagamentoRepository) {
    this.modelMapper = modelMapper;
    this.productRepository = productRepository;
    this.empresaRepository = empresaRepository;
    this.destinatarioService = destinatarioService;
    this.pagamentoRepository = pagamentoRepository;

  }

  public Nfce toEntity(NfceRequestDTO dto) {
    if (dto == null)
      return null;

    LOG.info("Mapeando NfceRequestDTO para Nfce entity");

    Nfce nfce = new Nfce();
    nfce.setId(null);
    nfce.setTpEmis(dto.getTpEmis() != null ? dto.getTpEmis() : 1);
    nfce.setXJust(dto.getXJust());
    nfce.setVProd(dto.getValorTotalNota() != null ? dto.getValorTotalNota() : 0.0);
    nfce.setVNF(dto.getValorTotalNota() != null ? dto.getValorTotalNota() : 0.0);
    nfce.setToken_fiscal("Autorizado");
    nfce.setDesconto(dto.getDesconto());
    nfce.setNNF(dto.getNumeroSequencial());
    nfce.setSerie(dto.getSerie());

    // Buscar empresa existente
    Empresa empresa = empresaRepository.findById(dto.getEmpresa().getId())
        .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresa().getId()));
    nfce.setEmpresa(empresa);
    /*
     * // Mapear destinatario
     * if (dto.getDestinatario() != null) {
     * Destinatario destinatario = this.toEntity(dto.getDestinatario());
     * nfce.setDestinatario(destinatario);
     * }
     */
    Pagamento pagamento = pagamentoRepository.findById(dto.getPagamentos().getId())
        .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrada: " + dto.getEmpresa().getId()));
    nfce.setPagamento(pagamento);

    DestinatarioDTO destinatarioDTO = destinatarioService.findOrCreate(dto.getDestinatario());
    Destinatario destinatario = this.toEntity(destinatarioDTO);
    nfce.setDestinatario(destinatario);

    // Mapear itens
    for (

    ItemNfceDTO itemDto : dto.getItens()) {
      Product product = productRepository.findById(itemDto.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDto.getProductId()));

      ItemNfce item = new ItemNfce();
      item.setCodigoPrincipal(itemDto.getCodigo_principal());
      item.setDescricao(itemDto.getDescricao());
      item.setQCom(itemDto.getQuantidade());
      item.setPrecoVenda(itemDto.getPrecoUnitario());

      ItemNfcePk pk = new ItemNfcePk();
      pk.setNfce(nfce);
      pk.setProduct(product);
      item.setId(pk);

      nfce.getItens().add(item);
    }

    return nfce;
  }

  public NfceResponseDTO toDtoResponseMin(Nfce nfce) {
    if (nfce == null)
      return null;

    LOG.info("Mapeando Nfce entity para NfceResponseDTO");

    NfceResponseDTO dto = new NfceResponseDTO();

    // Identificação
    dto.setId(nfce.getId().toString());
    dto.setAmbiente(nfce.getAmbiente());
    dto.setModelo(nfce.getMod());
    dto.setSerie(nfce.getSerie());
    dto.setNumeroSequencial(nfce.getNNF());

    // Datas
    dto.setDataEmissao(nfce.getDhEmi());

    // Status
    dto.setStatus(nfce.getStatus());
    dto.setMotivoStatus(nfce.getMotivo_status());

    // Valores
    dto.setSubtotal(nfce.getVProd());
    dto.setValorTotalNota(nfce.getVNF());
    dto.setDesconto(nfce.getDesconto());
    dto.setQuantidadeTotalItens(nfce.getVProd());

    // Chave e protocolo
    dto.setChave(nfce.getChave());
    dto.setProtocolo(nfce.getProtocolo());
    dto.setQrCode(nfce.getQrCode());

    // URL da chave (consulta SEFAZ)
    if (nfce.getChave() != null) {
      dto.setUrlChave(
          "https://www.sefaz.rj.gov.br/consulta?nfe=" + nfce.getChave());
    }

    // Tipo de emissão
    dto.setTipo(nfce.getTpEmis());

    // Destinatário

    DestinatarioDTO destinatarioDTO = toDTO(nfce.getDestinatario());
    dto.setDestinatario(destinatarioDTO);

    // Pagamento

    PagamentoDTO pagamentoDTO = toDTO(nfce.getPagamento());
    dto.setPagamentos(pagamentoDTO);

    EmpresaDTO empresaDTO = toDTO(nfce.getEmpresa());
    dto.setEmpresa(empresaDTO);

    // Itens (agora sempre carregados pelo @EntityGraph)
    double totalFederal = 0.0;
    double totalEstadual = 0.0;
    double totalMunicipal = 0.0;

    for (ItemNfce item : nfce.getItens()) {
      // 1. Calcula o valor total deste item (Quantidade * Preço Unitário)
      double valorTotalItem = item.getQCom() * item.getPrecoVenda();

      // 2. Alíquotas do IBPT para este NCM (Exemplo: Pão Francês)
      // No futuro, busque esses valores do banco de dados ou da tabela IBPT vinculada
      // ao NCM
      double aliqFederal = 4.20; // 4,20%
      double aliqEstadual = 12.00; // 12,00%
      double aliqMunicipal = 0.00; // 0,00%

      // 3. Calcula o imposto em valor (R$) para cada esfera deste item
      double impFederalItem = valorTotalItem * (aliqFederal / 100);
      double impEstadualItem = valorTotalItem * (aliqEstadual / 100);
      double impMunicipalItem = valorTotalItem * (aliqMunicipal / 100);

      // 4. Acumula nos totais da nota
      totalFederal += impFederalItem;
      totalEstadual += impEstadualItem;
      totalMunicipal += impMunicipalItem;

      // 5. Adiciona o item ao DTO normalmente
      ItemNfceDTO itemDTO = toDTO(item);
      dto.getItens().add(itemDTO);
    }

    // 6. Ao final do loop, grava os totais consolidados no DTO da NFC-e
    dto.setTribFederal(totalFederal);
    dto.setTribEstadual(totalEstadual);
    dto.setTribMunicipal(totalMunicipal);
    return dto;
  }

  public NfceRequestDTO toDtoResponse(Nfce nfce) {
    if (nfce == null)
      return null;

    LOG.info("Mapeando Nfce entity para NfceRequestDTO");

    NfceRequestDTO dto = new NfceRequestDTO();
    dto.setId(nfce.getId());
    dto.setTpEmis(nfce.getTpEmis());
    dto.setXJust(nfce.getXJust());
    dto.setValorTotalNota(nfce.getVNF() != null ? nfce.getVNF() : 0.0);
    dto.setDesconto(nfce.getDesconto());
    dto.setQtdeTotalItens(nfce.getItens() != null ? nfce.getItens().size() : 0);
    dto.setNumeroSequencial(nfce.getNNF());
    dto.setSerie(nfce.getSerie());

    if (nfce.getEmpresa() != null) {
      dto.setEmpresa(new EmpresaDTO(nfce.getEmpresa()));
    }
    if (nfce.getDestinatario() != null) {
      dto.setDestinatario(new DestinatarioDTO(nfce.getDestinatario()));

    }
    if (nfce.getPagamento() != null) {
      dto.setPagamentos(new PagamentoDTO(nfce.getPagamento()));
    }

    // Mapear itens
    for (ItemNfce item : nfce.getItens()) {
      ItemNfceDTO itemDTO = new ItemNfceDTO();
      itemDTO.setProductId(item.getId().getProduct().getId());
      itemDTO.setCodigo_principal(item.getCodigoPrincipal());
      itemDTO.setDescricao(item.getDescricao());
      itemDTO.setQuantidade(item.getQCom());
      itemDTO.setPrecoUnitario(item.getPrecoVenda());
      dto.getItens().add(itemDTO);
    }

    return dto;
  }

  // =============================NF-e====================================

  public Nfe toEntity(NfeRequestDTO dto) {
    if (dto == null)
      return null;

    LOG.info("Mapeando NfeRequestDTO para Nfe entity");

    Nfe nfe = new Nfe();
    nfe.setId(null);
    nfe.setTpEmis(dto.getTpEmis() != null ? dto.getTpEmis() : 1);
    nfe.setXJust(dto.getXJust());
    nfe.setVProd(dto.getValorTotalNota() != null ? dto.getValorTotalNota() : 0.0);
    nfe.setVNF(dto.getValorTotalNota() != null ? dto.getValorTotalNota() : 0.0);
    nfe.setToken_fiscal("Autorizado");
    nfe.setDesconto(dto.getDesconto());
    nfe.setNNF(dto.getNumeroSequencial());
    nfe.setSerie(dto.getSerie());

    // Buscar empresa existente
    Empresa empresa = empresaRepository.findById(dto.getEmpresa().getId())
        .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresa().getId()));
    nfe.setEmpresa(empresa);

    Pagamento pagamento = pagamentoRepository.findById(dto.getPagamentos().getId())
        .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrada: " + dto.getEmpresa().getId()));

    nfe.setPagamento(pagamento);
    nfe.setN_parc(dto.getN_parc());
    nfe.setBandeira(dto.getBandeira());

    DestinatarioDTO destinatarioDTO = destinatarioService.findOrCreate(dto.getDestinatario());
    Destinatario destinatario = this.toEntity(destinatarioDTO);
    nfe.setDestinatario(destinatario);

    // Mapear itens
    for (

    ItemNfeDTO itemDto : dto.getItens()) {
      Product product = productRepository.findById(itemDto.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDto.getProductId()));

      ItemNfe item = new ItemNfe();
      item.setCodigoPrincipal(itemDto.getCodigo_principal());
      item.setDescricao(itemDto.getDescricao());
      item.setQCom(itemDto.getQuantidade());
      item.setPrecoVenda(itemDto.getPrecoUnitario());

      ItemNfePk pk = new ItemNfePk();
      pk.setNfe(nfe);
      pk.setProduct(product);
      item.setId(pk);

      nfe.getItens().add(item);
    }

    return nfe;
  }

  public NfeMinResponse toDto(Nfe nfe) {
    if (nfe == null)
      return null;

    LOG.info("Mapeando Nfe entity para NfeResponseDTO");

    NfeMinResponse dto = new NfeMinResponse();

    // Identificação
    dto.setId(nfe.getId().toString());
    dto.setAmbiente(nfe.getAmbiente());
    dto.setSerie(nfe.getSerie());
    dto.setNumero(nfe.getNNF());

    // Datas
    dto.setDataEmissao(nfe.getDhEmi());

    // Status
    dto.setStatus(nfe.getStatus());
    dto.setMotivo_status(nfe.getMotivo_status());

    // Valores
    dto.setValorTotalNota(nfe.getVNF());
    dto.setDesconto(nfe.getDesconto());

    // Chave e protocolo
    dto.setChave(nfe.getChave());

    DestinatarioDTO destinatarioDTO = toDTO(nfe.getDestinatario());
    dto.setDestinatario(destinatarioDTO);
    // Pagamento

    PagamentoDTO pagamentoDTO = toDTO(nfe.getPagamento());
    dto.setPagamentos(List.of(pagamentoDTO));

    EmpresaDTO empresaDTO = toDTO(nfe.getEmpresa());
    dto.setEmitente(List.of(empresaDTO));

    for (ItemNfe item : nfe.getItens()) {
      ItemNfeDTO itemDTO = new ItemNfeDTO();
      itemDTO.setProductId(item.getId().getProduct().getId());
      itemDTO.setCodigo_principal(item.getCodigoPrincipal());
      itemDTO.setDescricao(item.getDescricao());
      itemDTO.setQuantidade(item.getQCom());
      itemDTO.setPrecoUnitario(item.getPrecoVenda());

      dto.getItens().add(itemDTO);
    }

    return dto;
  }

  public void copyDtoToEntity(NfeRequestDTO dto, Nfe entity) {

    entity.setTpEmis(dto.getTpEmis());
    entity.setXJust(dto.getXJust());

    entity.setVProd(dto.getValorTotalNota());
    entity.setVNF(dto.getValorTotalNota());

    entity.setDesconto(dto.getDesconto());

    entity.setN_parc(dto.getN_parc());
    entity.setBandeira(dto.getBandeira());

    // Empresa
    if (dto.getEmpresa() != null) {
      Empresa empresa = new Empresa();
      empresa.setId(dto.getEmpresa().getId());
      entity.setEmpresa(empresa);
    }

    // Destinatário
    if (dto.getDestinatario() != null) {
      Destinatario destinatario = new Destinatario();
      destinatario.setId(dto.getDestinatario().getId());
      entity.setDestinatario(destinatario);
    }

    // Pagamento
    if (dto.getPagamentos() != null) {
      Pagamento pagamento = new Pagamento();
      pagamento.setId(dto.getPagamentos().getId());
      entity.setPagamento(pagamento);
    }

    entity.getItens().clear();

    for (ItemNfeDTO itemDto : dto.getItens()) {

      ItemNfe item = new ItemNfe();

      item.setNfe(entity);

      Product product = new Product();
      product.setId(itemDto.getProductId());
      item.setProduct(product);

      item.setQCom(itemDto.getQuantidade());
      item.setPrecoVenda(itemDto.getPrecoUnitario());

      entity.getItens().add(item);
    }
  }

  public ProductMinDTO toDTO(Product entity) {
    LOG.info("Mapeando Product entity para ProductMinDTO");

    ProductMinDTO dto = new ProductMinDTO();
    dto.setId(entity.getId());
    dto.setX_prod(entity.getX_prod());
    dto.setC_prod(entity.getC_prod());
    dto.setC_EAN(entity.getC_EAN());
    dto.setNCM(entity.getNCM());
    dto.setCEST(entity.getCEST());
    dto.setCFOP(entity.getCFOP());
    dto.setU_com(entity.getU_com());
    dto.setV_un_com(entity.getV_un_com());
    dto.setQ_trib(entity.getQ_trib());
    dto.setV_un_trib(entity.getV_un_trib());
    dto.setInd_tot(entity.getInd_tot());
    dto.setPreco_custo(entity.getPreco_custo());
    dto.setQuantidade(entity.getQuantidade());
    dto.setV_prod(entity.getV_prod());
    dto.setAtivo(entity.getAtivo());
    dto.setImagemUrl(entity.getImagemUrl());

    dto.setCst(entity.getCst());
    dto.setIcmsAliquota(entity.getIcmsAliquota());
    dto.setFcpAliquota(entity.getFcpAliquota());
    dto.setOrigem(entity.getOrigem());
    dto.setModalidadeBc(entity.getModalidadeBc());
    dto.setUf(entity.getUf());
    dto.setTributoFiscalId(entity.getTributoFiscalId());

    return dto;
  }

  public Product toEntity(ProductMinDTO dto) {
    LOG.info("Mapeando ProductMinDTO para Product entity");
    LOG.info("DTO recebido - NCM: {}, CEST: {}, CFOP: {}", dto.getNCM(), dto.getCEST(), dto.getCFOP());

    Product entity = new Product();
    entity.setId(dto.getId()); // Garantir que o ID seja nulo para inserção
    entity.setX_prod(dto.getX_prod());
    entity.setC_prod(dto.getC_prod());
    entity.setC_EAN(dto.getC_EAN());
    entity.setNCM(dto.getNCM());
    entity.setCEST(dto.getCEST());
    entity.setCFOP(dto.getCFOP());
    entity.setU_com(dto.getU_com());
    entity.setV_un_com(dto.getV_un_com());
    entity.setQ_trib(dto.getQ_trib());
    entity.setV_un_trib(dto.getV_un_trib());
    entity.setInd_tot(dto.getInd_tot());
    entity.setPreco_custo(dto.getPreco_custo());
    entity.setQuantidade(dto.getQuantidade());
    entity.setAtivo(true);
    entity.setV_prod(dto.getV_prod() != null ? dto.getV_prod() : 0.0);
    entity.setImagemUrl(dto.getImagemUrl());

    entity.setCst(dto.getCst());
    entity.setIcmsAliquota(dto.getIcmsAliquota());
    entity.setFcpAliquota(dto.getFcpAliquota());
    entity.setOrigem(dto.getOrigem());
    entity.setModalidadeBc(dto.getModalidadeBc());
    entity.setUf(dto.getUf());
    entity.setTributoFiscalId(dto.getTributoFiscalId());

    // Calcular valor total se não foi fornecido
    if (entity.getV_prod() == null || entity.getV_prod() == 0.0) {
      entity.calcularValorTotal();
    }

    return entity;
  }

  public void copyDtoToEntity(ProductMinDTO dto, Product entity) {
    entity.setX_prod(dto.getX_prod());
    entity.setC_prod(dto.getC_prod());
    entity.setC_EAN(dto.getC_EAN());
    entity.setNCM(dto.getNCM());
    entity.setCEST(dto.getCEST());
    entity.setCFOP(dto.getCFOP());
    entity.setU_com(dto.getU_com());
    entity.setV_un_com(dto.getV_un_com());
    entity.setQ_trib(dto.getQ_trib());
    entity.setV_un_trib(dto.getV_un_trib());
    entity.setInd_tot(dto.getInd_tot());
    entity.setPreco_custo(dto.getPreco_custo());
    entity.setQuantidade(dto.getQuantidade());
    entity.setAtivo(true);
    entity.setV_prod(dto.getV_prod() != null ? dto.getV_prod() : 0.0);
    entity.setImagemUrl(dto.getImagemUrl());

    entity.setCst(dto.getCst());
    entity.setIcmsAliquota(dto.getIcmsAliquota());
    entity.setFcpAliquota(dto.getFcpAliquota());
    entity.setOrigem(dto.getOrigem());
    entity.setModalidadeBc(dto.getModalidadeBc());
    entity.setUf(dto.getUf());
    entity.setTributoFiscalId(dto.getTributoFiscalId());

    // Calcular valor total se não foi fornecido
    if (entity.getV_prod() == null || entity.getV_prod() == 0.0) {
      entity.calcularValorTotal();
    }
  }

  public Destinatario toEntity(DestinatarioDTO dto) {
    if (dto == null) {
      return null;
    }
    DestinatarioDTO destinatarioDTO = destinatarioService.findOrCreate(dto);
    Destinatario destinatario = new Destinatario();
    destinatario.setId(destinatarioDTO.getId());
    destinatario.setCpf(destinatarioDTO.getCpf());
    destinatario.setCnpj(destinatarioDTO.getCnpj());
    destinatario.setX_nome(destinatarioDTO.getX_nome());
    destinatario.setLogradouro(destinatarioDTO.getLogradouro());
    destinatario.setNumero(destinatarioDTO.getNumero());
    destinatario.setBairro(destinatarioDTO.getBairro());
    destinatario.setCMun(destinatarioDTO.getCMun());
    destinatario.setUf(destinatarioDTO.getUf());
    destinatario.setCep(destinatarioDTO.getCep());
    destinatario.setIndIEDest(destinatarioDTO.getIndIEDest());

    return destinatario;
  }

  public DestinatarioDTO toDTO(Destinatario entity) {
    if (entity == null) {
      return null;
    }
    return destinatarioService.findById(entity.getId());
  }

  public ItemNfce toEntity(ItemNfceDTO dto, Nfce nfce, Product product) {
    if (dto == null)
      return null;
    ItemNfce item = modelMapper.map(dto, ItemNfce.class);
    ItemNfcePk pk = new ItemNfcePk();
    pk.setNfce(nfce);
    pk.setProduct(product);
    item.setId(pk);
    return item;
  }

  public ItemNfceDTO toDTO(ItemNfce entity) {
    if (entity == null)
      return null;
    ItemNfceDTO dto = new ItemNfceDTO();
    if (entity.getId() != null && entity.getId().getProduct() != null) {
      dto.setProductId(entity.getId().getProduct().getId());
      dto.setCodigo_principal(entity.getCodigoPrincipal());
      dto.setDescricao(entity.getDescricao());
      dto.setQuantidade(entity.getQCom());
      dto.setPrecoUnitario(entity.getPrecoVenda());
    }
    return dto;
  }

  public Pagamento toEntity(PagamentoDTO dto) {
    if (dto == null)
      return null;
    return modelMapper.map(dto, Pagamento.class);
  }

  public PagamentoDTO toDTO(Pagamento entity) {
    if (entity == null)
      return null;
    return modelMapper.map(entity, PagamentoDTO.class);
  }

  public Empresa toEntity(EmpresaDTO dto) {
    if (dto == null)
      return null;
    return modelMapper.map(dto, Empresa.class);
  }

  public EmpresaDTO toDTO(Empresa entity) {
    if (entity == null)
      return null;
    return modelMapper.map(entity, EmpresaDTO.class);
  }

  public void updateEntityFromDTO(EmpresaDTO dto, Empresa entity) {
    if (dto == null || entity == null)
      return;
    modelMapper.map(dto, entity);
  }

  public HistoricoProdutoDTO toDTOHistoricoProduto(HistoricoProduto entity) {
    if (entity == null)
      return null;
    return modelMapper.map(entity, HistoricoProdutoDTO.class);
  }

  public HistoricoProduto toEntityHistoricoProduto(HistoricoProdutoDTO dto) {
    if (dto == null)
      return null;
    return modelMapper.map(dto, HistoricoProduto.class);
  }

  public TributoFiscalDTO toDTO(Tributofiscal entity) {

    return modelMapper.map(entity, TributoFiscalDTO.class);
  }

  // serie notas fiscais ======= empresa 1
  public NfceSerieDTO toDTOEmp1(NfceSerieEmpresa1 entity) {
    return modelMapper.map(entity, NfceSerieDTO.class);
  }

  public NfceSerieEmpresa1 toEntityEmp1(NfceSerieDTO dto) {
    return modelMapper.map(dto, NfceSerieEmpresa1.class);
  }

  // serie notas fiscais ======= empresa 2

  public NfceSerieDTO toDTOEmp2(NfceSerieEmpresa2 entity) {
    return modelMapper.map(entity, NfceSerieDTO.class);
  }

  public NfceSerieEmpresa2 toEntityEmp2(NfceSerieDTO dto) {
    return modelMapper.map(dto, NfceSerieEmpresa2.class);
  }

  // serie notas fiscais ======= empresa 3
  public NfceSerieDTO toDTOEmp3(NfceSerieEmpresa3 entity) {
    return modelMapper.map(entity, NfceSerieDTO.class);
  }

  public NfceSerieEmpresa3 toEntityEmp3(NfceSerieDTO dto) {
    return modelMapper.map(dto, NfceSerieEmpresa3.class);
  }

  // usuarios
  public User toEntity(UserDTO dto) {
    return modelMapper.map(dto, User.class);
  }

  public UserDTO toDTO(User entity) {
    return modelMapper.map(entity, UserDTO.class);
  }
}
