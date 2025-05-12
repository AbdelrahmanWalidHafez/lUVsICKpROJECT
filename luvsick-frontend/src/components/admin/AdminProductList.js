import React, { useState, useCallback } from 'react';
import styled from 'styled-components';
import axios from 'axios';

const Container = styled.div`
  // Add your styles here
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

const AdminProductList = () => {
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortField, setSortField] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const PAGE_SIZE = 10; // Consistent page size

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        pageNum: currentPage,
        pageSize: PAGE_SIZE,
        sortField,
        sortDir,
        ...(selectedCategory && { categoryName: selectedCategory })
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
  }, [currentPage, sortField, sortDir, selectedCategory]);

  return (
    <Container>
      {/* ... existing JSX ... */}
      
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

export default AdminProductList; 