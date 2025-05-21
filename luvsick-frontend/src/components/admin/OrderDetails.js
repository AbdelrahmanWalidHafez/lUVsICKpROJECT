import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import axios from '../../utils/axios';
import { useParams, useNavigate } from 'react-router-dom';

const Container = styled.div`
  padding: 2rem;
`;
const Title = styled.h2`
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 2rem;
`;
const Section = styled.div`
  margin-bottom: 2rem;
`;
const Label = styled.span`
  font-weight: 600;
  color: #444;
`;
const Value = styled.span`
  color: #222;
`;
const StatusSelect = styled.select`
  padding: 0.5rem 1rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  margin-right: 1rem;
`;
const UpdateButton = styled.button`
  padding: 0.5rem 1.5rem;
  background: #ff4b4b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: #d32f2f; }
`;
const ProductsTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
`;
const Th = styled.th`
  background: #f8f9fa;
  padding: 0.75rem;
  text-align: left;
  font-weight: 700;
  color: #333;
`;
const Td = styled.td`
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
`;
const SuccessMsg = styled.div`
  background: #e6ffed;
  color: #1a7f37;
  border: 1px solid #b7ebc6;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-weight: 600;
  font-size: 1.1rem;
`;

const ALL_STATUSES = ["SHIPPED", "CONFIRMED", "CANCELLED", "RECEIVED"];
const UPDATABLE_STATUSES = ["SHIPPED", "CONFIRMED", "CANCELLED", "RECEIVED"];

const OrderDetails = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [updating, setUpdating] = useState(false);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => { fetchOrder(); }, [id]);

  const fetchOrder = async () => {
    // Since there is no getOrderById endpoint, fetch all orders and filter
    try {
      const res = await axios.get('/api/v1/order/getOrders', { params: { pageNum: 1, sortDir: 'desc', sortField: 'createdAt' } });
      const found = res.data.find(o => o.id === id);
      setOrder(found || null);
      setNewStatus(found?.orderStatus || '');
    } catch (e) {
      setOrder(null);
    }
  };

  const handleUpdateStatus = async () => {
    if (!newStatus || newStatus === order.orderStatus) return;
    setUpdating(true);
    setSuccess('');
    try {
      await axios.put(`/api/v1/order/${id}`, null, { params: { orderStatus: newStatus } });
      await fetchOrder();
      setSuccess('Order status updated successfully!');
      setTimeout(() => setSuccess(''), 2000);
    } finally {
      setUpdating(false);
    }
  };

  if (!order) return <Container>Loading or order not found.</Container>;

  return (
    <Container>
      <Title>Order Details</Title>
      {success && <SuccessMsg>{success}</SuccessMsg>}
      <Section>
        <Label>Order ID: </Label><Value>{order.id}</Value><br />
        <Label>Customer: </Label><Value>{order.customerDTO?.name}</Value><br />
        <Label>Phone Number: </Label><Value>{order.customerDTO?.phoneNumber || 'N/A'}</Value><br />
        <Label>Address:</Label><br />
        <Value>
          {order.customerDTO?.city ? <div><b>City:</b> {order.customerDTO.city}</div> : null}
          {order.customerDTO?.street ? <div><b>Street:</b> {order.customerDTO.street}</div> : null}
          {order.customerDTO?.buildingNumber ? <div><b>Building Number:</b> {order.customerDTO.buildingNumber}</div> : null}
          <div><b>Flat Number:</b> {order.customerDTO?.flatNumber ?? 'N/A'}</div>
          {!order.customerDTO?.city && !order.customerDTO?.street && !order.customerDTO?.buildingNumber && order.customerDTO?.flatNumber == null && 'N/A'}
        </Value><br />
        <Label>Status: </Label>
        <StatusSelect value={newStatus} onChange={e => setNewStatus(e.target.value)}>
          {UPDATABLE_STATUSES.map(status => (
            <option key={status} value={status}>{status}</option>
          ))}
        </StatusSelect>
        <UpdateButton onClick={handleUpdateStatus} disabled={updating || newStatus === order.orderStatus}>
          {updating ? 'Updating...' : 'Update Status'}
        </UpdateButton>
      </Section>
      <Section>
        <Label>Total Price: </Label><Value>{order.totalPrice}</Value><br />
        <Label>Items: </Label><Value>{order.productResponseDTOS?.length}</Value><br />
      </Section>
      <Section>
        <Label>Products:</Label>
        <ProductsTable>
          <thead>
            <tr>
              <Th>Product</Th>
              <Th>Size</Th>
              <Th>Quantity</Th>
              <Th>Discounted Price</Th>
            </tr>
          </thead>
          <tbody>
            {order.productResponseDTOS?.map(product =>
              product.productSizeDTOS
                .filter(sizeObj => order.itemPerQuantity?.[sizeObj.id])
                .map(sizeObj => {
                  const quantity = order.itemPerQuantity[sizeObj.id];
                  const discountedPrice = (product.price - (product.price * (product.discount || 0) / 100)).toFixed(2);
                  return (
                    <tr key={sizeObj.id}>
                      <Td>{product.name}</Td>
                      <Td>{sizeObj.size}</Td>
                      <Td>{quantity}</Td>
                      <Td>{discountedPrice}</Td>
                    </tr>
                  );
                })
            )}
          </tbody>
        </ProductsTable>
      </Section>
      <UpdateButton onClick={() => navigate(-1)} style={{ background: '#888', marginTop: '2rem' }}>Back</UpdateButton>
    </Container>
  );
};

export default OrderDetails; 