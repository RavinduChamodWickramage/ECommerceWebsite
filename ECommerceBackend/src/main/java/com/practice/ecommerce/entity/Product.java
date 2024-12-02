package com.practice.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.practice.ecommerce.dto.ProductDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    @Lob
    private String description;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] img;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore //
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonManagedReference //
    private List<CartItems> cartItems;

    public ProductDto getDto() {
        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setName(name);
        productDto.setPrice(price);
        productDto.setDescription(description);
        productDto.setByteImg(img);
        productDto.setCategoryId(category.getId());
        productDto.setCategoryName(category.getName());
        return productDto;
    }

}
