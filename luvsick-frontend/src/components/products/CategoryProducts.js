import React, { useEffect, useState, useCallback } from 'react';
import styled from 'styled-components';
import { useParams, useNavigate } from 'react-router-dom';
import axios from '../../utils/axios';
import { FaSort, FaSortUp, FaSortDown } from 'react-icons/fa';

const Container = styled.div`
  padding: 2rem;
`;

const FiltersContainer = styled.div`
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
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

const ProductDescription = styled.p`
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

const SectionTitle = styled.h2`
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 2rem;
  text-align: left;
`;

const CategoryProducts = () => {
  const { categoryName } = useParams();
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortField, setSortField] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const PAGE_SIZE = 10; // Consistent page size

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        pageNum: currentPage,
        pageSize: PAGE_SIZE,
        sortField,
        sortDir,
        categoryName
      };
      const response = await axios.get('/api/v1/product/allProducts', { params });
      setProducts(response.data.content || response.data);
      
      // If the response includes pagination info, use it
      if (response.data.totalPages) {
        setTotalPages(response.data.totalPages);
      } else {
        // Otherwise, estimate total pages based on content length
        setTotalPages(Math.ceil((response.data.content || response.data).length / PAGE_SIZE));
      }
    } catch (err) {
      setError('Failed to fetch products.');
    } finally {
      setLoading(false);
    }
  }, [categoryName, currentPage, sortField, sortDir]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

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

  const handleProductClick = (product) => {
    navigate(`/product/${product.id}`, { state: { product } });
  };

  return (
    <>
      <Container>
        <SectionTitle>{categoryName}</SectionTitle>
        <FiltersContainer>
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
              <ProductCard key={product.id} onClick={() => handleProductClick(product)}>
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
                  <ProductDescription>{product.description}</ProductDescription>
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
    </>
  );
};

export default CategoryProducts; 