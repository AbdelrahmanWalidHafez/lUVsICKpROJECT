import React from 'react';
import styled from 'styled-components';
import { FaBox, FaShoppingCart, FaUsers, FaHeart, FaTags } from 'react-icons/fa';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import axios from '../utils/axios';
import { refreshCsrfToken } from '../utils/csrf';

const DashboardContainer = styled.div`
  display: flex;
  min-height: 100vh;
  background: #f8f9fa;
`;

const Sidebar = styled.div`
  width: 250px;
  background: white;
  padding: 2rem;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
`;

const LogoContainer = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
`;

const HeartLogo = styled(FaHeart)`
  color: #ff4b4b;
  font-size: 2rem;
  margin-right: 1rem;
`;

const Title = styled.h1`
  font-size: 1.5rem;
  font-weight: 800;
  letter-spacing: -1px;
`;

const TitlePart = styled.span`
  color: ${props => props.color};
`;

const MenuList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
`;

const MenuItem = styled.li`
  margin-bottom: 0.5rem;
`;

const MenuLink = styled(Link)`
  display: flex;
  align-items: center;
  padding: 1rem;
  color: #333;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  &:hover {
    background: #ff4b4b;
    color: white;
  }
  
  &.active {
    background: #ff4b4b;
    color: white;
  }
`;

const MenuIcon = styled.div`
  margin-right: 1rem;
  font-size: 1.2rem;
`;

const MainContent = styled.main`
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
`;

const Dashboard = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await refreshCsrfToken();
      await axios.post('/api/v1/auth/logout');
      localStorage.clear();
      navigate('/login', { replace: true });
    } catch (err) {
      alert('Logout failed. Please try again.');
    }
  };

  return (
    <DashboardContainer>
      <Sidebar>
        <LogoContainer>
          <HeartLogo />
          <Title>
            <TitlePart color="#000">Luv</TitlePart>
            <TitlePart color="#ff4b4b">sick</TitlePart>
          </Title>
        </LogoContainer>
        <MenuList>
          <MenuItem>
            <MenuLink to="/dashboard/product-list">
              <MenuIcon><FaBox /></MenuIcon>
              View Products
            </MenuLink>
          </MenuItem>
          <MenuItem>
            <MenuLink to="/dashboard/products">
              <MenuIcon><FaBox /></MenuIcon>
              Product Management
            </MenuLink>
          </MenuItem>
          <MenuItem>
            <MenuLink to="/dashboard/orders">
              <MenuIcon><FaShoppingCart /></MenuIcon>
              Orders
            </MenuLink>
          </MenuItem>
          <MenuItem>
            <MenuLink to="/dashboard/categories">
              <MenuIcon><FaTags /></MenuIcon>
              Categories
            </MenuLink>
          </MenuItem>
          <MenuItem>
            <MenuLink to="/dashboard/users">
              <MenuIcon><FaUsers /></MenuIcon>
              Add Users
            </MenuLink>
          </MenuItem>
        </MenuList>
        <button style={{marginTop: '2rem', width: '100%', background: '#ff4b4b', color: 'white', border: 'none', borderRadius: '8px', padding: '1rem', fontWeight: 600, cursor: 'pointer'}} onClick={handleLogout}>
          Logout
        </button>
      </Sidebar>
      <MainContent>
        <Outlet />
      </MainContent>
    </DashboardContainer>
  );
};

export default Dashboard; 