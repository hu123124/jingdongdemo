package com.example.jingdongdemo.service.impl;

import com.example.jingdongdemo.mapper.CartMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void updateQuantity_负数量_应抛异常且不落库() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.updateQuantity(1L, -1));
        assertEquals("商品数量不合法", ex.getMessage());
        verify(cartMapper, never()).updateQuantityWithId(anyLong(), any());
    }

    @Test
    void updateQuantity_null_应抛异常() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.updateQuantity(1L, null));
        assertEquals("商品数量不合法", ex.getMessage());
    }

    @Test
    void updateQuantity_合法数量_正常更新() {
        cartService.updateQuantity(1L, 2);
        verify(cartMapper).updateQuantityWithId(1L, 2);
    }
}