import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled, { keyframes } from 'styled-components';
import { FaUser, FaLock, FaHeart } from 'react-icons/fa';
import axios from '../utils/axios';
import { useAuth } from '../context/AuthContext';

const fadeIn = keyframes`
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

const LoginContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: white;
  padding: 20px;
`;

const LoginCard = styled.div`
  background: white;
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  margin: 1rem;
  animation: ${fadeIn} 0.6s ease-out;
  border: 1px solid #eee;
`;

const LogoContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 2rem;
`;

const HeartLogo = styled(FaHeart)`
  color: #ff4b4b;
  font-size: 2.5rem;
  margin-right: 1rem;
`;

const Title = styled.h1`
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: -1px;
`;

const TitlePart = styled.span`
  color: ${props => props.color};
`;

const Subtitle = styled.p`
  color: #666;
  text-align: center;
  margin-bottom: 2rem;
  font-size: 1rem;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

const InputGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  position: relative;
`;

const Label = styled.label`
  color: #333;
  font-weight: 600;
  font-size: 0.9rem;
  margin-left: 0.2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const Input = styled.input`
  padding: 1rem 1rem 1rem 3rem;
  border: 2px solid #eee;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;

  &:focus {
    outline: none;
    border-color: #ff4b4b;
    box-shadow: 0 0 0 3px rgba(255, 75, 75, 0.1);
  }

  &::placeholder {
    color: #999;
  }
`;

const IconWrapper = styled.div`
  position: absolute;
  left: 1rem;
  top: 2.3rem;
  color: #111;
  font-size: 1.2rem;
`;

const Button = styled.button`
  background: #ff4b4b;
  color: white;
  padding: 1rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 0.5rem;

  &:hover {
    background: #ff3333;
    transform: translateY(-1px);
    box-shadow: 0 5px 15px rgba(255, 75, 75, 0.2);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    background: #cccccc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
`;

const ErrorMessage = styled.div`
  color: #ff4b4b;
  text-align: center;
  margin-top: 1rem;
  padding: 0.8rem;
  border-radius: 8px;
  background: rgba(255, 75, 75, 0.1);
  font-size: 0.9rem;
  animation: ${fadeIn} 0.3s ease-out;
`;

const Auth = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await axios.post('/api/v1/auth/login', formData);
      if (response.data === "successful login") {
        login({ email: formData.email });
        navigate('/dashboard');
      } else {
        setError('Login failed. Please try again.');
      }
    } catch (err) {
      if (err.response) {
        setError(err.response.data.message || 'Login failed. Please try again.');
      } else {
        setError('Network error. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <LoginContainer>
      <LoginCard>
        <LogoContainer>
          <HeartLogo />
          <Title>
            <TitlePart color="#000">Luv</TitlePart>
            <TitlePart color="#ff4b4b">sick</TitlePart>
          </Title>
        </LogoContainer>
        <Subtitle>Please sign in to continue</Subtitle>
        <Form onSubmit={handleSubmit}>
          <InputGroup>
            <Label>
              <FaUser style={{ color: '#111' }} /> Email Address
            </Label>
            <IconWrapper>
              <FaUser />
            </IconWrapper>
            <Input
              type="email"
              name="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </InputGroup>
          <InputGroup>
            <Label>
              <FaLock style={{ color: '#111' }} /> Password
            </Label>
            <IconWrapper>
              <FaLock />
            </IconWrapper>
            <Input
              type="password"
              name="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </InputGroup>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? 'Signing in...' : 'Sign In'}
          </Button>
          {error && <ErrorMessage>{error}</ErrorMessage>}
        </Form>
      </LoginCard>
    </LoginContainer>
  );
};

export default Auth; 