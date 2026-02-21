package ru.yandex.practicum.shoppingstore.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.dto.store.ProductDto;
import ru.yandex.practicum.model.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.model.enums.ProductCategory;
import ru.yandex.practicum.model.enums.ProductState;
import ru.yandex.practicum.shoppingstore.exception.ProductNotFoundException;
import ru.yandex.practicum.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.shoppingstore.model.Product;
import ru.yandex.practicum.shoppingstore.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable) {
        log.info("Поиск товаров категории: {}", productCategory);
        Page<Product> products = productRepository.findAllByProductCategory(productCategory.name(), pageable);
        log.info("Найдено товаров: {}", products.getTotalElements());
        return products.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDto findProductById(UUID productId) {
        log.info("Поиск товара с ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар с ID " + productId + " не найден"));
        log.info("Товар найден: {}", product.getProductName());
        return productMapper.toDto(product);
    }

    @Transactional
    @Override
    public ProductDto addNewProduct(ProductDto dto) {
        if (dto.getProductId() != null) {
            throw new ValidationException(
                    "При создании товара поле productId должно быть null");
        }
        log.info("Создание нового товара: {}", dto.getProductName());
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        log.info("Товар создан с ID: {}", saved.getProductId());
        return productMapper.toDto(saved);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto dto) {
        if (dto.getProductId() == null) {
            throw new ValidationException(
                    "При обновлении товара поле productId не может быть пустым");
        }
        log.info("Обновление товара с ID: {}", dto.getProductId());
        
        // Проверяем существование товара
        productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар с ID " + dto.getProductId() + " не найден для обновления"));
        
        Product product = productMapper.toEntity(dto);
        Product updated = productRepository.save(product);
        log.info("Товар обновлён: {}", updated.getProductName());
        return productMapper.toDto(updated);
    }

    @Transactional
    @Override
    public Boolean removeProductById(UUID productId) {
        log.info("Удаление товара с ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар с ID " + productId + " не найден для удаления"));
        
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        log.info("Товар деактивирован: {}", product.getProductName());
        return true;
    }

    @Transactional
    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("Изменение статуса количества для товара ID: {}", request.getProductId());
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар с ID " + request.getProductId() + " не найден"));
        
        // Конвертируем Enum в String для сохранения в БД
        product.setQuantityState(request.getQuantityState().name());
        productRepository.save(product);
        log.info("Статус количества обновлён: {} -> {}",
                product.getProductName(), request.getQuantityState());
        return true;
    }
}
