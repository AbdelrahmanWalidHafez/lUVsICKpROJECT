import React, { createContext, useContext, useState, useEffect } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  // Initialize cart from localStorage
  const [cart, setCart] = useState(() => {
    const storedCart = localStorage.getItem('cart');
    return storedCart ? JSON.parse(storedCart) : [];
  });
  const [lastAdded, setLastAdded] = useState(null);

  // Persist cart to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  const addToCart = (item) => {
    setCart(prev => {
      // If same product and size exists, update quantity
      const existing = prev.find(
        i => i.productId === item.productId && i.size === item.size
      );
      let updatedCart;
      if (existing) {
        updatedCart = prev.map(i =>
          i.productId === item.productId && i.size === item.size
            ? { ...i, quantity: i.quantity + item.quantity }
            : i
        );
      } else {
        updatedCart = [...prev, item];
      }
      setLastAdded({ ...item, time: Date.now() });
      return updatedCart;
    });
  };

  const removeFromCart = (productId, size) => {
    setCart(prev => prev.filter(i => !(i.productId === productId && i.size === size)));
  };

  const updateQuantity = (productId, size, quantity) => {
    setCart(prev => prev.map(i =>
      i.productId === productId && i.size === size ? { ...i, quantity } : i
    ));
  };

  const clearCart = () => {
    setCart([]);
  };

  return (
    <CartContext.Provider value={{ cart, addToCart, removeFromCart, updateQuantity, clearCart, lastAdded }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext); 