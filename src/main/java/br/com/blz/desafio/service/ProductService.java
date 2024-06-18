package br.com.blz.desafio.service;

import br.com.blz.desafio.exception.DuplicateProductException;
import br.com.blz.desafio.model.Inventory;
import br.com.blz.desafio.model.Product;
import br.com.blz.desafio.model.Warehouse;
import br.com.blz.desafio.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(Product product) {
        if (!verificaDuplicidade(product)) {
            calculaInventoryQuantity(product);
            calculaIsMarketable(product);
            productRepository.save(product);
        }
    }

    public void updateProduct(Integer sku, Product updatedProduct) {
        Product existingProduct = getProductBySku(sku);
        if (existingProduct != null) {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setInventory(updatedProduct.getInventory());
            calculaInventoryQuantity(existingProduct);
            calculaIsMarketable(existingProduct);
            productRepository.save(existingProduct);
        } else {
            throw new RuntimeException();
        }
    }

    public Product getProductBySku(Integer sku) {
        return productRepository.findById(sku).orElse(null);
    }

    public void deleteProductBySku(Integer sku) {
        Product existingProduct = getProductBySku(sku);
        if (existingProduct != null) {
            productRepository.deleteById(sku);
        } else {
            throw new RuntimeException();
        }
    }

    private void calculaInventoryQuantity(Product product) {
        Inventory inventory = product.getInventory();
        List<Warehouse> warehouses = inventory.getWarehouses();
        int totalQuantity = 0;

        for (Warehouse warehouse : warehouses) {
            totalQuantity += warehouse.getQuantity();
        }
        inventory.setQuantity(totalQuantity);
    }

    private void calculaIsMarketable(Product product) {
        Inventory inventory = product.getInventory();
        int totalQuantity = inventory.getQuantity();
        product.setMarketable(totalQuantity > 0);
    }

    private boolean verificaDuplicidade(Product product) {
        Optional<Product> existingProduct = productRepository.findById(product.getSku());
        if (existingProduct.isPresent()) {
            throw new DuplicateProductException("Product com ID " + product.getSku() + " já existe.");
        } else {
            return false;
        }
    }
}
