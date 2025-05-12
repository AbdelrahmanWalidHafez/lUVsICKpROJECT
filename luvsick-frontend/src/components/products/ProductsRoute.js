import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ProductList from './ProductList';

const ProductsRoute = () => {
  const { user, isAuthenticated } = useAuth();
  const adminRoles = ['admin', 'manager', 'owner'];

  if (isAuthenticated && user && adminRoles.includes(user.role)) {
    return <Navigate to="/dashboard/products" replace />;
  }

  return <ProductList />;
};

export default ProductsRoute; 