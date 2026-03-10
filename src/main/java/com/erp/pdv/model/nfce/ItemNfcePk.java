package com.erp.pdv.model.nfce;

import com.erp.pdv.model.produto.Product;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemNfcePk {

  @ManyToOne
  @JoinColumn(name = "nfce_id")
  private Nfce nfce;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ItemNfcePk other = (ItemNfcePk) obj;
    if (nfce == null) {
      if (other.nfce != null)
        return false;
    } else if (!nfce.equals(other.nfce))
      return false;
    if (product == null) {
      if (other.product != null)
        return false;
    } else if (!product.equals(other.product))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nfce == null) ? 0 : nfce.hashCode());
    result = prime * result + ((product == null) ? 0 : product.hashCode());
    return result;
  }

}
