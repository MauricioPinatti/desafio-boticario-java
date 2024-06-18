package br.com.blz.desafio.controller;

import br.com.blz.desafio.exception.DuplicateProductException;
import br.com.blz.desafio.model.Product;
import br.com.blz.desafio.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody Product product) {
        try {
            productService.createProduct(product);
            logger.info("[CONTROLLER] - Produto criado com sucesso: {}", product.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateProductException e) {
            logger.error("[CONTROLLER] - Erro ao criar produto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{sku}")
    public ResponseEntity<Void> updateProduct(@PathVariable Integer sku, @RequestBody Product updatedProduct) {
        try {
            productService.updateProduct(sku, updatedProduct);
            logger.info("[CONTROLLER] - Produto {} atualizado com sucesso", sku);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("[CONTROLLER] - Erro ao atualizar produto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable Integer sku) {
        try {
            Product product = productService.getProductBySku(sku);
            logger.info("[CONTROLLER] - Produto {} encontrado com sucesso ", product.getSku());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            logger.error("[CONTROLLER] - Erro ao buscar produto: {}", e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProductBySku(@PathVariable Integer sku) {
        try {
            productService.deleteProductBySku(sku);
            logger.info("[CONTROLLER] - Produto {} deletado com sucesso", sku);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("[CONTROLER] - Erro ao deletar produto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
