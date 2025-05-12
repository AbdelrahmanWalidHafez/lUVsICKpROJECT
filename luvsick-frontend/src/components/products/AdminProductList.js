import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { FaTrash, FaSort, FaSortUp, FaSortDown } from 'react-icons/fa';
import axios from '../../utils/axios';
import { refreshCsrfToken } from '../../utils/csrf';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  padding: 2rem;
`;

const Controls = styled.div`
  display: flex;
  gap: 1.5rem;
  align-items: center;
  margin-bottom: 2rem;
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
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 2.5rem;
`;

const ProductCard = styled.div`
  background: white;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.10);
  transition: transform 0.3s cubic-bezier(.4,2,.6,1), box-shadow 0.3s;
  position: relative;
  cursor: pointer;
  padding: 0;
  min-height: 420px;
  &:hover {
    transform: translateY(-8px) scale(1.03);
    box-shadow: 0 8px 32px rgba(0,0,0,0.13);
  }
`;

const ProductImage = styled.img`
  width: 100%;
  height: 260px;
  object-fit: cover;
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
`;

const ProductInfo = styled.div`
  padding: 2rem 1.5rem 1.5rem 1.5rem;
`;

const ProductName = styled.h3`
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 0.7rem;
  color: #222;
`;

const ProductPrice = styled.div`
  font-size: 1.2rem;
  font-weight: 700;
  color: #ff4b4b;
  margin-bottom: 0.5rem;
`;

const ProductCategory = styled.div`
  font-size: 1rem;
  color: #666;
  margin-bottom: 0.5rem;
`;

const ProductDescription = styled.p`
  font-size: 1rem;
  color: #888;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
`;

const DeleteButton = styled.button`
  position: absolute;
  top: 18px;
  right: 18px;
  background: none;
  border: none;
  cursor: pointer;
  color: #ff4b4b;
  font-size: 1.7rem;
  opacity: 0;
  transition: opacity 0.2s;
  z-index: 2;
  ${ProductCard}:hover & {
    opacity: 1;
  }
`;

const Pagination = styled.div`
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-top: 2.5rem;
`;

const PageButton = styled.button`
  padding: 0.7rem 1.5rem;
  border: 2px solid #eee;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  &:hover {
    border-color: #ff4b4b;
  }
  &:disabled {
    background: #f8f9fa;
    cursor: not-allowed;
    border-color: #eee;
    color: #aaa;
  }
`;

const AdminProductList = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [sortField, setSortField] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [currentPage, setCurrentPage] = useState(1);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [selectedCategory, sortField, sortDir, currentPage]);

  const fetchCategories = async () => {
    try {
      const response = await axios.get('/api/v1/category/getCategories');
      setCategories(response.data);
    } catch (err) {}
  };

  const fetchProducts = async () => {
    try {
      const params = {
        sortField,
        sortDir,
        pageNum: currentPage
      };
      if (selectedCategory && selectedCategory.trim() !== '') {
        params.categoryName = selectedCategory;
      }
      const response = await axios.get('/api/v1/product/allProducts', { params });
      setProducts(response.data);
    } catch (err) {}
  };

  const handleProductEditClick = (product) => {
    navigate(`/dashboard/products/edit/${product.id}`, { state: { product } });
  };

  const handleDeleteProduct = async (productId) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      await refreshCsrfToken();
      await axios.delete(`/api/v1/product/${productId}`);
      fetchProducts();
    } catch (err) {
      alert('Failed to delete product.');
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

  return (
    <Container>
      <h2 style={{ fontSize: '1.7rem', fontWeight: 700, marginBottom: '1.5rem' }}>All Products</h2>
      <Controls>
        <Select
          value={selectedCategory}
          onChange={e => setSelectedCategory(e.target.value)}
        >
          <option value="">All Categories</option>
          {categories.map(category => (
            <option key={category.id} value={category.name}>{category.name}</option>
          ))}
        </Select>
        <SortButton onClick={() => handleSort('price')}>
          Price {getSortIcon('price')}
        </SortButton>
        <SortButton onClick={() => handleSort('createdAt')}>
          Date {getSortIcon('createdAt')}
        </SortButton>
      </Controls>
      <ProductGrid>
        {products.map(product => {
          const priceValue = parseFloat(product.price) || 0;
          const discountValue = parseFloat(product.discount) || 0;
          const discountedPrice = priceValue - (priceValue * discountValue / 100);
          return (
            <ProductCard key={product.id} onClick={() => handleProductEditClick(product)}>
              <ProductImage src={`http://localhost:8080/api/v1/product/image/${product.id}`} alt={product.name} />
              <DeleteButton
                onClick={e => { e.stopPropagation(); handleDeleteProduct(product.id); }}
                title="Delete Product"
              >
                <FaTrash />
              </DeleteButton>
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
                <ProductCategory>{product.categoryResponseDTO?.name}</ProductCategory>
                <ProductDescription>{product.description}</ProductDescription>
              </ProductInfo>
            </ProductCard>
          );
        })}
      </ProductGrid>
      <Pagination>
        <PageButton
          onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
          disabled={currentPage === 1}
        >
          Previous
        </PageButton>
        <PageButton
          onClick={() => setCurrentPage(prev => prev + 1)}
          disabled={products.length < 10}
        >
          Next
        </PageButton>
      </Pagination>
    </Container>
  );
};

export default AdminProductList; 