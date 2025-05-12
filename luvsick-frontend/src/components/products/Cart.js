import React from 'react';
import styled from 'styled-components';
import { useCart } from '../../context/CartContext';
import { useProducts } from '../../context/ProductContext';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  max-width: 700px;
  margin: 2rem auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.07);
  padding: 2rem;
`;

const CartTitle = styled.h1`
  font-size: 2rem;
  font-weight: 800;
  color: #222;
  margin-bottom: 2rem;
`;

const CartItem = styled.div`
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1.5rem;
`;

const ProductImage = styled.img`
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
`;

const Info = styled.div`
  flex: 1;
`;

const Name = styled.div`
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
`;

const Detail = styled.div`
  color: #666;
  font-size: 0.95rem;
  margin-bottom: 0.3rem;
`;

const Price = styled.div`
  font-size: 1.1rem;
  font-weight: 700;
  color: #ff4b4b;
`;

const CheckoutButton = styled.button`
  background: #ff4b4b;
  color: #fff;
  font-weight: 700;
  font-size: 1.1rem;
  padding: 1rem 2.5rem;
  border-radius: 10px;
  border: none;
  margin-top: 2rem;
  cursor: pointer;
  transition: background 0.2s;
  width: 100%;
  &:hover {
    background: #d32f2f;
  }
`;

const RemoveButton = styled.button`
  background: #ff4b4b;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-size: 1rem;
  margin-top: 0.5rem;
  transition: background 0.2s;
  &:hover {
    background: #d32f2f;
  }
`;

const QuantityControls = styled.div`
  display: flex;
  align-items: center;
  gap: 0.7rem;
  margin-top: 0.5rem;
`;

const QuantityButton = styled.button`
  background: #f8f9fa;
  border: 1px solid #eee;
  border-radius: 6px;
  font-size: 1.2rem;
  width: 32px;
  height: 32px;
  cursor: pointer;
  &:hover:enabled {
    background: #111;
    color: #fff;
  }
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

const Cart = () => {
  const { cart, updateQuantity, removeFromCart, clearCart } = useCart();
  const { products } = useProducts();
  const navigate = useNavigate();

  // Helper to get max stock for a cart item
  const getMaxStock = (item) => {
    const product = products.find(p => p.id === item.productId);
    if (!product || !product.productSizeDTOS) return null;
    const sizeObj = product.productSizeDTOS.find(
      s => (s.size || s.name)?.toLowerCase() === item.size?.toLowerCase()
    );
    return sizeObj ? sizeObj.quantity : null;
  };

  const getTotal = () =>
    cart.reduce(
      (sum, item) =>
        sum + (item.price - (item.price * item.discount / 100)) * item.quantity,
      0
    );

  return (
    <Container>
      <CartTitle>Your Cart</CartTitle>
      {cart.length === 0 ? (
        <>
          <div style={{ textAlign: 'center', color: '#888', margin: '3rem 0' }}>
            <div style={{ fontSize: '1.3rem', marginBottom: '2rem' }}>Nothing here to view</div>
            <button
              style={{
                background: '#ff4b4b',
                color: '#fff',
                fontWeight: 700,
                fontSize: '1.1rem',
                padding: '1rem 2.5rem',
                borderRadius: 10,
                border: 'none',
                cursor: 'pointer',
                transition: 'background 0.2s',
                marginTop: '1rem'
              }}
              onClick={() => window.location.href = '/'}
            >
              Continue Shopping
            </button>
          </div>
          <div style={{ position: 'fixed', left: 0, top: '50%', width: '100vw', height: 260, pointerEvents: 'none', zIndex: 9999, transform: 'translateY(-50%)' }}>
            <img
              src="/boring-hannover.gif"
              alt="Tumbleweed"
              style={{
                position: 'absolute',
                left: 0,
                bottom: 0,
                height: 260,
                width: 320,
                animation: 'tumbleweed-move 3.5s linear infinite',
                background: 'transparent'
              }}
            />
            <style>{`
              @keyframes tumbleweed-move {
                0% { left: 0; }
                100% { left: calc(100vw - 320px); }
              }
            `}</style>
          </div>
        </>
      ) : (
        <>
          {cart.map(item => {
            const maxStock = getMaxStock(item);
            return (
              <CartItem key={item.productId + '-' + item.size}>
                <ProductImage src={`http://localhost:8080${item.image}`} alt={item.name} />
                <Info>
                  <Name>{item.name}</Name>
                  <Detail>Size: {item.size}</Detail>
                  {maxStock === 0 ? (
                    <div style={{ color: 'red', fontWeight: 600, margin: '0.5rem 0' }}>Out of stock</div>
                  ) : (
                    <QuantityControls>
                      <QuantityButton
                        onClick={() => updateQuantity(item.productId, item.size, item.quantity - 1)}
                        disabled={item.quantity <= 1 || maxStock === null}
                      >-</QuantityButton>
                      <span>{item.quantity}</span>
                      <QuantityButton
                        onClick={() => updateQuantity(item.productId, item.size, item.quantity + 1)}
                        disabled={maxStock === null || item.quantity >= maxStock}
                      >+</QuantityButton>
                    </QuantityControls>
                  )}
                  <Detail style={{ color: '#888', fontSize: '0.95rem' }}>
                    In stock: {maxStock === null ? 'Unknown' : maxStock === 0 ? 'Out of stock' : maxStock}
                  </Detail>
                  <RemoveButton onClick={() => removeFromCart(item.productId, item.size)}>
                    Remove
                  </RemoveButton>
                  <Price>
                    LE {((item.price - (item.price * item.discount / 100)) * item.quantity).toFixed(2)}
                  </Price>
                </Info>
              </CartItem>
            );
          })}
          <Price style={{ textAlign: 'right', fontSize: '1.3rem', marginTop: '1rem' }}>
            Total: LE {getTotal().toFixed(2)}
          </Price>
          <CheckoutButton onClick={() => navigate('/checkout')}>Proceed to Checkout</CheckoutButton>
        </>
      )}
    </Container>
  );
};

export default Cart; 