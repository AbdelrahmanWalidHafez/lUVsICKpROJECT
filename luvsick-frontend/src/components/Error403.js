import React from 'react';
import styled from 'styled-components';
import { FaLock } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
  text-align: center;
`;

const Icon = styled(FaLock)`
  color: #ff4b4b;
  font-size: 4rem;
  margin-bottom: 1rem;
`;

const Title = styled.h1`
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 1rem;
`;

const Message = styled.p`
  color: #666;
  font-size: 1.2rem;
  margin-bottom: 2rem;
`;

const Button = styled.button`
  background: #ff4b4b;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 1rem 2rem;
  font-size: 1.1rem;
  cursor: pointer;
  &:hover {
    background: #ff3333;
  }
`;

const Error403 = () => {
  const navigate = useNavigate();
  return (
    <Container>
      <Icon />
      <Title>403 Forbidden</Title>
      <Message>You do not have permission to access this page.</Message>
      <Button onClick={() => navigate('/dashboard')}>Go to Dashboard</Button>
    </Container>
  );
};

export default Error403; 