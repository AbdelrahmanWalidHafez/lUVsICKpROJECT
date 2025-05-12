import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import axios from '../../utils/axios';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  padding: 2rem;
`;
const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
`;
const Title = styled.h2`
  font-size: 2rem;
  font-weight: 700;
  color: #333;
`;
const FiltersContainer = styled.div`
  display: flex;
  gap: 1rem;
  align-items: center;
`;
const Select = styled.select`
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  min-width: 180px;
  cursor: pointer;
  &:focus { outline: none; border-color: #ff4b4b; }
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
  &:hover { border-color: #ff4b4b; }
`;
const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  margin-top: 2rem;
`;
const Th = styled.th`
  background: #f8f9fa;
  padding: 1rem;
  text-align: left;
  font-weight: 700;
  color: #333;
`;
const Td = styled.td`
  padding: 1rem;
  border-bottom: 1px solid #eee;
`;
const Tr = styled.tr`
  cursor: pointer;
  &:hover { background: #f2f2f2; }
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
  &:hover:not(:disabled) { border-color: #ff4b4b; }
  &:disabled { background: #f8f9fa; cursor: not-allowed; border-color: #eee; color: #aaa; }
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

const ORDER_STATUSES = ["SHIPPED", "CONFIRMED", "CANCELLED", "RECEIVED"];

const OrdersList = () => {
  const [orders, setOrders] = useState([]);
  const [orderStatus, setOrderStatus] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [sortField, setSortField] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => { fetchOrders(); }, [orderStatus, currentPage, sortField, sortDir]);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const params = {
        ...(orderStatus && { orderStatus }),
        pageNum: currentPage,
        sortDir,
        sortField,
      };
      const res = await axios.get('/api/v1/order/getOrders', { params });
      setOrders(res.data);
      setTotalPages(res.data.length < 10 ? currentPage : currentPage + 1);
    } catch (e) {
      setOrders([]);
    } finally {
      setLoading(false);
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

  return (
    <Container>
      <Header>
        <Title>Orders</Title>
      </Header>
      <FiltersContainer>
        <Select value={orderStatus} onChange={e => setOrderStatus(e.target.value)}>
          <option value="">All Statuses</option>
          {ORDER_STATUSES.map(status => (
            <option key={status} value={status}>{status}</option>
          ))}
        </Select>
        <SortButton onClick={() => handleSort('totalPrice')}>Total Price {sortField === 'totalPrice' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</SortButton>
        <SortButton onClick={() => handleSort('createdAt')}>Date {sortField === 'createdAt' ? (sortDir === 'asc' ? '↑' : '↓') : ''}</SortButton>
      </FiltersContainer>
      <Table>
        <thead>
          <tr>
            <Th>ID</Th>
            <Th>Customer</Th>
            <Th>Status</Th>
            <Th>Total Price</Th>
            <Th>Created</Th>
          </tr>
        </thead>
        <tbody>
          {orders.map(order => (
            <Tr key={order.id} onClick={() => navigate(`/dashboard/orders/${order.id}`)}>
              <Td>{order.id}</Td>
              <Td>{order.customerDTO?.name}</Td>
              <Td>{order.orderStatus}</Td>
              <Td>{order.totalPrice}</Td>
              <Td>{order.createdAt || ''}</Td>
            </Tr>
          ))}
        </tbody>
      </Table>
      <Pagination>
        <PageButton onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))} disabled={currentPage === 1 || loading}>Previous</PageButton>
        <PageInfo>Page {currentPage} of {totalPages}</PageInfo>
        <PageButton onClick={() => setCurrentPage(prev => prev + 1)} disabled={orders.length < 10 || loading}>Next</PageButton>
      </Pagination>
    </Container>
  );
};

export default OrdersList; 