import React, { useState, useEffect, useCallback } from 'react';
import styled from 'styled-components';
import { FaSort, FaSortUp, FaSortDown, FaFilter, FaTrash } from 'react-icons/fa';
import axios from '../../utils/axios';
import { useNavigate } from 'react-router-dom';
import { refreshCsrfToken } from '../../utils/csrf';
import { useProducts } from '../../context/ProductContext';
import { useAuth } from '../../context/AuthContext';

const Container = styled.div`
  padding: 2rem;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
`;

const Title = styled.h1`
  font-size: 2rem;
  font-weight: 700;
  color: #333;
`;

const FiltersContainer = styled.div`
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
`;

const Select = styled.select`
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  min-width: 200px;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
  }
`;

const SortButton = styled.button`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  border: 2px solid #eee;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #ff4b4b;
  }
`;

const ProductGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
`;

const ProductCard = styled.div`
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  position: relative;

  &:hover {
    transform: translateY(-5px);
  }
`;

const ProductImage = styled.img`
  width: 100%;
  height: 200px;
  object-fit: cover;
`;

const ProductInfo = styled.div`
  padding: 1.5rem;
`;

const ProductName = styled.h3`
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
`;

const ProductPrice = styled.div`
  font-size: 1.1rem;
  font-weight: 700;
  color: #ff4b4b;
  margin-bottom: 0.5rem;
`;

const ProductCategory = styled.div`
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.5rem;
`;

const ProductDescription = styled.p`
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
`;

const SizesList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 1rem;
`;

const SizeBadge = styled.span`
  background: #f8f9fa;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #666;
`;

const Pagination = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
`;

const PageButton = styled.button`
  padding: 0.5rem 1rem;
  border: 2px solid #eee;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;

  &:hover:not(:disabled) {
    border-color: #ff4b4b;
  }

  &:disabled {
    background: #f8f9fa;
    cursor: not-allowed;
    border-color: #eee;
    color: #aaa;
  }
`;

const PageInfo = styled.div`
  font-size: 1rem;
  color: #666;
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  min-width: 120px;
  text-align: center;
`;

const LoadingOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const LoadingSpinner = styled.div`
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #ff4b4b;
  border-radius: 50%;
  animation: spin 1s linear infinite;

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;

const ProductList = () => {
  const { products, setProducts } = useProducts();
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [sortField, setSortField] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const [totalPages, setTotalPages] = useState(1);
  const PAGE_SIZE = 10; // Consistent page size

  // Redirect admin/manager/owner to admin product management
  useEffect(() => {
    const adminRoles = ['admin', 'manager', 'owner'];
    if (isAuthenticated && user && adminRoles.includes(user.role)) {
      navigate('/dashboard/products', { replace: true });
    }
  }, [isAuthenticated, user, navigate]);

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        pageNum: currentPage,
        pageSize: PAGE_SIZE,
        sortField,
        sortDir
      };

      if (selectedCategory && selectedCategory.trim() !== '') {
        params.categoryName = selectedCategory;
      }

      const response = await axios.get('/api/v1/product/allProducts', { params });
      setProducts(response.data.content || response.data); // Handle both paginated and non-paginated responses
      
      // If the response includes pagination info, use it
      if (response.data.totalPages) {
        setTotalPages(response.data.totalPages);
      } else {
        // Otherwise, estimate total pages based on content length
        setTotalPages(Math.ceil((response.data.content || response.data).length / PAGE_SIZE));
      }
    } catch (err) {
      console.error('Error fetching products:', err);
      setError(err.response?.data?.message || 'Failed to fetch products. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [selectedCategory, currentPage, sortField, sortDir, setProducts]);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const fetchCategories = async () => {
    try {
      const response = await axios.get('/api/v1/category/getCategories');
      setCategories(response.data);
    } catch (err) {
      setError('Failed to fetch categories');
    }
  };

  const handleSort = (field) => {
    if (field === sortField) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDir('desc');
    }
  };

  const getSortIcon = (field) => {
    if (field !== sortField) return <FaSort />;
    return sortDir === 'asc' ? <FaSortUp /> : <FaSortDown />;
  };

  const handleDelete = async (productId) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      try {
        await refreshCsrfToken();
        await axios.delete(`/api/v1/product/${productId}`);
        fetchProducts();
      } catch (err) {
        console.error('Error deleting product:', err);
        if (err.response?.status === 403) {
          setError('CSRF token validation failed. Please try again.');
        } else {
          setError(err.response?.data?.message || 'Failed to delete product. Please try again.');
        }
      }
    }
  };

  const handleProductClick = (product) => {
    const adminRoles = ['admin', 'manager', 'owner'];
    if (isAuthenticated && user && adminRoles.includes(user.role)) {
      navigate(`/products/edit/${product.id}`, { state: { product } });
    } else {
      navigate(`/product/${product.id}`, { state: { product } });
    }
  };

  return (
    <Container>
      {loading && (
        <LoadingOverlay>
          <LoadingSpinner />
        </LoadingOverlay>
      )}
      <Header>
        <Title>Products</Title>
      </Header>

      <FiltersContainer>
        <Select
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
        >
          <option value="">All Categories</option>
          {categories.map((category) => (
            <option key={category.id} value={category.name}>
              {category.name}
            </option>
          ))}
        </Select>

        <SortButton onClick={() => handleSort('price')}>
          Price {getSortIcon('price')}
        </SortButton>
        <SortButton onClick={() => handleSort('createdAt')}>
          Date {getSortIcon('createdAt')}
        </SortButton>
      </FiltersContainer>

      {error && <div style={{ color: 'red', marginBottom: '1rem' }}>{error}</div>}

      <ProductGrid>
        {products.map((product) => {
          const priceValue = parseFloat(product.price) || 0;
          const discountValue = parseFloat(product.discount) || 0;
          const discountedPrice = priceValue - (priceValue * discountValue / 100);
          return (
            <ProductCard 
              key={product.id} 
              onClick={() => handleProductClick(product)}
              style={{ cursor: 'pointer' }}
            >
              <ProductImage
                src={`http://localhost:8080/api/v1/product/image/${product.id}`}
                alt={product.name}
              />
              <ProductInfo>
                <ProductName>{product.name}</ProductName>
                <ProductPrice>
                  <span style={{ color: '#888' }}>
                    Original: <b>{priceValue.toFixed(2)} EGP</b>
                  </span>
                  {product.discount > 0 && (
                    <span style={{ color: '#ff4b4b', marginLeft: '1rem' }}>
                      After Discount: <b>{discountedPrice.toFixed(2)} EGP</b>
                    </span>
                  )}
                </ProductPrice>
                <ProductCategory>{product.categoryResponseDTO.name}</ProductCategory>
                <ProductDescription>{product.description}</ProductDescription>
                <SizesList>
                  {product.productSizeDTOS.map((size) => (
                    <SizeBadge key={size.name}>
                      {size.name}: {size.stock}
                    </SizeBadge>
                  ))}
                </SizesList>
              </ProductInfo>
            </ProductCard>
          );
        })}
      </ProductGrid>

      <Pagination>
        <PageButton
          onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
          disabled={currentPage === 1 || loading}
        >
          Previous
        </PageButton>
        <PageInfo>
          Page {currentPage} of {totalPages}
        </PageInfo>
        <PageButton
          onClick={() => setCurrentPage(prev => prev + 1)}
          disabled={currentPage >= totalPages || loading}
        >
          Next
        </PageButton>
      </Pagination>
    </Container>
  );
};

export default ProductList; 