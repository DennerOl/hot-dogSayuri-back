package com.erp.pdv.projections;

import java.time.LocalDateTime;

public interface NfceMinProjection {

  Long getId();

  Integer getSerie();

  String getChave();

  Long getNumeroNf();

  String getAmbiente();

  String getStatus();

  String getMotivoStatus();

  Double getValorTotalNota();

  LocalDateTime getDataHoraEmissao();

  // Itens (pode vir nulo se não tiver)

  Long getProductId();

  String getItemCodigoPrincipal();

  String getItemDescricao();

  Double getItemQCom();

  Double getItemPrecoVenda();

  // Destinatario
  Long getDestinatarioId();

  String getDestinatarioNome();

  String getDestinatarioCpf();

  String getDestinatarioCnpj();

  // Pagamento
  Long getPagId();

  String getPagamentoTipo();

  // Emitente
  Long getEmitenteId();

  String getEmitenteNome();

  String getEmitenteCnpj();

}
