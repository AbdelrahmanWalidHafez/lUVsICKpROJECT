import React, { useState } from 'react';
import styled from 'styled-components';
import { useCart } from '../../context/CartContext';
import axios from '../../utils/axios';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  max-width: 900px;
  margin: 2rem auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 2.5rem 2rem;
`;

const Title = styled.h2`
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 2rem;
`;

const CartSummary = styled.div`
  margin-bottom: 2.5rem;
`;

const CartItem = styled.div`
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 1.2rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
`;

const ProductImage = styled.img`
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
`;

const ItemInfo = styled.div`
  flex: 1;
`;

const ItemName = styled.div`
  font-weight: 600;
  font-size: 1.1rem;
`;

const ItemDetail = styled.div`
  color: #888;
  font-size: 0.98rem;
`;

const ItemPrice = styled.div`
  font-weight: 700;
  color: #ff4b4b;
`;

const Total = styled.div`
  text-align: right;
  font-size: 1.3rem;
  font-weight: 700;
  color: #ff4b4b;
  margin-top: 1.5rem;
`;

const Form = styled.form`
  margin-top: 2.5rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem 2rem;
  background: #fafbfc;
  border-radius: 12px;
  padding: 2rem 1.5rem;
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

const Label = styled.label`
  font-weight: 600;
  color: #333;
`;

const Input = styled.input`
  padding: 0.8rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  &:focus {
    outline: none;
    border-color: #ff4b4b;
  }
`;

const SubmitButton = styled.button`
  grid-column: 1 / -1;
  background: #ff4b4b;
  color: white;
  padding: 1.2rem 0;
  border: none;
  border-radius: 8px;
  font-size: 1.2rem;
  font-weight: 700;
  cursor: pointer;
  margin-top: 1.5rem;
  transition: all 0.3s ease;
  &:hover {
    background: #ff3333;
  }
`;

const ErrorMsg = styled.div`
  color: #ff4b4b;
  font-size: 1rem;
  grid-column: 1 / -1;
`;

const SuccessMsg = styled.div`
  color: #1bbf4b;
  font-size: 1.1rem;
  font-weight: 600;
  grid-column: 1 / -1;
  margin-bottom: 1rem;
`;

const Checkout = () => {
  const { cart, clearCart } = useCart();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: '',
    name: '',
    city: '',
    street: '',
    buildingNumber: '',
    flatNumber: '',
    phoneNumber: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const getTotal = () => {
    return cart.reduce((sum, item) => sum + ((item.price - (item.price * item.discount / 100)) * item.quantity), 0);
  };

  const handleChange = e => {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    // Basic validation
    for (const key in form) {
      if (!form[key]) {
        setError('Please fill in all fields.');
        setLoading(false);
        return;
      }
    }
    if (!/^01[0-2,5]{1}[0-9]{8}$/.test(form.phoneNumber)) {
      setError('Invalid Egyptian phone number.');
      setLoading(false);
      return;
    }
    if (!/^[A-Za-z0-9+_.-]+@(.+)$/.test(form.email)) {
      setError('Invalid email format.');
      setLoading(false);
      return;
    }
    // Build OrderDTO
    const productUUIDS = cart.map(item => (item.productId.startsWith('id') ? item.productId.slice(2) : item.productId));
    // Map: sizeId -> quantity
    const productSizesUUIDS = {};
    cart.forEach(item => {
      if (item.sizeId) {
        productSizesUUIDS[item.sizeId] = item.quantity;
      }
    });
    // Remove 'id' if present in form
    const { id, ...customerDTO } = form;
    const orderDTO = {
      customerDTO,
      productUUIDS,
      productSizesUUIDS
    };
    try {
      const response = await axios.post('/api/v1/order/createOrder', orderDTO);
      console.log('Order API response:', response);
      setSuccess('Order placed successfully!');
      setError('');
      clearCart();
      setTimeout(() => navigate('/'), 1500);
    } catch (err) {
      console.error('Order API error:', err, err.response);
      if (!success) {
        setError(err.response?.data?.message || 'Failed to place order.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container>
      <Title>Checkout</Title>
      <CartSummary>
        {cart.map(item => (
          <CartItem key={item.productId + '-' + item.size}>
            <ProductImage src={`http://localhost:8080${item.image}`} alt={item.name} />
            <ItemInfo>
              <ItemName>{item.name}</ItemName>
              <ItemDetail>Size: {item.size}</ItemDetail>
              <ItemDetail>Quantity: {item.quantity}</ItemDetail>
            </ItemInfo>
            <ItemPrice>
              LE {((item.price - (item.price * item.discount / 100)) * item.quantity).toFixed(2)}
            </ItemPrice>
          </CartItem>
        ))}
        <Total>Total: LE {getTotal().toFixed(2)}</Total>
      </CartSummary>
      <Form onSubmit={handleSubmit}>
        {success && <SuccessMsg>{success}</SuccessMsg>}
        {!success && error && <ErrorMsg>{error}</ErrorMsg>}
        <FormGroup>
          <Label>Email</Label>
          <Input name="email" type="email" value={form.email} onChange={handleChange} required />
        </FormGroup>
        <FormGroup>
          <Label>Name</Label>
          <Input name="name" value={form.name} onChange={handleChange} required />
        </FormGroup>
        <FormGroup>
          <Label>City</Label>
          <Input name="city" value={form.city} onChange={handleChange} required />
        </FormGroup>
        <FormGroup>
          <Label>Street</Label>
          <Input name="street" value={form.street} onChange={handleChange} required />
        </FormGroup>
        <FormGroup>
          <Label>Building Number</Label>
          <Input name="buildingNumber" value={form.buildingNumber} onChange={handleChange} required />
        </FormGroup>
        <FormGroup>
          <Label>Flat Number</Label>
          <Input name="flatNumber" value={form.flatNumber} onChange={handleChange} required />
        </FormGroup>
        <FormGroup>
          <Label>Phone Number</Label>
          <Input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required />
        </FormGroup>
        <SubmitButton type="submit" disabled={loading}>{loading ? 'Placing Order...' : 'Confirm Order'}</SubmitButton>
      </Form>
    </Container>
  );
};

export default Checkout; 