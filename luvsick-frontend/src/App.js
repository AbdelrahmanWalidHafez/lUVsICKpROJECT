import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Auth from './components/Auth';
import Dashboard from './components/Dashboard';
import ProductManagement from './components/products/ProductManagement';
import ProductList from './components/products/ProductList';
import EditProduct from './components/products/EditProduct';
import CategoryManagement from './components/categories/CategoryManagement';
import UserManagement from './components/users/UserManagement';
import { AuthProvider, useAuth } from './context/AuthContext';
import Error403 from './components/Error403';
import HomePage from './components/HomePage';
import Navbar from './components/Navbar';
import CategoryProducts from './components/products/CategoryProducts';
import { CartProvider } from './context/CartContext';
import ProductDetails from './components/products/ProductDetails';
import Cart from './components/products/Cart';
import { ProductProvider } from './context/ProductContext';
import ProductsRoute from './components/products/ProductsRoute';
import AdminProductList from './components/products/AdminProductList';
import Checkout from './components/products/Checkout';
import OrdersList from './components/admin/OrdersList';
import OrderDetails from './components/admin/OrderDetails';
import './App.css';

const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" />;
};

const PublicRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return !isAuthenticated ? children : <Navigate to="/dashboard" />;
};

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/home" element={<Navigate to="/" replace />} />
      <Route 
        path="/login" 
        element={
          <PublicRoute>
            <Auth />
          </PublicRoute>
        } 
      />
      <Route 
        path="/dashboard" 
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
      >
        <Route path="products" element={<ProductManagement />} />
        <Route path="products/edit/:id" element={<EditProduct />} />
        <Route path="product-list" element={<AdminProductList />} />
        <Route path="categories" element={<CategoryManagement />} />
        <Route path="users" element={<UserManagement />} />
        <Route path="orders" element={<OrdersList />} />
        <Route path="orders/:id" element={<OrderDetails />} />
      </Route>
      <Route path="/products" element={<ProductsRoute />} />
      <Route path="/category/:categoryName" element={<CategoryProducts />} />
      <Route path="/forbidden" element={<Error403 />} />
      <Route path="/product/:id" element={<ProductDetails />} />
      <Route path="/cart" element={<Cart />} />
      <Route path="/checkout" element={<Checkout />} />
    </Routes>
  );
};

const AppLayout = ({ children }) => {
  const location = useLocation();
  const hideNavbar = location.pathname.startsWith('/dashboard') || location.pathname === '/login';
  return (
    <>
      {!hideNavbar && <Navbar />}
      {children}
    </>
  );
};

const App = () => {
  return (
    <CartProvider>
      <AuthProvider>
        <ProductProvider>
          <Router>
            <AppLayout>
              <AppRoutes />
            </AppLayout>
          </Router>
        </ProductProvider>
      </AuthProvider>
    </CartProvider>
  );
};

export default App;
