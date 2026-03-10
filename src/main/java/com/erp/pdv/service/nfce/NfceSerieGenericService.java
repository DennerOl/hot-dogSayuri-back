package com.erp.pdv.service.nfce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.erp.pdv.dto.nfce.NfceSerieDTO;
import com.erp.pdv.service.exceptions.ResourceNotFoundException;
import com.erp.pdv.utils.Mapper;

/**
 * Service genérico para manipulação de séries NFC-e de diferentes empresas.
 * 
 * @param <E> Entidade da série
 * @param <R> Repositório da entidade
 */
public abstract class NfceSerieGenericService<E, R extends JpaRepository<E, Long>> {

  @Autowired
  protected R repository;

  @Autowired
  protected Mapper mapper;

  /**
   * Retorna o último número da série.
   */
  @Transactional
  public synchronized NfceSerieDTO getUltimoNumero() {
    E result = findFirstByOrderBySerieDesc();
    if (result == null) {
      throw new ResourceNotFoundException("Serie não encontrada");
    }
    return toDTO(result);
  }

  /**
   * Retorna a próxima sequência da série.
   */
  @Transactional
  public NfceSerieDTO getProximaSequencia() {
    int maxNumero = 999_999_999;
    int maxSerie = 999;
    E seq = findFirstByOrderBySerieDesc();
    if (seq == null) {
      seq = novaEntidade();
      setSerie(seq, 1);
      setUltimoNumero(seq, 0);
      seq = repository.save(seq);
    }
    int proximoNumero = getUltimoNumeroValue(seq) + 1;
    if (proximoNumero > maxNumero) {
      int novaSerie = getSerie(seq) + 1;
      if (novaSerie > maxSerie) {
        throw new RuntimeException("Limite máximo de série atingido!");
      }
      setSerie(seq, novaSerie);
      proximoNumero = 1;
    }
    setUltimoNumero(seq, proximoNumero);
    repository.save(seq);
    return toDTO(seq);
  }

  /**
   * Implementação específica para buscar a entidade com maior série.
   */
  protected abstract E findFirstByOrderBySerieDesc();

  /**
   * Implementação específica para converter entidade em DTO.
   */
  protected abstract NfceSerieDTO toDTO(E entity);

  /**
   * Cria uma nova instância da entidade.
   */
  protected abstract E novaEntidade();

  /**
   * Obtém o valor da série da entidade.
   */
  protected abstract int getSerie(E entity);

  /**
   * Define o valor da série na entidade.
   */
  protected abstract void setSerie(E entity, int serie);

  /**
   * Obtém o último número da entidade.
   */
  protected abstract int getUltimoNumeroValue(E entity);

  /**
   * Define o último número na entidade.
   */
  protected abstract void setUltimoNumero(E entity, int numero);
}