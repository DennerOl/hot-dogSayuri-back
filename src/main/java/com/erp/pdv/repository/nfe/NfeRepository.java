package com.erp.pdv.repository.nfe;

import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.pdv.model.nfe.Nfe;
import com.erp.pdv.projections.nfe.NfeMinProjection;

public interface NfeRepository extends JpaRepository<Nfe, Long> {

    /**
     * Busca uma NFC-e pelo ID já carregando os itens (itens não virão lazy).
     */
    @EntityGraph(attributePaths = "itens")
    Optional<Nfe> findWithItensById(Long id);

    @Query(value = """
            SELECT
                n.id AS id,
                n.serie AS serie,
                n.chave AS chave,
                n.numero_nf AS numeroNf,
                n.ambiente AS ambiente,
                n.status AS status,
                n.valor_total_nota AS valorTotalNota,
                n.data_hora_emissao AS dataHoraEmissao,
                n.motivo_status AS motivoStatus,
                n.parcelas,
                n.bandeira,

                -- Emitente
                e.id AS emitenteId,
                e.x_nome AS emitenteNome,
                e.cnpj AS emitenteCnpj,

                -- Destinatário
                d.id AS destinatarioId,
                d.x_nome AS destinatarioNome,
                d.cpf AS destinatarioCpf,
                d.cnpj AS destinatarioCnpj,

                -- Pagamento
                p.id AS pagId,
                p.t_pag AS pagamentoTipo,

                -- Itens
                i.product_id AS productId,
                i.codigo_principal AS itemCodigoPrincipal,
                i.descricao AS itemDescricao,
                i.q_com AS itemQCom,
                i.preco_venda AS itemPrecoVenda

            FROM tb_nfe n
            INNER JOIN tb_emitente e ON n.empresa_id = e.id
            LEFT JOIN tb_destinatario d ON n.destinatario_id = d.id
            INNER JOIN tb_pagamento p ON n.pagamento_id = p.id
            LEFT JOIN tb_item_nfe i ON i.nfe_id = n.id

            WHERE
                -- FILTRO DE DATA OPCIONAL
                (:minDate IS NULL OR :maxDate IS NULL
                    OR (n.data_hora_emissao IS NOT NULL
                        AND n.data_hora_emissao BETWEEN CAST(:minDate AS timestamp) AND CAST(:maxDate AS timestamp)))

                -- FILTRO DE cpf OPCIONAL
                AND (:cpf IS NULL OR :cpf = '' OR d.cpf = :cpf)

                -- FILTRO DE CNPJ OPCIONAL
                AND (:cnpj IS NULL OR :cnpj = '' OR e.cnpj = :cnpj)

                -- INCLUI ORÇAMENTOS COM data_hora_emissao NULL
                AND (n.data_hora_emissao IS NOT NULL OR :minDate IS NULL OR :maxDate IS NULL)

            ORDER BY
                n.data_hora_emissao DESC NULLS LAST,  -- Primeiro por data (NULLs no final)
                n.id DESC,                            -- Depois por ID (ordem de criação)
                i.product_id
            """, countQuery = """
            SELECT COUNT(DISTINCT n.id)
            FROM tb_nfe n
            INNER JOIN tb_emitente e ON n.empresa_id = e.id
            LEFT JOIN tb_destinatario d ON n.destinatario_id = d.id
            INNER JOIN tb_pagamento p ON n.pagamento_id = p.id
            WHERE
                (:minDate IS NULL OR :maxDate IS NULL
                    OR (n.data_hora_emissao IS NOT NULL
                        AND n.data_hora_emissao BETWEEN CAST(:minDate AS timestamp) AND CAST(:maxDate AS timestamp)))
                AND (:cpf IS NULL OR :cpf = '' OR d.cpf = :cpf)
                AND (:cnpj IS NULL OR :cnpj = '' OR e.cnpj = :cnpj)
                AND (n.data_hora_emissao IS NOT NULL OR :minDate IS NULL OR :maxDate IS NULL)
            """, nativeQuery = true)
    Page<NfeMinProjection> findAllNfe(
            @Param("minDate") String minDate,
            @Param("maxDate") String maxDate,
            @Param("cpf") String cpf,
            @Param("cnpj") String cnpj,
            Pageable pageable);

}
