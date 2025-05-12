import React, { useState, useEffect, useRef } from 'react';
import styled from 'styled-components';
import { NavLink, Link, useNavigate, useLocation } from 'react-router-dom';
import { FaHeart, FaShoppingCart, FaUser } from 'react-icons/fa';
import axios from '../utils/axios';
import { useCart } from '../context/CartContext';

const NavbarContainer = styled.nav`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 100;
  background: ${({ scrolled, isHome }) =>
    isHome ? (scrolled ? '#fff' : 'transparent') : '#111'};
  box-shadow: ${({ scrolled, isHome }) =>
    isHome ? (scrolled ? '0 2px 8px rgba(0,0,0,0.1)' : 'none') : '0 2px 8px rgba(0,0,0,0.1)'};
  transition: background 0.3s, box-shadow 0.3s;
`;

const NavbarContent = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1600px;
  margin: 0 auto;
  padding: 0.5rem 2rem;
`;

const NavSection = styled.div`
  display: flex;
  align-items: center;
  gap: 2rem;
`;

const CenterLogo = styled(Link)`
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  text-decoration: none;
  font-size: 2rem;
  font-weight: 800;
  color: ${({ isHome }) => (isHome ? '#000' : '#fff')};
`;

const HeartLogo = styled(FaHeart)`
  color: #ff4b4b;
  font-size: 2rem;
  margin: 0 0.5rem;
`;

const getNavColor = (isHome, scrolled) => {
  if (!isHome) return '#fff';
  if (scrolled) return '#111';
  return '#fff';
};

const MenuLink = styled(NavLink)`
  text-decoration: none;
  font-size: 1.1rem;
  font-weight: 600;
  padding: 0.5rem 0.8rem;
  border-radius: 6px;
  transition: color 0.3s, background 0.2s;
  color: ${({ isHome, scrolled }) => getNavColor(isHome, scrolled)};
  &.active {
    background: #ff4b4b;
    color: #fff !important;
  }
  &:hover {
    background: #f8f9fa;
    color: #ff4b4b;
  }
`;

const DropdownParent = styled.div`
  position: relative;
  display: inline-block;
`;

const DropdownButton = styled.div`
  color: ${({ isHome, scrolled }) => getNavColor(isHome, scrolled)};
  font-size: 1.1rem;
  font-weight: 600;
  padding: 0.5rem 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  user-select: none;
  transition: color 0.3s, background 0.2s;
  &:hover {
    background: ${({ scrolled }) => (scrolled ? '#f8f9fa' : 'rgba(255,255,255,0.1)')};
    color: #ff4b4b;
  }
  display: flex;
  align-items: center;
  gap: 0.3rem;
`;

const DropdownMenu = styled.ul`
  position: absolute;
  top: 2.5rem;
  left: 0;
  background: #fff;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  border-radius: 8px;
  min-width: 180px;
  padding: 0.5rem 0;
  z-index: 10;
  display: ${({ open }) => (open ? 'block' : 'none')};
  max-height: 400px;
  overflow-y: auto;
`;

const DropdownItem = styled(Link)`
  display: block;
  color: #111;
  padding: 0.7rem 1.2rem;
  text-decoration: none;
  font-size: 1rem;
  &:hover {
    background: #f8f9fa;
    color: #ff4b4b;
  }
`;

const Icons = styled.div`
  display: flex;
  align-items: center;
  gap: 1.5rem;
`;

const IconButton = styled.button`
  background: none;
  border: none;
  color: ${({ isHome, scrolled }) => getNavColor(isHome, scrolled)};
  font-size: 1.5rem;
  cursor: pointer;
  transition: color 0.3s;
  display: flex;
  align-items: center;
  &:hover {
    color: #ff4b4b;
  }
`;

const CartBadge = styled.span`
  position: absolute;
  top: -8px;
  right: -8px;
  background: #ff4b4b;
  color: #fff;
  border-radius: 50%;
  padding: 0.18em 0.55em;
  font-size: 0.85rem;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  z-index: 2;
`;

const CartIconContainer = styled.div`
  position: relative;
  display: inline-block;
  .cart-bump {
    animation: cart-bounce 0.4s;
  }
  @keyframes cart-bounce {
    0% { transform: scale(1); }
    30% { transform: scale(1.2); }
    60% { transform: scale(0.95); }
    100% { transform: scale(1); }
  }
`;

const Navbar = () => {
  const [categories, setCategories] = useState([]);
  const [categoriesOpen, setCategoriesOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const closeTimeout = useRef();
  const { cart, lastAdded } = useCart();
  const [cartBump, setCartBump] = useState(false);
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);

  const isHome = location.pathname === '/';

  useEffect(() => {
    const onScroll = () => {
      setScrolled(window.scrollY > 40);
    };
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const res = await axios.get('/api/v1/category/getCategories');
        setCategories(res.data);
      } catch (err) {
        setCategories([
          { id: 1, name: 'T-shirt' },
          { id: 2, name: 'SweatPants' },
          { id: 3, name: 'Jacket' },
        ]);
      }
    };
    fetchCategories();
  }, []);

  useEffect(() => {
    if (lastAdded) {
      setCartBump(true);
      const timer = setTimeout(() => setCartBump(false), 400);
      return () => clearTimeout(timer);
    }
  }, [lastAdded]);

  const handleDropdownEnter = () => {
    if (closeTimeout.current) clearTimeout(closeTimeout.current);
    setCategoriesOpen(true);
  };

  const handleDropdownLeave = () => {
    closeTimeout.current = setTimeout(() => setCategoriesOpen(false), 200);
  };

  return (
    <NavbarContainer scrolled={scrolled} isHome={isHome}>
      <NavbarContent>
        {/* Left Section */}
        <NavSection>
          <MenuLink to="/" isHome={isHome} scrolled={scrolled}>Home</MenuLink>
          <MenuLink to="/products" isHome={isHome} scrolled={scrolled}>All Products</MenuLink>
          <DropdownParent
            onMouseEnter={handleDropdownEnter}
            onMouseLeave={handleDropdownLeave}
          >
            <DropdownButton isHome={isHome} scrolled={scrolled}>
              Categories <span style={{fontSize:'0.8em'}}>&#9662;</span>
            </DropdownButton>
            <DropdownMenu open={categoriesOpen}>
              {categories.map(cat => (
                <DropdownItem key={cat.id} to={`/category/${cat.name}`}>{cat.name}</DropdownItem>
              ))}
            </DropdownMenu>
          </DropdownParent>
          <MenuLink to="/about" isHome={isHome} scrolled={scrolled}>About Us</MenuLink>
        </NavSection>
        {/* Center Logo */}
        <CenterLogo to="/" isHome={isHome}>
          <span style={{ color: isHome ? '#000' : '#fff', fontWeight: 800 }}>Luv</span>
          <HeartLogo />
          <span style={{ color: '#ff4b4b', fontWeight: 800 }}>sick</span>
        </CenterLogo>
        {/* Right Section (Icons) */}
        <Icons>
          <IconButton title="Login" onClick={() => navigate('/login')} isHome={isHome} scrolled={scrolled}>
            <FaUser />
          </IconButton>
          <Link to="/cart">
            <CartIconContainer>
              <IconButton
                title="Cart"
                isHome={isHome}
                scrolled={scrolled}
                className={cartBump ? 'cart-bump' : ''}
                style={{ position: 'relative' }}
              >
                <FaShoppingCart />
                {totalItems > 0 && <CartBadge>{totalItems}</CartBadge>}
              </IconButton>
            </CartIconContainer>
          </Link>
        </Icons>
      </NavbarContent>
    </NavbarContainer>
  );
};

export default Navbar;