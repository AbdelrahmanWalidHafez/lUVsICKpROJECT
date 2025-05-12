import React, { useEffect, useState, useRef } from 'react';
import styled, { keyframes } from 'styled-components';
import axios from '../utils/axios';
import { FaHeart } from 'react-icons/fa';
import { Link } from 'react-router-dom';

const HeroSection = styled.section`
  position: relative;
  width: 100vw;
  left: 50%;
  transform: translateX(-50%);
  height: 60vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
`;

const HeroImage = styled.img`
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100%;
  object-fit: cover;
  z-index: 0;
  pointer-events: none;
  user-select: none;
  transition: transform 0.2s;
`;

const HeroOverlay = styled.div`
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1;
`;

const Logo = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
`;

const HeartLogo = styled(FaHeart)`
  color: #ff4b4b;
  font-size: 2.5rem;
  margin: 0 1rem;
`;

const HeroText = styled.h1`
  color: #fff;
  font-size: 2.5rem;
  font-weight: 800;
  text-align: center;
  margin-bottom: 0.5rem;
`;

const HeroSubText = styled.h2`
  color: #fff;
  font-size: 1.5rem;
  font-weight: 400;
  text-align: center;
  margin-bottom: 2rem;
`;

const Section = styled.section`
  padding: 3rem 0 2rem 0;
  background: #fff;
`;

const SectionTitle = styled.h2`
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 2rem;
`;

const ProductsRow = styled.div`
  display: flex;
  overflow-x: auto;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto 2rem auto;
  padding-bottom: 1rem;
  &::-webkit-scrollbar {
    height: 8px;
  }
  &::-webkit-scrollbar-thumb {
    background: #eee;
    border-radius: 4px;
  }
`;

const ProductCard = styled.div`
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.07);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const ProductImage = styled.img`
  width: 100%;
  height: 220px;
  object-fit: cover;
`;

const ProductName = styled.div`
  font-weight: 600;
  margin: 1rem 0 0.5rem 0;
`;

const ProductPrice = styled.div`
  color: #ff4b4b;
  font-weight: 700;
  margin-bottom: 1rem;
`;

const CenteredSection = styled(Section)`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const CenteredSectionTitle = styled(SectionTitle)`
  text-align: center;
`;

const pulse = keyframes`
  0% { transform: scale(1); }
  50% { transform: scale(1.07); }
  100% { transform: scale(1); }
`;

const fadeIn = keyframes`
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
`;

const MadeWithLove = styled.h1`
  text-align: center;
  font-size: 2.2rem;
  font-weight: 900;
  color: #ff4b4b;
  margin-bottom: 0.5rem;
  letter-spacing: 0.1em;
  animation: ${pulse} 1.8s infinite;
`;

const LargeProductCard = styled(ProductCard)`
  max-width: 370px;
  min-width: 340px;
  box-shadow: 0 6px 24px rgba(0,0,0,0.10);
  padding-bottom: 1.5rem;
  opacity: 0;
  animation: ${fadeIn} 0.8s ease forwards;
  animation-delay: ${({ index }) => index * 0.12}s;
  will-change: transform, opacity;
  transition: transform 0.3s cubic-bezier(.25,.8,.25,1), box-shadow 0.3s;
  &:hover {
    transform: scale(1.045) translateY(-6px);
    box-shadow: 0 12px 32px rgba(0,0,0,0.13);
  }
`;

const LargeProductImage = styled(ProductImage)`
  height: 370px;
  max-width: 100%;
`;

const LargeProductName = styled(ProductName)`
  font-size: 1.4rem;
  margin-top: 1.2rem;
`;

const LargeProductPrice = styled(ProductPrice)`
  font-size: 1.3rem;
  margin-bottom: 1.2rem;
`;

const Subtitle = styled.div`
  text-align: center;
  color: #888;
  font-size: 1.15rem;
  margin-bottom: 2.2rem;
`;

const ViewAllButton = styled(Link)`
  display: inline-block;
  margin: 2.5rem auto 0 auto;
  background: #ff4b4b;
  color: #fff;
  font-weight: 700;
  font-size: 1.1rem;
  padding: 1rem 2.5rem;
  border-radius: 10px;
  text-decoration: none;
  box-shadow: 0 2px 8px rgba(255,75,75,0.08);
  transition: background 0.2s, transform 0.2s;
  &:hover {
    background: #d32f2f;
    transform: translateY(-2px) scale(1.04);
  }
`;

const HomePage = () => {
  const [newArrivals, setNewArrivals] = useState([]);
  const [scrollY, setScrollY] = useState(0);
  const productRowRef = useRef(null);

  useEffect(() => {
    const fetchNewArrivals = async () => {
      const res = await axios.get('/api/v1/product/newArrivals');
      setNewArrivals(res.data);
    };
    fetchNewArrivals();
  }, []);

  useEffect(() => {
    const handleScroll = () => {
      setScrollY(window.scrollY);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Parallax for hero image: move background position based on scroll
  const heroParallax = Math.max(-40, Math.min(40, scrollY * 0.25));

  return (
    <>
      <HeroSection>
        <HeroImage
          src={process.env.PUBLIC_URL + '/shutterstock_511127908-scaled.jpg'}
          alt="Hero"
          style={{ transform: `translateY(${heroParallax}px)` }}
        />
        <HeroOverlay>
          <Logo>
            <span style={{ color: '#000', fontWeight: 800, fontSize: '2.5rem' }}>Luv</span>
            <HeartLogo />
            <span style={{ color: '#ff4b4b', fontWeight: 800, fontSize: '2.5rem' }}>sick</span>
          </Logo>
          <HeroSubText>FEEL THE VIBE.</HeroSubText>
          <HeroText>CHIC IN THE HEAT</HeroText>
        </HeroOverlay>
      </HeroSection>
      <CenteredSection>
        <MadeWithLove>MADE WITH LOVE</MadeWithLove>
        <CenteredSectionTitle>Hot Off the Rack</CenteredSectionTitle>
        <Subtitle>Check out our latest arrivals and best sellers!</Subtitle>
        <ProductsRow ref={productRowRef}>
          {newArrivals.map((product, idx) => {
            const priceValue = parseFloat(product.price) || 0;
            const discountValue = parseFloat(product.discount) || 0;
            const discountedPrice = priceValue - (priceValue * discountValue / 100);
            const parallax = Math.min(30, Math.max(-30, (scrollY - 200) * 0.08));
            return (
              <Link key={product.id} to={`/product/${product.id}`} state={{ product }} style={{ textDecoration: 'none' }}>
                <LargeProductCard index={idx}>
                  <LargeProductImage
                    src={`http://localhost:8080/api/v1/product/image/${product.id}`}
                    alt={product.name}
                    style={{ transform: `translateY(${parallax}px)`, transition: 'transform 0.3s cubic-bezier(.25,.8,.25,1)' }}
                  />
                  <LargeProductName style={{ color: '#222' }}>{product.name}</LargeProductName>
                  <LargeProductPrice>
                    <span style={{ color: '#111', textDecoration: product.discount > 0 ? 'line-through' : 'none' }}>
                      LE {priceValue.toFixed(2)}
                    </span>
                    {product.discount > 0 && (
                      <span style={{ color: '#ff4b4b', marginLeft: '0.5rem', fontWeight: 700 }}>
                        LE {discountedPrice.toFixed(2)}
                      </span>
                    )}
                  </LargeProductPrice>
                </LargeProductCard>
              </Link>
            );
          })}
        </ProductsRow>
      </CenteredSection>
    </>
  );
};

export default HomePage; 