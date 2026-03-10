package com.erp.pdv.model.nfe;

import com.erp.pdv.model.produto.Product;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_item_nfe")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemNfe {

  @EmbeddedId
  private ItemNfePk id = new ItemNfePk();

  private String codigoPrincipal;
  private String descricao;
  private Double qCom; // Quantidade para venda
  private Double precoVenda;

  public ItemNfe(Nfe nfe, Product product, String codigoPrincipal, String descricao, Double qCom,
      Double precoVenda) {
    this.id.setNfe(nfe);
    this.id.setProduct(product);
    this.codigoPrincipal = codigoPrincipal;
    this.descricao = descricao;
    this.qCom = qCom;
    this.precoVenda = precoVenda;
  }

  public Nfe getNfe() {
    return id.getNfe();
  }

  public void setNfe(Nfe nfe) {
    id.setNfe(nfe);
  }

  public Product getProduct() {
    return id.getProduct();
  }

  public void setProduct(Product product) {
    id.setProduct(product);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ItemNfe other = (ItemNfe) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

}
